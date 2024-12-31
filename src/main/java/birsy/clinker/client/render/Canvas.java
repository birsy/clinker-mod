package birsy.clinker.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.ByteBufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexSorting;
import foundry.veil.api.client.render.framebuffer.AdvancedFbo;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;

public class Canvas {
    public final AdvancedFbo fbo;
    public final int width, height;
    public PoseStack poseStack;

    public static final MultiBufferSource.BufferSource BUFFER_SOURCE = MultiBufferSource.immediate(new ByteBufferBuilder(786432));

    public Canvas(AdvancedFbo fbo) {
        this.fbo = fbo;
        this.width = fbo.getWidth();
        this.height = fbo.getHeight();
        this.poseStack = new PoseStack();
    }

    public void beginDraw() {
        fbo.bind(true);

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.getModelViewMatrix().identity();
        RenderSystem.backupProjectionMatrix();
        RenderSystem.setProjectionMatrix(new Matrix4f(), VertexSorting.ORTHOGRAPHIC_Z);

        RenderSystem.defaultBlendFunc();
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();

        poseStack.pushPose();
        // 0, 0 in bottom left corner
        poseStack.scale(2.0F / width, 2.0F / height, 1.0F);
//        poseStack.translate(-width / 2.0F, -height / 2.0F, 0);
    }

    public void finishDraw() {
        poseStack.popPose();
        if (!poseStack.clear()) throw new IllegalStateException("Canvas pose stack not empty");
        BUFFER_SOURCE.endBatch();

        RenderSystem.restoreProjectionMatrix();
        RenderSystem.enableDepthTest();
        AdvancedFbo.unbind();
    }

    public static VertexConsumer getBuffer(RenderType renderType) {
        return BUFFER_SOURCE.getBuffer(renderType);
    }
}
