package zabi.minecraft.perkmastery.proxy;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.S23PacketBlockChange;
import net.minecraft.server.management.ItemInWorldManager;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent.BreakEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import zabi.minecraft.perkmastery.PerkMastery;
import zabi.minecraft.perkmastery.network.packets.SyncFilterToClient;
import zabi.minecraft.perkmastery.network.packets.SyncInventoryToClient;


public class ServerProxy extends CommonProxy {

	@Override
	public void setPlayerExtraInventory(EntityPlayer player, int slot, ItemStack is) {
		PerkMastery.network.sendToDimension(new SyncInventoryToClient(player.getDisplayName().getUnformattedText(), slot, is), slot);
	}

	@Override
	public void setPlayerFilter(EntityPlayer player, int slot, ItemStack is) {
		PerkMastery.network.sendToDimension(new SyncFilterToClient(player.getDisplayName().getUnformattedText(), slot, is), slot);
	}

	@Override
	public void registerKeyBindings() {
	}

	@Override
	public void registerAnimationHelper() {
	}

	@Override
	public EntityPlayer getSinglePlayer() {
		return null;
	}

	@Override
	public void setupHackyController(boolean enable) {
	}

	@Override
	public void registerRenderers() {
	}

	public static boolean harvestBlockAt(BlockPos pos, EntityPlayer player, BreakEvent event) {

		EntityPlayerMP playerMP = ((EntityPlayerMP) player);
		ItemInWorldManager IIWM = playerMP.theItemInWorldManager;

		boolean stackDestroyed = false;
		IBlockState state=player.worldObj.getBlockState(pos);
		BreakEvent subEvent = new BreakEvent(player.worldObj, pos, state, player);
		subEvent.setResult(Result.DENY); // To intercept it later in the handler and avoid StackOverflows
		PerkMastery.eventi.onBlockBreakEvent(subEvent);
		if (subEvent.isCanceled()) return false;

		boolean blockRemoved = false;

		if (IIWM.isCreative()) {
			blockRemoved = removeBlock(pos, false, playerMP);
			IIWM.thisPlayerMP.playerNetServerHandler.sendPacket(new S23PacketBlockChange(player.worldObj, pos));
		} else {
			ItemStack itemstack = playerMP.getCurrentEquippedItem();
			boolean canHarvest = state.getBlock().canHarvestBlock(event.world, pos, player);

			if (itemstack != null) {
				itemstack.onBlockDestroyed(player.worldObj, state.getBlock(), pos, playerMP);
				if (itemstack.stackSize == 0) {
					playerMP.destroyCurrentEquippedItem();
					stackDestroyed = true;
				}
			}

			blockRemoved = removeBlock(pos, canHarvest, playerMP);
			if (blockRemoved && canHarvest) {
				state.getBlock().harvestBlock(player.worldObj, playerMP, pos, state, player.worldObj.getTileEntity(pos));
				if (player.getHeldItem() != null) player.getHeldItem().getItem().onBlockStartBreak(player.getHeldItem(), pos, player);
			}
		}
		if (!IIWM.isCreative() && blockRemoved && event != null) {
			state.getBlock().dropXpOnBlockBreak(player.worldObj, pos, event.getExpToDrop());
		}
		return stackDestroyed;

	}

	private static boolean removeBlock(BlockPos pos, boolean canHarvest, EntityPlayerMP thisPlayerMP) {
		World theWorld = thisPlayerMP.worldObj;
		IBlockState block = theWorld.getBlockState(pos);
		block.getBlock().onBlockHarvested(theWorld, pos, block, thisPlayerMP);
		boolean flag = block.getBlock().removedByPlayer(theWorld, pos, thisPlayerMP, canHarvest);
		if (flag) block.getBlock().onBlockDestroyedByPlayer(theWorld, pos,block);
		return flag;
	}

	@Override
	public void registerModel(Block block) {}

	@Override
	public void registerModel(Item item) {}

}
