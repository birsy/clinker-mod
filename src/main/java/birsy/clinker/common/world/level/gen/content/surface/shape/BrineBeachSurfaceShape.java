package birsy.clinker.common.world.level.gen.content.surface.shape;

import birsy.clinker.common.world.level.gen.content.synthesizers.UtilitySynthesizers;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.noise.FNLNoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.noise.NoiseProvider;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.SynthesizerContext;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;

public class BrineBeachSurfaceShape extends SmoothEdgeSurfaceShape {
    public BrineBeachSurfaceShape(int baseHeight, int expectedBorderHeight) {
        super(baseHeight, baseHeight - 8, baseHeight + 24, expectedBorderHeight);
    }

    @Override
    protected Synthesizer createSurfaceSynthesizer(Synthesizer biomeSdfSynthesizer, Synthesizer heightmapSynthesizer, boolean nearbySurface) {
        Synthesizer islandShapeSynthesizer = Synthesizer.builder("island_shape")
                .withDependencies(biomeSdfSynthesizer)
                .withNoises(FNLNoiseProvider.create("island_shape", 1 / 130.0F, 4))
                .build(InterpolatingFieldResolution.COARSE_2D, ctx -> {
                    float biomeDistance = ctx.dependentValue(0);
                    float noise = ctx.noise(0).sample(ctx.x(), ctx.z());
                    noise *= 130.0F;
                    noise += Mth.clampedMap(biomeDistance, -30F, 10F, 0, 130.0F);
                    return noise;// + 80.0F;
                });

        Synthesizer islandHeightSynthesizer = Synthesizer.builder("island_height")
                .withNoises(FNLNoiseProvider.create("island_height", 1 / 80.0F, 3))
                .build(InterpolatingFieldResolution.COARSE_2D, ctx -> {
                    return Mth.clampedMap(ctx.noise(0).sample(ctx.x(), ctx.z()),
                            -1F, 1F,
                            baseHeight - 5, baseHeight + 15
                    );
                });

        return initSurfaceSynthesizer("flat_surface_shape")
                .withDependencies(heightmapSynthesizer, islandShapeSynthesizer, islandHeightSynthesizer, UtilitySynthesizers.ROCKY_CLIFF_PILLAR, UtilitySynthesizers.BASIC_NOISE_2D[4])
                .build(InterpolatingFieldResolution.COARSE_X_FINE_Y, ctx -> {
                    int y = ctx.y();
                    float baseHeight = ctx.dependentValue(0) + ctx.dependentValue(4) * 3;
                    float heightMapDensity = y - (baseHeight - 3);
                    float islandShape = ctx.dependentValue(1);

                    float islandHeight = ctx.dependentValue(2);
                    float upperIslandDensity = y - islandHeight;

                    float islandErosionHeight = Mth.lerp(0.1F, baseHeight, islandHeight);
                    float erosion = y < islandErosionHeight ?
                            Mth.clampedMap(y, baseHeight, islandErosionHeight, 0, 1) :
                            Mth.clampedMap(y, islandErosionHeight, islandHeight, 1, 0);
                    //erosion = Mth.sqrt(erosion);
                    float erosionStrength = (1F - ctx.dependentValue(3)) * 30.0F;
                    islandShape += erosion * erosionStrength;

                    float islandDensity =  MathUtils.smoothMax(islandShape, upperIslandDensity, 5.0F);

                    return MathUtils.smoothMin(heightMapDensity, islandDensity, 5.0F);
                });
    }
}
