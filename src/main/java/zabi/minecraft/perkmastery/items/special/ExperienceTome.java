package zabi.minecraft.perkmastery.items.special;

import java.util.ArrayList;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zabi.minecraft.perkmastery.Config;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer.PlayerClass;
import zabi.minecraft.perkmastery.items.ItemBase;


public class ExperienceTome extends ItemBase {

	public ExperienceTome(String modName, CreativeTabs tab) {
		super(modName, tab);
	}

	public ItemStack onEaten(ItemStack stack, World w, EntityPlayer p) {
		return stack;
	}

	public int getMaxItemUseDuration(ItemStack stack) {
		return 60;
	}

	public EnumAction getItemUseAction(ItemStack stack) {
		return EnumAction.BOW;
	}

	public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
		if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.MAGE, 5)) {
			player.setItemInUse(stack, this.getMaxItemUseDuration(stack));

			if (world.isRemote) {
				ArrayList<BlockPos> listaBlocchi = getBlocksInRadius(player, world, 8);
				signalBlocks(world, listaBlocchi);
			}

		} else {
			if (world.isRemote) player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("general.machinery.notenabled")));
		}
		return stack;
	}

	public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int ticksUsing) {
		ArrayList<BlockPos> listaBlocchi = getBlocksInRadius(player, world, 8);
		if (!world.isRemote && ticksUsing == 0) {
			for (BlockPos coo : listaBlocchi) {
				world.setBlockToAir(coo);
				world.spawnEntityInWorld(new EntityXPOrb(world, coo.getX(), coo.getY(), coo.getZ(), 10));
			}
			player.setCurrentItemOrArmor(0, null);
		}

	}

	public void onUsingTick(ItemStack stack, EntityPlayer player, int tickInUse) {
		if (tickInUse == 1) {
			onPlayerStoppedUsing(stack, player.worldObj, player, 0);
		}

	}

	@SideOnly(Side.CLIENT)
	private void signalBlocks(World world, ArrayList<BlockPos> listaBlocchi) {
		double speed = 2;
		for (BlockPos coo : listaBlocchi)
			for (int i = 0; i < 10; i++) {
				double vx = speed * (Math.random() - 0.5);
				double vy = speed * (Math.random() - 0.5);
				double vz = speed * (Math.random() - 0.5);
				world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, coo.getX() + 0.5 + (1.2 * vx / speed), coo.getY() + 0.5 + (1.2 * vy / speed), coo.getZ() + 0.5 + (1.2 * vz / speed), vx, vy, vz);
			}
	}

	private ArrayList<BlockPos> getBlocksInRadius(EntityPlayer player, World world, int radius) {
		ArrayList<BlockPos> listaBlocchi = new ArrayList<BlockPos>();
		for (int sx = -radius; sx <= radius; sx++)
			for (int sy = -radius; sy <= radius; sy++)
				for (int sz = -radius; sz <= radius; sz++) {
					BlockPos np=new BlockPos((int) player.posX + sx, (int) player.posY + sy, (int) player.posZ + sz);
					if (world.getBlockState(np).getBlock().equals(Blocks.bookshelf)) listaBlocchi.add(np);
					if (listaBlocchi.size() >= Config.maxBookshelvesScanned && Config.maxBookshelvesScanned > 0) break;
				}
		return listaBlocchi;
	}

}
