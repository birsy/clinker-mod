package birsy.clinker.client.render;

import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClinkerShaders {
    @Nullable
    private static ShaderInstance skyCloudShader;
    @Nullable
    private static ShaderInstance rendertypeEntityCutoutUnlitShader;
    @Nullable
    private static ShaderInstance rendertypeEntityCutoutNoCullUnlitShader;
    @Nullable
    private static ShaderInstance positionColorTextureUnclampedShader;
    @Nullable
    private static ShaderInstance chainLightningShader;
    @Nullable
    private static ShaderInstance fireSpewShader;

    @Nullable
    private static ShaderInstance skyOuterShader;
    @Nullable
    private static ShaderInstance skyOuterCloudShader;
    @Nullable
    private static ShaderInstance skyOuterStarShader;

    public static ShaderInstance getSkyCloudShader() {
        return Objects.requireNonNull(skyCloudShader, "Attempted to call getSkyCloudShader before shaders have finished loading.");
    }
    public static ShaderInstance getEntityCutoutUnlitShader() {
        return Objects.requireNonNull(rendertypeEntityCutoutUnlitShader, "Attempted to call getEntityCutoutUnlitShader before shaders have finished loading.");
    }
    public static ShaderInstance getEntityCutoutNoCullUnlitShader() {
        return Objects.requireNonNull(rendertypeEntityCutoutNoCullUnlitShader, "Attempted to call getEntityCutoutNoCullUnlitShader before shaders have finished loading.");
    }
    public static ShaderInstance getPositionColorTextureUnclampedShader() {
        return Objects.requireNonNull(positionColorTextureUnclampedShader, "Attempted to call getPositionColorTextureUnclampedShader before shaders have finished loading.");
    }
    public static ShaderInstance getChainLightningShader() {
        return Objects.requireNonNull(chainLightningShader, "Attempted to call getChainLightningShader before shaders have finished loading.");
    }
    public static ShaderInstance getFireSpewShader() {
        return Objects.requireNonNull(fireSpewShader, "Attempted to call getFireSpewShader before shaders have finished loading.");
    }
    public static ShaderInstance getSkyOuterShader() {
        return Objects.requireNonNull(skyOuterShader, "Attempted to call getSkyOuterShader before shaders have finished loading.");
    }
    public static ShaderInstance getSkyOuterCloudShader() {
        return Objects.requireNonNull(skyOuterCloudShader, "Attempted to call getSkyOuterCloudShader before shaders have finished loading.");
    }
    public static ShaderInstance getSkyOuterStarShader() {
        return Objects.requireNonNull(skyOuterStarShader, "Attempted to call getSkyOuterStarShader before shaders have finished loading.");
    }
    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        Clinker.LOGGER.info("registering shaders!");
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(Clinker.MOD_ID,"sky_cloud"), DefaultVertexFormat.POSITION_COLOR_TEX), (shader) -> skyCloudShader = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(Clinker.MOD_ID,"rendertype_entity_unlit_cutout"), DefaultVertexFormat.NEW_ENTITY), (shader) -> rendertypeEntityCutoutUnlitShader = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(Clinker.MOD_ID,"rendertype_entity_unlit_cutout_nocull"), DefaultVertexFormat.NEW_ENTITY), (shader) -> rendertypeEntityCutoutNoCullUnlitShader = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(Clinker.MOD_ID,"position_color_tex_unclamped"), DefaultVertexFormat.POSITION_COLOR_TEX), (shader) -> positionColorTextureUnclampedShader = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(Clinker.MOD_ID,"chain_lightning"), DefaultVertexFormat.NEW_ENTITY), (shader) -> chainLightningShader = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(Clinker.MOD_ID,"fire_spew"), DefaultVertexFormat.NEW_ENTITY), (shader) -> fireSpewShader = shader);

        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(Clinker.MOD_ID,"sky_outer"), DefaultVertexFormat.POSITION_TEX_COLOR), (shader) -> skyOuterShader = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(Clinker.MOD_ID,"sky_outer_cloud"), DefaultVertexFormat.POSITION_TEX_COLOR), (shader) -> skyOuterCloudShader = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), new ResourceLocation(Clinker.MOD_ID,"sky_outer_star"), DefaultVertexFormat.POSITION_TEX_COLOR), (shader) -> skyOuterStarShader = shader);
    }


}
