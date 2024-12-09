package birsy.clinker.core.registry;

import birsy.clinker.common.world.item.components.FuseTimer;
import birsy.clinker.common.world.item.components.OrdnanceEffects;
import birsy.clinker.core.Clinker;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.AddAttributeTooltipsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Clinker.MOD_ID);

    public static final Supplier<DataComponentType<OrdnanceEffects>> ORDNANCE_EFFECTS = DATA_COMPONENT_TYPES.registerComponentType(
            "ordnance_effects",
            builder -> builder.networkSynchronized(OrdnanceEffects.STREAM_CODEC)
                    .persistent(OrdnanceEffects.CODEC).cacheEncoding()
    );
    public static final Supplier<DataComponentType<FuseTimer>> FUSE_TIMER = DATA_COMPONENT_TYPES.registerComponentType(
            "fuse_timer",
            builder -> builder.networkSynchronized(FuseTimer.STREAM_CODEC)
                              .persistent(FuseTimer.CODEC)
    );
}
