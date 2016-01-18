package zabi.minecraft.perkmastery.items.special;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIAttackOnCollide;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zabi.minecraft.perkmastery.entity.TargetSelector;
import zabi.minecraft.perkmastery.items.ItemBase;
import zabi.minecraft.perkmastery.items.special.UndeadSoul.UndeadType;
import zabi.minecraft.perkmastery.misc.Log;


public class EvocationTome extends ItemBase {

	public EvocationTome(String modName, CreativeTabs tab) {
		super(modName, tab);
	}

	public static void addSoulToStack(ItemStack is, UndeadType type) {
		if (type == null) return;
		initTag(is);
		increaseCount(is, type);
	}

	private static void increaseCount(ItemStack is, UndeadType type) {
		is.getTagCompound().setInteger(type.name(), is.getTagCompound().getInteger(type.name()) + 1);
	}

	private static boolean decreaseCount(ItemStack is, UndeadType type) {
		if (is.getTagCompound().getInteger(type.name()) == 0) return false;
		is.getTagCompound().setInteger(type.name(), is.getTagCompound().getInteger(type.name()) - 1);
		return true;
	}

	private static boolean removeSoulFromStack(ItemStack is, UndeadType type) {
		initTag(is);
		return decreaseCount(is, type);
	}

	private static void initTag(ItemStack is) {
		if (is.getTagCompound() == null) {
			is.setTagCompound(new NBTTagCompound());
			for (UndeadType s : UndeadType.values())
				is.getTagCompound().setInteger(s.name(), 0);
		}
	}

	public static void summonAtPlayer(ItemStack is, EntityPlayer p, EntityLiving target) {
		if (!p.worldObj.isRemote) spawnEntity(is, p, target);
	}

	private static void spawnEntity(ItemStack is, EntityPlayer p, EntityLiving target) {
		int types = UndeadType.values().length;
		int soulIndex = (int) (Math.random() * types);
		for (int i = 0; i < types; i++) {
			UndeadType soul = UndeadType.values()[(i + soulIndex) % types];
			if (removeSoulFromStack(is, soul)) {
				EntityLiving e = getEntityFor(soul, p, target);
				if (e == null) {
					Log.e("Null entity generated!");
					continue;
				}
				e.setPosition(p.posX, p.posY, p.posZ);
				e.setHealth(6F);
				p.worldObj.spawnEntityInWorld(e);
				e.getEntityData().setString(TargetSelector.TAG_OWNER, p.getDisplayName().getUnformattedText());
				e.setCustomNameTag(p.getDisplayName().getUnformattedText() + " - " + StatCollector.translateToLocal("general.evocationNameTag"));
				e.tasks.addTask(0, new EntityAIAttackOnCollide((EntityCreature) e, EntityLiving.class, 1.6D, true));
				e.targetTasks.taskEntries.clear();
				e.targetTasks.addTask(1, new EntityAINearestAttackableTarget<EntityLiving>((EntityCreature) e, EntityLiving.class, 0, false, false, new TargetSelector(p, target, e)));
				e.setAttackTarget(target);
			}
		}
	}

	private static EntityLiving getEntityFor(UndeadType soul, EntityPlayer p, EntityLivingBase target) {

		if (soul.equals(UndeadType.SKELETON)) {
			EntitySkeleton e = new EntitySkeleton(p.worldObj);
			e.setSkeletonType(0);
			return e;
		} else if (soul.equals(UndeadType.ZOMBIE)) {
			EntityZombie e = new EntityZombie(p.worldObj);
			return e;
		}

		return null;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List lista, boolean p_77624_4_) {
		initTag(is);
		lista.add(EnumChatFormatting.ITALIC + EnumChatFormatting.AQUA.toString() + StatCollector.translateToLocal("general.collectedSouls"));
		for (UndeadType t : UndeadType.values()) {
			String s = EnumChatFormatting.BOLD + t.name() + ": ";
			s = s + EnumChatFormatting.RESET + "" + is.getTagCompound().getInteger(t.name());
			lista.add(s);
		}
	}

}
