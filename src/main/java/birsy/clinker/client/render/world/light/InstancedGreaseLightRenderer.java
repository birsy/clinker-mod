package birsy.clinker.client.render.world.light;

import birsy.clinker.client.render.ClinkerShaders;
import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import foundry.veil.Veil;
import foundry.veil.api.client.render.CullFrustum;
import foundry.veil.api.client.render.VeilRenderSystem;
import foundry.veil.api.client.render.light.data.PointLightData;
import foundry.veil.api.client.render.light.renderer.InstancedLightRenderer;
import foundry.veil.api.client.render.light.renderer.LightRenderHandle;
import foundry.veil.api.client.render.light.renderer.LightRenderer;
import foundry.veil.api.client.render.light.renderer.LightTypeRenderer;
import foundry.veil.api.client.render.rendertype.VeilRenderType;
import foundry.veil.api.client.render.vertex.VertexArrayBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class InstancedGreaseLightRenderer extends InstancedLightRenderer<GreaseLightData> {

    private static final ResourceLocation RENDER_TYPE = Clinker.resource("light/grease");

    public InstancedGreaseLightRenderer() {
        super(Float.BYTES * 7);
    }

    @Override
    protected MeshData createMesh() {
        BufferBuilder builder = RenderSystem.renderThreadTesselator().begin(VertexFormat.Mode.TRIANGLE_STRIP, DefaultVertexFormat.POSITION);
        LightTypeRenderer.createInvertedCube(builder);
        return builder.buildOrThrow();
    }

    @Override
    protected void setupBufferState(VertexArrayBuilder builder) {
        builder.setVertexAttribute(1, 2, 3, VertexArrayBuilder.DataType.FLOAT, false, 0);
        builder.setVertexAttribute(2, 2, 3, VertexArrayBuilder.DataType.FLOAT, false, Float.BYTES * 3);
        builder.setVertexAttribute(3, 2, 1, VertexArrayBuilder.DataType.FLOAT, false, Float.BYTES * 6);
    }

    @Override
    protected @Nullable RenderType getRenderType(List<? extends LightRenderHandle<GreaseLightData>> lights) {
        return VeilRenderType.get(RENDER_TYPE);
    }
}
