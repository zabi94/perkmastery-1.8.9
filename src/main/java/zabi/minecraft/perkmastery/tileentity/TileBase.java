package zabi.minecraft.perkmastery.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zabi.minecraft.perkmastery.Config;
import zabi.minecraft.perkmastery.libs.LibGeneral;


public abstract class TileBase extends TileEntity implements ITickable {

	private static final String UNIQUE_TAG = LibGeneral.MOD_ID;

	public TileBase() {
		super();
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		if (!tag.hasKey(UNIQUE_TAG)) tag.setTag(UNIQUE_TAG, new NBTTagCompound());
		NBTTagCompound infinigemsData = (NBTTagCompound) tag.getTag(UNIQUE_TAG);
		NBTLoad(infinigemsData);
	}

	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		NBTTagCompound infinigemsData = new NBTTagCompound();
		NBTSave(infinigemsData);
		tag.setTag(UNIQUE_TAG, infinigemsData);
	}

	protected abstract void NBTLoad(NBTTagCompound tag);

	protected abstract void NBTSave(NBTTagCompound tag);

	protected abstract void tick();

	@SuppressWarnings("rawtypes")
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbt = new NBTTagCompound();
		writeToNBT(nbt);
		return new S35PacketUpdateTileEntity(this.pos, 0, nbt);

	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
		readFromNBT((pkt.getNbtCompound()));
	}

	@Override
	public void update() {
		if (!Config.disableTileEntities) tick();

	}

	@SideOnly(Side.CLIENT)
	public int getUniqueAnimationDiscriminator() {
		return pos.getX()+pos.getY()+pos.getZ();
	}

}
