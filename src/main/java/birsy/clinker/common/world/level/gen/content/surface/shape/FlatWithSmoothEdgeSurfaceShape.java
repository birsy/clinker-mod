package birsy.clinker.common.world.level.gen.content.surface.shape;

import birsy.clinker.common.world.level.gen.content.synthesizers.UtilitySynthesizers;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;
import net.minecraft.util.Mth;

public class FlatWithSmoothEdgeSurfaceShape extends SmoothEdgeSurfaceShape {
    public FlatWithSmoothEdgeSurfaceShape(int baseHeight, int expectedBorderHeight) {
        super(baseHeight, baseHeight - 8, baseHeight + 8, expectedBorderHeight);
    }

    @Override
    protected Synthesizer createSurfaceSynthesizer(Synthesizer biomeSdfSynthesizer, Synthesizer heightmapSynthesizer, boolean nearbySurface) {
        return initSurfaceSynthesizer("flat_surface_shape")
                .withRange(nearbySurface ? minSurfaceY : Math.min(minSurfaceY, expectedBorderHeight), maxSurfaceY)
                .withDependencies(biomeSdfSynthesizer, heightmapSynthesizer, UtilitySynthesizers.HEIGHT_OFFSET)
                .build(InterpolatingFieldResolution.COARSE_X_FINE_Y,
                        ctx -> {
                            float biomeDist = ctx.dependentValue(0),
                                  heightmap = ctx.dependentValue(1),
                                  noiseOffset = ctx.dependentValue(2);
                            noiseOffset = Mth.lerp(
                                    Mth.clampedMap(biomeDist, 0, maximumBiomeDistance, 1, 0),
                                    noiseOffset, 0
                            );
                            return ctx.y() - heightmap + noiseOffset;
                        }
                );
    }
}
