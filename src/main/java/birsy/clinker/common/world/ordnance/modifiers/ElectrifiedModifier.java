package birsy.clinker.common.world.ordnance.modifiers;

import birsy.clinker.common.world.ordnance.OrdnanceGradient;
import birsy.clinker.core.registry.ClinkerOrdnanceModifierTypes;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.StreamCodec;

public final class ElectrifiedModifier extends SimpleOrdnanceModifier<ElectrifiedModifier> {
    public static final MapCodec<ElectrifiedModifier> CODEC =
            MapCodec.unit(ElectrifiedModifier::new);
    public static final StreamCodec<RegistryFriendlyByteBuf, ElectrifiedModifier> STREAM_CODEC =
            StreamCodec.of((buffer, value) -> {}, buffer -> new ElectrifiedModifier());
    public ElectrifiedModifier() {
        super(ClinkerOrdnanceModifierTypes.ELECTRIFIED.get(), Style.EMPTY.withColor(0x5a6f91));
    }

    @Override
    public int gradientModificationOrder() { return 2; }
    @Override
    public OrdnanceGradient mutateGradient(OrdnanceGradient gradient) {
        return new OrdnanceGradient(
                Math.clamp(gradient.startRed()   * 2.0F, 0, 1),
                Math.clamp(gradient.startGreen() * 2.0F, 0, 1),
                Math.clamp(gradient.startBlue()  * 2.0F, 0, 1),
                gradient.endRed(), gradient.endGreen(), gradient.endBlue(),
                gradient.overlayRed() * 0.2F,
                gradient.overlayGreen() * 0.2F,
                gradient.overlayBlue() * 0.2F,
                1.0F
        );
    }
}
