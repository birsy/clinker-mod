package birsy.clinker.client.render.world.blockentity;

import birsy.clinker.client.render.ClinkerRenderTypes;
import birsy.clinker.client.render.entity.model.base.BasicModelPart;
import birsy.clinker.common.world.block.blockentity.FairyFruitBlockEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Function;

public class FairyFruitRenderer<T extends FairyFruitBlockEntity> implements BlockEntityRenderer<T> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/block/fairy_fruit.png");
    public final FairyFruitModel leavesModel;
    public final FairyFruitModel fruitModel;
    private final Minecraft mc;
    public FairyFruitRenderer(BlockEntityRendererProvider.Context context) {
        super();
        this.mc = Minecraft.getInstance();
        this.leavesModel = new FairyFruitModel(RenderType::entityCutout);
        this.leavesModel.fruit.visible = false;
        this.fruitModel = new FairyFruitModel(ClinkerRenderTypes::entityTranslucentUnlit);
        this.fruitModel.leaf1.visible = false;
        this.fruitModel.leaf2.visible = false;
    }

    @Override
    public boolean shouldRender(T pBlockEntity, Vec3 pCameraPos) {
        Frustum frustum = mc.levelRenderer.cullingFrustum;
        return BlockEntityRenderer.super.shouldRender(pBlockEntity, pCameraPos) && frustum.isVisible(new AABB(pBlockEntity.getBlockPos(), pBlockEntity.getBlockPos().above(pBlockEntity.ropeHeight)).inflate(1.5));
    }

    @Override
    public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        List<Vec3> positions = pBlockEntity.locations;
        List<Vec3> pPositions = pBlockEntity.pLocations;

        if (positions.isEmpty()) return;

        // rendering the vine
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 0.5, 0.5);
        VertexConsumer buffer3 = pBufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        float totalLength = 0;
        for (int i = 1; i < positions.size(); i++) {
            Vec3 pos1 = pPositions.get(i - 1).lerp(positions.get(i - 1), pPartialTick);
            Vec3 pos2 = pPositions.get(i).lerp(positions.get(i), pPartialTick);

            Vec3 dir = pos2.subtract(pos1).normalize();

            Vec3 dir1 = dir;
            Vec3 dir2 = dir;

            // averaging current and previous/next directions to ensure no gaps in the rope...
            if (i - 1 > 0) {
                Vec3 pos0 = pPositions.get(0).lerp(positions.get(0), pPartialTick);
                dir1 = dir1.add(pos1.subtract(pos0).normalize()).scale(0.5).normalize();
            }
            if (i + 1 < positions.size()) {
                Vec3 pos3 = pPositions.get(i + 1).lerp(positions.get(i + 1), pPartialTick);
                dir2 = dir2.add(pos3.subtract(pos2).normalize()).scale(0.5).normalize();
            }

            drawVineBetweenPoints(pBlockEntity, pos1, dir1, pos2, dir2, totalLength, buffer3, pPoseStack, pPackedLight, pPackedOverlay);
            totalLength += pos1.distanceTo(pos2);
        }
        pPoseStack.popPose();

        // rendering the fruits
        Vec3 fruitPos = pPositions.get(positions.size() - 1).lerp(positions.get(positions.size() - 1), pPartialTick);
        Vec3 basePos = fruitPos;
        if (positions.size() > 1) basePos = pPositions.get(positions.size() - 2).lerp(positions.get(positions.size() - 2), pPartialTick);
        
        Vec3 direction = fruitPos.subtract(basePos).normalize();

        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 0.5, 0.5);

        pPoseStack.translate(fruitPos.x(), fruitPos.y(), fruitPos.z());

        Rotation rot = new Rotation(direction);
        pPoseStack.mulPose(Vector3f.ZP.rotation(rot.zRot));
        pPoseStack.mulPose(Vector3f.XP.rotation(rot.xRot));

        pPoseStack.scale(-1.0F, -1.0F, 1.0F);

        VertexConsumer buffer1 = pBufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        leavesModel.renderToBuffer(pPoseStack, buffer1, pPackedLight, pPackedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        VertexConsumer buffer2 = pBufferSource.getBuffer(ClinkerRenderTypes.entityTranslucentUnlit(TEXTURE));
        fruitModel.renderToBuffer(pPoseStack, buffer2, LightTexture.pack(15, 0),  OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        pPoseStack.popPose();
    }

    private void drawVineBetweenPoints(T entity, Vec3 point1, Vec3 point1Dir, Vec3 point2, Vec3 point2Dir, float uvOffset, VertexConsumer buffer, PoseStack stack, int packedLight, int packedOverlay) {
        Matrix4f matrix = stack.last().pose();
        float length = (float) point1.distanceTo(point2);
        float thickness = ((3.0F / 16.0F) * 0.5F) / Mth.sqrt(2);

        BlockPos samplePos1 = entity.getBlockPos().offset(point1.x() + 0.5, point1.y() - 1, point1.z() + 0.5);
        BlockPos samplePos2 = entity.getBlockPos().offset(point2.x() + 0.5, point2.y(), point2.z() + 0.5);
        int light1 = packedLight;
        int light2 = packedLight;
        if (entity.hasLevel()) {
            light1 = LightTexture.pack(entity.getLevel().getBrightness(LightLayer.BLOCK, samplePos1), entity.getLevel().getBrightness(LightLayer.SKY, samplePos1));
            light2 = LightTexture.pack(entity.getLevel().getBrightness(LightLayer.BLOCK, samplePos2), entity.getLevel().getBrightness(LightLayer.SKY, samplePos2));
        }

        Rotation rot1 = new Rotation(point1Dir);
        Rotation rot2 = new Rotation(point2Dir);

        float n = Mth.sqrt(2);
        Vector3f normal1 = new Vector3f(n, 0, n);
        normal1.transform(Vector3f.ZP.rotation(-rot1.zRot));
        normal1.transform(Vector3f.XP.rotation(-rot1.xRot));
        normal1.transform(stack.last().normal());

        Vector3f normal2 = new Vector3f(n, 0, n);
        normal2.transform(Vector3f.ZP.rotation(-rot2.zRot));
        normal2.transform(Vector3f.XP.rotation(-rot2.xRot));
        normal2.transform(stack.last().normal());

        // a little monstrous but it works and i will never have to touch this again!
        vertex(buffer, matrix, point1, -thickness, 0 , -thickness, rot1.xRot, rot1.zRot)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0, uvOffset * 0.5F)
                .overlayCoords(packedOverlay).uv2(light1)
                .normal(normal1.x(), normal1.y(), normal1.z()).endVertex();
        vertex(buffer, matrix, point1, thickness, 0, thickness, rot1.xRot, rot1.zRot)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv( (3.0F / 32.0F), uvOffset * 0.5F)
                .overlayCoords(packedOverlay).uv2(light1)
                .normal(normal1.x(), normal1.y(), normal1.z()).endVertex();
        vertex(buffer, matrix, point2, thickness, 0, thickness, rot2.xRot, rot2.zRot)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv( (3.0F / 32.0F), (uvOffset + length) * 0.5F)
                .overlayCoords(packedOverlay).uv2(light2)
                .normal(normal2.x(), normal2.y(), normal2.z()).endVertex();
        vertex(buffer, matrix, point2, -thickness, 0, -thickness, rot2.xRot, rot2.zRot)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv( 0, (uvOffset + length) * 0.5F)
                .overlayCoords(packedOverlay).uv2(light2)
                .normal(normal2.x(), normal2.y(), normal2.z()).endVertex();


        vertex(buffer, matrix, point1, thickness, 0 , -thickness, rot1.xRot, rot1.zRot)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0, uvOffset * 0.5F)
                .overlayCoords(packedOverlay).uv2(light1)
                .normal(normal1.x(), normal1.y(), normal1.z()).endVertex();
        vertex(buffer, matrix, point1, -thickness, 0, thickness, rot1.xRot, rot1.zRot)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv( (3.0F / 32.0F), uvOffset * 0.5F)
                .overlayCoords(packedOverlay).uv2(light1)
                .normal(normal1.x(), normal1.y(), normal1.z()).endVertex();
        vertex(buffer, matrix, point2, -thickness, 0, thickness, rot2.xRot, rot2.zRot)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv( (3.0F / 32.0F), (uvOffset + length) * 0.5F)
                .overlayCoords(packedOverlay).uv2(light2)
                .normal(normal2.x(), normal2.y(), normal2.z()).endVertex();
        vertex(buffer, matrix, point2, thickness, 0, -thickness, rot2.xRot, rot2.zRot)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv( 0, (uvOffset + length) * 0.5F)
                .overlayCoords(packedOverlay).uv2(light2)
                .normal(normal2.x(), normal2.y(), normal2.z()).endVertex();
    }

    public static VertexConsumer vertex(VertexConsumer buffer, Matrix4f matrix, Vec3 base, float x, float y, float z, float xRot, float zRot) {
        Vector3f pos = new Vector3f(x, y, z);
        pos.transform(Vector3f.ZP.rotation(-zRot));
        pos.transform(Vector3f.XP.rotation(-xRot));
        return buffer.vertex(matrix, pos.x() + (float)base.x(), pos.y() + (float)base.y(), pos.z() + (float)base.z());
    }

    record Rotation(float xRot, float zRot) {
        public Rotation(Vec3 direction) {
            this((float) -Mth.atan2(direction.z(), direction.y()), (float) -Mth.atan2(direction.x(), direction.y()));
        }
    }

    public static class FairyFruitModel extends Model {
        public final BasicModelPart fruit;
        public final BasicModelPart leaf1;
        public final BasicModelPart leaf2;
        public FairyFruitModel(Function<ResourceLocation, RenderType> pRenderType) {
            super(pRenderType);
            this.fruit = new BasicModelPart(BasicModelPart.toCubeList(CubeListBuilder.create().texOffs(3, 0).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 7.0F, 6.0F), 32, 32));
            this.leaf1 = new BasicModelPart(BasicModelPart.toCubeList(CubeListBuilder.create().texOffs(3, 13).addBox(-5.0F, -8.0F, 0.0F, 10.0F, 9.0F, 0.0F), 32, 32));
            this.leaf2 = new BasicModelPart(BasicModelPart.toCubeList(CubeListBuilder.create().texOffs(3, 3).addBox(0.0F, -8.0F, -5.0F, 0.0F, 9.0F, 10.0F), 32, 32));
        }

        @Override
        public void renderToBuffer(PoseStack pPoseStack, VertexConsumer pBuffer, int pPackedLight, int pPackedOverlay, float pRed, float pGreen, float pBlue, float pAlpha) {
            this.fruit.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
            this.leaf1.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
            this.leaf2.render(pPoseStack, pBuffer, pPackedLight, pPackedOverlay, pRed, pGreen, pBlue, pAlpha);
        }
    }
}
