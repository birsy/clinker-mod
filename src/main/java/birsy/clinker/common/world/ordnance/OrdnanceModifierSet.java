package birsy.clinker.common.world.ordnance;

import birsy.clinker.core.registry.ClinkerRegistries;
import birsy.clinker.core.registry.ClinkerTags;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Consumer;

public record OrdnanceModifierSet(ImmutableMap<OrdnanceModifierType<?>, OrdnanceModifier<?>> map) implements TooltipProvider {
    public static final Codec<OrdnanceModifierSet> CODEC =
            RecordCodecBuilder.create(
                    instance -> instance.group(
                            OrdnanceModifier.CODEC.listOf().fieldOf("modifiers").forGetter(set -> set.map.values().asList())
                    ).apply(instance, OrdnanceModifierSet::of)
            );
    public static final StreamCodec<RegistryFriendlyByteBuf, OrdnanceModifierSet> STREAM_CODEC =
            StreamCodec.composite(
                    OrdnanceModifier.STREAM_CODEC.apply(ByteBufCodecs.list()),
                    set -> set.map.values().asList(),
                    OrdnanceModifierSet::of
            );

    public static final OrdnanceModifierSet NONE = new OrdnanceModifierSet(ImmutableMap.of());

    public static OrdnanceModifierSet of(OrdnanceModifier<?>... modifiers) {
        ImmutableMap.Builder<OrdnanceModifierType<?>, OrdnanceModifier<?>> builder = ImmutableMap.builderWithExpectedSize(modifiers.length);
        for (OrdnanceModifier<?> ordnanceModifier : modifiers) builder.put(ordnanceModifier.type(), ordnanceModifier);
        return new OrdnanceModifierSet(builder.build());
    }

    public static OrdnanceModifierSet of(Collection<OrdnanceModifier<?>> modifiers) {
        ImmutableMap.Builder<OrdnanceModifierType<?>, OrdnanceModifier<?>> builder = ImmutableMap.builderWithExpectedSize(modifiers.size());
        for (OrdnanceModifier<?> ordnanceModifier : modifiers) builder.put(ordnanceModifier.type(), ordnanceModifier);
        return new OrdnanceModifierSet(builder.build());
    }

    public Tag serialize(RegistryAccess registryAccess) {
        if (this.map.isEmpty()) return new CompoundTag();
        return CODEC.encodeStart(registryAccess.createSerializationContext(NbtOps.INSTANCE), this).getOrThrow();
    }
    public static OrdnanceModifierSet deserialize(Tag tag, RegistryAccess registryAccess) {
        if (tag == null) return NONE;
        return CODEC.parse(registryAccess.createSerializationContext(NbtOps.INSTANCE), tag).result().orElse(NONE);
    }

    @Nullable
    public <T extends OrdnanceModifier<T>> T getModifier(OrdnanceModifierType<T> modifierType) {
        return (T) map.getOrDefault(modifierType, null);
    }

    public boolean hasModifier(TagKey<OrdnanceModifierType<?>> tag) {
        Optional<HolderSet.Named<OrdnanceModifierType<?>>> tagHolder = ClinkerRegistries.ORDNANCE_MODIFIER_TYPE_REGISTRY.getTag(tag);
        if (tagHolder.isEmpty()) return false;

        HolderSet.Named<OrdnanceModifierType<?>> tagSet = tagHolder.get();
        for (OrdnanceModifierType<?> type : this.map.keySet()) {
            Optional<ResourceKey<OrdnanceModifierType<?>>> key = ClinkerRegistries.ORDNANCE_MODIFIER_TYPE_REGISTRY.getResourceKey(type);
            if (key.isEmpty()) continue;
            Optional<Holder.Reference<OrdnanceModifierType<?>>> holder = ClinkerRegistries.ORDNANCE_MODIFIER_TYPE_REGISTRY.getHolder(key.get());
            if (holder.isEmpty()) continue;
            if (tagSet.contains(holder.get())) return true;
        }
        return false;
    }

    public boolean hasModifier(OrdnanceModifierType<?> modifierType) {
        return map.containsKey(modifierType);
    }

    public OrdnanceGradient gradient() {
        OrdnanceGradient gradient = new OrdnanceGradient();
        List<OrdnanceModifier> modifiersByGradientOrder = new ArrayList<>(this.map().values());
        modifiersByGradientOrder.sort(Comparator.<OrdnanceModifier>comparingInt(OrdnanceModifier::gradientModificationOrder)
                                                .thenComparingInt(modifier -> ClinkerRegistries.ORDNANCE_MODIFIER_TYPE_REGISTRY.getIdOrThrow(modifier.type())));
        for (OrdnanceModifier ordnanceModifier : modifiersByGradientOrder) {
            gradient = ordnanceModifier.mutateGradient(gradient);
        }
        return gradient;
    }

    public boolean canAddModifier(OrdnanceModifier<?> modifier) {
        for (OrdnanceModifier<?> existingModifier : map.values()) {
            if (!existingModifier.canAddModifier(this, modifier)) return false;
        }
        return true;
    }

    public OrdnanceModifierSet appendModifier(OrdnanceModifier<?> modifier) {
        if (!this.canAddModifier(modifier))
            throw new IllegalArgumentException("Modifier " + modifier + " incompatible with current modifier set!");

        Map<OrdnanceModifierType<?>, OrdnanceModifier<?>> clonedMap = new HashMap<>(map);
        if (clonedMap.containsKey(modifier.type())) {
            OrdnanceModifier<?> currentInstance = clonedMap.get(modifier.type());
            OrdnanceModifier<?> mergedModifier = currentInstance.merge(modifier);

            if (!currentInstance.canMerge(modifier) || mergedModifier == null)
                throw new IllegalArgumentException("Modifier " + modifier + " incompatible with current modifier set!");

            clonedMap.replace(modifier.type(), mergedModifier);
        } else {
            clonedMap.put(modifier.type(), modifier);
        }

        return new OrdnanceModifierSet(ImmutableMap.copyOf(clonedMap));
    }

    public void appendTooltips(Consumer<Component> tooltipAdder) {
        boolean hasDetonationModifier = this.hasModifier(ClinkerTags.OrdnanceModifiers.DETONATES);
        boolean hasDetonationCausingModifier = this.hasModifier(ClinkerTags.OrdnanceModifiers.CAUSES_DETONATION);

        if (!hasDetonationModifier)
            tooltipAdder.accept(Component.translatable("ordnance_modifier.clinker.dud").withStyle(Style.EMPTY.withColor(ChatFormatting.DARK_GRAY)));

        if (hasDetonationModifier && !hasDetonationCausingModifier)
            tooltipAdder.accept(Component.translatable("ordnance_modifier.clinker.fuseless").withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));

        this.map().values().stream().sorted(
                Comparator.<OrdnanceModifier>comparingInt(OrdnanceModifier::textOrder)
                        .thenComparingInt(modifier -> ClinkerRegistries.ORDNANCE_MODIFIER_TYPE_REGISTRY.getIdOrThrow(modifier.type()))
        ).forEachOrdered(modifier -> modifier.tooltip(this, tooltipAdder));
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        appendTooltips(tooltipAdder);
    }
}
