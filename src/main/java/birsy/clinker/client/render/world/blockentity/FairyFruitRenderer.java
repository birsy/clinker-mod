package birsy.clinker.client.render.world.blockentity;

import birsy.clinker.client.render.ClinkerRenderTypes;
import birsy.clinker.client.render.entity.model.base.BasicModelPart;
import birsy.clinker.common.blockentity.FairyFruitBlockEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
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
        this.fruitModel = new FairyFruitModel(ClinkerRenderTypes::entityTranslucentUnlit);
        this.fruitModel.leaf1.visible = false;
        this.fruitModel.leaf2.visible = false;
    }

    @Override
    public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        Vec3 fruitPos = pBlockEntity.pFruitLocation.lerp(pBlockEntity.fruitLocation, pPartialTick);
        Vec3 basePos = pBlockEntity.baseLocation.add(0, 0.5, 0);
        Vec3 dif = fruitPos.subtract(basePos);
        float length = (float) dif.length();
        Vec3 direction = dif.scale(1.0 / length);

        // rendering the fruits
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

        // rendering the vine
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 0.5, 0.5);

        VertexConsumer buffer3 = pBufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        drawVineBetweenPoints(pBlockEntity, basePos, direction, fruitPos, direction, buffer3, pPoseStack, pPackedLight, pPackedOverlay);
        pPoseStack.popPose();
    }

    private void drawVineBetweenPoints(T entity, Vec3 point1, Vec3 point1Dir, Vec3 point2, Vec3 point2Dir, VertexConsumer buffer, PoseStack stack, int packedLight, int packedOverlay) {
        Matrix4f matrix = stack.last().pose();
        float thickness = ((3.0F / 16.0F) * 0.5F) / Mth.sqrt(2);
        float length = (float) point1.distanceTo(point2);

        BlockPos samplePos1 = entity.getBlockPos().offset(point1.x() + 0.5, point1.y() + 0.5, point1.z() + 0.5);
        BlockPos samplePos2 = entity.getBlockPos().offset(point2.x() + 0.5, point2.y() + 0.5, point2.z() + 0.5);
        int light1 = packedLight;
        int light2 = packedLight;
        if (entity.hasLevel()) {
            light1 = LightTexture.pack(entity.getLevel().getBrightness(LightLayer.BLOCK, samplePos1), entity.getLevel().getBrightness(LightLayer.SKY, samplePos1));
            light2 = LightTexture.pack(entity.getLevel().getBrightness(LightLayer.BLOCK, samplePos2), entity.getLevel().getBrightness(LightLayer.SKY, samplePos2));
        }

        Rotation rot1 = new Rotation(point1Dir);
        Rotation rot2 = new Rotation(point2Dir);

        // a little monstrous but it works and i will never have to touch this again!
        vertex(buffer, matrix, point1, -thickness, 0 , -thickness, rot1.xRot, rot1.zRot)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0, 0)
                .overlayCoords(packedOverlay).uv2(light1)
                .normal(1, 0, 0).endVertex();
        vertex(buffer, matrix, point1, thickness, 0, thickness, rot1.xRot, rot1.zRot)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv( (3.0F / 32.0F), 0)
                .overlayCoords(packedOverlay).uv2(light1)
                .normal(1, 0, 0).endVertex();
        vertex(buffer, matrix, point2, thickness, 0, thickness, rot2.xRot, rot2.zRot)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv( (3.0F / 32.0F), length * 0.5F)
                .overlayCoords(packedOverlay).uv2(light2)
                .normal(1, 0, 0).endVertex();
        vertex(buffer, matrix, point2, -thickness, 0, -thickness, rot2.xRot, rot2.zRot)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv( 0, length * 0.5F)
                .overlayCoords(packedOverlay).uv2(light2)
                .normal(1, 0, 0).endVertex();

        vertex(buffer, matrix, point1, thickness, 0 , -thickness, rot1.xRot, rot1.zRot)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0, 0)
                .overlayCoords(packedOverlay).uv2(light1)
                .normal(1, 0, 0).endVertex();
        vertex(buffer, matrix, point1, -thickness, 0, thickness, rot1.xRot, rot1.zRot)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv( (3.0F / 32.0F), 0)
                .overlayCoords(packedOverlay).uv2(light1)
                .normal(1, 0, 0).endVertex();
        vertex(buffer, matrix, point2, -thickness, 0, thickness, rot2.xRot, rot2.zRot)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv( (3.0F / 32.0F), length * 0.5F)
                .overlayCoords(packedOverlay).uv2(light2)
                .normal(1, 0, 0).endVertex();
        vertex(buffer, matrix, point2, thickness, 0, -thickness, rot2.xRot, rot2.zRot)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv( 0, length * 0.5F)
                .overlayCoords(packedOverlay).uv2(light2)
                .normal(1, 0, 0).endVertex();
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
