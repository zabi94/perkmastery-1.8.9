package zabi.minecraft.perkmastery.items.special;

import java.util.List;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zabi.minecraft.perkmastery.blocks.BlockList;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer.PlayerClass;
import zabi.minecraft.perkmastery.items.ItemBase;


public class ScaffoldBuilder extends ItemBase {

	private static final String NBT_TAG = "scaffold_data";

	public ScaffoldBuilder(String modName, CreativeTabs tab) {
		super(modName, tab);
		this.setMaxStackSize(1);
	}

	@Override
	public boolean onItemUse(ItemStack is, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.BUILDER, 6)) {
			if (player.isSneaking()) {
				prepareNbt(is);
				if (is.getTagCompound().hasKey(NBT_TAG)) is.getTagCompound().removeTag(NBT_TAG);
				return true;
			}
			prepareNbt(is);
			BlockPos coords = getBoundBlock(is);
			if (coords == null) {
				is.getTagCompound().setIntArray(NBT_TAG, new int[] { pos.getX(), pos.getY(), pos.getZ() });
			} else {
				if (pos.getX() == coords.getX() || pos.getY() == coords.getY() || pos.getZ() == coords.getZ()) {
					int sx = Math.min(pos.getX(), coords.getX());
					int ex = Math.max(pos.getX(), coords.getX());
					int sy = Math.min(pos.getY(), coords.getY());
					int ey = Math.max(pos.getY(), coords.getY());
					int sz = Math.min(pos.getZ(), coords.getZ());
					int ez = Math.max(pos.getZ(), coords.getZ());
					for (int dx = sx; dx <= ex; dx++)
						for (int dy = sy; dy <= ey; dy++)
							for (int dz = sz; dz <= ez; dz++) {
								BlockPos np=new BlockPos(dx, dy, dz);
								if (world.isAirBlock(np) || world.getBlockState(np).getBlock().isReplaceable(world, np)) world.setBlockState(np, BlockList.scaffold.getDefaultState());
							}
				} else {
					int sx = Math.min(pos.getX(), coords.getX());
					int ex = Math.max(pos.getX(), coords.getX());
					int sy = Math.min(pos.getY(), coords.getY());
					int ey = Math.max(pos.getY(), coords.getY());
					int sz = Math.min(pos.getZ(), coords.getZ());
					int ez = Math.max(pos.getZ(), coords.getZ());
					for (int dx = sx; dx <= ex; dx++)
						for (int dy = sy; dy <= ey; dy++)
							for (int dz = sz; dz <= ez; dz++) {
								BlockPos np=new BlockPos(dx, dy, dz);
								if ((is.getItemDamage() != 0 || dz == sz || dz == ez || dx == sx || dx == ex || dy == sy || dy == ey) && (world.isAirBlock(np) || world.getBlockState(np).getBlock().isReplaceable(world, np))) world.setBlockState(np, BlockList.scaffold.getDefaultState());
							}
				}
				is.getTagCompound().removeTag(NBT_TAG);
			}
			return true;
		} else {
			if (world.isRemote) player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("general.machinery.notenabled")));
		}
		return false;
	}

	private static void prepareNbt(ItemStack is) {
		if (is.getTagCompound() == null) is.setTagCompound(new NBTTagCompound());
	}

	private static BlockPos getBoundBlock(ItemStack is) {
		if (!is.getTagCompound().hasKey(NBT_TAG)) return null;
		int[] coords = is.getTagCompound().getIntArray(NBT_TAG);
		return new BlockPos(coords[0], coords[1], coords[2]);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack is, EntityPlayer player, List lista, boolean p_77624_4_) {

		lista.add(EnumChatFormatting.ITALIC + EnumChatFormatting.AQUA.toString() + StatCollector.translateToLocal("general.scaffoldbuilder.mode." + (is.getItemDamage() == 0 ? "empty" : "full")));

		prepareNbt(is);
		if (is.getTagCompound().hasKey(NBT_TAG)) {
			lista.add(StatCollector.translateToLocal("general.scaffoldbuilder.boundto"));
			BlockPos cd = getBoundBlock(is);
			lista.add(cd.getX() + " - " + cd.getY() + " - " + cd.getZ());
			lista.add(EnumChatFormatting.DARK_GRAY + EnumChatFormatting.ITALIC.toString() + StatCollector.translateToLocal("general.scaffoldbuilder.cleanInstructions"));
		}
	}
}
