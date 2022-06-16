package birsy.clinker.client.render.entity.layer;

import birsy.clinker.client.render.entity.model.AnimFunctions;
import birsy.clinker.client.render.entity.model.GnomadAxemanModel;
import birsy.clinker.common.entity.GnomadAxemanEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.item.ItemStack;

public class GnomadItemBeltLayer<T extends GnomadAxemanEntity, M extends GnomadAxemanModel<T>> extends RenderLayer<T, M> {
    private final ItemInHandRenderer itemInHandRenderer;

    public GnomadItemBeltLayer(RenderLayerParent<T, M> parent, ItemInHandRenderer itemInHandRenderer) {
        super(parent);
        this.itemInHandRenderer = itemInHandRenderer;
    }

    @Override
    public void render(PoseStack pMatrixStack, MultiBufferSource pBuffer, int pPackedLight, T pLivingEntity, float pLimbSwing, float pLimbSwingAmount, float pPartialTicks, float pAgeInTicks, float pNetHeadYaw, float pHeadPitch) {
        /*
        slot 0 : right hand tool sheathe
        slot 1 : left hand tool sheathe
        slots 2 - 5 : potions
        slots 6 - 7 : misc.
        */

        pMatrixStack.pushPose();
        AnimFunctions.getGlobalTransForm(this.getParentModel().gnomadBody, pMatrixStack);

        //Renders the sheathed tools on the side.
        float sheathedItemScale = 2.5F / 3.0F;
        for (int i = 0; i < 2; i++) {
            ItemStack sheathedItem = pLivingEntity.getInventory().getItem(i);

            int sign = -((i * 2) - 1);
            pMatrixStack.pushPose();
            pMatrixStack.translate(sign * (5.0F / 16.0F), 3.0F / 16.0F + getSwing(pLimbSwing, pLimbSwingAmount, 0.2F), 0.0F);
            pMatrixStack.mulPose(Vector3f.YN.rotationDegrees(90.0F));
            pMatrixStack.mulPose(Vector3f.ZN.rotationDegrees(90.0F));
            pMatrixStack.scale(sheathedItemScale, sheathedItemScale, sheathedItemScale);
            itemInHandRenderer.renderItem(pLivingEntity, sheathedItem, ItemTransforms.TransformType.FIXED, false, pMatrixStack, pBuffer, pPackedLight);
            pMatrixStack.popPose();
        }

        float potionScale = 1.5F / 4.0F;

        ItemStack[] potions = Util.make(new ItemStack[4], (array) -> { for (int i = 0; i < array.length; i++) { array[i] = pLivingEntity.getInventory().getItem(i + 2);}});
        for (int i = 0; i < potions.length; i++) {
            ItemStack potion = potions[i];
            pMatrixStack.pushPose();
            pMatrixStack.translate(((3.0F / 16.0F) * (i - 2)) + (1.25F / 16.0F), 1.0F / 16.0F + (getSwing(pLimbSwing, pLimbSwingAmount, 0.1F + (i * 0.5F)) * 0.5F), -4.5F / 16.0F);
            pMatrixStack.scale(potionScale, potionScale, potionScale);
            pMatrixStack.mulPose(Vector3f.ZN.rotationDegrees(180.0F));
            pMatrixStack.mulPose(Vector3f.XN.rotationDegrees(-10.0F));
            pMatrixStack.mulPose(Vector3f.YN.rotationDegrees(-30.0F));
            itemInHandRenderer.renderItem(pLivingEntity, potion, ItemTransforms.TransformType.FIXED, false, pMatrixStack, pBuffer, pPackedLight);
            pMatrixStack.popPose();
        }

        pMatrixStack.popPose();
    }

    public float getSwing(float pLimbSwing, float pLimbSwingAmount, float offset) {
        float f = pLimbSwing;
        float f1 = pLimbSwingAmount * 2F;
        float globalSpeed = 1.25F;
        float globalHeight = 0.75F;

        float walkSpeed = 1.1F * globalSpeed;
        return ((float) -Math.abs(Math.sin(offset + (f * ((2.0F * walkSpeed) * 0.5F))) * f1 * 2 * globalHeight)) / 16.0F;
    }
}
