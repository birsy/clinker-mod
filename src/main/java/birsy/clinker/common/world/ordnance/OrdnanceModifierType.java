package birsy.clinker.common.world.ordnance;

import birsy.clinker.core.registration.DataValue;
import birsy.clinker.core.registry.ClinkerRegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

public final class OrdnanceModifierType<T extends OrdnanceModifier<T>> {
    public static final Codec<OrdnanceModifierType<?>> CODEC =
            ClinkerRegistries.ORDNANCE_MODIFIER_TYPE_REGISTRY.byNameCodec();
    public static final StreamCodec<RegistryFriendlyByteBuf, OrdnanceModifierType<?>> STREAM_CODEC =
            ByteBufCodecs.registry(ClinkerRegistries.ORDNANCE_MODIFIER_TYPE_REGISTRY_KEY);

    private final static Object2ObjectOpenHashMap<OrdnanceModifierType, Set<OrdnanceModifierType>> MUTUAL_EXCLUSIONS = new Object2ObjectOpenHashMap<>();
    static { MUTUAL_EXCLUSIONS.defaultReturnValue(Set.of()); } // return empty set by default

    private final MapCodec<T> codec;
    private final StreamCodec<RegistryFriendlyByteBuf, T> streamCodec;
    private final Holder.Reference<OrdnanceModifierType<?>> builtInRegistryHolder =
            ClinkerRegistries.ORDNANCE_MODIFIER_TYPE_REGISTRY.createIntrusiveHolder(this);

    public OrdnanceModifierType(MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        this.codec = codec;
        this.streamCodec = streamCodec;
    }

    public static void setMutuallyExclusive(OrdnanceModifierType<?>... types) {
        for (OrdnanceModifierType<?> a : types) {
            Set<OrdnanceModifierType> set = MUTUAL_EXCLUSIONS.computeIfAbsent(a, type -> new HashSet<>());
            for (OrdnanceModifierType<?> b : types) {
                if (a == b) continue;
                set.add(b);
            }
        }
    }

    public static boolean areMutuallyExclusive(OrdnanceModifierType a, OrdnanceModifierType b) {
        return MUTUAL_EXCLUSIONS.get(a).contains(b);
    }

    public MapCodec<T> codec() { return codec; }
    public StreamCodec<RegistryFriendlyByteBuf, T> streamCodec() { return streamCodec; }

    public boolean is(TagKey<OrdnanceModifierType<?>> tag) {
        return this.builtInRegistryHolder.is(tag);
    }
    public boolean is(HolderSet<OrdnanceModifierType<?>> tag) { return tag.contains(this.builtInRegistryHolder); }

    public Holder.Reference<OrdnanceModifierType<?>> builtInRegistryHolder() {
        return builtInRegistryHolder;
    }
}
