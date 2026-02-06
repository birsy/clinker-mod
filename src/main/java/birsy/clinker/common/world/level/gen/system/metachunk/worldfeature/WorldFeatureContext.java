package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature;

import birsy.clinker.common.world.level.gen.system.biome.BiomeBlender;
import birsy.clinker.common.world.level.gen.system.biome.BiomeList;
import birsy.clinker.common.world.level.gen.system.surface.shaper.SurfaceShaperSystem;

public record WorldFeatureContext(BiomeList biomeList, BiomeBlender biomeBlender, SurfaceShaperSystem surfaceShaperSystem) {
}
