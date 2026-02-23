package birsy.clinker.core.registry.worldgen;

import birsy.clinker.common.world.level.gen.system.biome.resolver.ProtoBiome;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerProtoBiomes {
    public static final DeferredRegister<ProtoBiome> PROTO_BIOMES = DeferredRegister.create(ClinkerRegistries.PROTO_BIOME_REGISTRY, Clinker.MOD_ID);

    public static final Supplier<ProtoBiome> UNINITIALIZED =
            PROTO_BIOMES.register("uninitialized", () -> new ProtoBiome());

    public static final Supplier<ProtoBiome> UPPER_SHELF =
            PROTO_BIOMES.register("upper_shelf", () -> new ProtoBiome());
    public static final Supplier<ProtoBiome> ASH_STEPPE =
            PROTO_BIOMES.register("ash_steppe", () -> new ProtoBiome(ClinkerBiomes.ASH_STEPPE));
    public static final Supplier<ProtoBiome> HEATH =
            PROTO_BIOMES.register("heath", () -> new ProtoBiome(ClinkerBiomes.HEATH));
    public static final Supplier<ProtoBiome> HEATH_THICKET =
            PROTO_BIOMES.register("heath_thicket", () -> new ProtoBiome(ClinkerBiomes.HEATH_THICKET));

    public static final Supplier<ProtoBiome> LOWER_SHELF =
            PROTO_BIOMES.register("lower_shelf", () -> new ProtoBiome());
    public static final Supplier<ProtoBiome> SHORE =
            PROTO_BIOMES.register("shore", () -> new ProtoBiome(ClinkerBiomes.SHORE));
    public static final Supplier<ProtoBiome> BRINE_SNAKES =
            PROTO_BIOMES.register("brine_snakes", () -> new ProtoBiome(ClinkerBiomes.BRINE_SNAKES));
    public static final Supplier<ProtoBiome> BRINE_SWAMP =
            PROTO_BIOMES.register("brine_swamp", () -> new ProtoBiome(ClinkerBiomes.BRINE_SWAMP));
}
