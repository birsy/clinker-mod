package birsy.clinker.common.world.level.gen.content.surface.shape;

import birsy.clinker.common.world.level.gen.content.synthesizers.UtilitySynthesizers;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.noise.FNLNoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;
import net.minecraft.util.Mth;

public class HeathSurfaceShape extends CliffySurfaceShape {
    public HeathSurfaceShape(int baseHeight, int expectedBorderHeight) {
        super(baseHeight, baseHeight - 5, baseHeight + 16, expectedBorderHeight);
    }

    @Override
    protected Synthesizer createSurfaceSynthesizer(Synthesizer biomeSdfSynthesizer) {
        return Synthesizer.builder()
                .withRange(minSurfaceY, maxSurfaceY, 100)
                .withNoises(FNLNoiseProvider.create("heathsurface"))
                .withDependencies(UtilitySynthesizers.BASIC_NOISE_2D[4], UtilitySynthesizers.ROCKY_CLIFF_Y, biomeSdfSynthesizer)
                .build(InterpolatingFieldResolution.VERY_COARSE_X_FINE_Y,
                        ctx -> {
                            float heightmapNoise = ctx.noise(0).sample(ctx.x() / 200.0, ctx.z() / 200.0);
                            heightmapNoise += ctx.dependentValue(0) * 0.25F;
                            float heightmap = Mth.clampedMap(
                                    heightmapNoise, -0.5F, 0.2F, baseHeight - 5, baseHeight + 15
                            );

                            heightmap = Math.min(
                                    heightmap,
                                    Mth.clampedMap(Math.abs(ctx.noise(0).sample(ctx.x() / 100.0, ctx.z() / 100.0)), 0F, 0.2F, baseHeight - 5, baseHeight + 15)
                            );

                            // lerp towards our base height near the edges...
                            heightmap = Math.min(
                                    heightmap,
                                    Mth.clampedMap(ctx.dependentValue(2), -24.0F, 0F, heightmap, baseHeight)
                            );

                            heightmap += ctx.dependentValue(0);
                            float rockyY = Mth.lerp(
                                    Mth.clampedMap(ctx.y(), baseHeight, baseHeight + 5, 0.2F, 0.75F),
                                    ctx.y(), ctx.dependentValue(1)
                            );
                            return  rockyY - heightmap;
                        }
                );
    }
}
