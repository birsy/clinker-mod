package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities;

import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingField;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;
import net.minecraft.world.level.ChunkPos;

public interface ModifiesFinalNoiseField extends WorldFeatureCapability {
    Synthesizer wrapFinalSynthesizer(Synthesizer synthesizer, WorldFeatureContext worldContext);
}
