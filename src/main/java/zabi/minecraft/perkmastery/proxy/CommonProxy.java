package zabi.minecraft.perkmastery.proxy;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;


public abstract class CommonProxy {

	public abstract void setPlayerExtraInventory(EntityPlayer player, int slot, ItemStack is);

	public abstract void setPlayerFilter(EntityPlayer player, int slot, ItemStack is);

	public abstract void registerKeyBindings();

	public abstract void registerAnimationHelper();

	public abstract EntityPlayer getSinglePlayer();

	public abstract void setupHackyController(boolean enable);

	public abstract void registerRenderers();
	
	public abstract void registerModel(Block block);
	
	public abstract void registerModel(Item item);

}
