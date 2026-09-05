package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities;

import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.field.InterpolatingField;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;

public interface ModifiesCaveDensity extends WorldFeatureCapability {
    void modifyCaveDensity(int minX, int minY, int minZ, int maxCaveHeight, NoiseFieldCache cache, InterpolatingField field, InterpolatingField maskField, WorldFeatureContext worldContext);
}
