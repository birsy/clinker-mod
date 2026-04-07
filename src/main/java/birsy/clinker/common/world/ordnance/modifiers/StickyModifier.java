package birsy.clinker.common.world.ordnance.modifiers;

import birsy.clinker.core.registry.ClinkerOrdnanceModifierTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;

public final class StickyModifier extends ColoredOrdnanceModifier<StickyModifier> {
    public static final MapCodec<StickyModifier> CODEC =
            RecordCodecBuilder.mapCodec(
                    instance -> instance
                            .group(Codec.INT.fieldOf("color").forGetter(StickyModifier::color))
                            .apply(instance, StickyModifier::new)
            );
    public static final StreamCodec<RegistryFriendlyByteBuf, StickyModifier> STREAM_CODEC =
            StreamCodec.of(
                    (buffer, modifier) -> buffer.writeInt(modifier.color()),
                    (buffer) -> new StickyModifier(buffer.readInt())
            );

    public StickyModifier(int color) {
        super(ClinkerOrdnanceModifierTypes.STICKY.get(), color, Style.EMPTY.withColor(ChatFormatting.DARK_GRAY));
    }
}
