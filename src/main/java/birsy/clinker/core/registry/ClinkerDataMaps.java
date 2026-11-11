package birsy.clinker.core.registry;

import birsy.clinker.client.render.world.blockentity.CounterRenderer;
import birsy.clinker.core.Clinker;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.datamaps.AdvancedDataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.DataMapValueMerger;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;

import java.util.ArrayList;
import java.util.List;

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

    public static final DataMapType<EntityType<?>, HolderSet<Block>> ENTITY_SKIP_COLLISIONS = AdvancedDataMapType
            .builder(Clinker.resource("entity_skip_collisions"), Registries.ENTITY_TYPE, RegistryCodecs.homogeneousList(Registries.BLOCK))
            .synced(RegistryCodecs.homogeneousList(Registries.BLOCK), true)
            .merger((registry, first, firstValue, second, secondValue) -> {
                List<Holder<Block>> combined = new ArrayList<>();
                firstValue.forEach(combined::add);
                secondValue.forEach(combined::add);
                return HolderSet.direct(combined);
            }).build();

    @SubscribeEvent
    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(ITEM_CONDUCTIVITY);
        event.register(ENTITY_CONDUCTIVITY);
    }
}
