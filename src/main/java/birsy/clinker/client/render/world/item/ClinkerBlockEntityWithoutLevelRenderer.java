package birsy.clinker.client.render.world.item;

import birsy.clinker.client.model.entity.MogulWarhookModel;
import birsy.clinker.common.world.item.MogulWarhookItem;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClinkerBlockEntityWithoutLevelRenderer extends BlockEntityWithoutLevelRenderer {
    public static ClinkerBlockEntityWithoutLevelRenderer INSTANCE = new ClinkerBlockEntityWithoutLevelRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());

    private static MogulWarhookModel.MogulWarhookSkeleton WARHOOK = MogulWarhookModel.skeleton;
    private static final ResourceLocation WARHOOK_LOCATION = new ResourceLocation(Clinker.MOD_ID, "textures/entity/mogul_warhook.png");

    public ClinkerBlockEntityWithoutLevelRenderer(BlockEntityRenderDispatcher pBlockEntityRenderDispatcher, EntityModelSet pEntityModelSet) {
        super(pBlockEntityRenderDispatcher, pEntityModelSet);
    }

    @Override
    public void renderByItem(ItemStack pStack, ItemDisplayContext pDisplayContext, PoseStack pPoseStack, MultiBufferSource pBuffer, int pPackedLight, int pPackedOverlay) {
        ItemRenderer renderer = Minecraft.getInstance().getItemRenderer();
        Item item = pStack.getItem();
        if (item instanceof MogulWarhookItem) {
            pPoseStack.pushPose();
            float scale = 1.0F / 16.0F;
            pPoseStack.scale(scale, scale, scale);
            pPoseStack.translate(7.5, 5.5, 10);
            if (pDisplayContext.firstPerson()) pPoseStack.mulPose(Axis.XP.rotationDegrees(-15));
            WARHOOK.render(0, pPoseStack, renderer.getFoilBuffer(pBuffer, RenderType.entityCutoutNoCull(WARHOOK_LOCATION), true, pStack.hasFoil()), pPackedLight, pPackedOverlay, 1, 1, 1, 1);
            pPoseStack.popPose();
        }
    }
}
