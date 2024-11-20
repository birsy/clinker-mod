package birsy.clinker.client.entity;

import birsy.clinker.common.world.entity.projectile.OrdnanceEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class OrdnanceRenderer extends EntityRenderer<OrdnanceEntity> {
    private static final ResourceLocation ORDNANCE_LOCATION = new ResourceLocation(Clinker.MOD_ID, "textures/entity/ordnance.png");

    public OrdnanceRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public void render(OrdnanceEntity pEntity, float pEntityYaw, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight) {

        VertexConsumer consumer = pBuffer.getBuffer(RenderType.entityCutout(this.getTextureLocation(pEntity)));

        float fuseTime = (pEntity.getFuseTime() + pPartialTick) / (pEntity.getMaxFuseTime() + 1.0F);
        float fuseFactor = fuseTime * 120;
        float bombFlash = Mth.clamp(Mth.sin(((fuseFactor * fuseFactor * Mth.PI) / 20.0F) * 0.02F), 0, 1) * fuseTime;
        float bigPuffTime = 110;
        if (fuseFactor > bigPuffTime) bombFlash = Math.max((float) (1 - Math.pow((fuseFactor - 120) / (120 - bigPuffTime), 4)) * fuseTime, bombFlash);

        int overlay = OverlayTexture.pack(bombFlash, pEntity.hurtMarked);
        int blockLight = pEntity.level().getBrightness(LightLayer.BLOCK, pEntity.blockPosition()),
            skyLight = pEntity.level().getBrightness(LightLayer.SKY, pEntity.blockPosition());
        int light = LightTexture.pack(Math.max((int) (bombFlash * 16), blockLight), skyLight);

        Vec3 directionTowardsCamera = this.entityRenderDispatcher.camera.getPosition().subtract(pEntity.getPosition(pPartialTick)).normalize();

        pPoseStack.pushPose();
        pPoseStack.translate(0, pEntity.getBbHeight() * 0.5F, 0);
        float size = 1 / 16.0F;
        size *= 1 + Mth.sqrt(bombFlash) * 0.2F;
        size *= 0.6F;
        pPoseStack.scale(size, size, size);

        drawBomb(pPoseStack, consumer, light, overlay, pEntity, pPartialTick, directionTowardsCamera);

        pPoseStack.popPose();

        super.render(pEntity, pEntityYaw, pPartialTick, pPoseStack, pBuffer, pPackedLight);
    }
    
    public void drawBomb(PoseStack stack, VertexConsumer consumer, int pPackedLight, int overlayTexture, OrdnanceEntity pEntity, float pPartialTick, Vec3 directionTowardsCamera) {
        stack.pushPose();
        Vec3 dir = directionTowardsCamera.scale(8 / 16.0F);
        stack.translate(dir.x(), dir.y(), dir.z());
        stack.mulPose(this.entityRenderDispatcher.cameraOrientation());
        stack.mulPose(Axis.ZP.rotation(pEntity.getSpin(pPartialTick)));

        stack.translate(0, 2 / 16.0F, 0);

        float u0 = 0;
        float u1 = 12.0F / 32.0F;
        float v0 = 0;
        float v1 = 1.0F;
        Vertex[] verticies = new Vertex[]{
                new Vertex(-6.0F, -9.0F, 0.0F, u1, v1),
                new Vertex(-6.0F, 9.0F, 0.0F, u1, v0),
                new Vertex(6.0F, 9.0F, 0.0F, u0, v0),
                new Vertex(6.0F, -9.0F, 0.0F, u0, v1)};
        for (Vertex vertex : verticies) {
            Vector4f vert = new Vector4f(vertex.position, 1.0F);
            vert = stack.last().pose().transform(vert);
            vertex.position.set(vert.x, vert.y, vert.z);
        }
        Vector3f normal = new Vector3f(0, 0, -1);
        normal = stack.last().normal().transform(normal);

        consumer.vertex(verticies[0].x(), verticies[0].y(), verticies[0].z(), 1, 1, 1, 1, verticies[0].u(), verticies[0].v(), overlayTexture, pPackedLight, normal.x, normal.y, normal.z);
        consumer.vertex(verticies[1].x(), verticies[1].y(), verticies[1].z(), 1, 1, 1, 1, verticies[1].u(), verticies[1].v(), overlayTexture, pPackedLight, normal.x, normal.y, normal.z);
        consumer.vertex(verticies[2].x(), verticies[2].y(), verticies[2].z(), 1, 1, 1, 1, verticies[2].u(), verticies[2].v(), overlayTexture, pPackedLight, normal.x, normal.y, normal.z);
        consumer.vertex(verticies[3].x(), verticies[3].y(), verticies[3].z(), 1, 1, 1, 1, verticies[3].u(), verticies[3].v(), overlayTexture, pPackedLight, normal.x, normal.y, normal.z);
        stack.popPose();
    }

    public void drawRope(PoseStack stack, VertexConsumer consumer, float xV, float yV, float zV, int pPackedLight, int overlayTexture, OrdnanceEntity pEntity, float pPartialTick, Vec3 directionTowardsCamera) {
        stack.pushPose();
        Vector3f offsetVector = new Vector3f(0, 5.5f, 0);
        offsetVector = this.entityRenderDispatcher.cameraOrientation().rotateLocalZ(pEntity.getSpin(pPartialTick), new Quaternionf()).transform(offsetVector);

        Vector3f localYVector = new Vector3f(xV, yV, zV);
        Vector3f localXVector = localYVector.cross((float) directionTowardsCamera.x(), (float) directionTowardsCamera.y(), (float) directionTowardsCamera.z(), new Vector3f()).normalize().mul(5.5F);

        float u0 = 12.0F / 32.0F;
        float u1 = 1;
        float v0 = 0;
        float v1 = 11.0F / 16.0F;

        Vertex[] verticies = new Vertex[]{
                new Vertex(localXVector.mul(-1, new Vector3f()).add(localYVector).add(offsetVector), u1, v0),
                new Vertex(localXVector.mul(-1, new Vector3f()).add(offsetVector), u0, v0),
                new Vertex(localXVector.mul( 1, new Vector3f()).add(offsetVector), u0, v1),
                new Vertex(localXVector.mul( 1, new Vector3f()).add(localYVector).add(offsetVector), u1, v1)};
        for (Vertex vertex : verticies) {
            Vector4f vert = new Vector4f(vertex.position, 1.0F);
            vert = stack.last().pose().transform(vert);
            vertex.position.set(vert.x, vert.y, vert.z);
        }
        Vector3f normal = new Vector3f(0, 0, -1);
        normal = stack.last().normal().transform(normal);

        consumer.vertex(verticies[0].x(), verticies[0].y(), verticies[0].z(), 1, 1, 1, 1, verticies[0].u(), verticies[0].v(), overlayTexture, pPackedLight, normal.x, normal.y, normal.z);
        consumer.vertex(verticies[1].x(), verticies[1].y(), verticies[1].z(), 1, 1, 1, 1, verticies[1].u(), verticies[1].v(), overlayTexture, pPackedLight, normal.x, normal.y, normal.z);
        consumer.vertex(verticies[2].x(), verticies[2].y(), verticies[2].z(), 1, 1, 1, 1, verticies[2].u(), verticies[2].v(), overlayTexture, pPackedLight, normal.x, normal.y, normal.z);
        consumer.vertex(verticies[3].x(), verticies[3].y(), verticies[3].z(), 1, 1, 1, 1, verticies[3].u(), verticies[3].v(), overlayTexture, pPackedLight, normal.x, normal.y, normal.z);

        stack.popPose();
    }

    private static class Vertex {
        final Vector3f position;
        final Vector2f textureCoordinates;

        private Vertex(float x, float y, float z, float u, float v) {
            this.position = new Vector3f(x, y, z);
            this.textureCoordinates = new Vector2f(u, v);
        }

        private Vertex(Vector3f pos, float u, float v) {
            this.position = pos;
            this.textureCoordinates = new Vector2f(u, v);
        }
        
        float x() {
            return position.x();
        }
        float y() {
            return position.y();
        }
        float z() {
            return position.z();
        }
        float u() {
            return textureCoordinates.x();
        }
        float v() {
            return textureCoordinates.y();
        }
    }

    @Override
    public ResourceLocation getTextureLocation(OrdnanceEntity pEntity) {
        return ORDNANCE_LOCATION;
    }
}
