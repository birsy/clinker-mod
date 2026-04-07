package birsy.clinker.common.world.ordnance.modifiers;

import birsy.clinker.core.registry.ClinkerOrdnanceModifierTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;

public final class UnstableModifier extends SimpleOrdnanceModifier<UnstableModifier> {
    public static final MapCodec<UnstableModifier> CODEC =
            MapCodec.unit(UnstableModifier::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, UnstableModifier> STREAM_CODEC =
            StreamCodec.of((buffer, value) -> {}, buffer -> new UnstableModifier());
    public UnstableModifier() { super(ClinkerOrdnanceModifierTypes.UNSTABLE.get(),  Style.EMPTY.withColor(0x80051c)); }
}
