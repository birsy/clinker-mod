package birsy.clinker.client.render.debug;

import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.PoseStack;
import foundry.veil.api.client.color.Color;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.debug.DebugRenderer;

import java.util.ArrayList;
import java.util.List;

public class MetaChunkDebugRenderer implements DebugRenderer.SimpleDebugRenderer {
    List<MetaChunkDebugInfo> metaChunks = new ArrayList<>();

    public void addMetaChunk(MetaChunkDebugInfo metaChunk) {
        this.metaChunks.add(metaChunk);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, double camX, double camY, double camZ) {
        poseStack.pushPose();
        poseStack.translate(-camX, -camY, -camZ);
        Color color = new Color();
        int height = 250;
        for (int i = 0; i < 7; i++) {
            for (MetaChunkDebugInfo metaChunk : metaChunks) {
                if (metaChunk.depth != i) continue;
                color.setHSV((metaChunk.depth / 7.0F) * 360.0F, 0.5F, 0.5F);
                //color.setHSV(metaChunk.depth % 2, 1.0F, 0.5F);
                int offset = (1 + metaChunk.depth) * 2;
                DebugRenderer.renderFilledBox(poseStack, bufferSource,
                        metaChunk.x + offset, height - metaChunk.depth*8, metaChunk.z + offset,
                        metaChunk.x + metaChunk.size - offset, height + 3 - metaChunk.depth*8, metaChunk.z + metaChunk.size - offset,
                        color.red(), color.green(), color.blue(), 0.8F);
            }
        }

        poseStack.popPose();
    }

    public record MetaChunkDebugInfo(int size, int depth, int x, int z) { }
}
