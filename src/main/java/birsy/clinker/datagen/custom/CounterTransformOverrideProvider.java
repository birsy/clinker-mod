package birsy.clinker.datagen.custom;

import birsy.clinker.client.resource.CounterTransformOverrideResource;
import birsy.clinker.client.resource.CounterTransformOverrideResource.TransformOverrideHolder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.*;
import java.util.concurrent.CompletableFuture;

public abstract class CounterTransformOverrideProvider implements DataProvider {
    private final PackOutput.PathProvider pathProvider;
    private final String namespace;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    private HolderLookup.Provider activeProvider;
    private Map<ResourceLocation, TransformOverrideHolder> activeMap;

    public CounterTransformOverrideProvider(PackOutput output, String namespace, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.pathProvider = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, CounterTransformOverrideResource.DIRECTORY);
        this.namespace = namespace;
        this.lookupProvider = lookupProvider;
    }

    protected abstract void gather();

    @Override
    public CompletableFuture<?> run(CachedOutput cachedOutput) {
        return this.lookupProvider.thenCompose(provider -> {
            this.activeProvider = provider;
            this.activeMap = new LinkedHashMap<>();

            gather();

            RegistryOps<JsonElement> ops = provider.createSerializationContext(JsonOps.INSTANCE);
            List<CompletableFuture<?>> futures = new ArrayList<>();
            activeMap.forEach((id, holder) -> {
                JsonElement json = TransformOverrideHolder.CODEC.encodeStart(ops, holder).getOrThrow();
                futures.add(DataProvider.saveStable(cachedOutput, json, pathProvider.json(id)));
            });

            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }

    protected OverrideBuilder create(String path) {
        return create(ResourceLocation.fromNamespaceAndPath(namespace, path));
    }
    protected OverrideBuilder create(ResourceLocation id) {
        return new OverrideBuilder(this, id);
    }

    @Override
    public String getName() {
        return "counter transform overrides";
    }

    public static final class OverrideBuilder {
        private final CounterTransformOverrideProvider provider;
        private final ResourceLocation id;
        private Optional<HolderSet<Item>> applyTo = Optional.empty();
        private Optional<ResourceLocation> parent = Optional.empty();
        private Optional<Matrix4f> transform = Optional.empty();
        private Optional<Vector3f> stackOffset = Optional.empty();
        private Optional<Vector3f> stackOffsetRandom = Optional.empty();
        private Optional<Float> stackAngleRandom = Optional.empty();

        private OverrideBuilder(CounterTransformOverrideProvider provider, ResourceLocation id) {
            this.provider = provider;
            this.id = id;
        }

        public OverrideBuilder applyTo(ItemLike... items) {
            List<Holder<Item>> holders = Arrays.stream(items)
                    .map(i -> i.asItem().builtInRegistryHolder().getDelegate())
                    .toList();
            this.applyTo = Optional.of(HolderSet.direct(holders));
            return this;
        }
        public OverrideBuilder applyTo(TagKey<Item> tag) {
            this.applyTo = Optional.of(this.provider.activeProvider.lookupOrThrow(Registries.ITEM).getOrThrow(tag));
            return this;
        }
        public OverrideBuilder applyTo(HolderSet<Item> items) {
            this.applyTo = Optional.of(items);
            return this;
        }

        public OverrideBuilder parent(ResourceLocation parentId) {
            this.parent = Optional.of(parentId);
            return this;
        }
        public OverrideBuilder parent(String path) {
            this.parent = Optional.of(ResourceLocation.fromNamespaceAndPath(this.provider.namespace, path));
            return this;
        }
        public OverrideBuilder transform(Matrix4f transform) {
            this.transform = Optional.of(transform);
            return this;
        }
        public OverrideBuilder stackOffset(float value) {
            return this.stackOffset(value, value, value);
        }
        public OverrideBuilder stackOffset(float x, float y, float z) {
            this.stackOffset = Optional.of(new Vector3f(x, y, z));
            return this;
        }
        public OverrideBuilder stackOffsetRandom(float value) {
            return this.stackOffsetRandom(value, value, value);
        }
        public OverrideBuilder stackOffsetRandom(float x, float y, float z) {
            this.stackOffsetRandom = Optional.of(new Vector3f(x, y, z));
            return this;
        }
        public OverrideBuilder stackAngleRandom(float degrees) {
            this.stackAngleRandom = Optional.of(degrees);
            return this;
        }

        public void build() {
            TransformOverrideHolder resource = new TransformOverrideHolder(applyTo, parent, transform, stackOffset, stackOffsetRandom, stackAngleRandom);
            if (this.provider.activeMap.containsKey(this.id))
                throw new IllegalStateException("duplicate counter transform override id: " + this.id);
            this.provider.activeMap.put(this.id, resource);
        }
    }
}