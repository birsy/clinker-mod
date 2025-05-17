package birsy.clinker.client.render.debug;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.api.client.color.Color;
import foundry.veil.api.client.util.DebugRenderHelper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;

import java.util.*;

public class MetaChunkDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    Map<MetaChunkDebugPos, MetaChunkDebugInfo> metaChunks = new HashMap<>();
    public static int maxDepth = 7;
    public void startMetaChunkGenerationDebug(MetaChunkDebugPos metaChunkPos, int chunkOwnerX, int chunkOwnerY, long threadOwner) {
        this.metaChunks.put(metaChunkPos, new MetaChunkDebugInfo(chunkOwnerX, chunkOwnerY, threadOwner));
        maxDepth = Math.max(metaChunkPos.depth, maxDepth);
    }

    public void completeMetaChunkGenerationDebug(MetaChunkDebugPos metaChunkPos, List<BlockPos> features) {
        if (metaChunks.containsKey(metaChunkPos)) {
            metaChunks.get(metaChunkPos).generating = false;
            metaChunks.get(metaChunkPos).featurePositions.addAll(features);
        } else {
            MetaChunkDebugInfo debugInfo = new MetaChunkDebugInfo(0, 0, 0L);
            debugInfo.generating = false;
            debugInfo.featurePositions.addAll(features);
            this.metaChunks.put(metaChunkPos, debugInfo);
        }
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, double camX, double camY, double camZ) {
        int height = 250;
        RenderSystem.setShaderFogEnd(10000.0F);
        poseStack.pushPose();
        BlockPos.MutableBlockPos featurePos = new BlockPos.MutableBlockPos();
        Color color = new Color();
        for (int i = 0; i < maxDepth; i++) {
            for (Map.Entry<MetaChunkDebugPos, MetaChunkDebugInfo> entry : metaChunks.entrySet()) {
                MetaChunkDebugPos pos = entry.getKey();
                MetaChunkDebugInfo info = entry.getValue();

                if (pos.depth != i) continue;
                float offset = (1 + pos.depth) * 0.5F;
                if (info.generating) {
                    poseStack.pushPose();
                    DebugRenderer.renderFloatingText(poseStack, bufferSource,
                            "Generating... From Chunk At: (" + (info.chunkOwnerX * 16) + ", " + (info.chunkOwnerY * 16) + ") on thread " + info.threadOwner,
                            pos.x + pos.size/2, height - pos.depth * 8 - 10, pos.z + pos.size/2,
                            -6959665, 1.0F);
                    poseStack.popPose();
                }
                poseStack.pushPose();
                poseStack.translate(-camX, -camY, -camZ);
                color.setHSV(((pos.depth / (float) maxDepth) * 720.0F) % 360.0F, 0.5F, 0.5F);
                DebugRenderer.renderFilledBox(poseStack, bufferSource,
                        pos.x + offset, height - pos.depth * 8, pos.z + offset,
                        pos.x + pos.size - offset, height + 3 - pos.depth * 8, pos.z + pos.size - offset,
                        info.generating ? 1.0F : color.red(), info.generating ? 0.0F : color.green(), info.generating ? 0.0F : color.blue(), 0.8F);

                for (BlockPos featurePosition : info.featurePositions) {
                    featurePos.set(featurePosition.getX(),height - pos.depth * 8 - 10, featurePosition.getZ());
                    float radius = Mth.lerp((pos.depth / (float) maxDepth), 5.0F, 10.0F);
                    DebugRenderHelper.renderSphere(
                            poseStack, bufferSource.getBuffer(RenderType.LINES), 16, radius,
                            featurePosition.getX() + 0.5, height - pos.depth * 8 - 32.5, featurePosition.getZ() + 0.5,
                            color.red(), color.green(), color.blue(), 1);
                }

                poseStack.popPose();
            }
        }


////            for (MetaChunkDebugInfo metaChunk : metaChunks) {
////                if (metaChunk.depth != i) continue;
////                color.setHSV((metaChunk.depth / 7.0F) * 360.0F, 0.5F, 0.5F);
////                int offset = (1 + metaChunk.depth) * 2;
////                DebugRenderer.renderFilledBox(poseStack, bufferSource,
////                        metaChunk.x + offset, height - metaChunk.depth * 8, metaChunk.z + offset,
////                        metaChunk.x + metaChunk.size - offset, height + 3 - metaChunk.depth * 8, metaChunk.z + metaChunk.size - offset,
////                        color.red(), color.green(), color.blue(), 0.8F);
////
////                for (BlockPos featurePosition : metaChunk.featurePositions) {
////                    featurePos.set(featurePosition.getX(),height - metaChunk.depth * 8 - 10, featurePosition.getZ());
////                    DebugRenderHelper.renderSphere(
////                            poseStack, bufferSource.getBuffer(RenderType.LINES), 16, 10.0F,
////                            featurePosition.getX() + 0.5, height - metaChunk.depth * 8 - 32.5, featurePosition.getZ() + 0.5,
////                            1, 0, 0, 1);
////                }
////            }

        poseStack.popPose();
    }

    public record MetaChunkDebugPos(int x, int z, int size, int depth) {}
    public static final class MetaChunkDebugInfo {
        private final List<BlockPos> featurePositions = new ArrayList<>();
        boolean generating = true;

        public final int chunkOwnerX;
        public final int chunkOwnerY;
        public final long threadOwner;

        public MetaChunkDebugInfo(int chunkOwnerX, int chunkOwnerY, long threadOwner) {
            this.chunkOwnerX = chunkOwnerX;
            this.chunkOwnerY = chunkOwnerY;
            this.threadOwner = threadOwner;
        }
    }
}
