package birsy.clinker.core.registry.world;

import birsy.clinker.common.world.level.gen.feature.placement.BelowHeightmapFilter;
import birsy.clinker.common.world.level.gen.feature.placement.HeightFilter;
import birsy.clinker.common.world.level.gen.feature.placement.HeightmapSteepnessFilter;
import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerPlacementModifierTypes {
    public static final DeferredRegister<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPES = DeferredRegister.create(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE, Clinker.MOD_ID);

    public static final Supplier<PlacementModifierType<HeightFilter>> HEIGHT_FILTER =
            PLACEMENT_MODIFIER_TYPES.register("height_filter", () -> () -> HeightFilter.CODEC);
    public static final Supplier<PlacementModifierType<HeightmapSteepnessFilter>> HEIGHTMAP_STEEPNESS_FILTER =
            PLACEMENT_MODIFIER_TYPES.register("heightmap_steepness_filter", () -> () -> HeightmapSteepnessFilter.CODEC);
    public static final Supplier<PlacementModifierType<BelowHeightmapFilter>> BELOW_HEIGHTMAP_FILTER =
            PLACEMENT_MODIFIER_TYPES.register("below_heightmap_filter", () -> () -> BelowHeightmapFilter.CODEC);
}
