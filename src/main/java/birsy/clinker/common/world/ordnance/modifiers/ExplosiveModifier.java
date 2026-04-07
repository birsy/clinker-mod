package birsy.clinker.common.world.ordnance.modifiers;

import birsy.clinker.common.world.ordnance.OrdnanceModifier;
import birsy.clinker.common.world.ordnance.OrdnanceModifierSet;
import birsy.clinker.common.world.ordnance.OrdnanceModifierType;
import birsy.clinker.core.registry.ClinkerOrdnanceModifierTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.Util;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.FastColor;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public record ExplosiveModifier(int power) implements OrdnanceModifier<ExplosiveModifier> {
    public static final MapCodec<ExplosiveModifier> CODEC =
            RecordCodecBuilder.mapCodec(
                    instance -> instance
                            .group(Codec.INT.fieldOf("power").forGetter(ExplosiveModifier::power))
                            .apply(instance, ExplosiveModifier::new)
            );
    public static final StreamCodec<RegistryFriendlyByteBuf, ExplosiveModifier> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, modifier) -> buffer.writeInt(modifier.power()),
                    (buffer) -> new ExplosiveModifier(buffer.readInt())
            );
    static final int MAX_POWER = 3;
    static final int[] COLOR_BY_POWER = Util.make(() -> {
        int[] colors = new int[MAX_POWER + 1];
        for (int i = 0; i <= MAX_POWER; i++) {
            colors[i] = FastColor.ARGB32.lerp((float) i / MAX_POWER, 0x8a864c, 0xffcc5e);
        }
        return colors;
    });
    @Override
    public boolean canMerge(OrdnanceModifier modifier) {
        return modifier instanceof ExplosiveModifier;
    }

    @Override
    public ExplosiveModifier merge(OrdnanceModifier modifier) {
        if (modifier instanceof ExplosiveModifier explosiveModifier)
            return new ExplosiveModifier(Math.min(this.power() + explosiveModifier.power(), MAX_POWER));
        return null;
    }

    @Override
    public void tooltip(@Nullable OrdnanceModifierSet set, Consumer<Component> tooltipAdder) {
        tooltipAdder.accept(
                Component.translatable("ordnance_modifier.clinker.explosive." + power())
                         .withStyle(Style.EMPTY.withColor(COLOR_BY_POWER[Math.clamp(this.power(), 0, MAX_POWER)]))
        );
    }

    @Override
    public OrdnanceModifierType<?> type() {
        return ClinkerOrdnanceModifierTypes.EXPLOSIVE.get();
    }
}
