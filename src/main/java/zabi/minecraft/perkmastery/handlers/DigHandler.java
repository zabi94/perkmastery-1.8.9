package zabi.minecraft.perkmastery.handlers;

import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import zabi.minecraft.perkmastery.libs.LibGameRules;
import zabi.minecraft.perkmastery.proxy.ServerProxy;


public class DigHandler {

	private static final int										VEIN_MAX_SIZE	= 40;
	private static final ConcurrentLinkedQueue<BlockPos>	visited			= new ConcurrentLinkedQueue<BlockPos>();

	public static boolean containsGlass(String unlocalizedName) {
		return unlocalizedName.contains("glass") || unlocalizedName.contains("pane") || unlocalizedName.contains("glowstone");
	}

	public static void applyFortune(BreakEvent evt) {
		if (!evt.getPlayer().capabilities.isCreativeMode) {
			try {
				if (!evt.state.getBlock().getItemDropped(evt.state, evt.world.rand, 0).equals(Item.getItemFromBlock(evt.state.getBlock())) && evt.world.getGameRules().getBoolean(LibGameRules.doTileDrops.name())) evt.world.spawnEntityInWorld(new EntityItem(evt.world, evt.pos.getX(), evt.pos.getY(), evt.pos.getZ(), new ItemStack(evt.state.getBlock().getItemDropped(evt.state, evt.world.rand, 0), 1, evt.state.getBlock().damageDropped(evt.state))));
			} catch (NullPointerException e) {
			}
		}
	}

	public static boolean isToolDelicate(BreakEvent evt) {
		if (!(evt.state.getBlock().canSilkHarvest(evt.world, evt.pos, evt.state, evt.getPlayer()) && containsGlass(evt.state.getBlock().getUnlocalizedName().toLowerCase()))) return false;
		ItemStack held = evt.getPlayer().getHeldItem();
		if (held == null || !(IntegrationHelper.isPickaxe(held))) return true;
		return false;
	}

	public static void applyVeinminer(BreakEvent evt, int x, int y, int z, boolean baseRun) {
		if (evt.world.isRemote) return;
		if (!IntegrationHelper.isPickaxe(evt.getPlayer().getHeldItem())) return;
		if (visited.size() > VEIN_MAX_SIZE) return;
		visited.offer(new BlockPos(x, y, z));
		for (int dx = -1; dx <= 1; dx++)
			for (int dy = -1; dy <= 1; dy++)
				for (int dz = -1; dz <= 1; dz++) {
					BlockPos coords = new BlockPos(x + dx, y + dy, z + dz);
					boolean isAlreadyVisited = visited.contains(coords);
					boolean isEquivalentToMined = checkVeinminer(evt, x + dx, y + dy, z + dz);
					boolean isValidCandidate = isOre(evt.world, x + dx, y + dy, z + dz);
					if (!isAlreadyVisited && isEquivalentToMined && isValidCandidate) {
						applyVeinminer(evt, x + dx, y + dy, z + dz, false);
					}
				}
		if (baseRun) {
			for (BlockPos cc : visited) {
				boolean stop = ServerProxy.harvestBlockAt(cc, evt.getPlayer(), evt);
				visited.remove(cc);
				if (stop) {
					visited.clear();
					break;
				}
			}
		}
	}

	private static boolean isOre(World world, int x, int y, int z) {
		BlockPos pos=new BlockPos(x, y, z);
		String blockName = world.getBlockState(pos).getBlock().getUnlocalizedName().toLowerCase();
		if (blockName.contains("ore")) return true;
		if (blockName.contains("netherquartz")) return true;
		if (world.getTileEntity(pos) != null) return false;
		return false;
	}

	private static boolean checkVeinminer(BreakEvent evt, int x, int y, int z) {
		BlockPos pos=new BlockPos(x, y, z);
		return areEquivalent(evt.state, evt.world.getBlockState(pos));
	}

	public static void applyCrumbling(int x, int y, int z, World world, EntityPlayer player) {
		BlockPos pos=new BlockPos(x,y,z);
		if (!world.isRemote && world.getBlockState(pos).getBlock().equals(Blocks.gravel)) {
			ServerProxy.harvestBlockAt(pos, null, null);
			applyCrumbling(x, y + 1, z, world, player);
		}
	}

	public static boolean areEquivalent(IBlockState bsa, IBlockState bsb) {
		if (bsa.getBlock() instanceof BlockRedstoneOre && bsb.getBlock() instanceof BlockRedstoneOre) return true;
		return bsa.equals(bsb);
	}

}
