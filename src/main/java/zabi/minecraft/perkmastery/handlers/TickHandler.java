package zabi.minecraft.perkmastery.handlers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zabi.minecraft.perkmastery.Config;
import zabi.minecraft.perkmastery.PerkMastery;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer.InventoryType;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer.PlayerClass;
import zabi.minecraft.perkmastery.entity.PortableFurnaceData;
import zabi.minecraft.perkmastery.gui.GuiHandler;
import zabi.minecraft.perkmastery.misc.Log;
import zabi.minecraft.perkmastery.network.packets.OpenGuiMessage;
import zabi.minecraft.perkmastery.proxy.ClientProxy;


public class TickHandler {

	public ArrayList<Item> updateBlackList = new ArrayList<Item>();
	// Sezione eventi

	@SubscribeEvent
	public void onPlayerTickEvent(TickEvent.PlayerTickEvent evt) {

		EntityPlayer player = evt.player;

		if (evt.side.equals(Side.SERVER)) {
			handleLootfinder(player);
			handleSaturation(player);
			handleBackpack(player);
			handleKnight(player);
			handleFastMiner(player);
			handleFurnace(player);
			handleFloorLayer(player);
			handleShadowForm(player);
		}
		handleParkour(player); // Has to run on client too

	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onRenderTick(TickEvent.RenderTickEvent evt) {
		OreDetectionHandler.getInstance().render(evt.renderTickTime);
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent(priority = EventPriority.NORMAL, receiveCanceled = true)
	public void onKeyPressedEvent(KeyInputEvent event) {
		if (ClientProxy.guiKey.isPressed()) {
			EntityPlayer player = PerkMastery.proxy.getSinglePlayer();
			PerkMastery.network.sendToServer(new OpenGuiMessage());
			PerkMastery.proxy.getSinglePlayer().openGui(PerkMastery.instance, GuiHandler.IDs.GUI_BOOK.ordinal(), player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
		}
	}

	// Sezione abilità

	private void handleSaturation(EntityPlayer player) { // EXPLORER
		if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.EXPLORER, 6) && !player.isPotionActive(MobEffects.SATURATION) && player.ticksExisted % Config.experienceCostTicksInterval == 0 && requestExperience(player)) {
			PotionEffect pfx = new PotionEffect(MobEffects.SATURATION, Config.experienceCostTicksInterval, 0, false,true);// Saturation
			pfx.getCurativeItems().clear();
			player.addPotionEffect(pfx);
		}

	}

	private boolean requestExperience(EntityPlayer player) {
		if (player.capabilities.isCreativeMode || Config.experienceCost <= 0) return true;

		if (player.experienceTotal >= Config.experienceCost) {
			addExperience(player, -Config.experienceCost);
			return true;
		}

		return false;
	}

	public static void addExperience(EntityPlayer player, int amount) {
		if (player.capabilities.isCreativeMode) return;
		int experience = getPlayerXP(player) + amount;
		player.experienceTotal = experience;
		player.experienceLevel = getLevelForExperience(experience);
		int expForLevel = getExperienceForLevel(player.experienceLevel);
		player.experience = (float) (experience - expForLevel) / (float) player.xpBarCap();
	}

	private static int getPlayerXP(EntityPlayer player) {
		return (int) (getExperienceForLevel(player.experienceLevel) + (player.experience * player.xpBarCap()));
	}

	private static int getExperienceForLevel(int level) {
		if (level == 0) { return 0; }
		if (level > 0 && level < 16) {
			return level * 17;
		} else if (level > 15 && level < 31) {
			return (int) (1.5 * Math.pow(level, 2) - 29.5 * level + 360);
		} else {
			return (int) (3.5 * Math.pow(level, 2) - 151.5 * level + 2220);
		}
	}

	private static int getLevelForExperience(int experience) {
		int i = 0;
		while (getExperienceForLevel(i) <= experience) {
			i++;
		}
		return i - 1;
	}

	@SideOnly(Side.SERVER)
	private void handleLootfinder(EntityPlayer player) { // EXPLORER
		if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.EXPLORER, 3)) {
			BlockPos bed = player.getBedLocation(player.worldObj.provider.getDimension());
			if (bed == null) return;
			double distanceFromBed = player.getDistance(bed.getX(), bed.getY(), bed.getZ());
			if (distanceFromBed > 256) {
				if (player.worldObj.rand.nextInt(20000) == 1) {
					ItemStack loot = getRandomLoot(player);
					if (loot != null) player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, player.posX, player.posY + 1, player.posZ, loot));
				}
			}
		}
	}

	private void handleBackpack(EntityPlayer player) {
		if (Config.extraInventoryTicks && ExtendedPlayer.isAbilityEnabled(player, PlayerClass.EXPLORER, 2)) {
			ItemStack[] stacks = ExtendedPlayer.getExtraInventory(player, InventoryType.REAL);
			for (ItemStack is : stacks)
				if (is != null) try {
					if (!updateBlackList.contains(is.getItem())) is.getItem().onUpdate(is, player.worldObj, player, -1, false);
				} catch (Exception e) {
					Log.e("Error trying to tick item");
					Log.e(e);
					Log.e("Adding " + is.getItem() + " to blacklist");
					if (!UpdateHandler.outdated) Log.e("\n\n\nPlease report to https://github.com/zabi94/perkmastery/issues\n\n\n");
					updateBlackList.add(is.getItem());
				}
		}
	}

	private static DamageSource shadowForm = new DamageSource("shadowForm").setDamageBypassesArmor().setMagicDamage();

	private void handleShadowForm(EntityPlayer player) { // ARCHER
		if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.ARCHER, 6) && player.isSneaking()) {
			if (!player.isPotionActive(MobEffects.INVISIBILITY)) {
				PotionEffect pfx = new PotionEffect(MobEffects.INVISIBILITY, Config.experienceCostTicksInterval, 1, false, false);
				pfx.getCurativeItems().clear();
				player.addPotionEffect(pfx);
			}
			if (player.ticksExisted % Config.experienceCostTicksInterval == 0 && !requestExperience(player)) {
				player.attackEntityFrom(shadowForm, 2F);
			}
		}

	}

	private void handleFurnace(EntityPlayer player) {// MINER

		if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.MINER, 6)) {
			PortableFurnaceData data = PortableFurnaceData.getDataFor(player);
			ItemStack[] inv = ExtendedPlayer.getExtraInventory(player, InventoryType.REAL);
			if (data.furnaceBurnTime > 0) {
				--data.furnaceBurnTime;
				data.write();
			}
			if (data.furnaceBurnTime != 0 || inv[23] != null && inv[25] != null) { // Se sta già cuocendo qualcosa || (c'è carbone + qualcosa da cucinare)
				if (data.furnaceBurnTime == 0 && data.canSmelt()) { // Se deve iniziare una nuova operazione di smelting
					data.currentItemBurnTime = data.furnaceBurnTime = TileEntityFurnace.getItemBurnTime(inv[25]); // Leggi il tempo necessario
					if (data.furnaceBurnTime > 0) { // Se c'è bisogno di cuocere
						if (inv[25] != null) { // Se c'è carbone
							--inv[25].stackSize; // Consuma carbone
							ExtendedPlayer.setInventorySlot(player, 25, inv[25]);
							if (inv[25].stackSize == 0) { // Se il carbone finisce
								ExtendedPlayer.setInventorySlot(player, 25, inv[25].getItem().getContainerItem(inv[25])); // Sostituiscilo con il contenitore (Per secchi di lava)
							}
						}
					}
				}

				if (data.isBurning() && data.canSmelt()) { // Se è tutto in regola per lo smelting
					++data.furnaceCookTime; // Avanza con il contatore
					if (data.furnaceCookTime == 200) { // Se il contatore raggiunge il limite
						data.furnaceCookTime = 0; // Resettalo
						PortableFurnaceData.smeltItem(inv, data, player); // Genera il risultato
					}
				} else { // Invece, se qualcosa non è in regola
					data.furnaceCookTime = 0; // Resetta il tempo di cottura
				}
			}

			data.write();
		}
	}

	private void handleFastMiner(EntityPlayer player) {// MINER
		if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.MINER, 1)) {
			if (player.getHeldItem(player.getActiveHand()) != null && (/* validItems.contains(player.getHeldItem().getItem()) || */IntegrationHelper.isPickaxe(player.getHeldItem(player.getActiveHand()))) && !player.isPotionActive(MobEffects.HASTE)) {
				PotionEffect pfx = new PotionEffect(MobEffects.HASTE, 0, 0, false,false);// Haste
				pfx.getCurativeItems().clear();
				player.addPotionEffect(pfx);
			}
		}

	}

	private void handleKnight(EntityPlayer player) { // WARRIOR
		if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.WARRIOR, 4)) {
			if (player.ticksExisted % 40 < 2 && player.isRiding()) {
				
				player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, player.getRidingEntity().getCollisionBoundingBox().expand(3, 3, 3)).stream()
				.filter(et -> !et.equals(player) && !et.equals(player.getRidingEntity()))
				.forEach(et -> {
					et.attackEntityFrom(DamageSource.causePlayerDamage(player), 2F);
				});
				
			}
		}

	}

	private void handleFloorLayer(EntityPlayer player) { // BUILDER
		if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.BUILDER, 5) && player.getHeldItem(player.getActiveHand()) != null && player.isSneaking() && Block.getBlockFromItem(player.getHeldItemMainhand().getItem()) != Blocks.AIR && player.onGround) {
			int y = (int) (player.posY - 0.5);

			ItemStack is = player.getHeldItemMainhand();
			int px = (int) Math.floor(player.posX);
			int pz = (int) Math.floor(player.posZ);

			testAndPlace(is, px, y, pz, player);
			testAndPlace(is, px + 1, y, pz, player);
			testAndPlace(is, px - 1, y, pz, player);
			testAndPlace(is, px, y, pz + 1, player);
			testAndPlace(is, px, y, pz - 1, player);
		}

	}

	private void handleParkour(EntityPlayer player) {// BUILDER
		if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.BUILDER, 4)) {
			if (player.isCollidedHorizontally) {
				player.fallDistance = 0.0F;
				if (player.isSneaking()) player.motionY = 0.0D;
				else
					player.motionY = 0.1176D;
			}
			if (!player.worldObj.isRemote) {
				double motionX = player.posX - player.lastTickPosX;
				double motionZ = player.posZ - player.lastTickPosZ;
				double motionY = player.posY - player.lastTickPosY - 0.765D;
				if (motionY > 0.0D && (motionX == 0D || motionZ == 0D)) player.fallDistance = 0.0F;
			} else if (player.getDisplayName().equals(PerkMastery.proxy.getSinglePlayer().getDisplayName())) {
				EntityPlayer p = PerkMastery.proxy.getSinglePlayer();
				if (p.isCollidedHorizontally) {
					p.fallDistance = 0.0F;
					if (p.isSneaking()) p.motionY = 0.0D;
					else
						p.motionY = 0.1176D;

				}
			}

		}

	}

	// Sezione metodi servizio
	@SideOnly(Side.SERVER)
	private ItemStack getRandomLoot(EntityPlayer player) {
		LootTable loottable = player.worldObj.getLootTableManager().getLootTableFromLocation(LootTableList.CHESTS_SIMPLE_DUNGEON);
		LootContext.Builder b = new LootContext.Builder((WorldServer) player.worldObj).withPlayer(player).withLuck(player.getLuck());
		List<ItemStack> list = loottable.generateLootForPools(player.getRNG(), b.build());
		return list.get(0);
	}

	public void testAndPlace(ItemStack is, int px, int y, int pz, EntityPlayer player) {
		BlockPos posn=new BlockPos(px, y, pz);
		if (is.stackSize > 0 && (player.worldObj.isAirBlock(posn) || player.worldObj.getBlockState(posn).getBlock().isFoliage(player.worldObj, posn)) && Block.getBlockFromItem(is.getItem()).isNormalCube(player.worldObj.getBlockState(posn))) {
			Block blk=Block.getBlockFromItem(player.getHeldItemMainhand().getItem());
			IBlockState state=blk.getStateFromMeta(player.getHeldItemMainhand().getItemDamage());
			player.worldObj.setBlockState(posn, state);
			if (!player.capabilities.isCreativeMode) {
				is.stackSize--;
				if (is.stackSize == 0) player.setHeldItem(EnumHand.MAIN_HAND, null);
			}

		}
	}

}
