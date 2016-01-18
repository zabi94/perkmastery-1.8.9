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
import zabi.minecraft.perkmastery.entity.ExtendedPlayer.PlayerClass;
import zabi.minecraft.perkmastery.misc.Log;
import zabi.minecraft.perkmastery.network.packets.SyncPlayer;


public class CommandSetAbilityLevel extends CommandBase {

	@SuppressWarnings("rawtypes")
	private final List aliases, numbers, classes;

	@SuppressWarnings("unchecked")
	public CommandSetAbilityLevel() {
		aliases = new ArrayList<String>();
		aliases.add("perkmastery-set-ability-level");
		aliases.add("pema-sal");
		numbers = new ArrayList<String>(6);
		classes = new ArrayList<String>(6);
		for (int i = 1; i < 7; i++) {
			numbers.add("" + i);
			classes.add(PlayerClass.values()[i - 1].name().toLowerCase());
		}

	}

	@Override
	public String getCommandName() {
		return "perkmastery-set-ability-level";
	}

	@Override
	public String getCommandUsage(ICommandSender p_71518_1_) {
		return "perkmastery-set-ability-level <username> <Class> <Level>";
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
					for (int classPlayer = 0; classPlayer < 6; classPlayer++) {
						if (PlayerClass.values()[classPlayer].name().toLowerCase().equals(args[1])) {
							int level = Integer.parseInt(args[2]);
							ExtendedPlayer.setClassLevelUnchecked(player, classPlayer, level);
							PerkMastery.network.sendTo(new SyncPlayer(player), player);
							return;
						}
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
