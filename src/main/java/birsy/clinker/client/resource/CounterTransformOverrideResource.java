package birsy.clinker.client.resource;

import birsy.clinker.client.render.world.blockentity.CounterRenderer.CounterTransformOverride;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.codecs.ExtraExtraCodecs;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.Pair;
import net.minecraft.client.Minecraft;
import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.VanillaRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.*;
import java.util.stream.Stream;

public class CounterTransformOverrideResource {
    public static final String DIRECTORY = "counter_transform_overrides";

    public static class Authority {
        public static final Authority INSTANCE = new Authority();
        private Map<Item, CounterTransformOverride> overridesByItem = Map.of();
        private Map<TagKey<Item>, CounterTransformOverride> overridesByTag = Map.of();
        private Map<Item, CounterTransformOverride> builtOverrides = Map.of();

        // because we can specify item tags in the overrides rather than just item ids,
        // but tags aren't actually loaded until the world loads (or on data reload)
        // we have to do some translation shenanigans in the meantime...
        public void buildOverrides(HolderLookup<Item> registry) {
            ImmutableMap.Builder<Item, CounterTransformOverride> builder = ImmutableMap.builder();

            // load items from tags
            // sorted from least specific to most specific, so more specific tags override less specific ones
            List<Pair<HolderSet.Named<Item>, CounterTransformOverride>> tagOverrides = overridesByTag.entrySet().stream()
                    .flatMap(entry -> registry.get(entry.getKey()).stream()
                            .map(holderSet -> Pair.of(holderSet, entry.getValue())))
                    .sorted(Comparator.comparingInt(entry -> entry.key().size()))
                    .toList().reversed();
            for (Pair<HolderSet.Named<Item>, CounterTransformOverride> tagOverride : tagOverrides) {
                for (Holder<Item> itemHolder : tagOverride.key())
                    builder.put(itemHolder.value(), tagOverride.value());
            }
            // load regular items
            for (Map.Entry<Item, CounterTransformOverride> entry : overridesByItem.entrySet())
                builder.put(entry.getKey(), entry.getValue());

            builtOverrides = builder.buildKeepingLast();
        }

        @Nullable
        public CounterTransformOverride get(Item item) {
            return builtOverrides.get(item);
        }
    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = Clinker.MOD_ID)
    public static class Reloader extends SimpleJsonResourceReloadListener {
        private static final Gson GSON = new GsonBuilder().create();
        private static final RegistryOps<JsonElement> ITEM_REGISTRY_OPS =
                HolderLookup.Provider.create(Stream.of(BuiltInRegistries.ITEM.asLookup()))
                        .createSerializationContext(JsonOps.INSTANCE);

        public Reloader() {
            super(GSON, DIRECTORY);
        }

        @Override
        protected void apply(Map<ResourceLocation, JsonElement> jsonMap, ResourceManager resourceManager, ProfilerFiller profiler) {
            Map<ResourceLocation, TransformOverrideHolder> holderMap = new HashMap<>();
            for (Map.Entry<ResourceLocation, JsonElement> entry : jsonMap.entrySet()) {
                ResourceLocation id = entry.getKey();
                JsonElement json = entry.getValue();
                TransformOverrideHolder.CODEC.parse(ITEM_REGISTRY_OPS, json)
                        .resultOrPartial(error -> Clinker.LOGGER.error("failed to parse counter transform override for {}: {}", id, error))
                        .ifPresent(result -> holderMap.put(id, result));
            }

            List<Map.Entry<ResourceLocation, TransformOverrideHolder>> itemsNeedingResolution =
                    holderMap.entrySet().stream()
                            .filter(entry -> entry.getValue().applyTo.isPresent())
                            .sorted(Comparator.comparingInt(holder -> holder.getValue().applyTo.get().size()))
                            .toList().reversed();

            Map<ResourceLocation, CounterTransformOverride> resolvedMap = new HashMap<>(holderMap.size());
            Map<Item, CounterTransformOverride> byItem = new HashMap<>();
            Map<TagKey<Item>, CounterTransformOverride> byTag = new HashMap<>();

            for (Map.Entry<ResourceLocation, TransformOverrideHolder> entry : itemsNeedingResolution) {
                ResourceLocation id = entry.getKey();
                TransformOverrideHolder holder = entry.getValue();

                try {
                    HolderSet<Item> items = holder.applyTo.get();
                    CounterTransformOverride resolved = holder.resolve(id, holderMap, resolvedMap, new LinkedHashSet<>());
                    if (items instanceof HolderSet.Named<Item> named) {
                        byTag.put(named.key(), resolved);
                    } else {
                        for (Holder<Item> item : items) byItem.put(item.value(), resolved);
                    }
                } catch (Exception error) {
                    Clinker.LOGGER.error("failed to resolve counter transform override for {}: {}", id, error);
                }
            }

            Authority.INSTANCE.overridesByItem = ImmutableMap.copyOf(byItem);
            Authority.INSTANCE.overridesByTag = ImmutableMap.copyOf(byTag);
            if (Minecraft.getInstance().level != null)
                Authority.INSTANCE.buildOverrides(Minecraft.getInstance().level.holderLookup(Registries.ITEM));
        }

