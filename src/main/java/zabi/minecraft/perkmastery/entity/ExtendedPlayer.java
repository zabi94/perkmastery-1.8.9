package zabi.minecraft.perkmastery.entity;

import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import zabi.minecraft.perkmastery.Config;
import zabi.minecraft.perkmastery.PerkMastery;
import zabi.minecraft.perkmastery.handlers.ToggleHandler;
import zabi.minecraft.perkmastery.libs.LibGameRules;
import zabi.minecraft.perkmastery.libs.LibGeneral;
import zabi.minecraft.perkmastery.misc.Log;
import zabi.minecraft.perkmastery.network.packets.AmuletShatter;
import zabi.minecraft.perkmastery.network.packets.ToggleAbility;
import zabi.minecraft.perkmastery.network.packets.ToggleAbilityForce;
import zabi.minecraft.perkmastery.network.packets.UnlockAbility;


public class ExtendedPlayer {

	public final static String TAG_PREFIX = LibGeneral.MOD_ID + ":PlayerExtraData", INVENTORY = "inventory", FILTER = "filter", ABILITIES_KEY = "abilities", ENABLED_KEY = "enabled", FURNACE_KEY = "furnace";

	public static int[] getAbilities(EntityPlayer p) {
		if (p!=null) {
			initPlayerDataSafe(p);
			return ((NBTTagCompound) p.getEntityData().getTag(TAG_PREFIX)).getIntArray(ABILITIES_KEY);
		}
		return new int[] {0,0,0,0,0,0};
	}

	public static byte[] getEnabledAbilities(EntityPlayer p) {
		if (p!=null) {
			initPlayerDataSafe(p);
			return ((NBTTagCompound) p.getEntityData().getTag(TAG_PREFIX)).getByteArray(ENABLED_KEY);
		}
		return new byte[] {0,0,0,0,0,0};
	}

	public static ItemStack[] getExtraInventory(EntityPlayer p, InventoryType type) {
		if (p!=null) {
			initPlayerDataSafe(p);
			if (type == InventoryType.FILTER) return pollFilter(p);
			if (type == InventoryType.REAL) return pollExtraInventoryNoFilter(p);
		}
		return null;
	}

	private static ItemStack[] pollExtraInventoryNoFilter(EntityPlayer p) {
		ItemStack[] res = new ItemStack[26];
		NBTTagCompound inv = (NBTTagCompound) ((NBTTagCompound) p.getEntityData().getTag(TAG_PREFIX)).getTag(INVENTORY);
		for (int i = 0; i < 26; i++)
			try {
				if (inv.hasKey("" + (i))) res[i] = ItemStack.loadItemStackFromNBT((NBTTagCompound) inv.getTag("" + (i)));
			} catch (Exception e) {
			}
		return res;
	}

	private static ItemStack[] pollFilter(EntityPlayer p) {
		ItemStack[] res = new ItemStack[5];
		NBTTagCompound inv = (NBTTagCompound) ((NBTTagCompound) p.getEntityData().getTag(TAG_PREFIX)).getTag(FILTER);
		for (int i = 0; i < 5; i++)
			try {
				if (inv.hasKey("" + (i))) res[i] = ItemStack.loadItemStackFromNBT((NBTTagCompound) inv.getTag("" + (i)));
			} catch (Exception e) {
			}
		return res;

	}

	public static void setInventorySlot(EntityPlayer p, int slot, ItemStack is) {
		if (p == null) return;
		try {
			NBTTagCompound extraData = (NBTTagCompound) p.getEntityData().getTag(TAG_PREFIX);
			NBTTagCompound inventoryTag = (NBTTagCompound) extraData.getTag(INVENTORY);
			NBTTagCompound content = new NBTTagCompound();
			if (is != null) is.writeToNBT(content);
			inventoryTag.setTag("" + slot, content);
			extraData.setTag(INVENTORY, inventoryTag);
			p.getEntityData().setTag(TAG_PREFIX, extraData);
		} catch (Exception e) {
		}
	}

	public static void setFilterSlot(EntityPlayer p, int slot, ItemStack is) {
		try {
			NBTTagCompound extraData = (NBTTagCompound) p.getEntityData().getTag(TAG_PREFIX);
			NBTTagCompound inventoryTag = (NBTTagCompound) (extraData).getTag(FILTER);
			NBTTagCompound content = new NBTTagCompound();
			if (is != null) is.writeToNBT(content);
			inventoryTag.setTag("" + slot, content);
			extraData.setTag(FILTER, inventoryTag);
			p.getEntityData().setTag(TAG_PREFIX, extraData);
		} catch (Exception e) {
		}
	}

