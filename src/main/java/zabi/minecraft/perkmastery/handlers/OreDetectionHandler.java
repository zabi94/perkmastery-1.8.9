package zabi.minecraft.perkmastery.handlers;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zabi.minecraft.perkmastery.Config;
import zabi.minecraft.perkmastery.PerkMastery;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer.PlayerClass;
import zabi.minecraft.perkmastery.visual.effects.OreHintFX;


@SideOnly(Side.CLIENT)
public class OreDetectionHandler {

	private static final OreDetectionHandler instance=new OreDetectionHandler();

	Random 						rnd=new Random();
	private static final int	RADIUS	= 8;

	private OreDetectionHandler() {
	}

	public static OreDetectionHandler getInstance() {
		return instance;
	}
	
	public void render(float ptick) {
		EntityPlayer player=PerkMastery.proxy.getSinglePlayer();
		if (ExtendedPlayer.isAbilityEnabled(player, PlayerClass.MINER, 3)) {
			World worldObj=player.worldObj;
			if (player.isSneaking() && player.getHeldItem() != null && IntegrationHelper.isPickaxe(player.getHeldItem())) {
				for (int i = -RADIUS; i <= RADIUS; i++)
					for (int j = -RADIUS; j <= RADIUS; j++)
						for (int k = -RADIUS; k <= RADIUS; k++) {
							BlockPos pos=new BlockPos((int) player.posX + i, (int) player.posY + j, (int) player.posZ + k);
							Block b = worldObj.getBlockState(pos).getBlock();
							if (b.getUnlocalizedName().toLowerCase().contains("ore")) {
								renderHintAt(pos, worldObj.getBlockState(pos), ptick, worldObj);
							}
						}
			}
		}
	}

	private void renderHintAt(BlockPos pos, IBlockState state, float ptick, World worldObj) {
		boolean special=false;
		try {
			ItemStack nextToSelected = PerkMastery.proxy.getSinglePlayer().inventory.mainInventory[(PerkMastery.proxy.getSinglePlayer().inventory.currentItem + 1) % 9];
			if (nextToSelected != null && Item.getItemFromBlock(state.getBlock()).equals(nextToSelected.getItem())) special=true;
		} catch (NullPointerException ignore) {
		}
		if (!Config.fpsSavingMode || (rnd.nextBoolean() && rnd.nextBoolean())) Minecraft.getMinecraft().effectRenderer.addEffect(new OreHintFX(worldObj, pos.getX(), pos.getY(), pos.getZ(), special));

	}

}
