package birsy.clinker.client.render.world.blockentity;

import birsy.clinker.common.world.block.blockentity.StoveBlockEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.ChestType;

public class StoveRenderer<T extends StoveBlockEntity> implements BlockEntityRenderer<T> {
    public static final ResourceLocation STOVE = Clinker.resource("textures/block/stove.png");
    public static final ResourceLocation STOVE_CHIMNEY = Clinker.resource("textures/block/stove_chimney.png");
    public static final ResourceLocation DOUBLE_STOVE = Clinker.resource("textures/block/stove_double.png");
    public static final ResourceLocation DOUBLE_STOVE_CHIMNEY = Clinker.resource("textures/block/stove_chimney_double.png");

    public static final ResourceLocation STOVE_LIT = Clinker.resource("textures/block/stove_lit.png");
    public static final ResourceLocation STOVE_CHIMNEY_LIT = Clinker.resource("textures/block/stove_chimney_lit.png");
    public static final ResourceLocation DOUBLE_STOVE_LIT = Clinker.resource("textures/block/stove_double_lit.png");
    public static final ResourceLocation DOUBLE_STOVE_CHIMNEY_LIT = Clinker.resource("textures/block/stove_chimney_double_lit.png");

    public static final ResourceLocation STOVE_INNER_LIT = Clinker.resource("textures/block/stove_inner_lit.png");
    public static final ResourceLocation DOUBLE_STOVE_INNER_LIT = Clinker.resource("textures/block/stove_double_inner_lit.png");


    private final StoveModel stove;
    private final StoveChimneyModel chimney;
    private final DoubleStoveModel doubleStove;
    private final DoubleStoveChimneyModel doubleChimney;

    public StoveRenderer(BlockEntityRendererProvider.Context context) {
        this.stove = new StoveModel(context.bakeLayer(StoveModel.LAYER_LOCATION));
        this.chimney = new StoveChimneyModel(context.bakeLayer(StoveChimneyModel.LAYER_LOCATION));
        this.doubleStove = new DoubleStoveModel(context.bakeLayer(DoubleStoveModel.LAYER_LOCATION));
        this.doubleChimney = new DoubleStoveChimneyModel(context.bakeLayer(DoubleStoveChimneyModel.LAYER_LOCATION));
    }

    @Override
    public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();

        pPoseStack.mulPose(Axis.ZP.rotation(Mth.PI));
        pPoseStack.translate(0, -1.5, 0);
        pPoseStack.translate(-0.5, 0.0, 0.5);

        this.renderStove(pPoseStack, pBufferSource, pPackedLight, pPackedOverlay, pBlockEntity.type, pBlockEntity.facingDirection, true, true   , 1);
        this.renderStove(pPoseStack, pBufferSource, pPackedLight, pPackedOverlay, pBlockEntity.type, pBlockEntity.facingDirection, false, true, .3F);

