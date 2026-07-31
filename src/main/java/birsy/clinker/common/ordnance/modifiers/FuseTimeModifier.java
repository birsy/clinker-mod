package birsy.clinker.common.ordnance.modifiers;

import birsy.clinker.common.ordnance.OrdnanceModifier;
import birsy.clinker.common.ordnance.OrdnanceModifierSet;
import birsy.clinker.common.ordnance.OrdnanceModifierType;
import birsy.clinker.core.registry.ClinkerOrdnanceModifierTypes;
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

    public int getFuseTicks() {
        return DEFAULT_FUSE_TIME + level() * 30;
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
        tooltipAdder.accept(
                Component.translatable("ordnance_modifier.clinker.fuse_time", getFuseTicks() / 20.0)
                        .withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY))
        );
    }

    @Override
    public OrdnanceModifierType<?> type() {
        return ClinkerOrdnanceModifierTypes.FUSE_TIME.get();
    }
}