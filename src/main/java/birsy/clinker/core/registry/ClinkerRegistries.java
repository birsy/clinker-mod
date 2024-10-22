package birsy.clinker.core.registry;

import birsy.clinker.core.Clinker;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClinkerRegistries {


    @SubscribeEvent
    public static void addNewRegistries(NewRegistryEvent event) {
        //event.register(ORDNANCE_MODIFIERS);
    }
}