        @SubscribeEvent
        public static void onTagsUpdated(TagsUpdatedEvent event) {
            Clinker.LOGGER.error("LOOK HERE: RUNNING TAGS UPDATED!!!!!");
            HolderLookup.Provider registries = event.getRegistryAccess();
            CounterTransformOverrideResource.Authority.INSTANCE.buildOverrides(registries.lookupOrThrow(Registries.ITEM));
        }
    }

    public record TransformOverrideHolder(
            Optional<HolderSet<Item>> applyTo,
            Optional<ResourceLocation> parent,
            Optional<Matrix4f> transform,
            Optional<Vector3f> stackOffset,
            Optional<Vector3f> stackOffsetRandom,
            Optional<Float> stackAngleRandom) {
        public static final Codec<TransformOverrideHolder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                        RegistryCodecs.homogeneousList(BuiltInRegistries.ITEM.key()).optionalFieldOf("apply_to").forGetter(TransformOverrideHolder::applyTo),
                        ResourceLocation.CODEC.optionalFieldOf("parent").forGetter(TransformOverrideHolder::parent),
                        ExtraExtraCodecs.FANCY_MATRIX4F.optionalFieldOf("transform").forGetter(TransformOverrideHolder::transform),
                        ExtraExtraCodecs.FANCY_VECTOR3F.optionalFieldOf("stack_offset").forGetter(TransformOverrideHolder::stackOffset),
                        ExtraExtraCodecs.FANCY_VECTOR3F.optionalFieldOf("stack_offset_randomization").forGetter(TransformOverrideHolder::stackOffsetRandom),
                        Codec.FLOAT.optionalFieldOf("stack_angle_randomization").forGetter(TransformOverrideHolder::stackAngleRandom)
                ).apply(instance, TransformOverrideHolder::new)
        );

        private CounterTransformOverride resolve(ResourceLocation key,
                                         Map<ResourceLocation, TransformOverrideHolder> holderMap,
                                         Map<ResourceLocation, CounterTransformOverride> resolvedMap,
                                         Set<ResourceLocation> visited) {
            if (visited.contains(key))
                throw new IllegalStateException("cyclic dependency detected ! full parent chain: " + visited);
            visited.add(key);

            CounterTransformOverride resolvedParent;
            if (parent.isPresent()) {
                ResourceLocation parentKey = parent.get();
                resolvedParent = resolvedMap.get(parentKey);
                if (resolvedParent == null) {
                    TransformOverrideHolder parentHolder = holderMap.get(parentKey);
                    if (parentHolder == null)
                        throw new IllegalStateException("unknown parent " + parentKey + " referenced by " + key);

                    resolvedParent = parentHolder.resolve(parentKey, holderMap, resolvedMap, visited);
                }
            } else {
                resolvedParent = CounterTransformOverride.DEFAULT;
            }
            CounterTransformOverride resolved = new CounterTransformOverride(
                    transform.orElse(resolvedParent.transform()),
                    stackOffset.orElse(resolvedParent.stackOffset()),
                    stackOffsetRandom.orElse(resolvedParent.stackOffsetRandom()),
                    stackAngleRandom.orElse(resolvedParent.stackAngleRandom())
            );
            resolvedMap.put(key, resolved);
            return resolved;
        }
    }
}
