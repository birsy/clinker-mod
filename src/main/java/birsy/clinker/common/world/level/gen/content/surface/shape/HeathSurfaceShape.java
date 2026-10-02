package birsy.clinker.common.world.level.gen.content.surface.shape;

import birsy.clinker.common.world.level.gen.content.synthesizers.UtilitySynthesizers;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;
import net.minecraft.util.Mth;

public class HeathSurfaceShape extends CliffySurfaceShape {
    public HeathSurfaceShape(int baseHeight, int expectedBorderHeight) {
        super(baseHeight, baseHeight, baseHeight + 10, expectedBorderHeight);
    }

    @Override
    protected Synthesizer createSurfaceSynthesizer(Synthesizer biomeSdfSynthesizer) {
        return Synthesizer.builder()
                .withRange(minSurfaceY, maxSurfaceY, 100)
                .withDependencies(UtilitySynthesizers.BASIC_NOISE_2D[4], UtilitySynthesizers.ROCKY_CLIFF_Y)
                .build(InterpolatingFieldResolution.FINE_Y,
                        ctx -> {
                            float heightmapNoise = ctx.dependentValue(0);
                            float heightmap = Mth.clampedMap(
                                    heightmapNoise, -0.5F, 0.5F, baseHeight, baseHeight + 10
                            );
                            float rockyY = Mth.lerp(0.75F, ctx.y(), ctx.dependentValue(1));
                            return rockyY - heightmap;
                        }
                );
    }
}
