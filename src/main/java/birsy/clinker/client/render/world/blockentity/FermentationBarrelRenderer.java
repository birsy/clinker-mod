package birsy.clinker.client.render.world.blockentity;

import birsy.clinker.common.world.block.FermentationBarrelBlock;
import birsy.clinker.common.world.block.blockentity.FermentationBarrelBlockEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

public class FermentationBarrelRenderer<T extends FermentationBarrelBlockEntity> implements BlockEntityRenderer<T> {
    public static final ResourceLocation TEXTURE = Clinker.resource("textures/block/fermentation_barrel/fermentation_barrel_lid.png");
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(Clinker.resource("fermentation_barrel"), "main");
    private final ModelPart lid;

    public FermentationBarrelRenderer(BlockEntityRendererProvider.Context context) {
        ModelPart modelpart = context.bakeLayer(LAYER_LOCATION);
        this.lid = modelpart.getChild("lid");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("lid", CubeListBuilder.create().texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 12.0F, 1.0F, 12.0F), PartPose.offset(0.0F, 15.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 64, 16);
    }

        @Override
    public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.mulPose(Axis.ZP.rotation((float) Math.PI));

        pPoseStack.translate(-1, -1.9375, 0);
        pPoseStack.translate(0.5, 0, 0.5);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(pBlockEntity.getBlockState().getValue(FermentationBarrelBlock.FACING).toYRot()));
        pPoseStack.translate(-0.5 + 0.125, 0, -0.5 + 0.125);
        lid.xRot = pBlockEntity.getOpenNess(pPartialTick);
        lid.render(pPoseStack, pBufferSource.getBuffer(RenderType.entitySolid(TEXTURE)), pPackedLight, pPackedOverlay);
        pPoseStack.popPose();
    }
}
