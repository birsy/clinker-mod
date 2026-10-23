package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities;

import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;
import net.minecraft.world.level.ChunkPos;

public interface ModifiesCaveNoiseField extends WorldFeatureCapability {
    NoiseFieldModifier modifierForCaveSynthesizer(Synthesizer synthesizer, WorldFeatureContext worldContext);
}
