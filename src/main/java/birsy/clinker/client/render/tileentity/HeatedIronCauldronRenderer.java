package birsy.clinker.client.render.tileentity;

import birsy.clinker.client.render.tileentity.model.HeatedCauldronModel;
import birsy.clinker.common.tileentity.HeatedIronCauldronTileEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import com.mojang.math.Vector3f;
import net.minecraft.world.level.LightLayer;

public class HeatedIronCauldronRenderer<T extends HeatedIronCauldronTileEntity> extends BlockEntityRenderer<T> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/blocks/heated_iron_cauldron/cauldron.png");
    protected static final ResourceLocation OVERLAY = new ResourceLocation(Clinker.MOD_ID, "textures/blocks/heated_iron_cauldron/cauldron_glow.png");
    private final HeatedCauldronModel cauldronModel;

    public HeatedIronCauldronRenderer(BlockEntityRenderDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        cauldronModel = new HeatedCauldronModel();
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
        cauldronModel.setSpoonRotation(tileEntityIn.getSpoonRotation(partialTicks));
        cauldronModel.setCauldronShake(MathUtils.bias(tileEntityIn.getCauldronShakeAmount(partialTicks), 0.5F), tileEntityIn.ageInTicks + partialTicks);
        cauldronModel.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityCutout(TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0f, 1.0f, 1.0f, 1.0f);

        final float intensity = 0.1f;
        float fade = MathUtils.mapRange(0, 100, 0, 1, tileEntityIn.getHeatOverlayStrength(partialTicks)) * ((Mth.sin(tileEntityIn.ageInTicks * 0.125f) * intensity) + (1 - intensity));
        cauldronModel.renderToBuffer(matrixStackIn, bufferIn.getBuffer(RenderType.entityTranslucent(OVERLAY)), LightTexture.pack(15, tileEntityIn.getLevel().getBrightness(LightLayer.SKY, tileEntityIn.getBlockPos())), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, fade);

        /**
        cauldronModel.setItemRotations(tileEntityIn.getItemRotations(0, partialTicks), tileEntityIn.getItemRotations(1, partialTicks), tileEntityIn.getItemRotations(2, partialTicks), tileEntityIn.getItemRotations(3, partialTicks));
        for(int index = 0; index < nonnulllist.size(); ++index) {
            ItemStack itemstack = nonnulllist.get(index);
            if (itemstack != ItemStack.EMPTY) {
                matrixStackIn.push();
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));
                matrixStackIn.scale(0.375F, 0.375F, 0.375F);
                cauldronModel.getRendererFromIndex(index).matrixStackFromModel(matrixStackIn);

                Minecraft.getInstance().getItemRenderer().renderItem(itemstack, ItemCameraTransforms.TransformType.GROUND, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
                matrixStackIn.pop();
            }
        }
         */
        NonNullList<ItemStack> nonnulllist = tileEntityIn.getInventory();
        for(int i = 0; i < nonnulllist.size(); ++i) {
            ItemStack itemstack = nonnulllist.get(i);
            if (itemstack != ItemStack.EMPTY) {
                matrixStackIn.pushPose();
                matrixStackIn.translate(0.5D, 0.44921875D, 0.5D);
                Direction direction1 = Direction.from2DDataValue(i % 4);
                float f = -direction1.toYRot();
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(f));
                matrixStackIn.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                matrixStackIn.translate(-0.3125D, -0.3125D, 0.0D);
                matrixStackIn.scale(0.375F, 0.375F, 0.375F);
                Minecraft.getInstance().getItemRenderer().renderStatic(itemstack, ItemTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
                matrixStackIn.popPose();
            }
        }
    }
}
