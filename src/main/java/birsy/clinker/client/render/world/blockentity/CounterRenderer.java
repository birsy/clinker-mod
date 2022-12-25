package birsy.clinker.client.render.world.blockentity;

import birsy.clinker.common.alchemy.workstation.PhyiscalItem;
import birsy.clinker.common.block.FermentationBarrelBlock;
import birsy.clinker.common.blockentity.CounterBlockEntity;
import birsy.clinker.common.blockentity.FermentationBarrelBlockEntity;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

public class CounterRenderer<T extends CounterBlockEntity> implements BlockEntityRenderer<T> {
    private final ItemRenderer itemRenderer;
    public CounterRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        int items = 0;
        for (PhyiscalItem item : pBlockEntity.workstation.getItems()) {
            pPoseStack.pushPose();
            float scale = 0.5F;
            scale += (items * 12.324232) % 0.1;
            Vec3 position = item.getPosition(pPartialTick).subtract(pBlockEntity.getBlockPos().getX(), pBlockEntity.getBlockPos().getY(), pBlockEntity.getBlockPos().getZ());
            pPoseStack.translate(position.x, position.y, position.z);
            pPoseStack.scale(scale, scale, scale);
            pPoseStack.translate(0, 0, 0);
            pPoseStack.mulPose(Vector3f.YP.rotationDegrees(items * 3312.53829F));

            int lightColor = 15728880;
            if (pBlockEntity.getLevel() != null) {
                lightColor = LevelRenderer.getLightColor(pBlockEntity.getLevel(), new BlockPos(item.getPosition(pPartialTick).x(), item.getPosition(pPartialTick).y() + 0.1, item.getPosition(pPartialTick).z()));
            }

            itemRenderer.renderStatic(item.asItemStack(), ItemTransforms.TransformType.GROUND, lightColor, pPackedOverlay, pPoseStack, pBufferSource, items + ItemTransforms.TransformType.GROUND.ordinal());

            pPoseStack.popPose();
            items++;
        }
    }
}
