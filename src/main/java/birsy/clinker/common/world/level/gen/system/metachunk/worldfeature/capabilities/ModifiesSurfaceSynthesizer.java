package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities;

import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;

public interface ModifiesSurfaceSynthesizer extends WorldFeatureCapability {
    Synthesizer.PassBuilder surfaceSynthesizerPass(int minY, int maxY, WorldFeatureContext worldContext);
}
