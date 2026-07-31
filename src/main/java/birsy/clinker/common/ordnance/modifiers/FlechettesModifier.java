package birsy.clinker.common.ordnance.modifiers;

import birsy.clinker.core.registry.ClinkerOrdnanceModifierTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;

public final class FlechettesModifier extends SimpleOrdnanceModifier<FlechettesModifier> {
    public static final MapCodec<FlechettesModifier> CODEC =
            MapCodec.unit(FlechettesModifier::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, FlechettesModifier> STREAM_CODEC =
            StreamCodec.of((buffer, value) -> {}, buffer -> new FlechettesModifier());
    public FlechettesModifier() { super(ClinkerOrdnanceModifierTypes.FLECHETTES.get(), Style.EMPTY.withColor(0x4f4f4f)); }
}
