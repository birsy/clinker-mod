package birsy.clinker.client.render.world.item;

import birsy.clinker.client.entity.mogul.MogulWeaponModels;
import birsy.clinker.common.world.item.MogulWarhookItem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;



public class ClinkerBlockEntityWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {
    public static ClinkerBlockEntityWithoutLevelRenderer INSTANCE = new ClinkerBlockEntityWithoutLevelRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());

    public ClinkerBlockEntityWithoutLevelRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        Item item = pStack.getItem();
        if (item instanceof MogulWarhookItem) {
            pPoseStack.pushPose();
            float scale = 1.0F / 16.0F;
            pPoseStack.scale(scale, scale, scale);
            pPoseStack.translate(7.5, 5.5, 10);
            if (pDisplayContext.firstPerson()) pPoseStack.mulPose(Axis.XP.rotationDegrees(-15));
            MogulWeaponModels.WARHOOK.render(
                    pPoseStack,
                    ItemRenderer.getFoilBuffer(pBuffer, RenderType.entityCutoutNoCull(MogulWeaponModels.WARHOOK_TEXTURE_LOCATION), true, pStack.hasFoil()),
                    pPackedLight, OverlayTexture.NO_OVERLAY,
                    1, 1, 1, 1
            );
            pPoseStack.popPose();
        }
    }
}
