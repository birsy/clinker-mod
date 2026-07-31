package birsy.clinker.client.render.world.blockentity;

import birsy.clinker.common.block.blockentity.MortarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class MortarRenderer<T extends MortarBlockEntity> implements BlockEntityRenderer<T> {
    private final ItemRenderer itemRenderer;
    public MortarRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 2.0 / 16.0, 0.5);

        poseStack.mulPose(Axis.YP.rotationDegrees(45 + blockEntity.getRotation(partialTick)));
        NonNullList<ItemStack> ingredients = blockEntity.ingredients;
        for (int i = 0; i < ingredients.size(); i++) {
            ItemStack ingredient = ingredients.get(i);
            if (ingredient.isEmpty()) continue;

            poseStack.mulPose(Axis.YP.rotationDegrees(90));
            poseStack.pushPose();
            int awfulSeed = ingredient.getItem().toString().length();
            float randomX = Mth.sin(i * 100 + awfulSeed) * 0.02F, randomY = Mth.abs(Mth.sin(i * 200 + awfulSeed)) * 0.03F;
            poseStack.translate(randomX, randomY, 2.3 / 16.0);
            poseStack.mulPose(Axis.XP.rotationDegrees(20));
            itemRenderer.renderStatic(ingredient, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}
