package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities;

import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;

public interface ModifiesFinalSynthesizer extends WorldFeatureCapability {
    Synthesizer.PassBuilder finalSynthesizerPass(int minY, int maxY, WorldFeatureContext worldContext);
}
