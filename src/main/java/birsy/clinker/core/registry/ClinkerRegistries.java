package birsy.clinker.core.registry;

import birsy.clinker.common.alchemy.chemicals.Chemical;
import birsy.clinker.core.Clinker;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;

import java.util.function.Supplier;

public class ClinkerRegistries {
    public static Supplier<IForgeRegistry<Chemical>> CHEMICAL;

    @Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public class RegistryEvents {

        @SubscribeEvent
        public static void registryEvent(NewRegistryEvent event) {
            CHEMICAL = event.create(new RegistryBuilder<Chemical>()
                                       .setIDRange(0, 0x0FFFFF)
                                       .setName(new ResourceLocation(Clinker.MOD_ID, "chemical")));
        }

    }
}
