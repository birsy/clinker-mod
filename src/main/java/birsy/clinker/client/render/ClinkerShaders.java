package birsy.clinker.client.render;

import birsy.clinker.core.Clinker;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Objects;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClinkerShaders {
    @Nullable
    private static ShaderInstance skyCloudShader;
    private static ShaderInstance rendertypeEntityCutoutUnlitShader;
    private static ShaderInstance rendertypeEntityCutoutNoCullUnlitShader;

    public static ShaderInstance getSkyCloudShader() {
        return Objects.requireNonNull(skyCloudShader, "Attempted to call getSkyCloudShader before shaders have finished loading.");
    }
    public static ShaderInstance getEntityCutoutUnlitShader() {
        return Objects.requireNonNull(rendertypeEntityCutoutUnlitShader, "Attempted to call getEntityCutoutUnlitShader before shaders have finished loading.");
    }
    public static ShaderInstance getEntityCutoutNoCullUnlitShader() {
        return Objects.requireNonNull(rendertypeEntityCutoutNoCullUnlitShader, "Attempted to call getEntityCutoutNoCullUnlitShader before shaders have finished loading.");
    }
    @SubscribeEvent
    public static void registerShaders(RegisterShadersEvent event) throws IOException {
        Clinker.LOGGER.info("registering shaders!");
        event.registerShader(new ShaderInstance(event.getResourceManager(), new ResourceLocation(Clinker.MOD_ID,"sky_cloud"), DefaultVertexFormat.POSITION_COLOR_TEX), (shader) -> skyCloudShader = shader);
        event.registerShader(new ShaderInstance(event.getResourceManager(), new ResourceLocation(Clinker.MOD_ID,"rendertype_entity_unlit_cutout"), DefaultVertexFormat.POSITION_COLOR_TEX), (shader) -> rendertypeEntityCutoutUnlitShader = shader);
        event.registerShader(new ShaderInstance(event.getResourceManager(), new ResourceLocation(Clinker.MOD_ID,"rendertype_entity_unlit_cutout_nocull"), DefaultVertexFormat.POSITION_COLOR_TEX), (shader) -> rendertypeEntityCutoutNoCullUnlitShader = shader);
    }
}
