package zabi.minecraft.perkmastery.tileentity;

import java.util.List;
import java.util.Random;

import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IChatComponent;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer;
import zabi.minecraft.perkmastery.entity.ExtendedPlayer.PlayerClass;
import zabi.minecraft.perkmastery.libs.LibGameRules;


public class TileEntityEnchanter extends TileBase implements IInventory {

	private ItemStack			content			= null;
	private int					ticks			= 0;
	private Random				rnd;
	private boolean				errored			= false;
	private static final String	TAG				= "tile_data";
	private static final String	T_TAG			= "req_ticks";
	private static final int	REQUIRED_TICKS	= 20*60;

	public TileEntityEnchanter() {
		rnd = new Random();
	}

	@Override
	protected void NBTLoad(NBTTagCompound tag) {
		if (tag.hasKey(TAG)) content = ItemStack.loadItemStackFromNBT(tag.getCompoundTag(TAG));
		if (tag.hasKey(T_TAG)) ticks = tag.getInteger(T_TAG);
	}

	@Override
	protected void NBTSave(NBTTagCompound tag) {
		if (content != null) {
			NBTTagCompound data = new NBTTagCompound();
			content.writeToNBT(data);
			tag.setTag(TAG, data);
			if (ticks != 0) tag.setInteger(T_TAG, ticks);
		}
	}

	@Override
	protected void tick() {

		// return;

		if (content != null && !content.isItemEnchanted() && ticks <= REQUIRED_TICKS) {
			if (ticks == REQUIRED_TICKS) enchant();
			ticks++;
		} else {
			ticks = 0;
		}
	}

	private void enchant() {
		List<EnchantmentData> enchantments = EnchantmentHelper.buildEnchantmentList(rnd, content, EnchantmentHelper.calcItemStackEnchantability(rnd, 2, 3, content) * 2);
		try {
			for (EnchantmentData data : enchantments)
				content.addEnchantment(data.enchantmentobj, data.enchantmentLevel);
			errored = false;
		} catch (NullPointerException e) {
			errored = true;
			return;
		}
		content.attemptDamageItem(content.getMaxDamage() / 8, rnd);
		if (content.getItemDamage()>content.getMaxDamage()) content=null;
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public ItemStack getStackInSlot(int slot) {
		if (slot != 0) return null;
		return content;
	}

	public void dropContents() {
		if (worldObj.getGameRules().getBoolean(LibGameRules.doTileDrops.name())) for (int i = 0; i < getSizeInventory(); i++) {
			if (getStackInSlot(i) != null) worldObj.spawnEntityInWorld(new EntityItem(worldObj, pos.getX(), pos.getY(), pos.getZ(), this.getStackInSlot(i)));
		}
	}

	@Override
	public ItemStack decrStackSize(int slot, int qt) {
		if (slot != 0) return null;
		if (content.stackSize > qt) return content.splitStack(qt);
		ItemStack res = content.copy();
		content = null;
		errored = false;
		return res;
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack is) {
		if (slot != 0) return;
		content = is;
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer player) {
		return ExtendedPlayer.isAbilityEnabled(player, PlayerClass.MAGE, 2);
	}


	@Override
	public boolean isItemValidForSlot(int slot, ItemStack is) {
		if (slot != 0) return false;
		return is.isItemEnchantable();
	}

	public double getProgress() {
		return (double) ticks / (double) REQUIRED_TICKS;
	}

	public boolean isErrored() {
		return errored;
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
	public void openInventory(EntityPlayer player) {
	}

	@Override
	public void closeInventory(EntityPlayer player) {
	}

	@Override
	public int getField(int id) {
		if (id==0) return ticks;
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		if (id==0) ticks=value;
		
	}

	@Override
	public int getFieldCount() {
		return 1;
	}

	@Override
	public void clear() {
		ticks=0;
		content=null;		
	}
	
	@Override
	public String getName() {
		return null;
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		ItemStack res=this.getStackInSlot(index);
		this.setInventorySlotContents(index, null);
		return res;
	}

}
