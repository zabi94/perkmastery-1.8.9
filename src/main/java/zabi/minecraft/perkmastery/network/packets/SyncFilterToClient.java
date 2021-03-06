package zabi.minecraft.perkmastery.network.packets;

import java.io.IOException;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer;
import zabi.minecraft.perkmastery.misc.Log;


public class SyncFilterToClient implements IMessage {

	private String		player;
	private int			slot;
	private ItemStack	is;

	public SyncFilterToClient() {
	}

	public SyncFilterToClient(String player, int slot, ItemStack is) {
		Log.d("Starting sync to client for " + player);
		this.player = player;
		this.slot = slot;
		this.is = is;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int nameLenght = buf.readInt();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < nameLenght; i++)
			sb.append(buf.readChar());
		player = sb.toString();

		slot = buf.readInt();

		PacketBuffer pb = new PacketBuffer(buf);
		try {
			is = pb.readItemStackFromBuffer();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void toBytes(ByteBuf buf) {
		int lenght = player.length();
		buf.writeInt(lenght);
		for (int i = 0; i < player.length(); i++)
			buf.writeChar(player.charAt(i));

		buf.writeInt(slot);

		PacketBuffer pb = new PacketBuffer(buf);
		try {
			pb.writeItemStackToBuffer(is);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static class Handler implements IMessageHandler<SyncFilterToClient, IMessage> {

		@Override
		public IMessage onMessage(SyncFilterToClient message, MessageContext ctx) {
			if (Minecraft.getMinecraft().thePlayer.getDisplayName().equals(message.player)) ExtendedPlayer.setFilterSlot(Minecraft.getMinecraft().thePlayer, message.slot, message.is);
			return null;
		}

	}

}
