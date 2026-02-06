package birsy.clinker.core.registry;

import birsy.clinker.common.page.Page;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureSpawnSet;
import birsy.clinker.core.Clinker;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;

@EventBusSubscriber(modid = Clinker.MOD_ID)
public class ClinkerDynamicRegistries {
    public static final ResourceKey<Registry<Page>> PAGE_REGISTRY_KEY =
            ResourceKey.createRegistryKey(Clinker.resource("pages"));

    @SubscribeEvent
    public static void registerDatapackRegistries(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(
                PAGE_REGISTRY_KEY,
                Page.CODEC, Page.CODEC
        );
    }
}
