package zabi.minecraft.perkmastery.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class JumpBoost implements IMessage {

	public JumpBoost() {
	}

	@Override
	public void fromBytes(ByteBuf buf) {
	}

	@Override
	public void toBytes(ByteBuf buf) {
	}

	public static class Handler implements IMessageHandler<JumpBoost, IMessage> {

		@Override
		public IMessage onMessage(JumpBoost message, MessageContext ctx) {
			ctx.getServerHandler().playerEntity.motionY += 0.15F;
			ctx.getServerHandler().playerEntity.fallDistance -= 0.15F;
			return null;
		}

	}

}
