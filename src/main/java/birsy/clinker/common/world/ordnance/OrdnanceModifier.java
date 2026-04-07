package birsy.clinker.common.world.ordnance;

import birsy.clinker.core.registry.ClinkerRegistries;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public interface OrdnanceModifier<T extends OrdnanceModifier<T>> {
    Codec<OrdnanceModifier<?>> CODEC =
            ClinkerRegistries.ORDNANCE_MODIFIER_TYPE_REGISTRY.byNameCodec()
                    .dispatch(OrdnanceModifier::type, OrdnanceModifierType::codec);
    StreamCodec<RegistryFriendlyByteBuf, OrdnanceModifier<?>> STREAM_CODEC =
            ByteBufCodecs.registry(ClinkerRegistries.ORDNANCE_MODIFIER_TYPE_REGISTRY_KEY)
                    .dispatch(OrdnanceModifier::type, OrdnanceModifierType::streamCodec);

    default int textOrder() { return 0; }

    default OrdnanceGradient mutateGradient(OrdnanceGradient gradient) { return gradient; }
    default int gradientModificationOrder() { return 0; }

    // returns whether this new modifier can be added to this modifier's ordnance
    // used for mutual exclusion and such.
    default boolean canAddModifier(OrdnanceModifierSet set, OrdnanceModifier<?> modifier) {
        if (modifier.type() == this.type()) {
            return canMerge(modifier);
        } else {
            return !OrdnanceModifierType.areMutuallyExclusive(this.type(), modifier.type());
        }
    }

    boolean canMerge(OrdnanceModifier modifier);
    @Nullable T merge(OrdnanceModifier modifier);
    void tooltip(@Nullable OrdnanceModifierSet set, Consumer<Component> tooltipAdder);
    OrdnanceModifierType<?> type();
}
