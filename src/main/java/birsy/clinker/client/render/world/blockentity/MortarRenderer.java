package birsy.clinker.client.render.world.blockentity;

import birsy.clinker.common.world.block.blockentity.MortarBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
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
    public void render(T pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        pPoseStack.pushPose();
        pPoseStack.translate(0.5, 2.0 / 16.0, 0.5);
        pPoseStack.mulPose(Axis.YP.rotationDegrees(45));
        NonNullList<ItemStack> ingredients = pBlockEntity.ingredients;
        for (int i = 0; i < ingredients.size(); i++) {
            ItemStack ingredient = ingredients.get(i);
            if (ingredient.isEmpty()) continue;

            pPoseStack.mulPose(Axis.YP.rotationDegrees(90));
            pPoseStack.pushPose();
            int awfulSeed = ingredient.getItem().toString().length();
            float randomX = Mth.sin(i * 100 + awfulSeed) * 0.02F, randomY = Mth.abs(Mth.sin(i * 200 + awfulSeed)) * 0.03F;
            pPoseStack.translate(randomX, randomY, 2.3 / 16.0);
            pPoseStack.mulPose(Axis.XP.rotationDegrees(20));
            itemRenderer.renderStatic(ingredient, ItemDisplayContext.GROUND, pPackedLight, pPackedOverlay, pPoseStack, pBufferSource, pBlockEntity.getLevel(), 0);
            pPoseStack.popPose();
        }
        pPoseStack.popPose();
    }
}
