package birsy.clinker.core.registry;

import birsy.clinker.client.render.world.light.GreaseLightData;
import birsy.clinker.client.render.world.light.InstancedGreaseLightRenderer;
import birsy.clinker.core.Clinker;
import foundry.veil.api.client.registry.LightTypeRegistry;
import foundry.veil.api.client.render.light.data.LightData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ClinkerLightTypes {
    public static final DeferredRegister<LightTypeRegistry.LightType<?>> LIGHT_TYPES = DeferredRegister.create(LightTypeRegistry.REGISTRY, Clinker.MOD_ID);;

    public static final Supplier<LightTypeRegistry.LightType<GreaseLightData>> GREASE = register(
            "grease",
            InstancedGreaseLightRenderer::new,
            (level, camera) -> new GreaseLightData().setTo(camera).setRadius(15.0F)
    );
    private static <T extends LightData> Supplier<LightTypeRegistry.LightType<T>> register(String name, LightTypeRegistry.RendererFactory<T> factory, @Nullable LightTypeRegistry.DebugLightFactory debugFactory) {
        return LIGHT_TYPES.register(name, () -> new LightTypeRegistry.LightType<>(factory, debugFactory));
    }
}
