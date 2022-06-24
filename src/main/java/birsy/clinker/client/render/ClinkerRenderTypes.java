package birsy.clinker.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;

public class ClinkerRenderTypes {
    protected static final RenderStateShard.LightmapStateShard LIGHTMAP = new RenderStateShard.LightmapStateShard(true);
    //private static final RenderType FIRE = RenderType.create("fire", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 131072, true, false, RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setShaderState(RENDERTYPE_CUTOUT_SHADER).setTextureState(BLOCK_SHEET).createCompositeState(true));

}
