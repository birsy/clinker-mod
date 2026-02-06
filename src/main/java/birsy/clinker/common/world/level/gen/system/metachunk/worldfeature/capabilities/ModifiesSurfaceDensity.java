package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities;

import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;

public interface ModifiesSurfaceDensity extends WorldFeatureCapability {
    void modifySurfaceDensity(int minX, int minY, int minZ, NoiseFieldCache cache, NoiseField field, WorldFeatureContext worldContext);
}
