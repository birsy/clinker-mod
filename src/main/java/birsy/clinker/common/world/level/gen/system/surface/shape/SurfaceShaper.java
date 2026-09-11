package birsy.clinker.common.world.level.gen.system.surface.shape;

import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;

public abstract class SurfaceShaper {
    abstract int baseHeight();
    abstract Synthesizer create(Synthesizer biomeSdfSynthesizer);
}
