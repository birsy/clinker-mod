package birsy.clinker.client.necromancer.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;

public class VertexBufferSkin implements Skin {
    private final VertexBuffer vertexBuffer;

    public VertexBufferSkin(VertexBuffer vertexBuffer) {
        this.vertexBuffer = vertexBuffer;
    }

    @Override
    public void draw(PoseStack poseStack) {
        vertexBuffer.drawWithShader(poseStack.last().pose(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
    }
}
