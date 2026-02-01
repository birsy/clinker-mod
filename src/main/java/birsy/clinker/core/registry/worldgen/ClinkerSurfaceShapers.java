package birsy.clinker.core.registry.worldgen;

import birsy.clinker.common.world.level.gen.content.surface.shaper.AshSteppeSurfaceShaper;
import birsy.clinker.common.world.level.gen.content.surface.shaper.BrineSwampSurfaceShaper;
import birsy.clinker.common.world.level.gen.content.surface.shaper.HeathSurfaceShaper;
import birsy.clinker.common.world.level.gen.content.surface.shaper.SnakesSurfaceShaper;
import birsy.clinker.common.world.level.gen.system.surface.shaper.BiomeSurfaceShaper;
import birsy.clinker.common.world.level.gen.system.surface.shaper.DefaultSurfaceShaper;
import birsy.clinker.common.world.level.gen.system.surface.shaper.SurfaceShaper;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerSurfaceShapers {
    public static final DeferredRegister<BiomeSurfaceShaper> SURFACE_SHAPERS =
            DeferredRegister.create(ClinkerRegistries.SURFACE_SHAPER_REGISTRY, Clinker.MOD_ID);

    public static final Supplier<BiomeSurfaceShaper> DEFAULT =
            register("default", Biomes.THE_VOID, new DefaultSurfaceShaper());

    public static final Supplier<BiomeSurfaceShaper> ASH_STEPPE =
            register("ash_steppe", ClinkerBiomes.ASH_STEPPE, new AshSteppeSurfaceShaper());
    public static final Supplier<BiomeSurfaceShaper> BRINE_SWAMP =
            register("brine_swamp", ClinkerBiomes.BRINE_SWAMP, new BrineSwampSurfaceShaper());
    public static final Supplier<BiomeSurfaceShaper> HEATH =
            register("heath", ClinkerBiomes.HEATH, new HeathSurfaceShaper());
    public static final Supplier<BiomeSurfaceShaper> HEATH_THICKET =
            register("heath_thicket", ClinkerBiomes.HEATH_THICKET, new HeathSurfaceShaper());
    public static final Supplier<BiomeSurfaceShaper> BRINE_SNAKES =
            register("brine_snakes", ClinkerBiomes.BRINE_SNAKES, new SnakesSurfaceShaper());
//    public static final BiomeShaper ASH_STEPPE = register(ClinkerBiomes.ASH_STEPPE, new AshSteppeBiomeShaper());
//    public static final BiomeShaper BRINE_SWAMP = register(ClinkerBiomes.BRINE_SWAMP, new BrineSwampBiomeShaper());
//
//    public static final BiomeShaper CLIFFSIDE = register(ClinkerBiomes.CLIFFSIDE, new LowerShelfBiomeShaper());
//    public static final BiomeShaper LOWER_SHELF = register(ClinkerBiomes.LOWER_SHELF, new LowerShelfBiomeShaper());

    public static Supplier<BiomeSurfaceShaper> register(String name, ResourceKey<Biome> biome, SurfaceShaper shaper) {
        return SURFACE_SHAPERS.register(name, () -> new BiomeSurfaceShaper(biome, shaper));
    }
    public static Supplier<BiomeSurfaceShaper> register(String name, TagKey<Biome> biomeTag, SurfaceShaper shaper) {
        return SURFACE_SHAPERS.register(name, () -> new BiomeSurfaceShaper(biomeTag, shaper));
    }
}
