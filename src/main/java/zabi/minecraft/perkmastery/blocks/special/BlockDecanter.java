package zabi.minecraft.perkmastery.blocks.special;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import zabi.minecraft.perkmastery.PerkMastery;
import zabi.minecraft.perkmastery.blocks.TileBlock;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer.PlayerClass;
import zabi.minecraft.perkmastery.gui.GuiHandler;
import zabi.minecraft.perkmastery.tileentity.TileEntityDecanter;

public class BlockDecanter extends TileBlock {

	public BlockDecanter(String name) {
		super(name, 2.0F, Material.iron, CreativeTabs.tabBrewing);
	}

	@Override
	protected TileEntity getNewTileInstance() {
		return new TileEntityDecanter();
	}
	
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.MAGE, 1)) {
			if (!world.isRemote) player.openGui(PerkMastery.instance, GuiHandler.IDs.GUI_DECANTER.ordinal(), world, pos.getX(), pos.getY(), pos.getZ());
		} else if (world.isRemote) player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("general.machinery.notenabled")));
		return true;
    }
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		((TileEntityDecanter) world.getTileEntity(pos)).dropContents();
		super.breakBlock(world, pos, state);
	}
	

}
