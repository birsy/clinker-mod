package birsy.clinker.client.render.world.blockentity;

import birsy.clinker.client.render.ClinkerRenderTypes;
import birsy.clinker.client.render.entity.model.base.BasicModelPart;
import birsy.clinker.common.world.block.blockentity.FairyFruitBlockEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.Quaterniond;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.Vec3;

import java.util.function.Function;

public class FairyFruitRenderer<T extends FairyFruitBlockEntity> implements BlockEntityRenderer<T> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/block/fairy_fruit.png");
    public final FairyFruitModel leavesModel;
    public final FairyFruitModel fruitModel;

    public FairyFruitRenderer(BlockEntityRendererProvider.Context context) {
        super();
        this.leavesModel = new FairyFruitModel(RenderType::entityCutout);
        this.leavesModel.fruit.visible = false;
        this.fruitModel = new FairyFruitModel(ClinkerRenderTypes::entityUnlitTranslucent);
        this.fruitModel.leaf1.visible = false;
        this.fruitModel.leaf2.visible = false;
        this.fruitModel.bud.visible = false;
    }

    @Override
    public boolean shouldRender(T pBlockEntity, Vec3 pCameraPos) {
        return BlockEntityRenderer.super.shouldRender(pBlockEntity, pCameraPos);
    }

    @Override
    public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if (pBlockEntity.segments.isEmpty()) return;
        Vec3 parentPosition = new Vec3(pBlockEntity.getBlockPos().getX(), pBlockEntity.getBlockPos().getY(), pBlockEntity.getBlockPos().getZ());
        float totalLength = pBlockEntity.getBlockPos().hashCode() % Mth.PI;
        VertexConsumer consumer = pBufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        pPoseStack.pushPose();

        for (int i = 0; i < pBlockEntity.segments.size(); i++) {
            FairyFruitBlockEntity.FairyFruitSegment segment = pBlockEntity.segments.get(i);
            Vec3 pos1 = segment.topJoint.getPosition(pPartialTick).subtract(parentPosition);
            Vec3 pos2 = segment.bottomJoint.getPosition(pPartialTick).subtract(parentPosition);
            //DebugRenderUtil.renderLine(pPoseStack, pBufferSource.getBuffer(RenderType.LINES), pos1.x(), pos1.y(), pos1.z(), pos2.x(), pos2.y(), pos2.z(), 1, 0, 1, 1);

            if (segment.isTip()) {
                consumer = pBufferSource.getBuffer(ClinkerRenderTypes.entityUnlitTranslucent(TEXTURE));

                pPoseStack.pushPose();

                Vec3 fruitPos = pos1;

                pPoseStack.translate(fruitPos.x(), fruitPos.y(), fruitPos.z());

                pPoseStack.pushPose();
                pPoseStack.mulPose(i > 1 ? pBlockEntity.segments.get(i - 1).getOrientation(pPartialTick).toMojangQuaternion() : segment.topJoint.getOrientation(pPartialTick).toMojangQuaternion());
                pPoseStack.scale(-1.0F, -1.0F, 1.0F);

                BlockPos bPos = pBlockEntity.getBlockPos().offset(fruitPos.x() + 0.5, fruitPos.y(), fruitPos.z() + 0.5);
                int light = LightTexture.pack(15, pBlockEntity.getLevel().getBrightness(LightLayer.SKY,bPos));
                leavesModel.bud.visible = false;
                leavesModel.renderToBuffer(pPoseStack, consumer, light, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
                pPoseStack.popPose();

                pPoseStack.pushPose();
                pPoseStack.mulPose(segment.topJoint.getOrientation(pPartialTick).toMojangQuaternion());
                pPoseStack.mulPose(Vector3f.YN.rotation((float) segment.bottomJoint.roll));
                pPoseStack.scale(-1.0F, -1.0F, 1.0F);

                fruitModel.renderToBuffer(pPoseStack, consumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                pPoseStack.popPose();

                pPoseStack.popPose();
            } else {
                boolean lowGlow = false;
                Quaterniond topOrientation = segment.topJoint.getOrientation(pPartialTick);
                Quaterniond bottomOrientation = segment.bottomJoint.getOrientation(pPartialTick);
                if (i + 1 < pBlockEntity.segments.size()) {
                    FairyFruitBlockEntity.FairyFruitSegment nextSegment = pBlockEntity.segments.get(i + 1);
                    lowGlow = nextSegment.isTip();
                }
                drawVineBetweenPoints(pBlockEntity, pos1, topOrientation.toMojangQuaternion(),
                        pos2, bottomOrientation.toMojangQuaternion(),
                        totalLength, totalLength += segment.length, consumer, pPoseStack, pPackedLight, pPackedOverlay, lowGlow);
            }
        }

        for (FairyFruitBlockEntity.FairyFruitSegment segment : pBlockEntity.segments) {

        }
        pPoseStack.popPose();

        /*for (FairyFruitBlockEntity.FairyFruitJoint joint : pBlockEntity.joints) {
            Vec3 jPos = joint.getPosition(pPartialTick).subtract(parentPosition);
            DebugRenderUtil.renderSphere(pPoseStack, pBufferSource.getBuffer(RenderType.LINES), 32, (float)joint.radius, jPos.x, jPos.y, jPos.z, 0.2F, 0.2F, 0.8F, 0.6F);
        }*/
    }

    private void drawVineBetweenPoints(T entity, Vec3 point1, Quaternion point1Rotation, Vec3 point2, Quaternion point2Rotation, float uvOffset0, float uvOffset1, VertexConsumer buffer, PoseStack stack, int packedLight, int packedOverlay, boolean lowGlow) {
        Matrix4f matrix = stack.last().pose();
        float thickness = ((8.0F / 16.0F) * 0.5F) / Mth.sqrt(2);

        BlockPos samplePos1 = entity.getBlockPos().offset(point1.x(), point1.y(), point1.z());
        BlockPos samplePos2 = entity.getBlockPos().offset(point2.x(), point2.y(), point2.z());
        int light1 = packedLight;
        int light2 = packedLight;
        if (entity.hasLevel()) {
            light1 = LightTexture.pack(entity.getLevel().getBrightness(LightLayer.BLOCK, samplePos1), entity.getLevel().getBrightness(LightLayer.SKY, samplePos1));
            light2 = LightTexture.pack(lowGlow ? 15 : entity.getLevel().getBrightness(LightLayer.BLOCK, samplePos2), entity.getLevel().getBrightness(LightLayer.SKY, samplePos2));
        }
        
        float n = Mth.sqrt(2);

        Vector3f normal = new Vector3f(0, 1, 0);

        /*Vector3f normal1a = new Vector3f(n, 0, n);
        normal1a.transform(point1Rotation);
        normal1a.transform(stack.last().normal());

        Vector3f normal2a = new Vector3f(n, 0, n);
        normal2a.transform(point2Rotation);
        normal2a.transform(stack.last().normal());*/
        /*Vector3f normal1b = new Vector3f(-n, 0, n);
        normal1b.transform(point1Rotation);
        normal1b.transform(stack.last().normal());

        Vector3f normal2b = new Vector3f(-n, 0, n);
        normal2b.transform(point2Rotation);
        normal2b.transform(stack.last().normal());*/

        // a little monstrous but it works and i will never have to touch this again!
        renderFace(true, point1, normal, point1Rotation, point2, normal, point2Rotation, thickness, uvOffset0, uvOffset1, light1, light2, buffer, matrix);
        renderFace(false, point1, normal, point1Rotation, point2, normal, point2Rotation, thickness, uvOffset0, uvOffset1, light1, light2, buffer, matrix);
    }

    public void renderFace(boolean orientation, Vec3 point1, Vector3f normal1, Quaternion point1Rotation, Vec3 point2, Vector3f normal2, Quaternion point2Rotation, float thickness, float uvOffset0, float uvOffset1, int light1, int light2, VertexConsumer buffer, Matrix4f matrix) {
        int packedOverlay = OverlayTexture.NO_OVERLAY;
        int dir = orientation ? -1 : 1;

        vertex(buffer, matrix, point1, dir * -thickness, 0 , -thickness, point1Rotation)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0, uvOffset0 * 0.5F)
                .overlayCoords(packedOverlay).uv2(light1)
                .normal(normal1.x(), normal1.y(), normal1.z()).endVertex();
        vertex(buffer, matrix, point1, dir * thickness, 0, thickness, point1Rotation)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv( (8.0F / 32.0F), uvOffset0 * 0.5F)
                .overlayCoords(packedOverlay).uv2(light1)
                .normal(normal1.x(), normal1.y(), normal1.z()).endVertex();
        vertex(buffer, matrix, point2, dir * thickness, 0, thickness, point2Rotation)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv( (8.0F / 32.0F), uvOffset1 * 0.5F)
                .overlayCoords(packedOverlay).uv2(light2)
                .normal(normal2.x(), normal2.y(), normal2.z()).endVertex();
        vertex(buffer, matrix, point2, dir * -thickness, 0, -thickness, point2Rotation)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv( 0, uvOffset1 * 0.5F)
                .overlayCoords(packedOverlay).uv2(light2)
                .normal(normal2.x(), normal2.y(), normal2.z()).endVertex();

        normal1.mul(-1);
        normal2.mul(-1);
        vertex(buffer, matrix, point2, dir * -thickness, 0, -thickness, point2Rotation)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv( (8.0F / 32.0F), uvOffset1 * 0.5F)
                .overlayCoords(packedOverlay).uv2(light2)
                .normal(normal2.x(), normal2.y(), normal2.z()).endVertex();
        vertex(buffer, matrix, point2, dir * thickness, 0, thickness, point2Rotation)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv( 0, uvOffset1 * 0.5F)
                .overlayCoords(packedOverlay).uv2(light2)
                .normal(normal2.x(), normal2.y(), normal2.z()).endVertex();
        vertex(buffer, matrix, point1, dir * thickness, 0, thickness, point1Rotation)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv( 0, uvOffset0 * 0.5F)
                .overlayCoords(packedOverlay).uv2(light1)
                .normal(normal1.x(), normal1.y(), normal1.z()).endVertex();
        vertex(buffer, matrix, point1, dir * -thickness, 0 , -thickness, point1Rotation)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv((8.0F / 32.0F), uvOffset0 * 0.5F)
                .overlayCoords(packedOverlay).uv2(light1)
                .normal(normal1.x(), normal1.y(), normal1.z()).endVertex();



    }

    public static VertexConsumer vertex(VertexConsumer buffer, Matrix4f matrix, Vec3 base, float x, float y, float z, Quaternion rotation) {
        Vector3f pos = new Vector3f(x, y, z);
        pos.transform(rotation);
        return buffer.vertex(matrix, pos.x() + (float)base.x(), pos.y() + (float)base.y(), pos.z() + (float)base.z());
    }

    public static class FairyFruitModel extends Model {
        public final BasicModelPart fruit;
        public final BasicModelPart bud;
        public final BasicModelPart leaf1;
        public final BasicModelPart leaf2;
        public FairyFruitModel(Function<ResourceLocation, RenderType> pRenderType) {
            super(pRenderType);
            this.fruit = new BasicModelPart(BasicModelPart.toCubeList(CubeListBuilder.create().texOffs(8, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 7.0F, 6.0F), 32, 32));
            this.bud = new BasicModelPart(BasicModelPart.toCubeList(CubeListBuilder.create().texOffs(8, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F), 32, 32));
            this.leaf1 = new BasicModelPart(BasicModelPart.toCubeList(CubeListBuilder.create().texOffs(8, 13).addBox(-5.0F, -8.0F, 0.0F, 10.0F, 9.0F, 0.0F), 32, 32));
            this.leaf2 = new BasicModelPart(BasicModelPart.toCubeList(CubeListBuilder.create().texOffs(8, 3).addBox(0.0F, -8.0F, -5.0F, 0.0F, 9.0F, 10.0F), 32, 32));
        }

        @Override
        public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
            this.fruit.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
            this.bud.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
            this.leaf1.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
            this.leaf2.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }
    }
}
