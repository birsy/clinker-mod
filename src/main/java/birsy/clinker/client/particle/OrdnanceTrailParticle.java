package birsy.clinker.client.particle;

import birsy.clinker.core.registry.ClinkerParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.api.distmarker.Dist;

import org.joml.Vector3f;

public class OrdnanceTrailParticle extends DustColorTransitionParticle {
    protected OrdnanceTrailParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, DustColorTransitionOptions pOptions, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pOptions, pSprites);
        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;
    }

    @Override
    public void tick() {
        super.tick();
        this.quadSize = Math.min(this.quadSize * 1.002F, 1.3F);
    }

    @Override
    public void lerpColors(float pPartialTick) {
        float factor = ((float)this.age + pPartialTick) / ((float)this.lifetime + 1.0F);
        factor = (float) (Math.pow(factor, 0.4));
        Vector3f color = new Vector3f(this.fromColor).lerp(this.toColor, factor);
        this.rCol = color.x();
        this.gCol = color.y();
        this.bCol = color.z();
    }

    
    public static class Provider implements ParticleProvider<DustColorTransitionOptions>, ParticleEngine.SpriteParticleRegistration<DustColorTransitionOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(DustColorTransitionOptions pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new OrdnanceTrailParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType, this.sprites);
        }

        @Override
        public ParticleProvider<DustColorTransitionOptions> create(SpriteSet pSprites) {
            return new OrdnanceTrailParticle.Provider(pSprites);
        }
    }

    public static class Options extends DustColorTransitionOptions {
        public Options(Vector3f color1, Vector3f color2, float size) {
            super(color1, color2, size);
        }

        @Override
        public ParticleType<DustColorTransitionOptions> getType() {
            return ClinkerParticles.ORDNANCE_TRAIL.get();
        }
    }
}
