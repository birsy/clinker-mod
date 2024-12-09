package birsy.clinker.core.registry;

import birsy.clinker.common.world.item.components.FuseTimer;
import birsy.clinker.common.world.item.components.LoadedItemStack;
import birsy.clinker.common.world.item.components.OrdnanceEffects;
import birsy.clinker.core.Clinker;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerDataComponents {
    public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Clinker.MOD_ID);

    // ordnance
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

    // alchemist's crossbow
    public static final Supplier<DataComponentType<LoadedItemStack>> LOADED_ITEM_STACK = DATA_COMPONENT_TYPES.registerComponentType(
            "loaded_item_stack",
            builder -> builder.networkSynchronized(LoadedItemStack.STREAM_CODEC)
                    .persistent(LoadedItemStack.CODEC)
    );
    public static final Supplier<DataComponentType<Unit>> REPEATER = DATA_COMPONENT_TYPES.registerComponentType(
            "repeater",
            builder -> builder.networkSynchronized(StreamCodec.unit(Unit.INSTANCE))
                              .persistent(Unit.CODEC)
    );
    public static final Supplier<DataComponentType<Boolean>> FIRING = DATA_COMPONENT_TYPES.registerComponentType(
            "firing",
            builder -> builder.networkSynchronized(ByteBufCodecs.BOOL)
    );
    public static final Supplier<DataComponentType<Integer>> TICK_DELAY = DATA_COMPONENT_TYPES.registerComponentType(
            "tick_delay",
            builder -> builder.networkSynchronized(ByteBufCodecs.INT)
    );
}
