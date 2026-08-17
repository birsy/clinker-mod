package birsy.clinker.client.render.world.blockentity;

import birsy.clinker.common.block.blockentity.EmbeddedAmberBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.api.client.necromancer.SkeletonParent;
import foundry.veil.api.client.necromancer.render.NecromancerEntityRenderer;
import net.minecraft.Util;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

public class EmbeddedAmberRenderer implements BlockEntityRenderer<EmbeddedAmberBlockEntity> {
    private static final Matrix4f[] ITEM_TRANSFORMATIONS = Util.make(() -> {
        RandomSource random = RandomSource.create(4374);
        Matrix4f[] array = new Matrix4f[64];
        for (int i = 0; i < array.length; i++) {
            Matrix4f matrix = new Matrix4f();
            matrix.rotateYXZ(
                    random.nextFloat() * Mth.PI * 2F,
                    (float) random.triangle(0, Math.PI * 0.2F),
                    (float) random.triangle(0, Math.PI * 0.2F)
            );
            float offset = 0.125F;
            matrix.translate(
                    Mth.map(random.nextFloat(), 0, 1, offset, 1 - offset) - 0.5F,
                    Mth.map(random.nextFloat(), 0, 1, offset, 1 - offset) - 0.5F,
                    Mth.map(random.nextFloat(), 0, 1, offset, 1 - offset) - 0.5F
            );
            array[i] = matrix;
        }
        return array;
    });
    private static final Quaternionf[] ENTITY_TRANSFORMATIONS = Util.make(() -> {
        RandomSource random = RandomSource.create(9046);
        Quaternionf[] array = new Quaternionf[64];
        for (int i = 0; i < array.length; i++) {
            Quaternionf quaternion = new Quaternionf();
            quaternion.rotateYXZ(
                    random.nextFloat() * Mth.PI * 2F,
                    (float) random.triangle(0, Math.PI * 0.2F),
                    (float) random.triangle(0, Math.PI * 0.2F)
            );
            array[i] = quaternion;
        }
        return array;
    });

    private final ItemRenderer itemRenderer;
    private final EntityRenderDispatcher entityRenderer;

    public EmbeddedAmberRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
        this.entityRenderer = context.getEntityRenderer();
    }

    @Override
    public void render(EmbeddedAmberBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        int posHash = blockEntity.getBlockPos().hashCode();
        ItemStack embeddedStack = blockEntity.getEmbeddedItem();
        if (!embeddedStack.isEmpty()) {
            for (int i = 0; i < Math.min(embeddedStack.getCount(), 16); i++) {
                poseStack.pushPose();
                poseStack.mulPose(ITEM_TRANSFORMATIONS[Math.floorMod(posHash + i, ITEM_TRANSFORMATIONS.length)]);
                poseStack.scale(0.5F, 0.5F, 0.5F);

                itemRenderer.renderStatic(embeddedStack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
                poseStack.popPose();
            }
        }

        Entity embeddedEntity = blockEntity.getEmbeddedEntity();
        if (embeddedEntity != null) {
            float offset = embeddedEntity.getBbHeight() * 0.5F;
            poseStack.mulPose(ENTITY_TRANSFORMATIONS[Math.floorMod(posHash + embeddedEntity.getUUID().hashCode(), ENTITY_TRANSFORMATIONS.length)]);
            poseStack.translate(0, -offset, 0);
            if (entityRenderer.getRenderer(embeddedEntity) instanceof NecromancerEntityRenderer necromancerEntityRenderer
                && embeddedEntity instanceof SkeletonParent skeletonParent && (skeletonParent.getSkeleton() == null || skeletonParent.getAnimator() == null)) {
                necromancerEntityRenderer.setupEntity(embeddedEntity);
            }
            entityRenderer.setRenderShadow(false);
            entityRenderer.render(embeddedEntity,
                    0, 0, 0,
                    0, 1F,
                    poseStack, bufferSource,
                    packedLight
            );
            entityRenderer.setRenderShadow(true);
        }

        poseStack.popPose();
    }
}
