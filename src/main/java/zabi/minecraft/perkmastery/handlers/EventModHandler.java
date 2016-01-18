package zabi.minecraft.perkmastery.handlers;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.EnumStatus;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.entity.player.PlayerUseItemEvent;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import zabi.minecraft.perkmastery.PerkMastery;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer.InventoryType;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer.PlayerClass;
import zabi.minecraft.perkmastery.items.ItemList;
import zabi.minecraft.perkmastery.items.special.EvocationTome;
import zabi.minecraft.perkmastery.items.special.UndeadSoul;
import zabi.minecraft.perkmastery.items.special.UndeadSoul.UndeadType;
import zabi.minecraft.perkmastery.libs.LibGameRules;
import zabi.minecraft.perkmastery.network.packets.ClientRequestForData;
import zabi.minecraft.perkmastery.network.packets.JumpBoost;


public class EventModHandler {

	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent event) {
		if (event.entity instanceof EntityPlayer) {

			if (!event.entity.worldObj.isRemote) {
				EntityPlayer player = (EntityPlayer) event.entity;

				// Fa un toggle dell'attività per sincronizzare il
				// HackyPlayerControllerMP
				ToggleHandler.toggleReachDistance(player, ExtendedPlayer.isAbilityEnabled(player, PlayerClass.BUILDER, 1));
				ToggleHandler.toggleWellTrained(player, ExtendedPlayer.isAbilityEnabled(player, PlayerClass.EXPLORER, 4));
			} else {
				PerkMastery.network.sendToServer(new ClientRequestForData());
				Thread updateChecker = new Thread(new UpdateHandler());
				updateChecker.setDaemon(true);
				updateChecker.setName("PerkMasteryUpdates");
				updateChecker.setPriority(Thread.MIN_PRIORITY);
				updateChecker.start();
			}
		}
	}

	@SubscribeEvent
	public void onPlayerJump(LivingJumpEvent event) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT && event.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = ((EntityPlayer) event.entityLiving);
			if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.EXPLORER, 4)) {
				player.motionY += 0.15F;
				player.fallDistance -= 0.15F;
				PerkMastery.network.sendToServer(new JumpBoost());
			}
		}
	}

	@SubscribeEvent
	public void onPlayerFall(LivingFallEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = ((EntityPlayer) event.entityLiving);
			if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.BUILDER, 4)) {
				player.fallDistance = player.fallDistance / 2F;
			}
		}
	}

	@SubscribeEvent
	public void onEntityDeath(LivingDeathEvent event) {
		if (event.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = ((EntityPlayer) event.entityLiving);
			if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.EXPLORER, 5) && event.source.equals(DamageSource.fall)) {
				player.setHealth(1);
				event.setCanceled(true);
			}
			if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.MAGE, 6) && ExtendedPlayer.hasDeathAmulet(player) && !event.isCanceled()) {
				ExtendedPlayer.destroyAmulet(player);
				player.setHealth(10);
				event.setCanceled(true);
			}
			if (!player.worldObj.isRemote && !event.isCanceled()) {
				ExtendedPlayer.dropItemsOnDeath(player);
			}
		} else {
			if (!event.entity.worldObj.isRemote && event.entity.worldObj.getGameRules().getBoolean(LibGameRules.doMobLoot.name()) && !event.isCanceled() && Math.random() < 0.1) {
				if (event.source.getEntity() != null && event.source.getEntity() instanceof EntityPlayer && ExtendedPlayer.isAbilityEnabled((EntityPlayer) event.source.getEntity(), PlayerClass.MAGE, 4)) {
					if ((event.entityLiving instanceof EntityZombie)) {
						event.entityLiving.worldObj.spawnEntityInWorld(new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, UndeadSoul.getNewSoul(UndeadType.ZOMBIE)));
					} else if ((event.entityLiving instanceof EntitySkeleton)) {
						event.entityLiving.worldObj.spawnEntityInWorld(new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, UndeadSoul.getNewSoul(UndeadType.SKELETON)));
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent event) {
		// PLAYER BERSAGLIO
		if (event.entityLiving instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.entityLiving;
			if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.WARRIOR, 6)) {
				if (((event.source.getSourceOfDamage() instanceof EntityLiving)) || ((event.source.getSourceOfDamage() instanceof EntityPlayer))) {
					if (!player.isPotionActive(Potion.absorption.id)) {
						PotionEffect pfx = new PotionEffect(Potion.absorption.id, 400, 1, false, false);// Absorption
						pfx.getCurativeItems().clear();
						player.addPotionEffect(pfx);
					}
				}
			}
			if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.WARRIOR, 3)) {
				for (int i = 0; i < 4; i++)
					if (ExtendedPlayer.getExtraInventory(player, InventoryType.REAL)[19 + i] != null) {
						event.ammount = 0.9F * event.ammount;
						ExtendedPlayer.getExtraInventory(player, InventoryType.REAL)[19 + i].attemptDamageItem(2, player.getRNG());
					}
			}
			if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.ARCHER, 2) && event.source.isProjectile() && !player.worldObj.isRemote && Math.random() < 0.7) {
				event.setCanceled(true);
			}

		}

		// PLAYER CAUSA
		if (event.source.getEntity() != null && event.source.getEntity() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) event.source.getEntity();
			if (!player.worldObj.isRemote) {
				if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.WARRIOR, 1) && !event.source.isProjectile()) event.entityLiving.attackEntityFrom(DamageSource.generic, 2F);
				if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.WARRIOR, 2)) {
					EntityLivingBase et = event.entityLiving;
					if (et.getHeldItem() != null && player.ticksExisted % 10 == 1) {
						et.entityDropItem(et.getHeldItem().copy(), 2F);
						et.setCurrentItemOrArmor(0, null);
					}
				}
				if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.WARRIOR, 5)) if (event.entityLiving instanceof IBossDisplayData) event.entityLiving.attackEntityFrom(DamageSource.generic, 6F);
				if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.ARCHER, 1) && event.source.isProjectile()) event.entityLiving.attackEntityFrom(DamageSource.generic, 2F);
				if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.ARCHER, 4) && !event.entityLiving.worldObj.isRemote && Math.random() < 0.1) {
					if (event.entityLiving instanceof EntityPlayer) {

						PotionEffect pfx = new PotionEffect(Potion.confusion.id, 60, 2, false,false);
						pfx.getCurativeItems().clear();
						PotionEffect pfx2 = new PotionEffect(Potion.moveSlowdown.id, 60, 1, false,false);
						pfx2.getCurativeItems().clear();
						event.entityLiving.addPotionEffect(pfx2);
						event.entityLiving.addPotionEffect(pfx);
					} else {
						PotionEffect pfx = new PotionEffect(Potion.moveSlowdown.id, 60, 1, false,false);
						event.entityLiving.addPotionEffect(pfx);
					}
				}
				if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.MAGE, 4) && !event.entityLiving.worldObj.isRemote) {
					ItemStack ci = player.inventory.mainInventory[(player.inventory.currentItem + 1) % 9];
					if (ci != null && ci.getItem().equals(ItemList.tomeEvocation)) {
						EvocationTome.summonAtPlayer(ci, player, (EntityLiving) event.entityLiving);
					}
				}
			}
		}
	}

	@SubscribeEvent
	public void onBlockBreakEvent(BreakEvent evt) {
		if (evt.getPlayer() == null) return;
		boolean delicate = (DigHandler.isToolDelicate(evt) && ExtendedPlayer.isAbilityEnabled(evt.getPlayer(), PlayerClass.BUILDER, 2));
		boolean silk = EnchantmentHelper.getSilkTouchModifier(evt.getPlayer());
		if (!evt.getResult().equals(Result.DENY) && ExtendedPlayer.isAbilityEnabled(evt.getPlayer(), PlayerClass.MINER, 5)) DigHandler.applyCrumbling(evt.pos.getX(), evt.pos.getY() + 1, evt.pos.getZ(), evt.world, evt.getPlayer());
		if (!evt.getResult().equals(Result.DENY) && ExtendedPlayer.isAbilityEnabled(evt.getPlayer(), PlayerClass.MINER, 2) && !evt.world.isRemote) DigHandler.applyVeinminer(evt, evt.pos.getX(), evt.pos.getY(), evt.pos.getZ(), true);
		else if (!silk && ExtendedPlayer.isAbilityEnabled(evt.getPlayer(), PlayerClass.MINER, 4) && !evt.world.isRemote && Math.random() < 0.3) DigHandler.applyFortune(evt);
		if (delicate) {
			if (evt.state.getBlock().canSilkHarvest(evt.world, evt.pos, evt.state, evt.getPlayer()) && DigHandler.containsGlass(evt.state.getBlock().getUnlocalizedName().toLowerCase())) {
				evt.setCanceled(true);
				evt.world.setBlockToAir(evt.pos);
				if (evt.world.getGameRules().getBoolean(LibGameRules.doTileDrops.name())) evt.world.spawnEntityInWorld(new EntityItem(evt.world, evt.pos.getX(), evt.pos.getY(), evt.pos.getZ(), new ItemStack(evt.state.getBlock(), 1, 0))); //TODO l'ultimo zero dovrebbe essere il metadato del blocco...
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerTrySleep(PlayerSleepInBedEvent evt) {
		if (ExtendedPlayer.isAbilityEnabled(evt.entityPlayer, PlayerClass.EXPLORER, 1) && !evt.entityPlayer.worldObj.isDaytime()) {
			evt.result = EnumStatus.OK;
			if (evt.entityPlayer.isRiding()) evt.entityPlayer.mountEntity((Entity) null);
			if (!evt.entityPlayer.worldObj.isAirBlock(evt.pos)) {
				EnumFacing l = evt.entityPlayer.worldObj.getBlockState(evt.pos).getBlock().getBedDirection(evt.entityPlayer.worldObj, evt.pos);
				float f1 = 0.5F;
				float f = 0.5F;
				switch (l.ordinal()) {
				case 0:
					f = 0.9F;
					break;
				case 1:
					f1 = 0.1F;
					break;
				case 2:
					f = 0.1F;
					break;
				case 3:
					f1 = 0.9F;
				}

				evt.entityPlayer.setPosition((double) ((float) evt.pos.getX() + f1), (double) ((float) evt.pos.getY() + 0.9375F), (double) ((float) evt.pos.getZ() + f));
			} else
				evt.entityPlayer.setPosition((double) ((float) evt.pos.getX() + 0.5F), (double) ((float) evt.pos.getY() + 0.9375F), (double) ((float) evt.pos.getZ() + 0.5F));

			ReflectionHelper.setPrivateValue(EntityPlayer.class, evt.entityPlayer, true, "sleeping", "field_71083_bS");//TODO
			ReflectionHelper.setPrivateValue(EntityPlayer.class, evt.entityPlayer, 0, "sleepTimer", "field_71076_b");//TODO
			evt.entityPlayer.playerLocation = evt.pos;
			evt.entityPlayer.motionX = evt.entityPlayer.motionZ = evt.entityPlayer.motionY = 0.0D;

			if (!evt.entityPlayer.worldObj.isRemote) evt.entityPlayer.worldObj.updateAllPlayersSleepingFlag();

		}
	}

	@SubscribeEvent
	public void onPlayerUseBow(PlayerUseItemEvent.Tick evt) {
		if (ExtendedPlayer.isAbilityEnabled(evt.entityPlayer, PlayerClass.ARCHER, 5) && evt.item.getItem().getItemUseAction(evt.item).equals(EnumAction.BOW)) {
			if (!evt.entityPlayer.isPotionActive(Potion.nightVision)) {
				PotionEffect pfx = new PotionEffect(Potion.nightVision.id, 10, 10, false,false);
				pfx.getCurativeItems().clear();
				evt.entityPlayer.addPotionEffect(pfx);
			}
		}
	}

	@SubscribeEvent
	public void onPlayerRespawn(PlayerEvent.Clone event) {
		ExtendedPlayer.setCompletePlayerData(event.entityPlayer, ExtendedPlayer.getCompletePlayerData(event.original));
	}

//	@SideOnly(Side.CLIENT)
//	@SubscribeEvent
//	public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
//		EntityPlayer player = PerkMastery.proxy.getSinglePlayer();
//		double playerX = player.prevPosX + (player.posX - player.prevPosX) * event.partialTicks;
//		double playerY = player.prevPosY + (player.posY - player.prevPosY) * event.partialTicks;
//		double playerZ = player.prevPosZ + (player.posZ - player.prevPosZ) * event.partialTicks;
//		GL11.glPushMatrix();
//		GL11.glTranslated(-playerX, -playerY, -playerZ);
//		Iterator<IRenderGeneral> it = EffectRegistry.getList();
//		try {
//			while (it.hasNext())
//				it.next().render(event.partialTicks);
//		} catch (Exception e) {
//			Log.e("Error rendering mod effect");
//			e.printStackTrace();
//			EffectRegistry.purge();
//		}
//		GL11.glPopMatrix();
//
//	}
//
//	@SubscribeEvent
//	public void onUnloadingWorld(WorldEvent.Unload event) {
//		if (event.world.isRemote) {
//			EffectRegistry.purge();
//		}
//	}

	@SubscribeEvent
	public void onArrowShot(ArrowLooseEvent evt) {
		if (evt.entityPlayer == null) return;
		if (ExtendedPlayer.isAbilityEnabled(evt.entityPlayer, PlayerClass.ARCHER, 4)) {
			evt.charge = (int) (2 * evt.charge);
		}
	}

	// @SubscribeEvent
	// public void onTargetChanged(LivingSetAttackTargetEvent event) {
	// if (event.target instanceof EntityPlayer && event.entityLiving instanceof EntityMob) {
	// EntityPlayer p = (EntityPlayer) event.target;
	// if (event.entityLiving.getDataWatcher().getWatchableObjectString(10).indexOf(p.getDisplayName()) >= 0) {
	//
	// ((EntityMob) event.entityLiving).setAttackTarget(p.getLastAttacker());
	// ((EntityMob) event.entityLiving).setTarget(p.getLastAttacker());
	// ((EntityMob) event.entityLiving).setRevengeTarget(p.getLastAttacker());
	// }
	// }
	// }

	@SubscribeEvent
	public void onItemPickup(EntityItemPickupEvent event) {
		if (event.entityPlayer != null && ExtendedPlayer.isAbilityEnabled(event.entityPlayer, PlayerClass.MINER, 5)) {
			ItemStack is = event.item.getEntityItem();
			Item item = is.getItem();
			int meta = is.getItemDamage();
			for (ItemStack filter : ExtendedPlayer.getExtraInventory(event.entityPlayer, InventoryType.FILTER)) {
				if (filter != null && filter.getItem().equals(item) && filter.getItemDamage() == meta) {
					event.item.setDead();
					event.setCanceled(true);
					return;
				}
			}
		}
	}
}
