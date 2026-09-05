package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities;

import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingField;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;

public interface ModifiesCaveDensity extends WorldFeatureCapability {
    void modifyCaveDensity(int minX, int minY, int minZ, int maxCaveHeight, InterpolatingField field, InterpolatingField maskField, WorldFeatureContext worldContext);
}
