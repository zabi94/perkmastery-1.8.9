package zabi.minecraft.perkmastery.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import zabi.minecraft.perkmastery.Config;


public class ReloadConfig implements IMessage {

	public ReloadConfig() {
	}

	@Override
	public void fromBytes(ByteBuf buf) {

	}

	@Override
	public void toBytes(ByteBuf buf) {

	}

	public static class Handler implements IMessageHandler<ReloadConfig, IMessage> {

		@Override
		public IMessage onMessage(ReloadConfig message, MessageContext ctx) {
			Config.reload();
			return null;
		}

	}

}
