package zabi.minecraft.perkmastery.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.registry.GameRegistry;
import zabi.minecraft.perkmastery.Config;
import zabi.minecraft.perkmastery.PerkMastery;
import zabi.minecraft.perkmastery.libs.LibGeneral;
import zabi.minecraft.perkmastery.misc.IJsonRenderizzabile;


public class ModBlockBase extends Block implements IJsonRenderizzabile {

	protected ModBlockBase(Material p_i45394_1_) {
		super(p_i45394_1_);
	}

	public ModBlockBase(String name, float hardness, Material material, CreativeTabs tab) {
		super(material);
		this.setUnlocalizedName(name);
		this.setHardness(hardness);
		this.setCreativeTab(Config.useSeparateCreativeTab?LibGeneral.CREATIVE_TAB:tab);
	}

	@Override
	public void register() {
		GameRegistry.registerBlock(this, getUnlocalizedName().substring(5));
	}
	
	@Override
	public void registerModel() {
		PerkMastery.proxy.registerModel(this);
	}

}