        pPoseStack.popPose();
    }

    public void renderStove(PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay, ChestType type, Direction direction, boolean onlyLip, boolean flames, float flameAlpha) {
        float alpha = flames ? flameAlpha : 1.0f;
        float brightAlpha = 1 - alpha * 0.8F;
        int light = flames ? LightTexture.pack(16, 16) : pPackedLight;
        pPoseStack.pushPose();

        pPoseStack.mulPose(Axis.YP.rotationDegrees(90 * (direction.get2DDataValue() + 2)));
        VertexConsumer vertexconsumer;
        int flameColor = FastColor.ARGB32.colorFromFloat(alpha, alpha, alpha, alpha);
        int flameColorBright = FastColor.ARGB32.colorFromFloat(brightAlpha, brightAlpha, brightAlpha, brightAlpha);
        if (type == ChestType.SINGLE) {
            this.chimney.chimney.visible = !onlyLip;
            this.stove.stove.visible = !onlyLip;

            vertexconsumer = flames ? pBufferSource.getBuffer(RenderType.energySwirl(STOVE_LIT, 0, 0)) : pBufferSource.getBuffer(RenderType.entityCutoutNoCull(STOVE));
            this.stove.renderToBuffer(pPoseStack, vertexconsumer, light, pPackedOverlay, flameColor);
            if (flames) {
                vertexconsumer = pBufferSource.getBuffer(RenderType.energySwirl(STOVE_INNER_LIT, 0, 0));
                this.stove.renderToBuffer(pPoseStack, vertexconsumer, light, pPackedOverlay, flameColorBright);
            }
            pPoseStack.translate(0, -1, 0);
            vertexconsumer = flames ? pBufferSource.getBuffer(RenderType.energySwirl(STOVE_CHIMNEY_LIT, 0, 0)) : pBufferSource.getBuffer(RenderType.entityCutoutNoCull(STOVE_CHIMNEY));
            alpha *= flames ? 0.5 : 1;
            flameColor = FastColor.ARGB32.colorFromFloat(alpha, alpha, alpha, alpha);
            this.chimney.renderToBuffer(pPoseStack, vertexconsumer, light, pPackedOverlay, flameColor);
        } else {
            if (type == ChestType.LEFT) pPoseStack.translate(-1, 0, 0);
            this.doubleChimney.chimney.visible = !onlyLip;
            this.doubleStove.stove.visible = !onlyLip;

            vertexconsumer = flames ? pBufferSource.getBuffer(RenderType.energySwirl(DOUBLE_STOVE_LIT, 0, 0)) : pBufferSource.getBuffer(RenderType.entityCutoutNoCull(DOUBLE_STOVE));
            this.doubleStove.renderToBuffer(pPoseStack, vertexconsumer, light, pPackedOverlay, flameColor);
            if (flames) {
                vertexconsumer = pBufferSource.getBuffer(RenderType.energySwirl(DOUBLE_STOVE_INNER_LIT, 0, 0));
                this.doubleStove.renderToBuffer(pPoseStack, vertexconsumer, light, pPackedOverlay, flameColorBright);
            }
            pPoseStack.translate(0, -1, 0);
            vertexconsumer = flames ? pBufferSource.getBuffer(RenderType.energySwirl(DOUBLE_STOVE_CHIMNEY_LIT, 0, 0)) : pBufferSource.getBuffer(RenderType.entityCutoutNoCull(DOUBLE_STOVE_CHIMNEY));
            alpha *= flames ? 0.5 : 1;
            flameColor = FastColor.ARGB32.colorFromFloat(alpha, alpha, alpha, alpha);
            this.doubleChimney.renderToBuffer(pPoseStack, vertexconsumer, light, pPackedOverlay, flameColor);
        }

        pPoseStack.popPose();
    }

    public class StoveModel extends Model {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Clinker.resource("stove"), "main");
        private final ModelPart stove;
        private final ModelPart lip;

        public StoveModel(ModelPart root) {
            super(RenderType::entitySolid);
            this.stove = root.getChild("stove");
            this.lip = root.getChild("lip");
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition stove = partdefinition.addOrReplaceChild("stove", CubeListBuilder.create(), PartPose.offset(8.0F, 24.0F, -8.0F));
            PartDefinition floor = stove.addOrReplaceChild("floor", CubeListBuilder.create(), PartPose.offset(-8.0F, 0.0F, 8.0F));
            PartDefinition cube_r1 = floor.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 25).addBox(-16.0F, -16.0F, 0.0F, 16.0F, 16.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(8.0F, -3.0F, -8.0F, -1.5708F, 0.0F, 0.0F));
            PartDefinition top = stove.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 5).addBox(-16.0F, -16.0F, 0.0F, 16.0F, 6.0F, 4.0F, new CubeDeformation(0.01F))
                    .texOffs(0, 15).addBox(-16.0F, -16.0F, 12.0F, 16.0F, 6.0F, 4.0F, new CubeDeformation(0.01F))
                    .texOffs(38, 39).addBox(-16.0F, -16.0F, 4.0F, 5.0F, 6.0F, 8.0F, new CubeDeformation(0.01F))
                    .texOffs(38, 25).addBox(-5.0F, -16.0F, 4.0F, 5.0F, 6.0F, 8.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 0.0F, 0.0F));
            PartDefinition walls = stove.addOrReplaceChild("walls", CubeListBuilder.create().texOffs(36, 0).addBox(-14.0F, -10.0F, 14.0F, 12.0F, 7.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 0.0F, 0.0F));
            PartDefinition rightwall = walls.addOrReplaceChild("rightwall", CubeListBuilder.create(), PartPose.offset(-8.0F, 0.0F, 8.0F));
            PartDefinition cube_r2 = rightwall.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 54).addBox(-16.0F, -10.0F, -16.0F, 16.0F, 7.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(8.0F, 0.0F, -8.0F, 0.0F, 1.5708F, 0.0F));
            PartDefinition leftwall = walls.addOrReplaceChild("leftwall", CubeListBuilder.create(), PartPose.offset(-8.0F, 0.0F, 8.0F));
            PartDefinition cube_r3 = leftwall.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 44).mirror().addBox(-16.0F, -10.0F, -3.0F, 16.0F, 7.0F, 3.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(8.0F, 0.0F, -8.0F, 0.0F, 1.5708F, 0.0F));
            PartDefinition lip = partdefinition.addOrReplaceChild("lip", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -3.0F, -10.0F, 16.0F, 3.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 24.0F, 0.0F));

            return LayerDefinition.create(meshdefinition, 64, 64);
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, int color) {
            stove.render(poseStack, buffer, packedLight, packedOverlay, color);
            lip.render(poseStack, buffer, packedLight, packedOverlay, color);
        }
    }
    public class StoveChimneyModel extends Model {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Clinker.resource("chimney"), "main");
        private final ModelPart chimney;

        public StoveChimneyModel(ModelPart root) {
            super(RenderType::entitySolid);
            this.chimney = root.getChild("chimney");
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition chimney = partdefinition.addOrReplaceChild("chimney", CubeListBuilder.create().texOffs(34, 0).addBox(-15.0F, -16.0F, 4.0F, 4.0F, 16.0F, 8.0F, new CubeDeformation(0.01F))
                    .texOffs(34, 24).addBox(-5.0F, -16.0F, 4.0F, 4.0F, 16.0F, 8.0F, new CubeDeformation(0.01F))
                    .texOffs(0, 19).addBox(-15.0F, -16.0F, 12.0F, 14.0F, 16.0F, 3.0F, new CubeDeformation(0.01F))
                    .texOffs(0, 0).addBox(-15.0F, -16.0F, 1.0F, 14.0F, 16.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offset(8.0F, 24.0F, -8.0F));

            return LayerDefinition.create(meshdefinition, 64, 64);
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
            chimney.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        }
    }
    public static class DoubleStoveModel extends Model {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Clinker.resource("doublestove"), "main");
        private final ModelPart stove;
        private final ModelPart lip;

        public DoubleStoveModel(ModelPart root) {
            super(RenderType::entitySolid);
            this.stove = root.getChild("stove");
            this.lip = root.getChild("lip");
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition stove = partdefinition.addOrReplaceChild("stove", CubeListBuilder.create().texOffs(68, 0).addBox(-5.0F, -10.0F, 6.0F, 26.0F, 7.0F, 2.0F, new CubeDeformation(0.01F))
                    .texOffs(0, 5).addBox(-8.0F, -16.0F, -8.0F, 32.0F, 6.0F, 4.0F, new CubeDeformation(0.01F))
                    .texOffs(0, 15).addBox(-8.0F, -16.0F, 4.0F, 32.0F, 6.0F, 4.0F, new CubeDeformation(0.01F))
                    .texOffs(72, 23).addBox(-8.0F, -16.0F, -4.0F, 5.0F, 6.0F, 8.0F, new CubeDeformation(0.01F))
                    .texOffs(72, 9).addBox(19.0F, -16.0F, -4.0F, 5.0F, 6.0F, 8.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 24.0F, 0.0F));
            PartDefinition cube_r1 = stove.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 54).addBox(-16.0F, -10.0F, 0.0F, 16.0F, 7.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(-8.0F, 0.0F, -8.0F, 0.0F, 1.5708F, 0.0F));
            PartDefinition cube_r2 = stove.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 44).mirror().addBox(-16.0F, -10.0F, 13.0F, 16.0F, 7.0F, 3.0F, new CubeDeformation(0.01F)).mirror(false), PartPose.offsetAndRotation(8.0F, 0.0F, -8.0F, 0.0F, 1.5708F, 0.0F));
            PartDefinition cube_r3 = stove.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 25).addBox(-16.0F, -16.0F, -3.0F, 32.0F, 16.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offsetAndRotation(8.0F, 0.0F, -8.0F, -1.5708F, 0.0F, 0.0F));
            PartDefinition lip = partdefinition.addOrReplaceChild("lip", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -3.0F, -10.0F, 32.0F, 3.0F, 2.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, 24.0F, 0.0F));

            return LayerDefinition.create(meshdefinition, 128, 64);
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
            stove.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
            lip.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        }
    }
    public static class DoubleStoveChimneyModel extends Model {
        public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Clinker.resource("doublestovechimney"), "main");
        private final ModelPart chimney;

        public DoubleStoveChimneyModel(ModelPart root) {
            super(RenderType::entitySolid);
            this.chimney = root.getChild("chimney");
        }

        public static LayerDefinition createBodyLayer() {
            MeshDefinition meshdefinition = new MeshDefinition();
            PartDefinition partdefinition = meshdefinition.getRoot();

            PartDefinition chimney = partdefinition.addOrReplaceChild("chimney", CubeListBuilder.create().texOffs(66, 0).addBox(-15.0F, -16.0F, 4.0F, 4.0F, 16.0F, 8.0F, new CubeDeformation(0.01F))
                    .texOffs(66, 24).addBox(11.0F, -16.0F, 4.0F, 4.0F, 16.0F, 8.0F, new CubeDeformation(0.01F))
                    .texOffs(0, 19).addBox(-15.0F, -16.0F, 12.0F, 30.0F, 16.0F, 3.0F, new CubeDeformation(0.01F))
                    .texOffs(0, 0).addBox(-15.0F, -16.0F, 1.0F, 30.0F, 16.0F, 3.0F, new CubeDeformation(0.01F)), PartPose.offset(8.0F, 24.0F, -8.0F));

            return LayerDefinition.create(meshdefinition, 128, 64);
        }

        @Override
        public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, int color) {
            chimney.render(poseStack, vertexConsumer, packedLight, packedOverlay, color);
        }
    }
}
