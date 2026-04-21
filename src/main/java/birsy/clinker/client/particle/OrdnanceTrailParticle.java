package birsy.clinker.client.particle;

import birsy.clinker.common.world.ordnance.OrdnanceGradient;
import birsy.clinker.core.registry.ClinkerParticles;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.DustColorTransitionOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ScalableParticleOptionsBase;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;

import org.joml.Vector3f;

public class OrdnanceTrailParticle extends DustParticleBase<OrdnanceTrailParticle.Options> {
    final OrdnanceGradient gradient;
    final float timeOffset;

    protected OrdnanceTrailParticle(ClientLevel level, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, OrdnanceTrailParticle.Options options, SpriteSet pSprites) {
        super(level, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, options, pSprites);
        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;

        this.gradient = options.gradient;
        this.timeOffset = level.getRandom().nextFloat();
    }

    @Override
    public void tick() {
        super.tick();
        this.quadSize = Math.min(this.quadSize * 1.002F, 1.3F);
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        float factor = (this.age + partialTicks) / (this.lifetime + 1.0F);
        factor = Mth.clamp((float) Math.pow(factor, 0.4F) + timeOffset * 0.1F, 0, 1);
        this.rCol = this.gradient.red(factor);
        this.gCol = this.gradient.green(factor);
        this.bCol = this.gradient.blue(factor);
        super.render(buffer, renderInfo, partialTicks);
    }
    
    public static class Provider implements ParticleProvider<OrdnanceTrailParticle.Options>, ParticleEngine.SpriteParticleRegistration<OrdnanceTrailParticle.Options> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(OrdnanceTrailParticle.Options pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new OrdnanceTrailParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, pType, this.sprites);
        }

        @Override
        public ParticleProvider<OrdnanceTrailParticle.Options> create(SpriteSet pSprites) {
            return new Provider(pSprites);
        }
    }


    public static class Options extends ScalableParticleOptionsBase {
        public static final MapCodec<OrdnanceTrailParticle.Options> CODEC = RecordCodecBuilder.mapCodec(
                instance -> instance.group(
                                OrdnanceGradient.CODEC.fieldOf("gradient").forGetter(options -> options.gradient),
                                SCALE.fieldOf("scale").forGetter(ScalableParticleOptionsBase::getScale)
                        ).apply(instance, Options::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, OrdnanceTrailParticle.Options> STREAM_CODEC = StreamCodec.composite(
                OrdnanceGradient.STREAM_CODEC, options -> options.gradient,
                ByteBufCodecs.FLOAT, ScalableParticleOptionsBase::getScale,
                OrdnanceTrailParticle.Options::new
        );

        final OrdnanceGradient gradient;
        public Options(OrdnanceGradient gradient, float size) {
            super(size);
            this.gradient = gradient;
        }

        @Override
        public ParticleType<OrdnanceTrailParticle.Options> getType() {
            return ClinkerParticles.ORDNANCE_TRAIL.get();
        }
    }
}