	public static boolean isAnyAbilityPurchasedForClass(EntityPlayer p, PlayerClass clas) {
		return getAbilities(p)[clas.ordinal()] != 0;
	}

	public static int getAbilityLevelFor(EntityPlayer p, PlayerClass clas) {
		return getAbilities(p)[clas.ordinal()];
	}

	private static void initPlayerDataSafe(EntityPlayer p) {
		if (p!=null) {
			if (!p.getEntityData().hasKey(TAG_PREFIX)) {
				NBTTagCompound tag = new NBTTagCompound();

				tag.setIntArray(ABILITIES_KEY, new int[6]);
				tag.setByteArray(ENABLED_KEY, new byte[6]);
				tag.setTag(INVENTORY, new NBTTagCompound());
				tag.setTag(FILTER, new NBTTagCompound());
				tag.setIntArray(FURNACE_KEY, new int[3]);
				p.getEntityData().setTag(TAG_PREFIX, tag);
			}
		}
	}

	public static void setCompletePlayerData(EntityPlayer p, NBTTagCompound tag) {
		p.getEntityData().setTag(TAG_PREFIX, tag);
	}

	public static NBTTagCompound getCompletePlayerData(EntityPlayer p) {
		return (NBTTagCompound) p.getEntityData().getTag(TAG_PREFIX).copy();
	}

	public static boolean canAffordAbility(EntityPlayer player, int abilityLevel) {
		return player.experienceLevel >= getRequiredLevelsForAbility(player, abilityLevel) || player.capabilities.isCreativeMode;
	}

	public static int getRequiredLevelsForAbility(EntityPlayer p, int abilityLevel) {
		if (abilityLevel == 6) return 50;
		if (abilityLevel == 1 && hasAnyAbility(p)) return Config.newPerkTreeCost;
		return 10 + (abilityLevel - 1) * 5;
	}

	public static boolean hasAnyAbility(EntityPlayer p) {
		for (PlayerClass pc : PlayerClass.values())
			if (isAnyAbilityPurchasedForClass(p, pc)) return true;
		return false;
	}

	public static boolean canPlayerAccessTree(EntityPlayer p, PlayerClass pc) {
		boolean b0 = isAnyAbilityPurchasedForClass(p, pc);
		b0 = b0 || !hasAnyAbility(p);
		b0 = b0 || canAquireAnyNewTree(p);
		return b0;
	}

	public static boolean canAquireAnyNewTree(EntityPlayer p) {
		boolean b0 = true;
		for (PlayerClass pc : PlayerClass.values())
			b0 = b0 && isAnyAbilityPurchasedForClass(p, pc);
		if (b0) return false; // Ha tutte le abilità
		return !hasUnfinishedTrees(p) && Config.newPerkTreeCost >= 0;
	}

	public static boolean hasUnfinishedTrees(EntityPlayer p) {
		for (PlayerClass pc : PlayerClass.values())
			if (isAnyAbilityPurchasedForClass(p, pc) && getAbilityLevelFor(p, pc) < 6) return true;
		return false;
	}

	// level is 1 to 6
	public static void setClassLevelChecked(EntityPlayer p, int clazz, int level) {
		if (!canAffordAbility(p, level)) { return; }

		if (getAbilities(p)[clazz] != level - 1 && !p.capabilities.isCreativeMode) {
			Log.e("Cannot unlock ability!");
			return;
		}

		if (!p.capabilities.isCreativeMode) requestXP(p, level);
		setClassLevelUnchecked(p, clazz, level);

	}

	// Level is 1 to 6
	public static void setClassLevelUnchecked(EntityPlayer p, int clas, int level) {
		if (p!=null) {
			initPlayerDataSafe(p);
			int[] abs = getAbilities(p);
			abs[clas] = level;
			NBTTagCompound tag = ((NBTTagCompound) p.getEntityData().getTag(TAG_PREFIX));
			tag.setIntArray(ABILITIES_KEY, abs);
			p.getEntityData().setTag(TAG_PREFIX, tag);
		}
	}

	public static void setEnabledAbilities(EntityPlayer p, int clas, byte abils) {
		if (p!=null) {
			initPlayerDataSafe(p);
			byte[] ens = getEnabledAbilities(p);
			ens[clas] = abils;
			NBTTagCompound tag = ((NBTTagCompound) p.getEntityData().getTag(TAG_PREFIX));
			tag.setByteArray(ENABLED_KEY, ens);
			p.getEntityData().setTag(TAG_PREFIX, tag);
		}
	}

