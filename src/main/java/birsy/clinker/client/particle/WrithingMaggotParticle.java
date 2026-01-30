package birsy.clinker.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class WrithingMaggotParticle extends SimpleAnimatedParticle {
    float previousQuadSize = this.quadSize;
    float rotationSpeed;
    protected WrithingMaggotParticle(ClientLevel pLevel, double pX, double pY, double pZ, double dX, double dY, double dZ, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ, pSprites, 1.0F);
        this.xd = dX; this.yd = dY; this.zd = dZ;
        this.setSpriteFromAge(pSprites);
        this.quadSize = 0;
        this.previousQuadSize = 0;
        this.lifetime = this.random.nextInt(80, 120);
        this.rotationSpeed = 0.2F * (this.random.nextFloat() > 0.5 ? -1 : 1) + (this.random.nextFloat() * 2 - 1) * 0.1F;

        this.setColor(
                this.random.nextInt(200, 255) / 255.0F,
                this.random.nextInt(210, 255) / 255.0F,
                this.random.nextInt(180, 255) / 255.0F);
    }

    @Override
    public void tick() {
        this.previousQuadSize = this.quadSize;
        this.oRoll = this.roll;
        super.tick();

        // writhe back and forth
        float writheTime = Mth.map(
                Mth.sin(this.age * 0.35F + this.lifetime),
                -1, 1, 0, 2
        );
        this.setSprite(this.sprites.get(Math.round(writheTime), 2));

        // scale down at the end of its lifetime
        float scaleFactor = Mth.clampedMap(this.age, this.lifetime * 0.8F, this.lifetime, 1, 0);
        scaleFactor *= Mth.clampedMap(this.age, 0, 3, 0, 1);
        scaleFactor = (float) Mth.smoothstep(scaleFactor);
        this.quadSize = 0.2F * scaleFactor;

        if (this.onGround) {
            // randomly jump, if on the ground...
            if (this.random.nextInt(20) == 0) {
                this.y += 0.01;
                this.xd += (this.random.nextFloat() * 2 - 1) * 0.1F;
                this.zd += (this.random.nextFloat() * 2 - 1) * 0.1F;
                this.yd = this.random.nextFloat() * 0.3F;

                this.onGround = false;
                this.stoppedByCollision = false;
                if (this.random.nextInt(3) == 0)
                    this.level.playLocalSound(this.x, this.y, this.z, SoundEvents.COD_FLOP, SoundSource.HOSTILE, 0.5F, this.random.nextFloat(), true);
            }
        } else {
            this.roll += this.rotationSpeed;
        }
        this.setAlpha(1.0F);
    }

    @Override
    protected void renderRotatedQuad(VertexConsumer buffer, Camera camera, Quaternionf quaternion, float partialTicks) {
        Vec3 cameraPos = camera.getPosition();
        float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - cameraPos.x());
        float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - cameraPos.y()) + this.getQuadSize(partialTicks) * 0.5F;
        float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - cameraPos.z());
        this.renderRotatedQuad(buffer, quaternion, x, y, z, partialTicks);
    }

    @Override
    public float getQuadSize(float partialTicks) {
        return Mth.lerp(partialTicks, this.previousQuadSize, this.quadSize);
    }

    @Override
    public int getLightColor(float partialTick) {
        return LevelRenderer.getLightColor(this.level, BlockPos.containing(this.x, this.y, this.z));
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double dX, double dY, double dZ) {
            return new WrithingMaggotParticle(pLevel, pX, pY, pZ, dX, dY, dZ, this.sprites);
        }
    }
}
