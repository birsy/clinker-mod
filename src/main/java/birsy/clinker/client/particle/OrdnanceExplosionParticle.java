package birsy.clinker.client.particle;

import birsy.clinker.core.registry.ClinkerParticles;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.DustColorTransitionParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;

import org.joml.Vector3f;

public class OrdnanceExplosionParticle extends DustColorTransitionParticle {
    private double x, y, z, finalX, finalY, finalZ;
    private Vec3 direction;
    protected OrdnanceExplosionParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, DustColorTransitionOptions pOptions, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pOptions, pSprites);
        this.x = pX;
        this.y = pY;
        this.z = pZ;
        this.finalX = pX + pXSpeed;
        this.finalY = pY + pYSpeed;
        this.finalZ = pZ + pZSpeed;
    }

    @Override
    public void tick() {
        super.tick();
        float lerpSpeed = 0.7F;
        this.x = Mth.lerp(lerpSpeed, this.x, this.finalX);
        this.y = Mth.lerp(lerpSpeed, this.y, this.finalY);
        this.z = Mth.lerp(lerpSpeed, this.z, this.finalZ);
        this.finalY += 0.02;
        this.setPos(this.x, this.y, this.z);
    }

    
    public static class Provider implements ParticleProvider<DustColorTransitionOptions> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(DustColorTransitionOptions pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new OrdnanceExplosionParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType, this.sprites);
        }
    }

    public static class Options extends DustColorTransitionOptions {
        public Options(Vector3f color1, Vector3f color2, float size) {
            super(color1, color2, size);
        }

        @Override
        public ParticleType<DustColorTransitionOptions> getType() {
            return ClinkerParticles.ORDNANCE_EXPLOSION.get();
        }
    }
}
