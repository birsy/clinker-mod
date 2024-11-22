package birsy.clinker.client.particle;

import birsy.clinker.core.util.MathUtils;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;



public class SnoozeParticle extends SimpleAnimatedParticle {
    final double initialX;
    final double initialY;
    final double initialZ;

    final float spiralStrength;
    final float particleSpeed;
    final float snoozeHeight;

    protected SnoozeParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pRed, double pGreen, double pBlue, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, pSprites, 0.0125F);
        this.initialX = pX;
        this.initialY = pY;
        this.initialZ = pZ;
       // this.setColor((float)pRed, (float)pGreen, (float)pBlue);
        this.setFadeColor(1648944);
        this.setColor(0x50A59f5B);
        this.quadSize *= 0.75F;
        this.lifetime = 100 + this.random.nextInt(60);
        this.spiralStrength = MathUtils.getRandomFloatBetween(this.random, 9.0F, 11.0F);
        this.particleSpeed = MathUtils.getRandomFloatBetween(this.random, 6.0F, 10.0F);
        this.snoozeHeight = MathUtils.getRandomFloatBetween(this.random, 0.1F, 0.3F);
        this.setSpriteFromAge(pSprites);
    }

    @Override
    public void tick() {
        super.tick();
        float progress = (float)(this.age) / (float)(this.lifetime);

        float snoozeTime = progress * this.particleSpeed;
        float spiral = snoozeTime / this.spiralStrength;
        this.setAlpha((progress * -1) + 1);
        this.setPos(this.initialX + (Mth.cos(snoozeTime) * spiral), this.initialY + (snoozeTime * this.snoozeHeight), this.initialZ + (Mth.sin(snoozeTime) * spiral));
    }

    public int getLightColor(float pPartialTick) {
        BlockPos blockpos = BlockPos.containing(this.x, this.y, this.z);
        return this.level.hasChunkAt(blockpos) ? LevelRenderer.getLightColor(this.level, blockpos) : 0;
    }

    
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pRed, double pGreen, double pBlue) {
            return new SnoozeParticle(pLevel, pX, pY, pZ, pRed, pGreen, pBlue, this.sprites);
        }
    }
}
