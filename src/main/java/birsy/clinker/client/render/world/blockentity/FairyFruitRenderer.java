package birsy.clinker.client.render.world.blockentity;

import birsy.clinker.client.render.ClinkerRenderTypes;
import birsy.clinker.client.render.entity.model.base.BasicModelPart;
import birsy.clinker.common.world.block.blockentity.fairyfruit.FairyFruitBlockEntity;
import birsy.clinker.common.world.block.blockentity.fairyfruit.FairyFruitJoint;
import birsy.clinker.common.world.block.blockentity.fairyfruit.FairyFruitSegment;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaterniond;
import org.joml.Quaternionf;
import org.joml.Vector3f;

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
    }

    @Override
    public boolean shouldRender(T pBlockEntity, Vec3 pCameraPos) {
        return BlockEntityRenderer.super.shouldRender(pBlockEntity, pCameraPos);
    }

    @Override
    public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if (pBlockEntity.segments.isEmpty()) return;
        Vec3 parentPosition = new Vec3(pBlockEntity.getBlockPos().getX(), pBlockEntity.getBlockPos().getY(), pBlockEntity.getBlockPos().getZ());
        Vec3 forward = FairyFruitBlockEntity.ORIENTATION_FORWARD;
        float totalLength = pBlockEntity.getBlockPos().hashCode() % Mth.PI;
        VertexConsumer consumer = pBufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        pPoseStack.pushPose();

        int fruitLight = 15;

        for (int i = 0; i < pBlockEntity.segments.size(); i++) {
            FairyFruitSegment segment = pBlockEntity.segments.get(i);
            if (segment.shouldBeRemoved) break;
            Vec3 topPos = segment.getTopJoint().getClientPosition(pPartialTick).subtract(parentPosition);
            Vec3 bottomPos = segment.getBottomJoint().getClientPosition(pPartialTick).subtract(parentPosition);
            //DebugRenderUtil.renderLine(pPoseStack, pBufferSource.getBuffer(RenderType.LINES), topPos.x(), topPos.y(), topPos.z(), bottomPos.x(), bottomPos.y(), bottomPos.z(), 1, 0, 1, 1);

            if (segment.isTip()) {
                FairyFruitJoint.FruitStage stage = segment.getBottomJoint().getFruitStage();
                consumer = pBufferSource.getBuffer(ClinkerRenderTypes.entityUnlitTranslucent(new ResourceLocation("")));

                pPoseStack.pushPose();

                Vec3 fruitPos = topPos;

                pPoseStack.translate(fruitPos.x(), fruitPos.y(), fruitPos.z());

                pPoseStack.pushPose();
                pPoseStack.mulPose(new Quaternionf(i > 1 ? pBlockEntity.segments.get(i - 1).getOrientation(pPartialTick, forward) : segment.getTopJoint().getOrientation(pPartialTick)));
                pPoseStack.scale(-1.0F, -1.0F, 1.0F);

                BlockPos bPos = pBlockEntity.getBlockPos().offset((int) (fruitPos.x() + 0.5), (int) fruitPos.y(), (int) (fruitPos.z() + 0.5));
                int light = stage == FairyFruitJoint.FruitStage.FRUIT ? LightTexture.pack(fruitLight, pBlockEntity.getLevel().getBrightness(LightLayer.SKY,bPos)) :
                        LightTexture.pack(pBlockEntity.getLevel().getBrightness(LightLayer.BLOCK, bPos), pBlockEntity.getLevel().getBrightness(LightLayer.SKY,bPos));

                leavesModel.bud.visible = false;
                this.leavesModel.leaf1.visible = false;
                this.leavesModel.leaf2.visible = false;
                this.leavesModel.root1.visible = true;
                this.leavesModel.root2.visible = true;
                leavesModel.renderToBuffer(pPoseStack, consumer, light, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
                pPoseStack.popPose();

                pPoseStack.pushPose();
                pPoseStack.mulPose(new Quaternionf(segment.getTopJoint().getOrientation(pPartialTick)));
                pPoseStack.scale(-1.0F, -1.0F, 1.0F);

                this.fruitModel.setVisibility(stage);
                fruitModel.renderToBuffer(pPoseStack, consumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                pPoseStack.popPose();

                pPoseStack.popPose();
            } else {
                boolean lowGlow = false;
                Quaterniond topOrientation = segment.getTopJoint().getOrientation(pPartialTick);
                Quaterniond bottomOrientation = segment.getBottomJoint().getOrientation(pPartialTick);
                if (i + 1 < pBlockEntity.segments.size()) {
                    FairyFruitSegment nextSegment = pBlockEntity.segments.get(i + 1);
                    lowGlow = nextSegment.isTip() && nextSegment.getBottomJoint().getFruitStage() == FairyFruitJoint.FruitStage.FRUIT;
                }
                drawVineBetweenPoints(pBlockEntity, topPos, new Quaternionf(topOrientation),
                        bottomPos, new Quaternionf(bottomOrientation),
                        totalLength, totalLength += segment.getLength(pPartialTick), consumer, pPoseStack, pPackedLight, pPackedOverlay, lowGlow, fruitLight);
            }
        }

        pPoseStack.popPose();

        /*for (FairyFruitBlockEntity.FairyFruitJoint joint : pBlockEntity.joints) {
            Vec3 jPos = joint.getClientPosition(pPartialTick).subtract(parentPosition);
            DebugRenderUtil.renderSphere(pPoseStack, pBufferSource.getBuffer(RenderType.LINES), 32, (float)joint.radius, jPos.x, jPos.y, jPos.z, 0.2F, 0.2F, 0.8F, 0.6F);
        }*/
    }

    private void drawVineBetweenPoints(T entity, Vec3 point1, Quaternionf point1Rotation, Vec3 point2, Quaternionf point2Rotation, float uvOffset0, float uvOffset1, VertexConsumer buffer, PoseStack stack, int packedLight, int packedOverlay, boolean lowGlow, int fruitLight) {
        Matrix4f matrix = stack.last().pose();
        float thickness = ((8.0F / 16.0F) * 0.5F) / Mth.sqrt(2);

        BlockPos samplePos1 = entity.getBlockPos().offset((int) point1.x(), (int) point1.y(), (int) point1.z());
        BlockPos samplePos2 = entity.getBlockPos().offset((int) point2.x(), (int) point2.y(), (int) point2.z());
        int light1 = packedLight;
        int light2 = packedLight;
        if (entity.hasLevel()) {
            light1 = LightTexture.pack(entity.getLevel().getBrightness(LightLayer.BLOCK, samplePos1), entity.getLevel().getBrightness(LightLayer.SKY, samplePos1));
            light2 = LightTexture.pack(lowGlow ? fruitLight : entity.getLevel().getBrightness(LightLayer.BLOCK, samplePos2), entity.getLevel().getBrightness(LightLayer.SKY, samplePos2));
        }

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

    public void renderFace(boolean orientation, Vec3 point1, Vector3f normal1, Quaternionf point1Rotation, Vec3 point2, Vector3f normal2, Quaternionf point2Rotation, float thickness, float uvOffset0, float uvOffset1, int light1, int light2, VertexConsumer buffer, Matrix4f matrix) {
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

    public static VertexConsumer vertex(VertexConsumer buffer, Matrix4f matrix, Vec3 base, float x, float y, float z, Quaternionf rotation) {
        Vector3f pos = new Vector3f(x, y, z);
        pos = rotation.transform(pos);
        return buffer.vertex(matrix, pos.x() + (float)base.x(), pos.y() + (float)base.y(), pos.z() + (float)base.z());
    }

    public static class FairyFruitModel extends Model {
        public final BasicModelPart fruit;
        public final BasicModelPart bud;
        public final BasicModelPart root1;
        public final BasicModelPart root2;

        public final BasicModelPart leaf1;
        public final BasicModelPart leaf2;

        public FairyFruitModel(Function<ResourceLocation, RenderType> pRenderType) {
            super(pRenderType);
            this.fruit = new BasicModelPart(BasicModelPart.toCubeList(CubeListBuilder.create().texOffs(8, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 7.0F, 6.0F), 32, 32));

            this.bud = new BasicModelPart(BasicModelPart.toCubeList(CubeListBuilder.create().texOffs(8, 22).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 5.0F, 4.0F), 32, 32));

            this.root1 = new BasicModelPart(BasicModelPart.toCubeList(CubeListBuilder.create().texOffs(26, 24).addBox(-5.0F, -8.0F, 0.0F, 4.0F, 5.0F, 0.0F), 32, 32));
            //this.root1.yRot = 0.7854F;
            //this.root1.yRotInit = 0.7854F;
            this.root2 = new BasicModelPart(BasicModelPart.toCubeList(CubeListBuilder.create().texOffs(26, 24).addBox(-5.0F, -8.0F, 0.0F, 4.0F, 5.0F, 0.0F), 32, 32));
            //this.root2.yRot = -0.7854F;
            //this.root2.yRotInit = -0.7854F;

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

        public void setVisibility(FairyFruitJoint.FruitStage stage) {
            this.fruit.visible = false;
            this.bud.visible   = false;
            this.root1.visible = false;
            this.root2.visible = false;

            switch (stage) {
                case ROOT:
                    this.root1.visible = true;
                    this.root2.visible = true;
                    break;
                case BUD:
                    this.bud.visible = true;
                    break;
                case FRUIT:
                    this.fruit.visible = true;
                    break;
            }
        }
    }
}
