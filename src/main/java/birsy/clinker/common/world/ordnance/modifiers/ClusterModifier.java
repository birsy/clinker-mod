package birsy.clinker.common.world.ordnance.modifiers;

import birsy.clinker.common.world.ordnance.OrdnanceModifier;
import birsy.clinker.common.world.ordnance.OrdnanceModifierSet;
import birsy.clinker.common.world.ordnance.OrdnanceModifierType;
import birsy.clinker.core.registry.ClinkerOrdnanceModifierTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public final class ClusterModifier implements OrdnanceModifier<ClusterModifier> {
    public static final MapCodec<ClusterModifier> CODEC =
            RecordCodecBuilder.mapCodec(
                    instance -> instance
                            .group(OrdnanceModifierSet.CODEC.fieldOf("sub_bomb").forGetter(modifier -> modifier.subBombModifiers))
                            .apply(instance, ClusterModifier::new)
            );
    public static final StreamCodec<RegistryFriendlyByteBuf, ClusterModifier> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, modifier) -> OrdnanceModifierSet.STREAM_CODEC.encode(buffer, modifier.subBombModifiers),
                    (buffer) -> new ClusterModifier(OrdnanceModifierSet.STREAM_CODEC.decode(buffer))
            );

    public final OrdnanceModifierSet subBombModifiers;

    public ClusterModifier(OrdnanceModifierSet subBombModifiers) {
        this.subBombModifiers = subBombModifiers;
    }

    @Override
    public boolean canMerge(OrdnanceModifier modifier) {
        return false;
    }

    @Override
    public @Nullable ClusterModifier merge(OrdnanceModifier modifier) {
        return null;
    }

    @Override
    public void tooltip(@Nullable OrdnanceModifierSet set, Consumer<Component> tooltipAdder) {
        tooltipAdder.accept(
                Component.translatable("ordnance_modifier.clinker.cluster")
                        .withStyle(Style.EMPTY.withColor(0x694037))
        );
        subBombModifiers.appendTooltips(subTooltipAdder(tooltipAdder));
    }

    private Consumer<Component> subTooltipAdder(Consumer<Component> tooltipAdder) {
        return (component) -> tooltipAdder.accept(Component.literal("└ ").withStyle(Style.EMPTY.withColor(0x694037)).append(component));
    }

    @Override
    public OrdnanceModifierType<?> type() {
        return ClinkerOrdnanceModifierTypes.CLUSTER.get();
    }
}
