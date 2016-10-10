package zabi.minecraft.perkmastery.entity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer.InventoryType;
import zabi.minecraft.perkmastery.items.ItemList;


public class PlayerExtraInventory implements IInventory {

	EntityPlayer player;

	public PlayerExtraInventory(EntityPlayer p) {
		player = p;
	}

	@Override
	public int getSizeInventory() {
		return 26;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		return ExtendedPlayer.getExtraInventory(player, InventoryType.REAL)[slot];
	}

	@Override
	public ItemStack decrStackSize(int slot, int qt) {
		// Log.i("Decr ss");
		ItemStack s = getStackInSlot(slot);
		if (s == null) return null;
		ItemStack res;
		if (s.stackSize > qt) res = s.splitStack(qt);
		else {
			res = s.copy();
			s = null;
		}
		ExtendedPlayer.setInventorySlot(player, slot, s);

		return res;
	}


	@Override
	public void setInventorySlotContents(int slot, ItemStack is) {
		ExtendedPlayer.setInventorySlot(player, slot, is);

	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
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
		if (slot == 18) return stack.getItem().equals(ItemList.boneAmulet);
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
	public ITextComponent getDisplayName() {
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