	public static void setInventory(EntityPlayer p, ItemStack[] inv) {
		if (p!=null) {
			initPlayerDataSafe(p);
			for (int i = 0; i < inv.length; i++)
				setInventorySlot(p, i, inv[i]);
		}
	}

	public static void setFilter(EntityPlayer p, ItemStack[] flt) {
		if (p!=null) {
			initPlayerDataSafe(p);
			for (int i = 0; i < flt.length; i++)
				setFilterSlot(p, i, flt[i]);
		}
	}

	private static void requestXP(EntityPlayer p, int level) {
		p.addExperienceLevel(-getRequiredLevelsForAbility(p, level));
	}

	public static void requestUnlockLevel(EntityPlayer p, PlayerClass pc, int level) {
		PerkMastery.network.sendToServer(new UnlockAbility(pc.ordinal(), level));
		setClassLevelChecked(p, pc.ordinal(), level);
	}

	public static void toggleAbilityChecked(EntityPlayer p, boolean active, PlayerClass pc, int level) {
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) PerkMastery.network.sendToServer(new ToggleAbility(active, pc.ordinal(), level));

		if (getAbilityLevelFor(p, pc) < level) {
			Log.e("Tried to toggle a non-owned ability");
			return;
		}

		byte res = getEnabledAbilities(p)[pc.ordinal()];
		if (active) res |= getBit(level);
		else
			res &= (~getBit(level));
		setEnabledAbilities(p, pc.ordinal(), res);

		if (pc.equals(PlayerClass.BUILDER) && level == 1) ToggleHandler.toggleReachDistance(p, active);
		if (pc.equals(PlayerClass.EXPLORER) && level == 4) ToggleHandler.toggleWellTrained(p, active);

	}

	public static void toggleAbilityUnchecked(EntityPlayer p, boolean active, PlayerClass pc, int level) {

		if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT) PerkMastery.network.sendToServer(new ToggleAbilityForce(active, pc.ordinal(), level));

		byte res = getEnabledAbilities(p)[pc.ordinal()];
		if (active) res |= getBit(level);
		else
			res &= (~getBit(level));
		setEnabledAbilities(p, pc.ordinal(), res);

		if (pc.equals(PlayerClass.BUILDER) && level == 1) ToggleHandler.toggleReachDistance(p, active);
		if (pc.equals(PlayerClass.EXPLORER) && level == 4) ToggleHandler.toggleWellTrained(p, active);

	}

	public static boolean isAbilityEnabled(EntityPlayer p, PlayerClass pc, int level) {
		byte map = getEnabledAbilities(p)[pc.ordinal()];
		byte res = (byte) (map & getBit(level));
		return (res) == getBit(level);
	}

	public static boolean hasPlayerAquired(EntityPlayer p, PlayerClass pc, int level) {
		return getAbilities(p)[pc.ordinal()] >= level;
	}

	private static int getBit(int opt) {
		return (int) Math.pow(2, opt - 1);
	}

	public static boolean hasDeathAmulet(EntityPlayer p) {
		boolean exists = getExtraInventory(p, InventoryType.REAL)[18] != null;
		return exists;
	}

	public static void destroyAmulet(EntityPlayer p) {
		setInventorySlot(p, 18, null);
		if (FMLCommonHandler.instance().getEffectiveSide() == Side.SERVER) PerkMastery.network.sendTo(new AmuletShatter(), (EntityPlayerMP) p);
	}

	public static void setFurnaceData(EntityPlayer p, int[] data) {
		if (p!=null) {
			initPlayerDataSafe(p);
			((NBTTagCompound) p.getEntityData().getTag(TAG_PREFIX)).setIntArray(FURNACE_KEY, data);
		}
	}

	public static int[] getFurnaceData(EntityPlayer p) {
		initPlayerDataSafe(p);
		try {
			return ((NBTTagCompound) p.getEntityData().getTag(TAG_PREFIX)).getIntArray(FURNACE_KEY);
		} catch (Exception e) {
			return new int[] { 0, 0, 0 };
		}
	}

	public enum PlayerClass {
		MAGE, ARCHER, MINER, BUILDER, WARRIOR, EXPLORER
	}

	public enum InventoryType {
		FILTER, REAL
	}

	public static void dropItemsOnDeath(EntityPlayer player) {

		if (player.worldObj.getGameRules().getBoolean(LibGameRules.keepInventory.name())) return;
		for (int i = 0; i < getExtraInventory(player, InventoryType.REAL).length; i++) {
			try {
				player.worldObj.spawnEntityInWorld(new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, getExtraInventory(player, InventoryType.REAL)[i].copy()));
			} catch (NullPointerException e) {
			}
			ExtendedPlayer.setInventorySlot(player, i, null);
		}

	}

}
