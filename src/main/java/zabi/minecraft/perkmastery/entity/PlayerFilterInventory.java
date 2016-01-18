package zabi.minecraft.perkmastery.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IChatComponent;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer.InventoryType;


public class PlayerFilterInventory implements IInventory {

	EntityPlayer player;

	public PlayerFilterInventory(EntityPlayer p) {
		player = p;
	}

	@Override
	public int getSizeInventory() {
		return 5;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return ExtendedPlayer.getExtraInventory(player, InventoryType.FILTER)[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int qt) {
		ItemStack res = getStackInSlot(slot);
		ExtendedPlayer.setFilterSlot(player, slot, null);
		return res;
	}


	@Override
	public void setInventorySlotContents(int slot, ItemStack is) {
		ExtendedPlayer.setFilterSlot(player, slot, is);

	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void markDirty() {
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer user) {
		return user.equals(player);
	}

	@Override
	public boolean isItemValidForSlot(int slot, ItemStack stack) {
		return true;
	}
	
	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	@Override
	public IChatComponent getDisplayName() {
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack res=getStackInSlot(index);
		setInventorySlotContents(index, null);
		return res;
	}

	@Override
	public void openInventory(EntityPlayer player) {}

	@Override
	public void closeInventory(EntityPlayer player) {}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {		
	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		for (int i=0;i<getSizeInventory();i++) removeStackFromSlot(i);		
	}

}
