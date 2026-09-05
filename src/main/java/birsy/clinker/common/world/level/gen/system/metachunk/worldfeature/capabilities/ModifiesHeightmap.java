package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities;

import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingField;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;

public interface ModifiesHeightmap extends WorldFeatureCapability {
    void modifyHeightmap(int minX, int minZ, InterpolatingField field, InterpolatingField[] biomeHeightmaps, WorldFeatureContext worldContext);
}
