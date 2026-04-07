package birsy.clinker.common.world.ordnance.modifiers;

import birsy.clinker.core.registry.ClinkerOrdnanceModifierTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;

public final class PhosphorusModifier extends SimpleOrdnanceModifier<PhosphorusModifier> {
    public static final MapCodec<PhosphorusModifier> CODEC =
            MapCodec.unit(PhosphorusModifier::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, PhosphorusModifier> STREAM_CODEC =
            StreamCodec.of((buffer, value) -> {}, buffer -> new PhosphorusModifier());
    public PhosphorusModifier() { super(ClinkerOrdnanceModifierTypes.PHOSPHOROUS.get(), Style.EMPTY.withColor(0x5c8255)); }
}
