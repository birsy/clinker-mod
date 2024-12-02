package birsy.clinker.client.render.world.blockentity;

import birsy.clinker.client.render.BasicModelPart;
import birsy.clinker.client.render.ClinkerRenderTypes;
import birsy.clinker.client.render.RenderUtils;
import birsy.clinker.common.world.block.blockentity.fairyfruit.FairyFruitBlockEntity;
import birsy.clinker.common.world.block.blockentity.fairyfruit.FairyFruitVine;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.JomlConversions;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.function.Function;

public class FairyFruitRenderer<T extends FairyFruitBlockEntity> implements BlockEntityRenderer<T> {
    public static final ResourceLocation TEXTURE = Clinker.resource("textures/block/fairy_fruit.png");
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
    public AABB getRenderBoundingBox(T blockEntity) {
        if (blockEntity.vine == null) return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity);
        if (blockEntity.vine.segments.isEmpty()) return BlockEntityRenderer.super.getRenderBoundingBox(blockEntity);
        return new AABB(blockEntity.vine.origin, blockEntity.vine.getEndPosition()).inflate(2);
    }

    @Override
    public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if (pBlockEntity.vine == null) return;
        if (pBlockEntity.vine.segments.isEmpty()) return;

        pBlockEntity.light.setPosition(JomlConversions.toJOML(pBlockEntity.vine.getEndPosition()));
        pBlockEntity.light.setRadius(10);

        Vec3 parentPosition = new Vec3(pBlockEntity.getBlockPos().getX(), pBlockEntity.getBlockPos().getY(), pBlockEntity.getBlockPos().getZ());
        List<FairyFruitVine.FairyFruitVineSegment> segments = pBlockEntity.vine.segments;

        float totalDistance = pBlockEntity.lengthOffset;
        Vec3 previousSegmentPosition = pBlockEntity.vine.origin.subtract(parentPosition);

        Vector3f[] biTangents = {new Vector3f(1, 0, 1).normalize(), new Vector3f(1, 0, -1).normalize()};
        for (int i = 0; i < segments.size(); i++) {
            FairyFruitVine.FairyFruitVineSegment segment = segments.get(i);
            Vec3 segmentPosition = segment.getPosition(pPartialTick).subtract(parentPosition);
            Vec3 nextSegmentPosition;
            if (i == segments.size() - 1) {
                nextSegmentPosition = segmentPosition.add(segmentPosition.subtract(previousSegmentPosition));
            } else {
                nextSegmentPosition = segments.get(i + 1).getPosition(pPartialTick).subtract(parentPosition);
            }

            Vector3f tangent = previousSegmentPosition.subtract(segmentPosition).normalize().toVector3f();
            Quaternionf rotation = new Quaternionf().rotateX((float) Mth.atan2(tangent.z, tangent.y)).rotateZ((float) -Mth.atan2(tangent.x, tangent.y));

            Vector3f biTangent1 = biTangents[0].rotate(rotation, new Vector3f());
            Vector3f normal1 = tangent.cross(biTangent1, new Vector3f()).normalize();
            Vector3f biTangent2 = biTangents[1].rotate(rotation, new Vector3f());
            Vector3f normal2 = tangent.cross(biTangent2, new Vector3f()).normalize();

            Vector3f nextTangent = nextSegmentPosition.subtract(segmentPosition).normalize().toVector3f();
            Quaternionf nextRotation = new Quaternionf().rotateX((float) Mth.atan2(nextTangent.z, nextTangent.y)).rotateZ((float) Mth.atan2(nextTangent.x, nextTangent.y));
            Vector3f nextBiTangent1 = biTangents[0].rotate(nextRotation, new Vector3f()).mul(-1);
            Vector3f nextNormal1 = nextTangent.cross(nextBiTangent1, new Vector3f()).normalize().mul(-1);
            Vector3f nextBiTangent2 = biTangents[1].rotate(nextRotation, new Vector3f()).mul(-1);
            Vector3f nextNormal2 = nextTangent.cross(nextBiTangent2, new Vector3f()).normalize().mul(-1);

            int light = LightTexture.pack( pBlockEntity.getLevel().getBrightness(LightLayer.BLOCK, BlockPos.containing(segmentPosition.add(parentPosition))),
                    pBlockEntity.getLevel().getBrightness(LightLayer.SKY, BlockPos.containing(segmentPosition.add(parentPosition))));
            int nextLight = LightTexture.pack( pBlockEntity.getLevel().getBrightness(LightLayer.BLOCK, BlockPos.containing(nextSegmentPosition.add(parentPosition))),
                    pBlockEntity.getLevel().getBrightness(LightLayer.SKY, BlockPos.containing(nextSegmentPosition.add(parentPosition))));
            if (i == segments.size() - 1) nextLight = LightTexture.pack(15, pBlockEntity.getLevel().getBrightness(LightLayer.SKY, BlockPos.containing(segmentPosition.add(parentPosition))));

            float distanceBetweenPoints = (float) previousSegmentPosition.distanceTo(segmentPosition);

            float v1 = (totalDistance) * 0.5F;
            float v2 = (totalDistance + distanceBetweenPoints) * 0.5F;
            totalDistance += distanceBetweenPoints;

            RenderUtils.drawFaceBetweenPoints(pBufferSource.getBuffer(ClinkerRenderTypes.entityUnlitCutout(TEXTURE)), pPoseStack, 0.25F * Mth.SQRT_OF_TWO,
                    previousSegmentPosition.toVector3f(), tangent, normal1, biTangent1, light, OverlayTexture.NO_OVERLAY, 0, v1,
                    segmentPosition.toVector3f(), nextTangent, nextNormal1, nextBiTangent1, nextLight, OverlayTexture.NO_OVERLAY, 0.25F, v2);
            RenderUtils.drawFaceBetweenPoints(pBufferSource.getBuffer(ClinkerRenderTypes.entityUnlitCutout(TEXTURE)), pPoseStack, 0.25F,
                    previousSegmentPosition.toVector3f(), tangent, normal1.mul(-1), biTangent1.mul(-1), light, OverlayTexture.NO_OVERLAY, 0, v1,
                    segmentPosition.toVector3f(), nextTangent, nextNormal1.mul(-1), nextBiTangent1.mul(-1), nextLight, OverlayTexture.NO_OVERLAY, 0.25F, v2);

            RenderUtils.drawFaceBetweenPoints(pBufferSource.getBuffer(ClinkerRenderTypes.entityUnlitCutout(TEXTURE)), pPoseStack, 0.25F * Mth.SQRT_OF_TWO,
                    previousSegmentPosition.toVector3f(), tangent, normal2, biTangent2, light, OverlayTexture.NO_OVERLAY, 0, v1,
                    segmentPosition.toVector3f(), nextTangent, nextNormal2, nextBiTangent2, nextLight, OverlayTexture.NO_OVERLAY, 0.25F, v2);
            RenderUtils.drawFaceBetweenPoints(pBufferSource.getBuffer(ClinkerRenderTypes.entityUnlitCutout(TEXTURE)), pPoseStack, 0.25F,
                    previousSegmentPosition.toVector3f(), tangent, normal2.mul(-1), biTangent2.mul(-1), light, OverlayTexture.NO_OVERLAY, 0, v1,
                    segmentPosition.toVector3f(), nextTangent, nextNormal2.mul(-1), nextBiTangent2.mul(-1), nextLight, OverlayTexture.NO_OVERLAY, 0.25F, v2);

            previousSegmentPosition = segmentPosition;
        }

        Vec3 previousPos;
        if (segments.size() > 2) {
            previousPos = segments.get(segments.size() - 2).getPosition(pPartialTick).subtract(parentPosition);
        } else {
            previousPos = pBlockEntity.vine.origin.subtract(parentPosition);
        }
        Vec3 fruitPos = segments.get(segments.size() - 1).getPosition(pPartialTick).subtract(parentPosition);
        pPoseStack.pushPose();
        pPoseStack.translate(fruitPos.x(), fruitPos.y(), fruitPos.z());
        pPoseStack.mulPose(Axis.XP.rotationDegrees(180));
        Quaternionf rotation = new Quaternionf().rotateTo(new Vector3f(0, -1, 0), fruitPos.subtract(previousPos).multiply(-1, 1, 1).normalize().toVector3f());
        pPoseStack.mulPose(rotation);
        int packedFruitLight = LightTexture.pack(15, pBlockEntity.getLevel().getBrightness(LightLayer.SKY, BlockPos.containing(fruitPos.add(parentPosition))));
        this.leavesModel.renderToBuffer(pPoseStack, pBufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE)), packedFruitLight, pPackedOverlay, 1, 1, 1, 1);
        pPoseStack.mulPose(rotation.slerp(new Quaternionf(), 0.5F));
        pPoseStack.translate(0, 0.03, 0);
        this.fruitModel.bud.visible = false;
        this.fruitModel.renderToBuffer(pPoseStack, pBufferSource.getBuffer(ClinkerRenderTypes.entityUnlitTranslucent(TEXTURE)), packedFruitLight, pPackedOverlay, 1, 1, 1, 0.8F);
        pPoseStack.popPose();
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
            this.root2 = new BasicModelPart(BasicModelPart.toCubeList(CubeListBuilder.create().texOffs(26, 24).addBox(-5.0F, -8.0F, 0.0F, 4.0F, 5.0F, 0.0F), 32, 32));

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
