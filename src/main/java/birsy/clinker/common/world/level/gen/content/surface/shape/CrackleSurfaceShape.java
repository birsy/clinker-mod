package birsy.clinker.common.world.level.gen.content.surface.shape;

import birsy.clinker.common.world.level.gen.content.synthesizers.UtilitySynthesizers;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.noise.FNLNoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;

public class CrackleSurfaceShape extends CliffySurfaceShape {
    final int bottomHeight, topHeight;

    public CrackleSurfaceShape(int bottomHeight, int topHeight, int expectedBorderHeight) {
        super(bottomHeight, bottomHeight - 16, topHeight + 16, expectedBorderHeight);
        this.bottomHeight = bottomHeight;
        this.topHeight = topHeight;
    }

    @Override
    protected Synthesizer createSurfaceSynthesizer(Synthesizer biomeSdfSynthesizer) {
        return Synthesizer.builder()
                .withRange(minSurfaceY, maxSurfaceY, 100)
                .withNoises(FNLNoiseProvider.create("gapradius"))
                .withDependencies(biomeSdfSynthesizer, UtilitySynthesizers.HEIGHT_OFFSET, UtilitySynthesizers.VORONOI_EDGE_DISTANCE_24, UtilitySynthesizers.ROCKY_CLIFF_Y)
                .build(InterpolatingFieldResolution.FINE, ctx -> {
                    double offset = ctx.dependentValue(1);
                    double distanceFromBottom = ctx.y() - bottomHeight + offset;
                    double distanceFromTop = ctx.y() - topHeight - offset;

                    double rockyY = ctx.dependentValue(3);
                    double gapRadiusOffset = ctx.noise(0).sample(ctx.x() / 24.0, rockyY / 16.0, ctx.z() / 24.0) * 2.0;

                    double crackleGapRadius = Mth.clampedMap(ctx.dependentValue(0), -8, 0, 3, 20);
                    crackleGapRadius += gapRadiusOffset;
                    double crackle = -ctx.dependentValue(2) + crackleGapRadius;

                    double crackleRock = Math.max(distanceFromTop, crackle);
                    return MathUtils.smoothMin(distanceFromBottom, crackleRock, 2.0);
                });
    }
}
