package zabi.minecraft.perkmastery.tileentity;

import net.minecraftforge.fml.common.registry.GameRegistry;
import zabi.minecraft.perkmastery.libs.LibGeneral;


public class TileList {

	public static void register() {
		GameRegistry.registerTileEntity(TileEntityDecanter.class, LibGeneral.MOD_ID + ":decanter");
		GameRegistry.registerTileEntity(TileEntityEnchanter.class, LibGeneral.MOD_ID + ":enchanter");
		GameRegistry.registerTileEntity(TileEntityDisenchanter.class, LibGeneral.MOD_ID + ":disenchanter");
	}
}
