package birsy.clinker.common.world.ordnance.modifiers;

import birsy.clinker.core.registry.ClinkerOrdnanceModifierTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;

public final class ThornedModifier extends SimpleOrdnanceModifier<ThornedModifier> {
    public static final MapCodec<ThornedModifier> CODEC =
            MapCodec.unit(ThornedModifier::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, ThornedModifier> STREAM_CODEC =
            StreamCodec.of((buffer, value) -> {}, buffer -> new ThornedModifier());
    public ThornedModifier() { super(ClinkerOrdnanceModifierTypes.THORNED.get(), Style.EMPTY.withColor(0x5e452e)); }
}
