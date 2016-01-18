package zabi.minecraft.perkmastery.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import zabi.minecraft.perkmastery.blocks.special.BlockDecanter;
import zabi.minecraft.perkmastery.blocks.special.BlockDisenchanter;
import zabi.minecraft.perkmastery.blocks.special.BlockEnchanter;
import zabi.minecraft.perkmastery.blocks.special.BlockScaffold;
import zabi.minecraft.perkmastery.misc.IJsonRenderizzabile;
import zabi.minecraft.perkmastery.misc.IRegistrabile;


public class BlockList {

	public static final BlockEnchanter		enchanter		= new BlockEnchanter("enchanter", 3.0F, Material.rock, CreativeTabs.tabBlock);
	public static final BlockDisenchanter	disenchanter	= new BlockDisenchanter("disenchanter", 3.0F, Material.rock, CreativeTabs.tabBlock);
	public static final BlockDecanter		decanter		= new BlockDecanter("decanter");
	public static final BlockScaffold		scaffold		= new BlockScaffold("scaffold", 0F, Material.wood);
	
	private static final IJsonRenderizzabile[] lista=new IJsonRenderizzabile[] {enchanter,disenchanter,decanter,scaffold};

	public static void register() {
		for (IRegistrabile ir:lista) ir.register();
	}
	
	public static void registerModels() {
		for (IJsonRenderizzabile ij:lista) ij.registerModel();
	}
	
	

}
