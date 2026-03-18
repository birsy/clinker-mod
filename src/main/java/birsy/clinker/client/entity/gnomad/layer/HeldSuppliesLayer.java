package birsy.clinker.client.entity.gnomad.layer;

import birsy.clinker.client.entity.gnomad.SuppliesDelivererSkeleton;
import birsy.clinker.common.world.entity.gnomad.SuppliesDeliverer;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerItems;
import foundry.veil.api.client.necromancer.Skeleton;
import foundry.veil.api.client.necromancer.SkeletonParent;
import foundry.veil.api.client.necromancer.render.NecromancerEntityRenderLayer;
import foundry.veil.api.client.necromancer.render.NecromancerEntityRenderer;
import foundry.veil.api.client.necromancer.render.NecromancerRenderer;
import foundry.veil.api.client.render.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.joml.Matrix4x3f;

public class HeldSuppliesLayer<E extends LivingEntity & SuppliesDeliverer & SkeletonParent<E, S>, S extends Skeleton & SuppliesDelivererSkeleton> extends NecromancerEntityRenderLayer<E, S> {
    private static final ItemStack[] SUPPLIES_ITEMS = {
            Items.ARROW.getDefaultInstance(),
            ClinkerItems.ORDNANCE.toStack(),
            ClinkerItems.ORDNANCE.toStack(),
            Items.POTION.getDefaultInstance(),
            ClinkerItems.LEAD_INGOT.toStack(),
            Items.ARROW.getDefaultInstance(),
    };
    private final ItemRenderer itemRenderer;
    private final Matrix4x3f parentTransform = new Matrix4x3f();

    public HeldSuppliesLayer(NecromancerEntityRenderer<E, S> renderer) {
        super(renderer);
        itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public void render(E parent, S skeleton, NecromancerRenderer renderer, MatrixStack matrixStack, int packedLight, float partialTicks) {
        if (!parent.isHoldingDelivery()) return;

        matrixStack.matrixPush();
        matrixStack.position().mul(skeleton.suppliesParentBone().getModelTransform(parentTransform.identity(), partialTicks));
        matrixStack.applyScale(10.0F);
        skeleton.suppliesOffset(matrixStack);

        for (int i = 0; i < SUPPLIES_ITEMS.length; i++) {
            ItemStack item = SUPPLIES_ITEMS[i];
            matrixStack.matrixPush();
            matrixStack.translate(
                    ((i * 983.48201889F % 2.0F) - 1.0F) * 0.1F,
                    ((i * 123.12312314F % 2.0F) - 1.0F) * 0.1F,
                    0.06F * (i - SUPPLIES_ITEMS.length/2.0F)
            );
            matrixStack.rotateZYX(((i * 85.43873F % 2.0F) - 1.0F) * 0.5F, 0, 0);

            itemRenderer.renderStatic(
                    parent,
                    item,
                    ItemDisplayContext.NONE,
                    false,
                    matrixStack.toPoseStack(),
                    renderer,
                    parent.level(),
                    packedLight,
                    OverlayTexture.pack(0, parent.hurtTime > 0 || parent.deathTime > 0),
                    0
            );
            matrixStack.matrixPop();
        }
        matrixStack.matrixPop();
    }
}
