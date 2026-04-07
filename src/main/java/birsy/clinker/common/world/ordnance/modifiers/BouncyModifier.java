package birsy.clinker.common.world.ordnance.modifiers;

import birsy.clinker.core.registry.ClinkerOrdnanceModifierTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Consumer;

public final class BouncyModifier extends ColoredOrdnanceModifier<BouncyModifier> {
    public static final MapCodec<BouncyModifier> CODEC =
            RecordCodecBuilder.mapCodec(
                    instance -> instance
                            .group(Codec.INT.fieldOf("color").forGetter(BouncyModifier::color))
                            .apply(instance, BouncyModifier::new)
            );
    public static final StreamCodec<RegistryFriendlyByteBuf, BouncyModifier> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, modifier) -> buffer.writeInt(modifier.color()),
                    (buffer) -> new BouncyModifier(buffer.readInt())
            );

    public BouncyModifier(int color) {
        super(ClinkerOrdnanceModifierTypes.BOUNCY.get(), color, Style.EMPTY.withColor(ChatFormatting.DARK_GRAY));
    }
}
