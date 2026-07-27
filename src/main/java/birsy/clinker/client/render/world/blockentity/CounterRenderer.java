package birsy.clinker.client.render.world.blockentity;

import birsy.clinker.client.resource.CounterTransformOverrideResource;
import birsy.clinker.common.world.block.blockentity.CounterBlockEntity;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.codecs.ExtraExtraCodecs;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import org.joml.*;

import java.lang.Math;

public class CounterRenderer implements BlockEntityRenderer<CounterBlockEntity> {
    private final ItemRenderer itemRenderer;

    public CounterRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(CounterBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(CounterBlockEntity.SLOT_SIZE * 0.5, 1, CounterBlockEntity.SLOT_SIZE * 0.5);

        BlockPos pos = blockEntity.getBlockPos().above();
        int light = LightTexture.pack(
                blockEntity.getLevel().getLightEngine().getLayerListener(LightLayer.BLOCK).getLightValue(pos),
                blockEntity.getLevel().getLightEngine().getLayerListener(LightLayer.SKY).getLightValue(pos)
        );

        for (int x = 0; x < CounterBlockEntity.SLOT_SIDE_LENGTH; x++) {
            float xFac = x * CounterBlockEntity.SLOT_SIZE;
            for (int z = 0; z < CounterBlockEntity.SLOT_SIDE_LENGTH; z++) {
                float zFac = z * CounterBlockEntity.SLOT_SIZE;

                int itemIndex = CounterBlockEntity.getIndex(x, z);
                ItemStack item = blockEntity.items.get(itemIndex);
                if (item.isEmpty()) continue;

                poseStack.pushPose();
                poseStack.translate(xFac, 0, zFac);
                poseStack.mulPose(Axis.YP.rotation(blockEntity.itemRotations.get(itemIndex)));

                int stackCount = Math.min(item.getCount(), 8);
                for (int i = 0; i < stackCount; i++) {
                    poseStack.pushPose();
                    applyAdditionalItemTransforms(poseStack, blockEntity.getLevel(), item, i);
                    itemRenderer.renderStatic(item, ItemDisplayContext.GROUND, light, packedOverlay, poseStack, bufferSource, blockEntity.getLevel(), 0);
                    poseStack.popPose();
                }
                poseStack.popPose();
            }
        }
        poseStack.popPose();
    }

    private static final Matrix4f GENERIC_3D_TRANSFORM = new Matrix4f().translate(0, -0.055F, 0),
            GENERIC_FLAT_TRANSFORM = new Matrix4f().translate(0, 0.02F, 0).rotateX(Mth.HALF_PI);
    private static final Vector4fc[] RANDOM_STACK_OFFSETS = Util.make(() -> {
        Vector4fc[] result = new Vector4fc[8];
        result[0] = new Vector4f();
        RandomSource random = RandomSource.create(80085);
        for (int i = 1; i < result.length; i++) {
            result[i] = new Vector4f(
                    (float) random.triangle(0, 1),
                    (float) random.triangle(0, 1),
                    (float) random.triangle(0, 1),
                    (float) random.triangle(0, 1)
            );
        }
        return result;
    });
    private static final Vector3f SCRATCH_3F = new Vector3f();
    private static final Vector4f SCRATCH_4F = new Vector4f();
    public void applyAdditionalItemTransforms(PoseStack poseStack, Level level, ItemStack stack, int stackNumber) {
        // if the item is in the transform override data map, use that
        // otherwise, make an educated guess
        CounterTransformOverride transformOverride = CounterTransformOverrideResource.Authority.INSTANCE.get(stack.getItem());
        boolean is3d = false;

        Vector3f stackOffset = SCRATCH_3F;
        Vector4f stackRandom = SCRATCH_4F;
        float stackAngleRandom = CounterTransformOverride.DEFAULT.stackAngleRandom();
        if (transformOverride != null) {
            stackOffset.set(transformOverride.stackOffset());
            stackRandom.set(transformOverride.stackOffsetRandom(), 1.0F);
            stackAngleRandom = transformOverride.stackAngleRandom;
        } else {
            is3d = itemRenderer.getModel(stack, level, null, 0).isGui3d();
            if (is3d) {
                stackOffset.set(0, 0, 0);
                stackRandom.set(0.1F,  0.05F, 0.1F, 1.0F);
            } else {
                stackOffset.set(0, 0.02F, 0);
                stackRandom.set(0);
            }
        }

        // stack transform goes first
        poseStack.translate(stackOffset.x * stackNumber, stackOffset.y * stackNumber, stackOffset.z * stackNumber);
        Vector4fc randomStackOffset = RANDOM_STACK_OFFSETS[stackNumber % RANDOM_STACK_OFFSETS.length];
        poseStack.translate(stackRandom.x * randomStackOffset.x(), stackRandom.y * randomStackOffset.y(), stackRandom.z * randomStackOffset.z());
        poseStack.mulPose(Axis.YP.rotationDegrees(randomStackOffset.w() * stackAngleRandom));

        // then the general transform...
        if (transformOverride != null) {
            poseStack.mulPose(transformOverride.transform());
        } else {
            if (is3d) poseStack.mulPose(GENERIC_3D_TRANSFORM);
            else poseStack.mulPose(GENERIC_FLAT_TRANSFORM);
        }
    }

    public record CounterTransformOverride(Matrix4f transform, Vector3f stackOffset, Vector3f stackOffsetRandom, float stackAngleRandom) {
        public static final CounterTransformOverride DEFAULT = new CounterTransformOverride(
                new Matrix4f(), new Vector3f(0, 0.02F, 0), new Vector3f(), 10.0F
        );
    }
}
