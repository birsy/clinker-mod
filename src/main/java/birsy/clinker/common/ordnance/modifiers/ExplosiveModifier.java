package birsy.clinker.common.ordnance.modifiers;

import birsy.clinker.common.ordnance.OrdnanceGradient;
import birsy.clinker.common.ordnance.OrdnanceModifier;
import birsy.clinker.common.ordnance.OrdnanceModifierSet;
import birsy.clinker.common.ordnance.OrdnanceModifierType;
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
import net.minecraft.util.Mth;

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

    public float getExplosionRadius() {
        return Mth.map(this.power, 0, MAX_POWER, 4F, 8F);
    }

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

    @Override
    public int gradientModificationOrder() { return 1; }
    @Override
    public OrdnanceGradient mutateGradient(OrdnanceGradient gradient) {
        float factor = (float) Mth.clamp(this.power(), 0, MAX_POWER) / MAX_POWER;
        return new OrdnanceGradient(
                gradient.startRed(), gradient.startGreen(), gradient.startBlue(),

                Mth.lerp(factor, gradient.endRed(), 1.0F),
                Mth.lerp(factor, gradient.endGreen(), 0.8F),
                Mth.lerp(factor, gradient.endBlue(), 0.4F),

                gradient.overlayRed(), gradient.overlayGreen(), gradient.overlayBlue(), gradient.overlayAlpha()
        );
    }
}
