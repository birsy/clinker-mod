package birsy.clinker.client.particle;

import birsy.clinker.core.registry.ClinkerParticles;
import birsy.clinker.core.util.noise.FastNoiseLite;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import org.joml.Vector3d;

public class BlossomBugParticle extends SingleQuadParticle {
    private final Vector3d home;
    private final Vector3d directionToHome = new Vector3d();
    private final boolean orbitReversed = random.nextBoolean();
    private final double sampleOffset = random.nextDouble() * 100;
    private static final FastNoiseLite noise = new FastNoiseLite();
    static {
        noise.SetFrequency(1.0F);
        noise.SetNoiseType(FastNoiseLite.NoiseType.ValueCubic);
        noise.SetFractalType(FastNoiseLite.FractalType.None);
    }

    protected BlossomBugParticle(ClientLevel level, BlockPos home, double x, double y, double z) {
        super(level, x, y, z);
        this.home = new Vector3d(home.getX() + 0.5, home.getY() + 0.4, home.getZ() + 0.5);
        this.quadSize = Mth.lerp(this.random.nextFloat(), 0.3F, 0.55F);
        this.lifetime = level.random.nextInt(6, 12) * 20;

        this.gravity = 0;
        this.friction = 1;
        this.setAlpha(0);
        float brightness = this.random.nextFloat();
        this.setColor(0.67F * brightness, 0.75F * brightness, 0.35F * brightness);
    }

    @Override protected float getU0() { return 0; }
    @Override protected float getV0() { return 0; }
    @Override protected float getU1() { return 1; }
    @Override protected float getV1() { return 1; }

    @Override
    public void tick() {
        super.tick();
        double ageFactor = (float) this.age / (this.lifetime - 1);
        double flicker = Mth.map(Math.sin(this.age / 7.0), -1, 1, 0.5, 1);
        this.setAlpha((float) (Mth.clampedMap(ageFactor, 0.0, 0.1, 0, 1) *
                               Mth.clampedMap(ageFactor, 0.5, 1.0, 1, 0) * flicker));
        this.home.sub(this.x, this.y, this.z, this.directionToHome).normalize();
        double towardsHomeX = this.directionToHome.x,
               towardsHomeZ = this.directionToHome.z;

        this.directionToHome.cross(0, 1, 0);

        double orbitSpeed = (orbitReversed ? -1 : 1) * (getNoise(0.02, 0, 0.5) + 0.5) * 0.06;
        double towardsHomeSpeed = getNoise(0.03, 10, 0.05);
        this.xd = directionToHome.x * orbitSpeed + towardsHomeSpeed * towardsHomeX;
        this.zd = directionToHome.z * orbitSpeed + towardsHomeSpeed * towardsHomeZ;
        this.yd = directionToHome.y * orbitSpeed + getNoise(0.05, 20, 0.02);
    }

    private double getNoise(double changeSpeed, double sampleOffset, double maxSpeed) {
        return noise.GetNoise(this.age * changeSpeed, sampleOffset + this.sampleOffset) * maxSpeed;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.FULL_BRIGHT;
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ClinkerParticleRenderTypes.BLOSSOM_BUG;
    }

    public record BlossomBugParticleOptions(BlockPos homePos) implements ParticleOptions {
        public static final MapCodec<BlossomBugParticleOptions> CODEC = RecordCodecBuilder.mapCodec(
                builder -> builder.group(
                        BlockPos.CODEC.fieldOf("home_pos").forGetter(particleOptions -> particleOptions.homePos)
                ).apply(builder, BlossomBugParticleOptions::new)
        );
        public static final StreamCodec<RegistryFriendlyByteBuf, BlossomBugParticleOptions> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC, particleOptions -> particleOptions.homePos,
                BlossomBugParticleOptions::new
        );

        @Override
        public ParticleType<?> getType() {
            return ClinkerParticles.BLOSSOM_BUG.get();
        }
    }

    public static class Provider implements ParticleProvider<BlossomBugParticleOptions> {
        public Provider(SpriteSet pSprites) {}

        public Particle createParticle(BlossomBugParticleOptions options, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new BlossomBugParticle(pLevel, options.homePos(), pX, pY, pZ);
        }
    }
}
