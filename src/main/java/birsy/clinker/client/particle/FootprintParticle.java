package birsy.clinker.client.particle;

import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;

public class FootprintParticle extends TextureSheetParticle {
    final float angle;

    protected FootprintParticle(ClientLevel level, double x, double y, double z, double xSpeed, double zSpeed, double size, SpriteSet pSprites) {
        super(level,
                Math.floor(x) + Math.floor(Mth.frac(x) * 16.0) / 16.0,
                y + 0.005,
                Math.floor(z) + Math.floor(Mth.frac(z) * 16.0) / 16.0,
                xSpeed, 0.0, zSpeed);
        this.quadSize = (float) size;
        this.sprite = pSprites.get(level.random);

        double angleFromSpeed = Mth.atan2(xSpeed, zSpeed);
        this.angle = Double.isNaN(angleFromSpeed) ? 0.0F : (float) angleFromSpeed;
        this.hasPhysics = false;
        this.lifetime = 100;
    }

    @Override protected float getU0() { return 0; }
    @Override protected float getV0() { return 0; }
    @Override protected float getU1() { return 1; }
    @Override protected float getV1() { return 1; }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        }
    }

    private static final Quaternionf rotation = new Quaternionf();
    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        rotation.identity();
        rotation.rotateX(-Mth.HALF_PI);
        //rotation.rotateZ(angle);

        float age = this.age + partialTicks;
        float alpha = Mth.clampedMap(age, this.lifetime * 0.5F, this.lifetime + 1, 1, 0);
        alpha *= Mth.clampedMap(age, 0, 3, 0, 1);
        alpha *= 0.3F;
        this.setAlpha(alpha);

        this.setColor((this.angle / Mth.PI) * 0.5F + 0.5F, 0F, 0F);

        this.renderRotatedQuad(buffer, renderInfo, rotation, partialTicks);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ClinkerParticleRenderTypes.FOOTPRINT;
    }

    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pZSpeed, double size) {
            return new FootprintParticle(pLevel, pX, pY, pZ, pXSpeed, pZSpeed, size, this.sprites);
        }
    }
}
