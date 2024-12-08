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

@EventBusSubscriber(value = Dist.CLIENT, modid = Clinker.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ClinkerShaders {

    public static final ResourceLocation LIGHT_GREASE = Clinker.resource("light/grease");


    @Nullable
    private static ShaderInstance skyCloudShader;
    @Nullable
    private static ShaderInstance rendertypeEntityCutoutUnlitShader;
    @Nullable
    private static ShaderInstance rendertypeEntityCutoutNoCullUnlitShader;
    @Nullable
    private static ShaderInstance positionColorTextureUnclampedShader;
    @Nullable
    private static ShaderInstance positionColorUnclampedShader;
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
    @Nullable
    private static ShaderInstance skyStarShader;

    @Nullable
    private static ShaderInstance cloudShader;

    //@Nullable
    //private static ShaderInstance skinnedEntityShader;

    public static ShaderInstance getEntityCutoutUnlitShader() {
        return Objects.requireNonNull(rendertypeEntityCutoutUnlitShader, "Attempted to call getEntityCutoutUnlitShader before shaders have finished loading.");
    }
    public static ShaderInstance getEntityCutoutNoCullUnlitShader() {
        return Objects.requireNonNull(rendertypeEntityCutoutNoCullUnlitShader, "Attempted to call getEntityCutoutNoCullUnlitShader before shaders have finished loading.");
    }
    public static ShaderInstance getPositionColorTextureUnclampedShader() {
        return Objects.requireNonNull(positionColorTextureUnclampedShader, "Attempted to call getPositionColorTextureUnclampedShader before shaders have finished loading.");
    }
    public static ShaderInstance getPositionColorUnclampedShader() {
        return Objects.requireNonNull(positionColorUnclampedShader, "Attempted to call getPositionColorUnclampedShader before shaders have finished loading.");
    }
    public static ShaderInstance getChainLightningShader() {
        return Objects.requireNonNull(chainLightningShader, "Attempted to call getChainLightningShader before shaders have finished loading.");
    }
    public static ShaderInstance getFireSpewShader() {
        return Objects.requireNonNull(fireSpewShader, "Attempted to call getFireSpewShader before shaders have finished loading.");
    }

    public static ShaderInstance getSkyCloudShader() {
        return Objects.requireNonNull(skyCloudShader, "Attempted to call getSkyCloudShader before shaders have finished loading.");
    }
    public static ShaderInstance getSkyStarShader() {
        return Objects.requireNonNull(skyStarShader, "Attempted to call getSkyStarShader before shaders have finished loading.");
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

    public static ShaderInstance getCloudShader() {
        return Objects.requireNonNull(cloudShader, "Attempted to call getCloudShader before shaders have finished loading.");
    }

    //public static ShaderInstance getSkinnedEntityShader() {
    //    return Objects.requireNonNull(skinnedEntityShader, "Attempted to call getSkinnedEntityShader before shaders have finished loading.");
    //}

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

        //event.registerShader(new ShaderInstance(event.getResourceProvider(), Clinker.resource("skinned_entity"), NecromancerVertexFormat.SKINNED_ENTITY), (shader) -> skinnedEntityShader = shader);
    }


}
