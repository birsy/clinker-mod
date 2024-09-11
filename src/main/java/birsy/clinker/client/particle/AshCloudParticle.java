package birsy.clinker.client.particle;

import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    public void move(double pX, double pY, double pZ) {
        this.setBoundingBox(this.getBoundingBox().move(pX, pY, pZ));
        this.setLocationFromBoundingbox();
    }

    public void render(VertexConsumer pBuffer, Camera camera, float pPartialTicks) {
        Vec3 vec3 = camera.getPosition();
        float x = (float)(Mth.lerp(pPartialTicks, this.xo, this.x) - vec3.x());
        float y = (float)(Mth.lerp(pPartialTicks, this.yo, this.y) - vec3.y());
        float z = (float)(Mth.lerp(pPartialTicks, this.zo, this.z) - vec3.z());

        Quaternionf rotation;

        if (this.roll == 0.0F) {
            rotation = camera.rotation();
        } else {
            rotation = new Quaternionf(camera.rotation());
            float roll = Mth.lerp(pPartialTicks, this.oRoll, this.roll);
            rotation.mul(Axis.ZP.rotation(roll));
        }

        Vector3f[] verticies = new Vector3f[]{
                new Vector3f(-1.0F, -1.0F, 0.0F),
                new Vector3f(-1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, 1.0F, 0.0F),
                new Vector3f(1.0F, -1.0F, 0.0F)
        };
        float quadSize = this.getQuadSize(pPartialTicks);

        for (Vector3f vertex : verticies) {
            rotation.transform(vertex);
            vertex.mul(quadSize);
            vertex.add(x, y, z);
        }

        float u0 = this.getU0();
        float u1 = this.getU1();
        float v0 = this.getV0();
        float v1 = this.getV1();
        int packedLight = this.getLightColor(pPartialTicks);
        pBuffer.vertex(verticies[0].x(), verticies[0].y(), verticies[0].z()).uv(u1, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        pBuffer.vertex(verticies[1].x(), verticies[1].y(), verticies[1].z()).uv(u1, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        pBuffer.vertex(verticies[2].x(), verticies[2].y(), verticies[2].z()).uv(u0, v0).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        pBuffer.vertex(verticies[3].x(), verticies[3].y(), verticies[3].z()).uv(u0, v1).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
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
