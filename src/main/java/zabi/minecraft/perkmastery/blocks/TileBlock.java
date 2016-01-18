package zabi.minecraft.perkmastery.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import zabi.minecraft.perkmastery.Config;
import zabi.minecraft.perkmastery.PerkMastery;
import zabi.minecraft.perkmastery.libs.LibGeneral;
import zabi.minecraft.perkmastery.misc.IJsonRenderizzabile;


public abstract class TileBlock extends BlockContainer implements IJsonRenderizzabile {

	public TileBlock(String name, float hardness, Material material, CreativeTabs tab) {
		super(material);
		this.setCreativeTab(Config.useSeparateCreativeTab?LibGeneral.CREATIVE_TAB:tab);
		this.setUnlocalizedName(name);
		this.setHardness(hardness);
	}

	public void register() {
		GameRegistry.registerBlock(this, getUnlocalizedName().substring(5));
	}

	@Override
	public int getRenderType() {
		return -1;
	}

	@Override
	public boolean isOpaqueCube() {
		return false;

	}

	@Override
	public TileEntity createNewTileEntity(World world, int integ) {
		return getNewTileInstance();
	}
	
	@Override
	public void registerModel() {
		PerkMastery.proxy.registerModel(this);
	}

	protected abstract TileEntity getNewTileInstance();

}
