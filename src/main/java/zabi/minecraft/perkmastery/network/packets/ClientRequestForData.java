package zabi.minecraft.perkmastery.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class ClientRequestForData implements IMessage {

	public ClientRequestForData() {
	}

	@Override
	public void fromBytes(ByteBuf buf) {
	}

	@Override
	public void toBytes(ByteBuf buf) {
	}

	public static class Handler implements IMessageHandler<ClientRequestForData, IMessage> {

		@Override
		public IMessage onMessage(ClientRequestForData message, MessageContext ctx) {
			return new SyncPlayer(ctx.getServerHandler().playerEntity);
		}

	}

}
