package birsy.clinker.core.registry;

import birsy.clinker.core.Clinker;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

@EventBusSubscriber(modid = Clinker.MOD_ID)
public class ClinkerDataMaps {
    public static final DataMapType<Item, Float> ITEM_CONDUCTIVITY = DataMapType.builder(
            Clinker.resource("item_conductivity"),
            Registries.ITEM,
            Codec.FLOAT
    ).synced(Codec.FLOAT, true).build();

    public static final DataMapType<EntityType<?>, Float> ENTITY_CONDUCTIVITY = DataMapType.builder(
            Clinker.resource("entity_conductivity"),
            Registries.ENTITY_TYPE,
            Codec.FLOAT
    ).synced(Codec.FLOAT, true).build();

    @SubscribeEvent
    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(ITEM_CONDUCTIVITY);
        event.register(ENTITY_CONDUCTIVITY);
    }
}
