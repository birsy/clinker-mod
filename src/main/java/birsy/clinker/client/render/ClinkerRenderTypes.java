package birsy.clinker.client.render;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.ClientHooks;

import java.util.function.BiFunction;
import java.util.function.Function;

import static net.minecraft.client.renderer.RenderStateShard.*;

public class ClinkerRenderTypes {
    private static final RenderStateShard.ShaderStateShard ENTITY_UNLIT_TRANSLUCENT_SHADER =
            new RenderStateShard.ShaderStateShard(ClientHooks.ClientEvents::getEntityTranslucentUnlitShader);
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_UNLIT_TRANSLUCENT = Util.memoize((resourceLocation, outline) -> {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(ENTITY_UNLIT_TRANSLUCENT_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(outline);
        return RenderType.create("entity_unlit_translucent",
                DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, compositeState);
    });
    public static RenderType entityUnlitTranslucent(ResourceLocation pLocation) { return ENTITY_UNLIT_TRANSLUCENT.apply(pLocation, true); }

    private static final RenderStateShard.ShaderStateShard ENTITY_UNLIT_CUTOUT_SHADER =
            new RenderStateShard.ShaderStateShard(ClinkerShaders::getEntityCutoutUnlitShader);
    private static final Function<ResourceLocation, RenderType> ENTITY_UNLIT_CUTOUT = Util.memoize((resourceLocation) -> {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(ENTITY_UNLIT_CUTOUT_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(NO_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
        return RenderType.create("entity_unlit_cutout",
                DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, compositeState);
    });
    public static RenderType entityUnlitCutout(ResourceLocation pLocation) { return ENTITY_UNLIT_CUTOUT.apply(pLocation); }

    private static final RenderStateShard.ShaderStateShard ENTITY_UNLIT_CUTOUT_NOCULL_SHADER =
            new RenderStateShard.ShaderStateShard(ClinkerShaders::getEntityCutoutNoCullUnlitShader);
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_UNLIT_CUTOUT_NOCULL = Util.memoize((resourceLocation, bool) -> {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(ENTITY_UNLIT_CUTOUT_NOCULL_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(NO_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(bool);
        return RenderType.create("entity_unlit_cutout_nocull",
                DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, compositeState);
    });
    public static RenderType entityUnlitCutoutNoCull(ResourceLocation pLocation) { return ENTITY_UNLIT_CUTOUT_NOCULL.apply(pLocation, true); }

    private static final RenderStateShard.ShaderStateShard CHAIN_LIGHTNING_SHADER =
            new RenderStateShard.ShaderStateShard(ClinkerShaders::getChainLightningShader);
    public static final RenderType CHAIN_LIGHTNING = Util.make(() -> {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(CHAIN_LIGHTNING_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_PARTICLES, false, false))
                .setTransparencyState(ADDITIVE_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(false);
        return RenderType.create("chain_lightning",
                DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, compositeState);
    });

    private static final RenderStateShard.ShaderStateShard FIRE_SPEW_SHADER =
            new RenderStateShard.ShaderStateShard(ClinkerShaders::getFireSpewShader);
    public static final RenderType FIRE_SPEW = Util.make(() -> {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(FIRE_SPEW_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_PARTICLES, false, false))
                .setTransparencyState(ADDITIVE_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setLightmapState(NO_LIGHTMAP)
                .setOverlayState(NO_OVERLAY)
                .createCompositeState(false);
        return RenderType.create("fire_spew",
                DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 256, true, false, compositeState);
    });

    private static final RenderStateShard.ShaderStateShard POSITION_TEXTURE_COLOR_SHADER =
            new RenderStateShard.ShaderStateShard(GameRenderer::getPositionTexColorShader);
    private static final Function<ResourceLocation, RenderType> CANVAS_TEXTURED = Util.memoize((resourceLocation) -> {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(POSITION_TEXTURE_COLOR_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setLightmapState(NO_LIGHTMAP)
                .setOverlayState(NO_OVERLAY)
                .createCompositeState(false);
        return RenderType.create("canvas_textured",
                DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 256, true, true, compositeState);
    });
    public static RenderType canvasTextured(ResourceLocation pLocation) { return CANVAS_TEXTURED.apply(pLocation); }

}
