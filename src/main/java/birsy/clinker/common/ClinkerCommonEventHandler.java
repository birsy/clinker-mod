package birsy.clinker.common;

import birsy.clinker.client.render.page.PageAtlas;
import birsy.clinker.common.page.Page;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerDynamicRegistries;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.server.MinecraftServer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@EventBusSubscriber(modid = Clinker.MOD_ID)
public class ClinkerCommonEventHandler {
    @SubscribeEvent
    public static void onDataLoad(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        // set up the page key lookup.
        Page.KEY_LOOKUP.clear();
        for (Holder.Reference<Page> pageReference : server.registryAccess().lookupOrThrow(ClinkerDynamicRegistries.PAGE_REGISTRY_KEY).listElements().toList()) {
            Page.KEY_LOOKUP.put(pageReference.value(), pageReference.getKey());
        }
    }
}
