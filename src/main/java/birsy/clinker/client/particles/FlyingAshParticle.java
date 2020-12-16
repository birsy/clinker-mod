package birsy.clinker.client.particles;

import java.util.Random;

import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.RisingParticle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlyingAshParticle extends RisingParticle {
   protected FlyingAshParticle(ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, float p_i232459_14_, IAnimatedSprite sprite) {
      super(worldIn, x, y, z, 0.1F, -0.1F, 0.1F, xSpeed, ySpeed, zSpeed, p_i232459_14_, sprite, 0.0F, 20, -5.0E-4D, false);
      this.particleRed = 0.3984375F;
      this.particleGreen = 0.38671875F;
      this.particleBlue = 0.37890625F;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite sprite;

      public Factory(IAnimatedSprite p_i232460_1_) {
         this.sprite = p_i232460_1_;
      }

      public Particle makeParticle(BasicParticleType typeIn, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         Random random = worldIn.rand;
         double d0 = (double)random.nextFloat() * -1.9D * (double)random.nextFloat() * 0.1D;
         double d1 = (double)random.nextFloat() * -0.5D * (double)random.nextFloat() * 0.1D * 5.0D;
         double d2 = ((double)random.nextFloat() * -1.9D * (double)random.nextFloat() * 0.1D) * random.nextFloat() * 10;
         return new FlyingAshParticle(worldIn, x, y, z, d0, d1, d2, 1.0F, this.sprite);
      }
   }
}
