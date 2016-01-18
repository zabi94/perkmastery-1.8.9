package zabi.minecraft.perkmastery.visual.effects;

import net.minecraft.client.particle.EntityEnchantmentTableParticleFX;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import zabi.minecraft.perkmastery.Config;


public class RuneFadeFX extends EntityEnchantmentTableParticleFX {

	private static final double	RADIUS	= 1.1;
	private double				radDev;
	private double				px, pz;
	private boolean				reverted, errored;
	private int					startAngle;

	public RuneFadeFX(World world, double x, double y, double z, boolean errored) {
		super(world, x, y, z, 0, 0, 0);
		this.errored = errored;
		this.particleMaxAge = 100;
		this.motionY = 0.04;

		if (Config.fpsSavingMode) {
			this.particleMaxAge = 90;
			this.radDev = 0;
			posY += 0.2;
		} else {
			this.radDev = rand.nextGaussian() * 0.1;
			posY += rand.nextGaussian() * 0.2;
		}

		px = posX;
		pz = posZ;
		this.particleScale *= 1.8;

		if (errored) {
			if (Config.fpsSavingMode) {
				this.particleRed = 0.6F;
			} else {
				this.particleRed = 0.5F + (float) (0.2 * rand.nextGaussian());
			}
			this.particleBlue = this.particleGreen = 0;
			this.motionY = -0.02;
			this.posY += 0.5;
			this.particleMaxAge *= 2;
		}

		startAngle = rand.nextInt(360);
		prevPosX = posX = getPosX(0);
		prevPosZ = posZ = getPosZ(0);
		reverted = rand.nextBoolean();
		this.particleMaxAge *= 0.8;
	}

	public void onUpdate() {
		float f = (float) this.particleAge / (float) this.particleMaxAge;
		if (!Config.fpsSavingMode) this.particleAlpha = (1.2F - f * f);
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;
		this.posY += (motionY * f);
		this.posX = getPosX(f);
		this.posZ = getPosZ(f);
		if (this.particleAge++ >= this.particleMaxAge) this.setDead();
	}

	private double getRadius() {
		return RADIUS + radDev;
	}

	private double getPosX(float f) {

		double conic = (1 - f);
		if (reverted) f = -f;
		if (errored) f *= 2.1;
		return px + Math.cos(f * (6 + radDev) + startAngle) * getRadius() * conic;
	}

	private double getPosZ(float f) {
		double conic = (1 - f);
		if (reverted) f = -f;
		if (errored) f *= 4.3;
		return pz + Math.sin(f * (6 + radDev) + startAngle) * getRadius() * conic;
	}
	
	@SideOnly(Side.CLIENT)
    public int getBrightnessForRender(float partialTicks) {
        return 256;
    }

}
