package zabi.minecraft.perkmastery.visual.effects;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class OreHintFX extends EntitySmokeFX {

	private double sx,sy,sz,dx,dy,dz;
	
	public OreHintFX(World worldIn, int xCoordIn, int yCoordIn, int zCoordIn, boolean special) {
		super(worldIn, xCoordIn+0.5, yCoordIn+0.5, zCoordIn+0.5, 0, 0, 0, 1F);
		this.noClip=true;
		sx=posX;
		sy=posY;
		sz=posZ;
		dx=rand.nextGaussian()*0.2;
		dy=rand.nextGaussian()*0.2;
		dz=rand.nextGaussian()*0.2;
		motionX=motionY=motionZ=0;
		particleBlue=particleRed=particleGreen=1F;
		if (special) {
			particleRed=particleBlue=0F;
			particleGreen=1F;
		}
		this.particleMaxAge=20;
		this.posX=sx+dx;
		this.posY=sy+dy;
		this.posZ=sz+dz;
	}

	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (this.particleAge++ >= this.particleMaxAge) {
			this.setDead();
		}

		this.setParticleTextureIndex(7 - this.particleAge * 8 / this.particleMaxAge);
		double progress=(double)this.particleAge/(double)this.particleMaxAge+0.1;
		particleScale=(float) (progress+0.2);
		this.posX=sx+(dx*(1-progress));
		this.posY=sy+(dy*(1-progress));
		this.posZ=sz+(dz*(1-progress));

	}

	public void renderParticle(WorldRenderer wrend, Entity entity, float pticks, float f1, float f2, float f3, float f4, float f5){
		GL11.glEnable(GL11.GL_DEPTH_TEST);
		GL11.glDisable(GL11.GL_BLEND);
		super.renderParticle(wrend, entity, pticks, f1, f2, f3, f4, f5);
		GL11.glDisable(GL11.GL_DEPTH_TEST);

    }
	
	@SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float partialTicks) {
        return 256;
    }	
	
}
