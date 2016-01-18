package zabi.minecraft.perkmastery.blocks.special;

import java.util.Random;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zabi.minecraft.perkmastery.PerkMastery;
import zabi.minecraft.perkmastery.blocks.TileBlock;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer.PlayerClass;
import zabi.minecraft.perkmastery.gui.GuiHandler;
import zabi.minecraft.perkmastery.tileentity.TileEntityEnchanter;
import zabi.minecraft.perkmastery.visual.effects.RuneFadeFX;


public class BlockEnchanter extends TileBlock {

	public BlockEnchanter(String name, float hardness, Material material, CreativeTabs tab) {
		super(name, hardness, material, tab);
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.75F, 1.0F);
	}

	@Override
	protected TileEntity getNewTileInstance() {
		return new TileEntityEnchanter();
	}

	@Override
	public boolean isOpaqueCube() {
		return true;
	}

	@Override
	public int getRenderType() {
		return 3;
	}
	
	@Override
	public boolean isFullCube() {
        return false;
    }

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.MAGE, 2)) {
			if (!world.isRemote) player.openGui(PerkMastery.instance, GuiHandler.IDs.GUI_ENCHANTER.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
		} else if (world.isRemote) player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("general.machinery.notenabled")));
		return true;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand) {
		Minecraft.getMinecraft().effectRenderer.addEffect(new RuneFadeFX(world, pos.getX() + 0.5, pos.getY() + 0.4, pos.getZ() + 0.5, ((TileEntityEnchanter) world.getTileEntity(pos)).isErrored()));
	}


	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		((TileEntityEnchanter) world.getTileEntity(pos)).dropContents();
		super.breakBlock(world, pos, state);
	}

}
