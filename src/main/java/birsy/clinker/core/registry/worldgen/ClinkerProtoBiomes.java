package birsy.clinker.core.registry.worldgen;

import birsy.clinker.common.world.level.gen.OthershoreBiomeSource;
import birsy.clinker.common.world.level.gen.system.biome.ProtoBiome;
import birsy.clinker.common.world.level.gen.system.noise.NoiseComputer;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldFiller;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldType;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldTypes;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerProtoBiomes {
    public static final DeferredRegister<ProtoBiome> PROTO_BIOMES = DeferredRegister.create(ClinkerRegistries.PROTO_BIOME_REGISTRY, Clinker.MOD_ID);

    public static final Supplier<ProtoBiome> UNINITIALIZED =
            PROTO_BIOMES.register("uninitialized", () -> new ProtoBiome());

    public static final Supplier<ProtoBiome> UPPER_SHELF =
            PROTO_BIOMES.register("upper_shelf", () -> new ProtoBiome(ClinkerBiomes.ASH_STEPPE));
    public static final Supplier<ProtoBiome> LOWER_SHELF =
            PROTO_BIOMES.register("lower_shelf", () -> new ProtoBiome(ClinkerBiomes.BRINE_SWAMP));
}
