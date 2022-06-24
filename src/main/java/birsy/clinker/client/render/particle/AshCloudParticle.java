package birsy.clinker.client.render.particle;

import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AshCloudParticle extends TextureSheetParticle {
    protected final SpriteSet sprites;
    private final float pAlpha;
    protected AshCloudParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
        super(pLevel, pX, pY, pZ);
        this.sprites = pSprites;
        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;
        this.quadSize *= 8.75F;
        this.pAlpha = this.random.nextFloat() * 0.5F;
        this.alpha = 0.0F;
        this.lifetime = 60 + this.random.nextInt(12);
        this.pickSprite(pSprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.oRoll = this.roll;
        float progress = (float)(this.age) / (float)(this.lifetime);
        float fadeStart = 0.2F;
        this.alpha = progress < fadeStart ? MathUtils.mapRange(0, fadeStart, 0, 1, progress) : MathUtils.mapRange(fadeStart, 1, 1, 0, progress);
        this.alpha *= this.pAlpha;
        this.roll += this.zd;
        this.zd += random.nextGaussian() * 0.005;
        this.move(0, 0, 0.5 * MathUtils.bias(progress, -0.5));
    }

    @Override
    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
        super.render(pBuffer, pRenderInfo, pPartialTicks);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void move(double pX, double pY, double pZ) {
        this.setBoundingBox(this.getBoundingBox().move(pX, pY, pZ));
        this.setLocationFromBoundingbox();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet pSprites) {
            this.sprites = pSprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new AshCloudParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
        }
    }
}
