package birsy.clinker.common.entity.system;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerDataMaps;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.registries.datamaps.DataMapsUpdatedEvent;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(modid = Clinker.MOD_ID)
public enum EntityCollisionCache {
    INSTANCE;

    private final Map<EntityType<?>, Set<Block>> cache = new ConcurrentHashMap<>();

    public Set<Block> getSkippedBlocks(@Nullable Entity entity) {
        if (entity == null) return Set.of();
        return cache.computeIfAbsent(entity.getType(), type -> {
            HolderSet<Block> holderSet = type.builtInRegistryHolder()
                    .getData(ClinkerDataMaps.ENTITY_SKIP_COLLISIONS);
            if (holderSet == null) return Set.of();

            Set<Block> set = new HashSet<>(holderSet.size());
            for (Holder<Block> blockHolder : holderSet)
                set.add(blockHolder.value());

            return set;
        });
    }

    @SubscribeEvent
    public static void refreshCacheFromDataMaps(DataMapsUpdatedEvent event) { INSTANCE.cache.clear(); }
    @SubscribeEvent
    public static void refreshCacheFromTags(TagsUpdatedEvent event) { INSTANCE.cache.clear(); }
}
