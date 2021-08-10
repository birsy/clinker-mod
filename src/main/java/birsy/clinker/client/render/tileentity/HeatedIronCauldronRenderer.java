package birsy.clinker.client.render.tileentity;

import birsy.clinker.client.render.tileentity.model.HeatedCauldronModel;
import birsy.clinker.common.tileentity.HeatedIronCauldronTileEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.LightType;

public class HeatedIronCauldronRenderer<T extends HeatedIronCauldronTileEntity> extends TileEntityRenderer<T> {
    protected static final ResourceLocation TEXTURE = new ResourceLocation(Clinker.MOD_ID, "textures/blocks/heated_iron_cauldron/cauldron.png");
    protected static final ResourceLocation OVERLAY = new ResourceLocation(Clinker.MOD_ID, "textures/blocks/heated_iron_cauldron/cauldron_glow.png");
    private final HeatedCauldronModel cauldronModel;

    public HeatedIronCauldronRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
        cauldronModel = new HeatedCauldronModel();
    }

    @Override
    public void render(T tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        cauldronModel.setSpoonRotation(tileEntityIn.getSpoonRotation(partialTicks));
        cauldronModel.setCauldronShake(MathUtils.bias(tileEntityIn.getCauldronShakeAmount(partialTicks), 0.5F), tileEntityIn.ageInTicks + partialTicks);
        cauldronModel.render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntityCutout(TEXTURE)), combinedLightIn, combinedOverlayIn, 1.0f, 1.0f, 1.0f, 1.0f);

        final float intensity = 0.1f;
        float fade = MathUtils.mapRange(0, 100, 0, 1, tileEntityIn.getHeatOverlayStrength(partialTicks)) * ((MathHelper.sin(tileEntityIn.ageInTicks * 0.125f) * intensity) + (1 - intensity));
        cauldronModel.render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntityTranslucent(OVERLAY)), LightTexture.packLight(15, tileEntityIn.getWorld().getLightFor(LightType.SKY, tileEntityIn.getPos())), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, fade);

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
                matrixStackIn.push();
                matrixStackIn.translate(0.5D, 0.44921875D, 0.5D);
                Direction direction1 = Direction.byHorizontalIndex(i % 4);
                float f = -direction1.getHorizontalAngle();
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(f));
                matrixStackIn.rotate(Vector3f.XP.rotationDegrees(90.0F));
                matrixStackIn.translate(-0.3125D, -0.3125D, 0.0D);
                matrixStackIn.scale(0.375F, 0.375F, 0.375F);
                Minecraft.getInstance().getItemRenderer().renderItem(itemstack, ItemCameraTransforms.TransformType.FIXED, combinedLightIn, combinedOverlayIn, matrixStackIn, bufferIn);
                matrixStackIn.pop();
            }
        }
    }
}
