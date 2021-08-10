package birsy.clinker.client.particles;

import java.util.Random;

import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.BaseAshSmokeParticle;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class FlyingAshParticle extends BaseAshSmokeParticle {
   protected FlyingAshParticle(ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, float p_i232459_14_, SpriteSet sprite) {
      super(worldIn, x, y, z, 0.1F, -0.1F, 0.1F, xSpeed, ySpeed, zSpeed, p_i232459_14_, sprite, 0.0F, 20, -5.0E-4D, false);
      this.rCol = 0.3984375F;
      this.gCol = 0.38671875F;
      this.bCol = 0.37890625F;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Factory(SpriteSet p_i232460_1_) {
         this.sprite = p_i232460_1_;
      }

      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         Random random = worldIn.random;
         double d0 = (double)random.nextFloat() * -1.9D * (double)random.nextFloat() * 0.1D;
         double d1 = (double)random.nextFloat() * -0.5D * (double)random.nextFloat() * 0.1D * 5.0D;
         double d2 = ((double)random.nextFloat() * -1.9D * (double)random.nextFloat() * 0.1D) * random.nextFloat() * 10;
         return new FlyingAshParticle(worldIn, x, y, z, d0, d1, d2, 1.0F, this.sprite);
      }
   }
}
