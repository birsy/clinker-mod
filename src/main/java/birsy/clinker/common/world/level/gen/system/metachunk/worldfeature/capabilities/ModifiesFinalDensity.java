package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities;

import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.field.InterpolatingField;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;

public interface ModifiesFinalDensity extends WorldFeatureCapability {
    void modifyFinalDensity(int minX, int minY, int minZ, NoiseFieldCache cache, InterpolatingField field, WorldFeatureContext worldContext);
}
