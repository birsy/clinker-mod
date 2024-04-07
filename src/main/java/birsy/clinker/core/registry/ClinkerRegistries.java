package birsy.clinker.core.registry;

import birsy.clinker.common.world.entity.ordnance.modifer.OrdnanceModifier;
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
    public static final ResourceKey<Registry<OrdnanceModifier>> ORDNANCE_MODIFIER_REGISTRY_KEY =
            ResourceKey.createRegistryKey(new ResourceLocation(Clinker.MOD_ID, "ordnance_modifer"));
    public static final Registry<OrdnanceModifier> ORDNANCE_MODIFIERS = new RegistryBuilder<>(ORDNANCE_MODIFIER_REGISTRY_KEY).sync(true).create();

    @SubscribeEvent
    public static void addNewRegistries(NewRegistryEvent event) {
        event.register(ORDNANCE_MODIFIERS);
    }
}
