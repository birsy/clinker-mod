package birsy.clinker.client.render.world.light;

import birsy.clinker.client.render.ClinkerShaders;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import foundry.veil.api.client.render.CullFrustum;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.renderer.InstancedLightRenderer;
import foundry.veil.api.client.render.light.renderer.LightRenderer;
import foundry.veil.api.client.render.light.renderer.LightTypeRenderer;
import foundry.veil.api.client.render.vertex.VertexArrayBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class RimLightRenderer extends InstancedLightRenderer<RimLight> {

    public RimLightRenderer() {
        super(Float.BYTES * 7);
    }

    @Override
    protected MeshData createMesh() {
        Tesselator tesselator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tesselator.begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION);
        LightTypeRenderer.createInvertedCube(bufferBuilder);
        return bufferBuilder.buildOrThrow();
    }

    @Override
    protected void setupBufferState(VertexArrayBuilder builder) {
        builder.setVertexAttribute(1, 2, 3, VertexArrayBuilder.DataType.FLOAT, false, 0);
        builder.setVertexAttribute(2, 2, 3, VertexArrayBuilder.DataType.FLOAT, false, Float.BYTES * 3);
        builder.setVertexAttribute(3, 2, 1, VertexArrayBuilder.DataType.FLOAT, false, Float.BYTES * 6);
    }

    @Override
    protected void setupRenderState(@NotNull LightRenderer lightRenderer, @NotNull List<RimLight> lights) {
        VeilRenderSystem.setShader(ClinkerShaders.LIGHT_RIM)
                .setFloat("GameTime", RenderSystem.getShaderGameTime());
    }

    @Override
    protected void clearRenderState(@NotNull LightRenderer lightRenderer, @NotNull List<RimLight> lights) {
    }

    @Override
    protected boolean isVisible(RimLight light, CullFrustum frustum) {
        return frustum.testSphere(light.getPosition(), light.getRadius() * 1.4F);
    }
}
