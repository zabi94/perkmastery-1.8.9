package zabi.minecraft.perkmastery.network;

import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import zabi.minecraft.perkmastery.network.packets.AmuletShatter;
import zabi.minecraft.perkmastery.network.packets.ClientRequestForData;
import zabi.minecraft.perkmastery.network.packets.JumpBoost;
import zabi.minecraft.perkmastery.network.packets.OpenGuiMessage;
import zabi.minecraft.perkmastery.network.packets.ReloadConfig;
import zabi.minecraft.perkmastery.network.packets.SyncPlayer;
import zabi.minecraft.perkmastery.network.packets.ToggleAbility;
import zabi.minecraft.perkmastery.network.packets.ToggleAbilityForce;
import zabi.minecraft.perkmastery.network.packets.UnlockAbility;


public class NetworkModRegistry {

	public static void registerMessages(SimpleNetworkWrapper net) {
		int id = 0;

		net.registerMessage(AmuletShatter.Handler.class, AmuletShatter.class, id++, Side.CLIENT);
		net.registerMessage(SyncPlayer.Handler.class, SyncPlayer.class, id++, Side.CLIENT);
		net.registerMessage(ReloadConfig.Handler.class, ReloadConfig.class, id++, Side.CLIENT);

		net.registerMessage(UnlockAbility.Handler.class, UnlockAbility.class, id++, Side.SERVER);
		net.registerMessage(ToggleAbility.Handler.class, ToggleAbility.class, id++, Side.SERVER);
		net.registerMessage(ToggleAbilityForce.Handler.class, ToggleAbilityForce.class, id++, Side.SERVER);
		net.registerMessage(JumpBoost.Handler.class, JumpBoost.class, id++, Side.SERVER);
		net.registerMessage(OpenGuiMessage.Handler.class, OpenGuiMessage.class, id++, Side.SERVER);
		net.registerMessage(ClientRequestForData.Handler.class, ClientRequestForData.class, id++, Side.SERVER);
		

	}
}
