package birsy.clinker.client.render;

import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

@EventBusSubscriber(value = Dist.CLIENT, modid = Clinker.MOD_ID)
public class ClinkerShaders {
    // veil shaders
    public static final ResourceLocation PAGE_TEXTURE = Clinker.resource("page/texture");
    public static final ResourceLocation VOLUME = Clinker.resource("volume/volume");
    public static final ResourceLocation CLOUD_DENSITY = Clinker.resource("cloud/cloud_density");
    public static final ResourceLocation CLOUD = Clinker.resource("cloud/cloud");

    public static final ResourceLocation LIGHT_GREASE = Clinker.resource("light/grease");
    public static final ResourceLocation LIGHT_RIM = Clinker.resource("light/rim");

    public static final ResourceLocation PARTICLE_BLOSSOM_BUG = Clinker.resource("particle/blossom_bug");

    public static final ResourceLocation FOG_LAYER = Clinker.resource("fog_layer");

    // vanilla shaders
    private static ShaderInstance rendertypeEntityCutoutUnlitShader;
    public static ShaderInstance getEntityCutoutUnlitShader() {
        return Objects.requireNonNull(rendertypeEntityCutoutUnlitShader, "Attempted to call getEntityCutoutUnlitShader before shaders have finished loading.");
    }

    private static ShaderInstance rendertypeEntityCutoutNoCullUnlitShader;
    public static ShaderInstance getEntityCutoutNoCullUnlitShader() {
        return Objects.requireNonNull(rendertypeEntityCutoutNoCullUnlitShader, "Attempted to call getEntityCutoutNoCullUnlitShader before shaders have finished loading.");
    }

    private static ShaderInstance positionColorTextureUnclampedShader;
    public static ShaderInstance getPositionColorTextureUnclampedShader() {
        return Objects.requireNonNull(positionColorTextureUnclampedShader, "Attempted to call getPositionColorTextureUnclampedShader before shaders have finished loading.");
    }

    private static ShaderInstance positionColorUnclampedShader;
    public static ShaderInstance getPositionColorUnclampedShader() {
        return Objects.requireNonNull(positionColorUnclampedShader, "Attempted to call getPositionColorUnclampedShader before shaders have finished loading.");
    }

    private static ShaderInstance chainLightningShader;
    public static ShaderInstance getChainLightningShader() {
        return Objects.requireNonNull(chainLightningShader, "Attempted to call getChainLightningShader before shaders have finished loading.");
    }

    private static ShaderInstance fireSpewShader;
    public static ShaderInstance getFireSpewShader() {
        return Objects.requireNonNull(fireSpewShader, "Attempted to call getFireSpewShader before shaders have finished loading.");
    }

    private static ShaderInstance skyCloudShader;
    public static ShaderInstance getSkyCloudShader() {
        return Objects.requireNonNull(skyCloudShader, "Attempted to call getSkyCloudShader before shaders have finished loading.");
    }

    private static ShaderInstance skyOuterShader;
    public static ShaderInstance getSkyOuterShader() {
        return Objects.requireNonNull(skyOuterShader, "Attempted to call getSkyOuterShader before shaders have finished loading.");
    }

    private static ShaderInstance skyOuterCloudShader;
    public static ShaderInstance getSkyOuterCloudShader() {
        return Objects.requireNonNull(skyOuterCloudShader, "Attempted to call getSkyOuterCloudShader before shaders have finished loading.");
    }

    private static ShaderInstance skyOuterStarShader;
    public static ShaderInstance getSkyOuterStarShader() {
        return Objects.requireNonNull(skyOuterStarShader, "Attempted to call getSkyOuterStarShader before shaders have finished loading.");
    }

    private static ShaderInstance skyStarShader;
    public static ShaderInstance getSkyStarShader() {
        return Objects.requireNonNull(skyStarShader, "Attempted to call getSkyStarShader before shaders have finished loading.");
    }

    private static ShaderInstance cloudShader;
    public static ShaderInstance getCloudShader() {
        return Objects.requireNonNull(cloudShader, "Attempted to call getCloudShader before shaders have finished loading.");
    }

    private static ShaderInstance vitriolShader;
    public static ShaderInstance getVitriolShader() {
        return Objects.requireNonNull(vitriolShader, "Attempted to call getVitriolShader before shaders have finished loading.");
    }

    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        Clinker.LOGGER.info("registering shaders!");
        event.registerShader(new ShaderInstance(event.getResourceProvider(), Clinker.resource("rendertype_entity_unlit_cutout"), DefaultVertexFormat.NEW_ENTITY), (shader) -> rendertypeEntityCutoutUnlitShader = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), Clinker.resource("rendertype_entity_unlit_cutout_nocull"), DefaultVertexFormat.NEW_ENTITY), (shader) -> rendertypeEntityCutoutNoCullUnlitShader = shader);

        event.registerShader(new ShaderInstance(event.getResourceProvider(), Clinker.resource("position_color_tex_unclamped"), DefaultVertexFormat.POSITION_TEX_COLOR), (shader) -> positionColorTextureUnclampedShader = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), Clinker.resource("position_color_unclamped"), DefaultVertexFormat.POSITION_COLOR), (shader) -> positionColorUnclampedShader = shader);

        event.registerShader(new ShaderInstance(event.getResourceProvider(), Clinker.resource("chain_lightning"), DefaultVertexFormat.NEW_ENTITY), (shader) -> chainLightningShader = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), Clinker.resource("fire_spew"), DefaultVertexFormat.NEW_ENTITY), (shader) -> fireSpewShader = shader);

        event.registerShader(new ShaderInstance(event.getResourceProvider(), Clinker.resource("sky_cloud"), DefaultVertexFormat.POSITION_TEX_COLOR), (shader) -> skyCloudShader = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), Clinker.resource("sky_star"), DefaultVertexFormat.POSITION_TEX_COLOR), (shader) -> skyStarShader = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), Clinker.resource("sky_outer"), DefaultVertexFormat.POSITION_TEX_COLOR), (shader) -> skyOuterShader = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), Clinker.resource("sky_outer_cloud"), DefaultVertexFormat.POSITION_TEX_COLOR), (shader) -> skyOuterCloudShader = shader);
        event.registerShader(new ShaderInstance(event.getResourceProvider(), Clinker.resource("sky_outer_star"), DefaultVertexFormat.POSITION_TEX_COLOR), (shader) -> skyOuterStarShader = shader);

        event.registerShader(new ShaderInstance(event.getResourceProvider(), Clinker.resource("cloud"), DefaultVertexFormat.POSITION_TEX_COLOR), (shader) -> cloudShader = shader);

        event.registerShader(new ShaderInstance(event.getResourceProvider(), Clinker.resource("vitriol"), DefaultVertexFormat.BLOCK), (shader) -> vitriolShader = shader);
    }


}
