package birsy.clinker.common.world.level.gen.system.surface.shape;

import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;

public abstract class SurfaceShape {
    public final int baseHeight, minSurfaceY, maxSurfaceY;

    protected SurfaceShape(int baseHeight, int minSurfaceY, int maxSurfaceY) {
        this.baseHeight = baseHeight;
        this.minSurfaceY = minSurfaceY;
        this.maxSurfaceY = maxSurfaceY;
    }

    protected abstract Synthesizer create(Synthesizer biomeSdfSynthesizer, boolean surfaceContainedInChunk);
}
