package birsy.clinker.common.world.level.gen.content.surface.shape;

import birsy.clinker.common.world.level.gen.OthershoreGenerationConstants;
import birsy.clinker.common.world.level.gen.content.synthesizers.UtilitySynthesizers;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;
import birsy.clinker.common.world.level.gen.system.surface.shape.SurfaceShape;
import net.minecraft.util.Mth;

public abstract class CliffAndOverhangEdgeSurfaceShape extends SurfaceShape {
    final int upperSurfaceBound, lowerSurfaceBound;
    final int cliffBaseHeight, cliffOutset;
    final int overhangBaseHeight, overhangOutset;
    final int overhangDepth;

    /*
    *   surface
    * ->base     ____
    *   height        \_
    *                   \_
    *   cliff             \
    * ->base               | cliff outset
    *   height            /
    *                   /
    *   overhang       |
    * ->base           |
    *   height        /
    *                | overhang outset
    *                |_______________
    */
    protected CliffAndOverhangEdgeSurfaceShape(int surfaceHeight, int upperSurfaceBound, int lowerSurfaceBound,
                                               int cliffBaseHeight, int cliffOutset,
                                               int overhangBaseHeight, int overhangOutset) {
        super(surfaceHeight, upperSurfaceBound, lowerSurfaceBound);
        this.upperSurfaceBound = upperSurfaceBound;
        this.lowerSurfaceBound = lowerSurfaceBound;
        this.cliffBaseHeight = cliffBaseHeight;
        this.cliffOutset = cliffOutset;
        this.overhangBaseHeight = overhangBaseHeight;
        this.overhangOutset = overhangOutset;

        this.overhangDepth = cliffOutset - overhangOutset;
    }

    protected CliffAndOverhangEdgeSurfaceShape(int surfaceHeight, int upperSurfaceBound, int lowerSurfaceBound, int cliffBaseHeight) {
        this(surfaceHeight, upperSurfaceBound, lowerSurfaceBound,
                cliffBaseHeight, 28,
                OthershoreGenerationConstants.SEA_HEIGHT, 28 - 20);
    }


    protected abstract Synthesizer createSurfaceSynthesizer(Synthesizer biomeSdfSynthesizer);

    protected Synthesizer createCliffSynthesizer(Synthesizer biomeSdfSynthesizer) {
        return Synthesizer.builder("cliffs_surface_shape")
                .withDependencies(biomeSdfSynthesizer, UtilitySynthesizers.ROCKY_CLIFF_Y, UtilitySynthesizers.ROCKY_CLIFF_PILLAR)
                .build(InterpolatingFieldResolution.COARSE_X_FINE_Y,
                        ctx -> {
                            float y = ctx.dependentValue(1);
                            float pillars = ctx.dependentValue(2) * overhangDepth;
                            boolean isOverhang = y > cliffBaseHeight;
                            float extent = isOverhang ?
                                    Mth.clampedMap(y, baseHeight, cliffBaseHeight, 0, 1) :
                                    Mth.clampedMap(y, cliffBaseHeight, overhangBaseHeight, 1, 0);
                            extent = isOverhang ?
                                    Mth.lerp(extent, 0, cliffOutset) :
                                    Mth.lerp(extent * extent, overhangOutset + pillars, cliffOutset);
                            return ctx.dependentValue(0) - extent;
                        }
                );
    }

    @Override
    protected Synthesizer create(Synthesizer biomeSdfSynthesizer, boolean surfaceContainedInChunk) {
        Synthesizer cliffSynthesizer = createCliffSynthesizer(biomeSdfSynthesizer);
        if (surfaceContainedInChunk) {
            return Synthesizer.builder("cliff_combiner_with_surface")
                    .withDependencies(cliffSynthesizer, createSurfaceSynthesizer(biomeSdfSynthesizer))
                    .build(InterpolatingFieldResolution.COARSE_X_FINE_Y,
                            ctx -> {
                                float surfaceDist = ctx.dependentValue(1);
                                float cliffDist = ctx.dependentValue(0);
                                return Math.max(surfaceDist, cliffDist);
                            }
                    );
        } else {
            return Synthesizer.builder("cliff_combiner_with_surface_placeholder")
                    .withDependencies(cliffSynthesizer)
                    .build(InterpolatingFieldResolution.COARSE_X_FINE_Y,
                            ctx -> {
                                float surfaceDist = ctx.y() - baseHeight;
                                float cliffDist = ctx.dependentValue(0);
                                return Math.max(surfaceDist, cliffDist);
                            }
                    );
        }
    }
}
