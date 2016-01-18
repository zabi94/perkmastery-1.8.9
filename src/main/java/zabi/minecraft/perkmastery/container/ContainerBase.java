package zabi.minecraft.perkmastery.container;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;


public class ContainerBase extends Container {

	protected void addPlayerSlots(InventoryPlayer playerInventory, int x, int y) {
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, x + j * 18, y + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(playerInventory, i, x + i * 18, y + 58));
		}
	}

	protected void addPlayerSlots(InventoryPlayer playerInventory) {
		int x = 8;
		int y = 84;
		for (int i = 0; i < 3; ++i) {
			for (int j = 0; j < 9; ++j) {
				addSlotToContainer(new Slot(playerInventory, j + i * 9 + 9, x + j * 18, y + i * 18));
			}
		}

		for (int i = 0; i < 9; ++i) {
			addSlotToContainer(new Slot(playerInventory, i, x + i * 18, y + 58));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		return null;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}

	@Override
	protected boolean mergeItemStack(ItemStack givenStack, int slotFirst, int slotLast, boolean startFromLast) {

		boolean successful = false;

		int startSlot, endSlot;
		if (startFromLast) {
			startSlot = Math.max(slotLast, slotFirst);
			endSlot = Math.min(slotLast, slotFirst);
		} else {
			startSlot = Math.min(slotLast, slotFirst);
			endSlot = Math.max(slotLast, slotFirst);
		}

		for (int currentDestinationSlotNumber = startSlot; (!startFromLast && currentDestinationSlotNumber <= endSlot) || (startFromLast && currentDestinationSlotNumber >= endSlot); currentDestinationSlotNumber += startFromLast ? -1 : 1) {
			Slot currentDestinationSlot = (Slot) this.inventorySlots.get(currentDestinationSlotNumber);
			ItemStack stackAlreadyThere = currentDestinationSlot.getStack();
			if (stackAlreadyThere != null && stackAlreadyThere.getItem() == givenStack.getItem() && (!givenStack.getHasSubtypes() || givenStack.getItemDamage() == stackAlreadyThere.getItemDamage()) && ItemStack.areItemStackTagsEqual(givenStack, stackAlreadyThere)) {
				int maxFinalStackSize = Math.min(stackAlreadyThere.getMaxStackSize(), currentDestinationSlot.getSlotStackLimit());
				if (stackAlreadyThere.stackSize < maxFinalStackSize) {
					int transferrable = maxFinalStackSize - stackAlreadyThere.stackSize;
					int transfering = Math.min(transferrable, givenStack.stackSize);
					stackAlreadyThere.stackSize += transfering;
					currentDestinationSlot.putStack(stackAlreadyThere);
					givenStack.stackSize -= transfering;
					currentDestinationSlot.onSlotChanged();
					successful = true;
				}
			} else if (stackAlreadyThere == null) {
				int transferrable = currentDestinationSlot.getSlotStackLimit();
				ItemStack taken = null;
				if (givenStack.stackSize > transferrable) taken = givenStack.splitStack(transferrable);
				else {
					taken = givenStack.copy();
					givenStack.stackSize = 0;
				}
				currentDestinationSlot.putStack(taken);
				currentDestinationSlot.onSlotChanged();
				successful = true;
			}
			if (givenStack.stackSize == 0) return successful;
		}

		return successful;
	}

}