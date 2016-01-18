package zabi.minecraft.perkmastery.visual.renderer;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;


public abstract class RendererBase<T extends TileEntity> extends TileEntitySpecialRenderer<T> {

	@Override
	public void renderTileEntityAt(T te, double x, double y, double z, float ptick, int destroyStage) {

		GL11.glPushMatrix();
		GL11.glTranslatef((float) x + 0.5F, (float) y + 1.5F, (float) z + 0.5F);
		GL11.glRotated(180, 1, 0, 0);
		Minecraft.getMinecraft().renderEngine.bindTexture(getTexture(te, x, y, z));
		getModel(te, x, y, z).render((Entity) null, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
		GL11.glPopMatrix();

	}

	public abstract ModelBase getModel(TileEntity te, double x, double y, double z);

	public abstract ResourceLocation getTexture(TileEntity te, double x, double y, double z);
}
