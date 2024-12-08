package birsy.clinker.core.registry;

import birsy.clinker.client.render.world.light.GreaseLight;
import birsy.clinker.client.render.world.light.GreaseLightRenderer;
import birsy.clinker.core.Clinker;
import foundry.veil.api.client.registry.LightTypeRegistry;
import foundry.veil.api.client.render.light.Light;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class ClinkerLightTypes {
    public static final DeferredRegister<LightTypeRegistry.LightType<?>> LIGHT_TYPES = DeferredRegister.create(LightTypeRegistry.REGISTRY, Clinker.MOD_ID);;

    public static final Supplier<LightTypeRegistry.LightType<GreaseLight>> GREASE = register(
            "grease",
            GreaseLightRenderer::new,
            (level, camera) -> new GreaseLight().setTo(camera).setRadius(15.0F)
    );

    private static <T extends Light> Supplier<LightTypeRegistry.LightType<T>> register(String name, LightTypeRegistry.RendererFactory<T> factory, @Nullable LightTypeRegistry.DebugLightFactory debugFactory) {
        return LIGHT_TYPES.register(name, () -> new LightTypeRegistry.LightType<>(factory, debugFactory));
    }
}
