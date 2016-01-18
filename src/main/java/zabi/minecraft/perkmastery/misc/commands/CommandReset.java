package zabi.minecraft.perkmastery.misc.commands;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import zabi.minecraft.perkmastery.PerkMastery;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer;
import zabi.minecraft.perkmastery.misc.Log;
import zabi.minecraft.perkmastery.network.packets.SyncPlayer;


public class CommandReset extends CommandBase {

	@SuppressWarnings("rawtypes")
	private final List aliases;

	@SuppressWarnings("unchecked")
	public CommandReset() {
		aliases = new ArrayList<String>();
		aliases.add("perkmastery-reset-all");
		aliases.add("pema-ra");
	}


	@Override
	public String getCommandName() {
		return "perkmastery-reset-all";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "perkmastery-reset-all <username>";
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public List getCommandAliases() {
		return aliases;
	}

	@Override
	public void processCommand(ICommandSender sender, String[] args) {
		try {
			for (Object oplayer : MinecraftServer.getServer().getConfigurationManager().playerEntityList.toArray()) {
				EntityPlayerMP player = (EntityPlayerMP) oplayer;
				if (player.getDisplayName().equals(args[0])) {
					for (int i = 0; i < 6; i++) {
						ExtendedPlayer.setClassLevelUnchecked(player, i, 0);
						ExtendedPlayer.setEnabledAbilities(player, i, (byte) 0);
						PerkMastery.network.sendTo(new SyncPlayer(player), player);
					}
					return;
				}
			}
		} catch (Exception e) {
			Log.e("Cannot execute command");
		}

	}

	@Override
	public boolean canCommandSenderUseCommand(ICommandSender user) {
		return (!(user instanceof EntityPlayer) || ((user instanceof EntityPlayer) && (((EntityPlayer) user).capabilities.isCreativeMode)));
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return index == 0;
	}

}
