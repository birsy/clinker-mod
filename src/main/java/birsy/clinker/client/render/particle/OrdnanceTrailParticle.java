package birsy.clinker.client.render.particle;

import birsy.clinker.core.registry.ClinkerParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
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
        this.quadSize = Math.min(this.quadSize * 1.011F, 1.3F);
    }

    @OnlyIn(Dist.CLIENT)
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
