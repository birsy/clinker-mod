package birsy.clinker.client.render.world.blockentity;

import birsy.clinker.common.block.blockentity.MortarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class MortarRenderer<T extends MortarBlockEntity> implements BlockEntityRenderer<T> {
    private final ItemRenderer itemRenderer;
    private static final Vector3fc[] randomOffsets = Util.make(() -> {
        Vector3fc[] result = new Vector3fc[64];
        RandomSource randomSource = RandomSource.create(80085);
        for (int i = 0; i < result.length; i++)
            result[i] = new Vector3f(
                    (float) randomSource.triangle(0, 1),
                    (float) randomSource.triangle(0, 1),
                    (float) randomSource.triangle(0, 1)
            );
        return result;
    });
    public MortarRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(T blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 2.0 / 16.0, 0.5);

        float recipeProgress = blockEntity.getRecipeProgress(partialTick);
        float inverseRecipeProgress = 1.0F - recipeProgress;

        ItemStack result = blockEntity.result;
        if (!result.isEmpty()) {
            int count = Math.min(result.getCount(), 8);
            for (int i = 0; i < count; i++) {
                poseStack.pushPose();
                Vector3fc offset = randomOffsets[i];
                poseStack.translate(offset.x() * 0.12, offset.y() * 0.02, offset.z() * 0.12);
                poseStack.scale(recipeProgress, recipeProgress, recipeProgress);
                itemRenderer.renderStatic(result, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
                poseStack.popPose();
            }
        }

        poseStack.mulPose(Axis.YP.rotationDegrees(45 + blockEntity.getRotation(partialTick)));
        NonNullList<ItemStack> ingredients = blockEntity.ingredients;
        for (int i = 0; i < ingredients.size(); i++) {
            ItemStack ingredient = ingredients.get(i);
            if (ingredient.isEmpty()) continue;

            poseStack.pushPose();
            poseStack.mulPose(Axis.YP.rotationDegrees(90 * i));
            int awfulSeed = ingredient.getItem().toString().length();
            float randomX = Mth.sin(i * 100 + awfulSeed) * 0.02F, randomY = Mth.abs(Mth.sin(i * 200 + awfulSeed)) * 0.03F;
            poseStack.translate(randomX, randomY, 2.3 / 16.0);
            poseStack.mulPose(Axis.XP.rotationDegrees(20));
            poseStack.scale(inverseRecipeProgress, inverseRecipeProgress, inverseRecipeProgress);
            itemRenderer.renderStatic(ingredient, ItemDisplayContext.GROUND, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
            poseStack.popPose();
        }
        poseStack.popPose();
    }
}
