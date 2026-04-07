package birsy.clinker.core.registry;

import birsy.clinker.common.world.ordnance.OrdnanceModifier;
import birsy.clinker.common.world.ordnance.OrdnanceModifierType;
import birsy.clinker.common.world.ordnance.modifiers.*;
import birsy.clinker.core.Clinker;
import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerOrdnanceModifierTypes {
    public static final DeferredRegister<OrdnanceModifierType<?>> ORDNANCE_MODIFIER_TYPES =
            DeferredRegister.create(ClinkerRegistries.ORDNANCE_MODIFIER_TYPE_REGISTRY, Clinker.MOD_ID);

    public static void defineMutualExclusivities() {
        OrdnanceModifierType.setMutuallyExclusive(EXPLOSIVE.get(), FLECHETTES.get());
        OrdnanceModifierType.setMutuallyExclusive(BOUNCY.get(), STICKY.get(), UNSTABLE.get());
    }

    public static final Supplier<OrdnanceModifierType<ExplosiveModifier>> EXPLOSIVE =
            register("explosive", ExplosiveModifier.CODEC, ExplosiveModifier.STREAM_CODEC);

    public static final Supplier<OrdnanceModifierType<FlechettesModifier>> FLECHETTES =
            register("flechettes", FlechettesModifier.CODEC, FlechettesModifier.STREAM_CODEC);

    public static final Supplier<OrdnanceModifierType<PhosphorusModifier>> PHOSPHOROUS =
            register("phosphorus", PhosphorusModifier.CODEC, PhosphorusModifier.STREAM_CODEC);

    public static final Supplier<OrdnanceModifierType<ElectrifiedModifier>> ELECTRIFIED =
            register("electrified", ElectrifiedModifier.CODEC, ElectrifiedModifier.STREAM_CODEC);

    public static final Supplier<OrdnanceModifierType<ThornedModifier>> THORNED =
            register("thorned", ThornedModifier.CODEC, ThornedModifier.STREAM_CODEC);

    public static final Supplier<OrdnanceModifierType<BouncyModifier>> BOUNCY =
            register("bouncy", BouncyModifier.CODEC, BouncyModifier.STREAM_CODEC);

    public static final Supplier<OrdnanceModifierType<StickyModifier>> STICKY =
            register("sticky", StickyModifier.CODEC, StickyModifier.STREAM_CODEC);

    public static final Supplier<OrdnanceModifierType<UnstableModifier>> UNSTABLE =
            register("unstable", UnstableModifier.CODEC, UnstableModifier.STREAM_CODEC);

    public static final Supplier<OrdnanceModifierType<ClusterModifier>> CLUSTER =
            register("cluster", ClusterModifier.CODEC, ClusterModifier.STREAM_CODEC);

    public static final Supplier<OrdnanceModifierType<FuseTimeModifier>> FUSE_TIME =
            register("fuse_time", FuseTimeModifier.CODEC, FuseTimeModifier.STREAM_CODEC);

    private static <T extends OrdnanceModifier<T>> Supplier<OrdnanceModifierType<T>> register(String name, MapCodec<T> codec, StreamCodec<RegistryFriendlyByteBuf, T> streamCodec) {
        return ORDNANCE_MODIFIER_TYPES.register(name, () -> new OrdnanceModifierType<T>(codec, streamCodec));
    }
}
