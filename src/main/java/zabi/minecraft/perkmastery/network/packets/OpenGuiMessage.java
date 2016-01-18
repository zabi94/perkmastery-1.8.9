package zabi.minecraft.perkmastery.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.BlockWorkbench;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerWorkbench;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import zabi.minecraft.perkmastery.PerkMastery;
import zabi.minecraft.perkmastery.gui.GuiHandler;


public class OpenGuiMessage implements IMessage {

	private int guiExtra = -1;

	public OpenGuiMessage() {
	}

	public OpenGuiMessage(int guiVar) {
		guiExtra = guiVar;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		guiExtra = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(guiExtra);
	}

	public static class Handler implements IMessageHandler<OpenGuiMessage, IMessage> {

		@Override
		public IMessage onMessage(OpenGuiMessage message, MessageContext ctx) {
			EntityPlayer player = ctx.getServerHandler().playerEntity;
			// Log.i(message.guiExtra);
			if (message.guiExtra == -1) player.openGui(PerkMastery.instance, GuiHandler.IDs.GUI_BOOK.ordinal(), player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
			else if (message.guiExtra == 1) {
//				((EntityPlayerMP) player).getNextWindowId();
//				((EntityPlayerMP) player).playerNetServerHandler.sendPacket(new S2DPacketOpenWindow(((EntityPlayerMP) player).currentWindowId, 1, "Crafting", 9, true));
//				((EntityPlayerMP) player).openContainer = new ContainerWorkbench(((EntityPlayerMP) player).inventory, ((EntityPlayerMP) player).worldObj, (int) player.posX, (int) player.posY, (int) player.posZ) {
//
//					public boolean canInteractWith(EntityPlayer p_75145_1_) {
//						return true;
//					}
//				};
//				((EntityPlayerMP) player).openContainer.windowId = ((EntityPlayerMP) player).currentWindowId;
//				((EntityPlayerMP) player).openContainer.addCraftingToCrafters(((EntityPlayerMP) player));
				player.displayGui(new BlockWorkbench.InterfaceCraftingTable(player.worldObj, player.playerLocation) {
					@Override
					public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
		                return new ContainerWorkbench(playerInventory, playerIn.worldObj, playerIn.playerLocation) {
		                	@Override
		                	public boolean canInteractWith(EntityPlayer playerIn) { return true; }
		                };		            
		            }
				});
			} else {
				if (message.guiExtra == 0) message.guiExtra = GuiHandler.IDs.GUI_EXTENDED_INVENTORY.ordinal() + 2;
				else if (message.guiExtra == 2) message.guiExtra = GuiHandler.IDs.GUI_FURNACE.ordinal() + 2;
				else if (message.guiExtra == 4) message.guiExtra = GuiHandler.IDs.GUI_FILTER.ordinal() + 2;
				player.openGui(PerkMastery.instance, message.guiExtra - 2, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
			}
			return null;
		}

	}

}
