package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities;

import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;

public interface ModifiesCaveSynthesizer extends WorldFeatureCapability {
    Synthesizer.PassBuilder caveSynthesizerPass(int minY, int maxY, WorldFeatureContext worldContext);
}
