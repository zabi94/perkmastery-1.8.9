package zabi.minecraft.perkmastery.network.packets;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer;


public class UnlockAbility implements IMessage {

	private int tree, level;

	public UnlockAbility() {
	}

	public UnlockAbility(int tree, int level) {
		this.tree = tree;
		this.level = level;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		tree = buf.readInt();
		level = buf.readInt();

	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(tree);
		buf.writeInt(level);
	}

	public static class Handler implements IMessageHandler<UnlockAbility, IMessage> {

		@Override
		public IMessage onMessage(UnlockAbility message, MessageContext ctx) {
			ExtendedPlayer.setClassLevelChecked(ctx.getServerHandler().playerEntity, message.tree, message.level);
			return null;
		}

	}

}
