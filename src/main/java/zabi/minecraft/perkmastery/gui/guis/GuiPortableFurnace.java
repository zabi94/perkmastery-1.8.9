package zabi.minecraft.perkmastery.gui.guis;

import org.lwjgl.opengl.GL11;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import zabi.minecraft.perkmastery.container.ContainerPortableFurnace;
import zabi.minecraft.perkmastery.entity.PortableFurnaceData;
import zabi.minecraft.perkmastery.gui.GuiBase;


public class GuiPortableFurnace extends GuiBase {

	private static final ResourceLocation	texture	= new ResourceLocation("textures/gui/container/furnace.png");
	private PortableFurnaceData				tileFurnace;

	public GuiPortableFurnace(Container container) {
		super(container);
		tileFurnace = ((ContainerPortableFurnace) container).tileFurnace;
	}

	@Override
	protected ResourceLocation getTexture() {
		return texture;
	}

	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(getTexture());
		int k = (this.width - this.xSize) / 2;
		int l = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(k, l, 0, 0, this.xSize, this.ySize);

		if (this.tileFurnace.isBurning()) {
			int i1 = this.tileFurnace.getBurnTimeRemainingScaled(13);
			this.drawTexturedModalRect(k + 56, l + 36 + 12 - i1, 176, 12 - i1, 14, i1 + 1);
			i1 = this.tileFurnace.getCookProgressScaled(24);
			this.drawTexturedModalRect(k + 79, l + 34, 176, 14, i1 + 1, 16);
		}
	}

}
