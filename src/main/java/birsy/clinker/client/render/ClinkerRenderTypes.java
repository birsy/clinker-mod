package birsy.clinker.client.render;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.ClientHooks;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public class ClinkerRenderTypes {
    protected static final RenderStateShard.TransparencyStateShard NO_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("no_transparency", () -> {
        RenderSystem.disableBlend();
    }, () -> {
    });
    protected static final RenderStateShard.TransparencyStateShard TRANSLUCENT_TRANSPARENCY = new RenderStateShard.TransparencyStateShard("translucent_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderStateShard.TransparencyStateShard ADDITIVE_TRANSPARENCY = new RenderStateShard.TransparencyStateShard(
            "additive_transparency", () -> {
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
    }, () -> {
        RenderSystem.disableBlend();
        RenderSystem.defaultBlendFunc();
    });
    protected static final RenderStateShard.CullStateShard NO_CULL = new RenderStateShard.CullStateShard(false);
    protected static final RenderStateShard.LightmapStateShard LIGHTMAP = new RenderStateShard.LightmapStateShard(true);
    protected static final RenderStateShard.OverlayStateShard OVERLAY = new RenderStateShard.OverlayStateShard(true);
    protected static final RenderStateShard.LightmapStateShard NO_LIGHTMAP = new RenderStateShard.LightmapStateShard(true);
    protected static final RenderStateShard.OverlayStateShard NO_OVERLAY = new RenderStateShard.OverlayStateShard(true);

    private static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_UNLIT_TRANSLUCENT_SHADER = new RenderStateShard.ShaderStateShard(ClientHooks.ClientEvents::getEntityTranslucentUnlitShader);
    private static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_UNLIT_CUTOUT_SHADER = new RenderStateShard.ShaderStateShard(ClinkerShaders::getEntityCutoutUnlitShader);
    private static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_UNLIT_CUTOUT_NOCULL_SHADER = new RenderStateShard.ShaderStateShard(ClinkerShaders::getEntityCutoutNoCullUnlitShader);
    private static final RenderStateShard.ShaderStateShard RENDERTYPE_CHAIN_LIGHTNING = new RenderStateShard.ShaderStateShard(ClinkerShaders::getChainLightningShader);
    private static final RenderStateShard.ShaderStateShard RENDERTYPE_FIRE_SPEW = new RenderStateShard.ShaderStateShard(ClinkerShaders::getFireSpewShader);

    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_UNLIT_TRANSLUCENT = Util.memoize((resourceLocation, outline) -> {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_ENTITY_UNLIT_TRANSLUCENT_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(outline);
        return RenderType.create("rendertype_entity_unlit_translucent", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, compositeState);
    });
    private static final Function<ResourceLocation, RenderType> ENTITY_UNLIT_CUTOUT = Util.memoize((resourceLocation) -> {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_ENTITY_UNLIT_CUTOUT_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(NO_TRANSPARENCY)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
        return RenderType.create("rendertype_entity_unlit_cutout", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, compositeState);
    });
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_UNLIT_CUTOUT_NOCULL = Util.memoize((resourceLocation, bool) -> {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_ENTITY_UNLIT_CUTOUT_NOCULL_SHADER)
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(NO_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(bool);
        return RenderType.create("rendertype_entity_unlit_cutout_nocull", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, compositeState);
    });
    private static final Function<ResourceLocation, RenderType> CHAIN_LIGHTNING = Util.memoize((resourceLocation) -> {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_CHAIN_LIGHTNING)
                .setTextureState(new RenderStateShard.TextureStateShard(resourceLocation, false, false))
                .setTransparencyState(ADDITIVE_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(false);
        return RenderType.create("rendertype_chain_lightning", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, false, compositeState);
    });
    private static final Function<ResourceLocation, RenderType> FIRE_SPEW = Util.memoize((resourceLocation) -> {
        RenderType.CompositeState compositeState = RenderType.CompositeState.builder()
                .setShaderState(RENDERTYPE_FIRE_SPEW)
                .setTextureState(new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_PARTICLES, false, false))
                .setTransparencyState(ADDITIVE_TRANSPARENCY)
                .setCullState(NO_CULL)
                .setLightmapState(NO_LIGHTMAP)
                .setOverlayState(NO_OVERLAY)
                .createCompositeState(false);
        return RenderType.create("rendertype_fire_spew", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 256, true, false, compositeState);
    });

    public static RenderType entityUnlitTranslucent(ResourceLocation pLocation, boolean pOutline) {
        return ENTITY_UNLIT_TRANSLUCENT.apply(pLocation, pOutline);
    }
    public static RenderType entityUnlitTranslucent(ResourceLocation pLocation) {
        return entityUnlitTranslucent(pLocation, true);
    }
    public static RenderType entityUnlitCutout(ResourceLocation pLocation) {
        return ENTITY_UNLIT_CUTOUT.apply(pLocation);
    }
    public static RenderType entityUnlitCutoutNoCull(ResourceLocation pLocation) {
        return ENTITY_UNLIT_CUTOUT_NOCULL.apply(pLocation, true);
    }
    public static RenderType chainLightning() {
        return CHAIN_LIGHTNING.apply(TextureAtlas.LOCATION_PARTICLES);
    }
    public static RenderType fireSpew() {
        return FIRE_SPEW.apply(TextureAtlas.LOCATION_PARTICLES);
    }

}
