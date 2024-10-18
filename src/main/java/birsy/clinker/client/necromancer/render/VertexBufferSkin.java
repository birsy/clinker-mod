package birsy.clinker.client.necromancer.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexBuffer;

public class VertexBufferSkin implements Skin {
    private final VertexBuffer vbo;

    public VertexBufferSkin(VertexBuffer vbo) {
        this.vbo = vbo;
    }

    @Override
    public void draw(PoseStack poseStack) {
        vbo.bind();
        vbo.drawWithShader(poseStack.last().pose(), RenderSystem.getProjectionMatrix(), RenderSystem.getShader());
        VertexBuffer.unbind();
    }
}
