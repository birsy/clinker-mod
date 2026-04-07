package birsy.clinker.common.world.ordnance.modifiers;

import birsy.clinker.common.world.ordnance.OrdnanceModifier;
import birsy.clinker.common.world.ordnance.OrdnanceModifierSet;
import birsy.clinker.common.world.ordnance.OrdnanceModifierType;
import birsy.clinker.core.registry.ClinkerOrdnanceModifierTypes;
import birsy.clinker.core.registry.ClinkerTags;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public record FuseTimeModifier(int level) implements OrdnanceModifier<FuseTimeModifier> {
    public static final MapCodec<FuseTimeModifier> CODEC =
            RecordCodecBuilder.mapCodec(
                    instance -> instance
                            .group(Codec.INT.fieldOf("level").forGetter(modifier -> modifier.level))
                            .apply(instance, FuseTimeModifier::new)
            );
    public static final StreamCodec<RegistryFriendlyByteBuf, FuseTimeModifier> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, modifier) -> buffer.writeInt(modifier.level),
                    (buffer) -> new FuseTimeModifier(buffer.readInt())
            );
    public static final int DEFAULT_FUSE_TIME = 120;
    public static final int MIN_LEVEL = -2, MAX_LEVEL = 2;

    public static int getFuseTicks(OrdnanceModifierSet modifiers) {
        if (modifiers == null) return DEFAULT_FUSE_TIME;
        if (!modifiers.hasModifier(ClinkerTags.OrdnanceModifiers.HAS_FUSE)) return Integer.MAX_VALUE;
        if (!modifiers.hasModifier(ClinkerOrdnanceModifierTypes.FUSE_TIME.get())) return DEFAULT_FUSE_TIME;
        FuseTimeModifier modifier = modifiers.getModifier(ClinkerOrdnanceModifierTypes.FUSE_TIME.get());
        return DEFAULT_FUSE_TIME + modifier.level * 30;
    }

    public static int getFuseTicks(int level) {
        return DEFAULT_FUSE_TIME + level * 30;
    }

    @Override
    public boolean canMerge(OrdnanceModifier modifier) {
        return modifier instanceof FuseTimeModifier;
    }

    @Override
    public FuseTimeModifier merge(OrdnanceModifier modifier) {
        if (modifier instanceof FuseTimeModifier fuseTimeModifier)
            return new FuseTimeModifier(Math.clamp(this.level + fuseTimeModifier.level, MIN_LEVEL, MAX_LEVEL));
        return null;
    }

    @Override
    public void tooltip(@Nullable OrdnanceModifierSet set, Consumer<Component> tooltipAdder) {
        if (set == null || set.hasModifier(ClinkerTags.OrdnanceModifiers.HAS_FUSE))
            tooltipAdder.accept(
                    Component.translatable("ordnance_modifier.clinker.fuse_time", getFuseTicks(this.level()) / 20.0)
                             .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY))
            );
    }

    @Override
    public OrdnanceModifierType<?> type() {
        return ClinkerOrdnanceModifierTypes.FUSE_TIME.get();
    }
}