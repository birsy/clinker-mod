package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities;

import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingField;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;

public interface ModifiesFinalDensity extends WorldFeatureCapability {
    void modifyFinalDensity(int minX, int minY, int minZ, InterpolatingField field, WorldFeatureContext worldContext);
}
