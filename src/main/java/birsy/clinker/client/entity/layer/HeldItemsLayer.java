package birsy.clinker.client.entity.layer;

import foundry.veil.api.client.necromancer.Bone;
import foundry.veil.api.client.necromancer.Skeleton;
import foundry.veil.api.client.necromancer.SkeletonParent;
import foundry.veil.api.client.necromancer.render.NecromancerEntityRenderLayer;
import foundry.veil.api.client.necromancer.render.NecromancerEntityRenderer;
import foundry.veil.api.client.necromancer.render.NecromancerRenderer;
import foundry.veil.api.client.render.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4x3f;
import org.joml.Quaternionf;

public class HeldItemsLayer<E extends Mob & SkeletonParent<E, S>, S extends Skeleton & HeldItemsLayer.ItemHoldingSkeleton> extends NecromancerEntityRenderLayer<E, S> {
    private final ItemInHandRenderer itemRenderer;
    private final Matrix4x3f scratchTransform = new Matrix4x3f();
    private final Quaternionf scratchRotation = new Quaternionf();

    public HeldItemsLayer(NecromancerEntityRenderer<E, S> renderer) {
        super(renderer);
        itemRenderer = Minecraft.getInstance().getEntityRenderDispatcher().getItemInHandRenderer();
    }

    @Override
    public void render(E parent, S skeleton, NecromancerRenderer renderer, MatrixStack matrixStack, int packedLight, float partialTicks) {
        boolean mainHandIsLeft = parent.isLeftHanded();
        ItemStack mainHandItem = parent.getMainHandItem();

        boolean offHandIsLeft = !mainHandIsLeft;
        ItemStack offHandItem = parent.getOffhandItem();

        matrixStack.matrixPush();

        if (!mainHandItem.isEmpty()) {
            matrixStack.matrixPush();
            matrixStack.position().mul(
                    (mainHandIsLeft ? skeleton.leftHandPivotBone() : skeleton.rightHandPivotBone())
                    .getModelTransform(scratchTransform.identity(), scratchRotation.identity(), partialTicks)
            );
            matrixStack.applyScale(16.0F);
            itemRenderer.renderItem(
                    parent,
                    mainHandItem,
                    mainHandIsLeft ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                    mainHandIsLeft,
                    matrixStack.toPoseStack(),
                    renderer,
                    packedLight
            );
            matrixStack.matrixPop();
        }

        if (!offHandItem.isEmpty()) {
            matrixStack.matrixPush();
            matrixStack.position().mul(
                    (offHandIsLeft ? skeleton.leftHandPivotBone() : skeleton.rightHandPivotBone())
                    .getModelTransform(scratchTransform.identity(), scratchRotation.identity(), partialTicks)
            );
            matrixStack.applyScale(16.0F);
            itemRenderer.renderItem(
                    parent,
                    offHandItem,
                    offHandIsLeft ? ItemDisplayContext.THIRD_PERSON_LEFT_HAND : ItemDisplayContext.THIRD_PERSON_RIGHT_HAND,
                    offHandIsLeft,
                    matrixStack.toPoseStack(),
                    renderer,
                    packedLight
            );
            matrixStack.matrixPop();
        }

        matrixStack.matrixPop();
    }

    public interface ItemHoldingSkeleton {
        Bone leftHandPivotBone();
        Bone rightHandPivotBone();
    }
}
