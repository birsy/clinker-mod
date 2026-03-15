package birsy.clinker.core.registry.worldgen;

import birsy.clinker.common.world.level.gen.system.biome.resolver.ProtoBiome;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ClinkerProtoBiomes {
    public static final DeferredRegister<ProtoBiome> PROTO_BIOMES = DeferredRegister.create(ClinkerRegistries.PROTO_BIOME_REGISTRY, Clinker.MOD_ID);

    public static final Supplier<ProtoBiome> UNINITIALIZED =
            PROTO_BIOMES.register("uninitialized", () -> new ProtoBiome());


    public static final Supplier<ProtoBiome> ASH_STEPPE =
            PROTO_BIOMES.register("ash_steppe", () -> new ProtoBiome(ClinkerBiomes.ASH_STEPPE));
    public static final Supplier<ProtoBiome> HEATH =
            PROTO_BIOMES.register("heath", () -> new ProtoBiome(ClinkerBiomes.HEATH));
    public static final Supplier<ProtoBiome> HEATH_THICKET =
            PROTO_BIOMES.register("heath_thicket", () -> new ProtoBiome(ClinkerBiomes.HEATH_THICKET));


    public static final Supplier<ProtoBiome> SHORE =
            PROTO_BIOMES.register("shore", () -> new ProtoBiome(ClinkerBiomes.SHORE));
    public static final Supplier<ProtoBiome> BRINE_SNAKES =
            PROTO_BIOMES.register("brine_snakes", () -> new ProtoBiome(ClinkerBiomes.BRINE_SNAKES));
    public static final Supplier<ProtoBiome> BRINE_SWAMP =
            PROTO_BIOMES.register("brine_swamp", () -> new ProtoBiome(ClinkerBiomes.BRINE_SWAMP));

    public static final Supplier<ProtoBiome> UPPER_SHELF =
            PROTO_BIOMES.register("upper_shelf", () -> new ProtoBiome(ClinkerBiomes.TEMPLATE_UPPER_SHELF));
    public static final Supplier<ProtoBiome> UPPER_SHELF_PLATEAU =
            PROTO_BIOMES.register("upper_shelf_plateau", () -> new ProtoBiome(ClinkerBiomes.TEMPLATE_UPPER_SHELF_PLATEAU));
    public static final Supplier<ProtoBiome> LOWER_SHELF =
            PROTO_BIOMES.register("lower_shelf", () -> new ProtoBiome(ClinkerBiomes.TEMPLATE_LOWER_SHELF));
    public static final Supplier<ProtoBiome> SHELF_BORDER =
            PROTO_BIOMES.register("shelf_border", () -> new ProtoBiome(ClinkerBiomes.TEMPLATE_SHELF_BORDER));
    public static final Supplier<ProtoBiome> SHELF_BORDER_CRACKLE =
            PROTO_BIOMES.register("shelf_border_crackle", () -> new ProtoBiome(ClinkerBiomes.TEMPLATE_SHELF_BORDER_CRACKLE));
    public static final Supplier<ProtoBiome> BEACH =
            PROTO_BIOMES.register("beach", () -> new ProtoBiome(ClinkerBiomes.TEMPLATE_BEACH));
    public static final Supplier<ProtoBiome> SEA =
            PROTO_BIOMES.register("sea", () -> new ProtoBiome(ClinkerBiomes.TEMPLATE_SEA));

    public static final Supplier<ProtoBiome>[] BASE_SECTIONS = registerProtoBiomeArray("base_section", 5, null);

    private static DeferredHolder<ProtoBiome, ProtoBiome>[] registerProtoBiomeArray(String name, int count, @Nullable ResourceKey<Biome> result) {
        DeferredHolder<ProtoBiome, ProtoBiome>[] protoBiomes = new DeferredHolder[count];
        for (int i = 0; i < count; i++) protoBiomes[i] = PROTO_BIOMES.register(name + "_" + i, () -> new ProtoBiome(result));
        return protoBiomes;
    }
}
