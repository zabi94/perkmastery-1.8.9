package zabi.minecraft.perkmastery.blocks.special;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.StatCollector;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zabi.minecraft.perkmastery.Config;
import zabi.minecraft.perkmastery.blocks.BlockList;
import zabi.minecraft.perkmastery.blocks.ModBlockBase;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer.PlayerClass;


public class BlockScaffold extends ModBlockBase {

	public BlockScaffold(String name, float hardness, Material material) {
		super(name, hardness, material,null);
		setLightOpacity(1);
		this.setCreativeTab(null);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, float hitX, float hitY, float hitZ) {
		if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.BUILDER, 6)) {
			if (player.getHeldItem() != null) {
				if (Block.getBlockFromItem(player.getHeldItem().getItem()) != null) {
					if (Block.getBlockFromItem(player.getHeldItem().getItem()).equals(BlockList.scaffold)) return false;
					if (Block.getBlockFromItem(player.getHeldItem().getItem()).isOpaqueCube()) {
						recursiveSubstitution(world, pos, player.getHeldItem(), player.capabilities.isCreativeMode, 0);
						if (player.getHeldItem().stackSize < 1) player.setCurrentItemOrArmor(0, null);
						return true;
					}
				}
			}
		} else {
			if (world.isRemote) player.addChatMessage(new ChatComponentText(StatCollector.translateToLocal("general.machinery.notenabled")));
			return true;
		}

		return false;
	}

	private static void recursiveSubstitution(World world, BlockPos pos, ItemStack itemInUse, boolean isCreative, int iteration) {
		if (iteration > Config.maxIterations) { return; }
		if (world.getBlockState(pos).getBlock().equals(BlockList.scaffold)) {

			Block block = Block.getBlockFromItem(itemInUse.getItem());
			int meta = itemInUse.getItemDamage();
			world.setBlockState(pos, block.getStateFromMeta(meta));
			if (!isCreative) itemInUse.stackSize--;
			if (itemInUse.stackSize < 1) return;

			for (int dx = -1; dx <= 1; dx++)
				for (int dy = -1; dy <= 1; dy++)
					for (int dz = -1; dz <= 1; dz++) {
						BlockPos np=new BlockPos(pos.getX()+dx, pos.getY()+dy, pos.getZ()+dz);
						if (dx != 0 || dy != 0 || dz != 0) recursiveSubstitution(world, np, itemInUse, isCreative, iteration + 1);
					}
		}
	}

	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
		if (player.isSneaking()) recursiveBreak(world, pos, 0);
	}

	private void recursiveBreak(World world, BlockPos pos, int iteration) {
		if (iteration > Config.maxIterations) { return; }
		if (world.getBlockState(pos).getBlock().equals(BlockList.scaffold)) {
			world.setBlockToAir(pos);
			
			for (int dx = -1; dx <= 1; dx++)
				for (int dy = -1; dy <= 1; dy++)
					for (int dz = -1; dz <= 1; dz++) {
						BlockPos np=new BlockPos(pos.getX()+dx, pos.getY()+dy, pos.getZ()+dz);
						if (dx != 0 || dy != 0 || dz != 0) recursiveBreak(world, np, iteration+1);
					}
		}

	}

	@Override
	public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
		return null;
	}

	@Override
	public void setBlockBoundsForItemRender() {
		this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public boolean isOpaqueCube() {
		return false;
	}

	@Override
	public Item getItemDropped(IBlockState state, Random rand, int fortune) {
		return null;
	}

	@Override
	public int quantityDropped(Random p_149745_1_) {
		return 0;
	}

	public boolean isBlockSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return false;
    }
	
	@Override
	public boolean isFullCube() {
        return false;
    }
	
	@Override
	@SideOnly(Side.CLIENT)
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

	public boolean isVisuallyOpaque() {
        return false;
    }
	
	@SideOnly(Side.CLIENT)
    public boolean isTranslucent() {
        return true;
    }
}
