package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities;

import birsy.clinker.common.world.level.gen.system.noise.PaddedNoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;

public interface ModifiesWaterfallPresence extends WorldFeatureCapability {
    void modifyWaterfallPresence(int minX, int minY, int minZ, PaddedNoiseFieldCache cache, NoiseField field, WorldFeatureContext worldContext);
}
