package zabi.minecraft.perkmastery.gui.guis;

import java.util.ArrayList;

import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import zabi.minecraft.perkmastery.container.ContainerDisenchanter.SlotDisenchanterBooks;
import zabi.minecraft.perkmastery.container.ContainerDisenchanter.SlotDisenchanterOutput;
import zabi.minecraft.perkmastery.gui.GuiBase;
import zabi.minecraft.perkmastery.libs.LibGeneral;
import zabi.minecraft.perkmastery.tileentity.TileEntityDisenchanter;


public class GuiDisenchanter extends GuiBase {

	private static final ResourceLocation	texture	= new ResourceLocation(LibGeneral.MOD_ID, "textures/gui/disenchanter.png");
	private TileEntityDisenchanter			te;

	public GuiDisenchanter(Container cont, TileEntityDisenchanter disenchanter) {
		super(cont);
		te = disenchanter;
	}

	@Override
	protected ResourceLocation getTexture() {
		return texture;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float ptick, int x, int y) {
		mc.getTextureManager().bindTexture(texture);
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
		double progress = te.getProgress();
		this.drawTexturedModalRect(guiLeft + 119, guiTop + 35, 0, 166, 22 * progress, 15);

	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		ArrayList<String> tooltip = new ArrayList<String>();
		
		Slot s=getSlotUnderMouse();
		
		if (s!=null) {
			if (s instanceof SlotDisenchanterOutput && s.getHasStack() && te.getStackInSlot(1)!=null && te.getStackInSlot(2)!=null) tooltip.add(EnumChatFormatting.RED+StatCollector.translateToLocal("general.disenchanter.warn.output"));
			if (s instanceof SlotDisenchanterBooks && s.getHasStack() && te.getStackInSlot(1)!=null && te.getStackInSlot(2)!=null) tooltip.add(EnumChatFormatting.RED+StatCollector.translateToLocal("general.disenchanter.warn.books"));
		}
		
		if (tooltip.size()>0) {
			
			int k = (this.width - this.xSize) / 2;
			int l = (this.height - this.ySize) / 2;
			this.zLevel=0;
			int dx=fontRendererObj.getStringWidth(tooltip.get(tooltip.size()-1))/2;
			drawHoveringText(tooltip, mouseX - k - dx , mouseY - l- 20, fontRendererObj);
		}
	}

}
