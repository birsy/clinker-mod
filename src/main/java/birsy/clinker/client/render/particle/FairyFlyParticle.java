package birsy.clinker.client.render.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;

@OnlyIn(Dist.CLIENT)
public class FairyFlyParticle extends Particle {
    protected float quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
    static ParticleRenderType PARTICLE_COLOR = new ParticleRenderType() {
        public void begin(BufferBuilder p_107462_, TextureManager p_107463_) {
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            p_107462_.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_LIGHTMAP);
        }

        public void end(Tesselator p_107465_) {
            p_107465_.end();
        }

        public String toString() {
            return "PARTICLE_COLOR";
        }
    };
    List<Vec3> previousPositions = Util.make(new ArrayList<>(5), (list) -> {
        for (int i = 0; i < 4; i++) {
            list.add(new Vec3(0, 0, 0));
        }
    });

    protected FairyFlyParticle(ClientLevel pLevel, double pX, double pY, double pZ) {
        super(pLevel, pX, pY, pZ);
    }

    protected FairyFlyParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
    }

    @Override
    public void tick() {
        super.tick();
        previousPositions.add(0, new Vec3(this.x, this.y, this.z));
        previousPositions.remove(previousPositions.size() - 1);
    }

    public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {

        Quaternionf quaternion;
        if (this.roll == 0.0F) {
            quaternion = pRenderInfo.rotation();
        } else {
            quaternion = new Quaternionf(pRenderInfo.rotation());
            float f3 = Mth.lerp(pPartialTicks, this.oRoll, this.roll);
            quaternion.mul(Axis.ZP.rotation(f3));
        }

        Vector3f[] verticies1 = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float quadSize = this.getQuadSize(pPartialTicks);

        Vec3 vec3 = pRenderInfo.getPosition();
        float x1 = (float)(Mth.lerp(pPartialTicks, this.xo, this.x) - vec3.x());
        float y1 = (float)(Mth.lerp(pPartialTicks, this.yo, this.y) - vec3.y());
        float z1 = (float)(Mth.lerp(pPartialTicks, this.zo, this.z) - vec3.z());

        for(int i = 0; i < 4; ++i) {
            Vector3f vertex = verticies1[i];
            quaternion.transform(vertex);
            vertex.mul(quadSize);
            vertex.add(x1, y1, z1);
        }

        this.alpha = 1.0F;
        this.rCol = 1.0F;
        this.gCol = 1.0F;
        this.bCol = 1.0F;

        int packedLight = this.getLightColor(pPartialTicks);
        pBuffer.vertex(verticies1[0].x(), verticies1[0].y(), verticies1[0].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        pBuffer.vertex(verticies1[1].x(), verticies1[1].y(), verticies1[1].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        pBuffer.vertex(verticies1[2].x(), verticies1[2].y(), verticies1[2].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        pBuffer.vertex(verticies1[3].x(), verticies1[3].y(), verticies1[3].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();

        int size = this.previousPositions.size();
        Vec3 trailPos = this.previousPositions.get(size - 2);
        Vec3 pTrailPos = this.previousPositions.get(size - 1);
        float x2 = (float)(Mth.lerp(pPartialTicks, pTrailPos.x, trailPos.x) - vec3.x());
        float y2 = (float)(Mth.lerp(pPartialTicks, pTrailPos.y, trailPos.y) - vec3.y());
        float z2 = (float)(Mth.lerp(pPartialTicks, pTrailPos.z, trailPos.z) - vec3.z());

        Vector3f[] verticies2 = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};

        for(int i = 0; i < 4; ++i) {
            Vector3f vertex = verticies2[i];
            quaternion.transform(vertex);
            vertex.mul(quadSize);
            vertex.add(x2, y2, z2);
        }

        pBuffer.vertex(verticies2[0].x(), verticies2[0].y(), verticies2[0].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        pBuffer.vertex(verticies2[1].x(), verticies2[1].y(), verticies2[1].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        pBuffer.vertex(verticies2[2].x(), verticies2[2].y(), verticies2[2].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
        pBuffer.vertex(verticies2[3].x(), verticies2[3].y(), verticies2[3].z()).color(this.rCol, this.gCol, this.bCol, this.alpha).uv2(packedLight).endVertex();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return PARTICLE_COLOR;
    }

    public float getQuadSize(float partialTicks) {
        return this.quadSize;
    }

    public Particle scale(float pScale) {
        this.quadSize *= pScale;
        return super.scale(pScale);
    }


    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        public Provider(SpriteSet pSprites) {
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed) {
            return new FairyFlyParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
        }
    }
}
