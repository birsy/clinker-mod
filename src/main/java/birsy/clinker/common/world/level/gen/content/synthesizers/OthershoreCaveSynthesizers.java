package birsy.clinker.common.world.level.gen.content.synthesizers;

import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.Synthesizer;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.noise.FNLNoiseProvider;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;

public class OthershoreCaveSynthesizers {
    public static final Synthesizer ENTRANCE_MASK = Synthesizer.builder()
            .withNoises(FNLNoiseProvider.create("cave_entrance"))
            .build(InterpolatingFieldResolution.COARSE_2D, (ctx) ->
                    Mth.map(ctx.noise(0).sample(ctx.x() / 128.0, ctx.z() / 128.0), 0.55F, 0.8F, 0.0F, 1.0F)
            );

    public static final Synthesizer SPELEOTHEMS = Synthesizer.builder()
            .withNoises(FNLNoiseProvider.create("speleothem"))
            .build(InterpolatingFieldResolution.COARSE_Y, (ctx) -> {
                float frequency = 1 / 12.0F;
                float value = ctx.noise(0).sample(ctx.x() * frequency, ctx.y() * frequency * 0.08, ctx.z() * frequency) / frequency;
                value += 8;
                return value;
            });

    public static final Synthesizer NOODLES = Synthesizer.builder()
            .withNoises(FNLNoiseProvider.create("cave_noodle_a"), FNLNoiseProvider.create("cave_noodle_b"),
                       FNLNoiseProvider.create("cave_noodle_c"), FNLNoiseProvider.create("cave_noodle_d"))
            .withDependencies(SPELEOTHEMS)
            .withRange(0, Integer.MAX_VALUE, -100)
            .build(InterpolatingFieldResolution.COARSE, (ctx) -> {
                float frequency = 1.0F / 150.0F;
                float sX = ctx.x() * frequency, sZ = ctx.z() * frequency, sY0 = ctx.y() * frequency, sY1 = sY0 * 2;

                float caveNoiseA = ctx.noise(0).sample(sX, sY0, sZ);
                float caveNoiseB = ctx.noise(1).sample(sX, sY1, sZ);

                float caveNoiseC = ctx.noise(2).sample(sX, sY0, sZ);
                float caveNoiseD = ctx.noise(3).sample(sX, sY1, sZ);

                float sumOfSquaresA = Mth.sqrt(caveNoiseA * caveNoiseA + caveNoiseB * caveNoiseB) / frequency;
                sumOfSquaresA = 30 - sumOfSquaresA;
                float sumOfSquaresB = Mth.sqrt(caveNoiseC * caveNoiseC + caveNoiseD * caveNoiseD) / frequency;
                sumOfSquaresB = 25 - sumOfSquaresB;

                float noodleCaves = Math.max(sumOfSquaresA, sumOfSquaresB);

                float speleothem = ctx.dependentValue(0);
                speleothem = MathUtils.smoothMin(speleothem, 0, 3);

                return MathUtils.smoothMin(noodleCaves + speleothem * 5, ctx.y(), 5);
            });

    public static final Synthesizer AQUIFER_CEILING_HEIGHT = Synthesizer.builder()
            .withNoises(FNLNoiseProvider.create("aquifer_ceiling_height"))
            .build(InterpolatingFieldResolution.COARSE_2D, (ctx) ->
                    Mth.clampedMap(
                            ctx.noise(0).sample(ctx.x() / 64.0F, ctx.z() / 64.0F),
                            -1, 1, -15, 3
                    )
            );
    public static final Synthesizer AQUIFER_ISLANDS = Synthesizer.builder()
            .withNoises(FNLNoiseProvider.create("aquifer_islands"))
            .build(InterpolatingFieldResolution.COARSE_2D, (ctx) ->
                    ctx.noise(0).sample( ctx.x() / 128.0F, ctx.z() / 128.0F)
            );
    public static final Synthesizer AQUIFER_WALLS = Synthesizer.builder()
            .withNoises(FNLNoiseProvider.create("aquifer_wall"), FNLNoiseProvider.create("aquifer_wall_holes"), FNLNoiseProvider.create("aquifer_wall_holes_small"))
            .build(InterpolatingFieldResolution.COARSE, (ctx) -> {
                float frequency = 1.0F / 190.0F;
                float aquiferWall = Math.abs(ctx.noise(0).sample( ctx.x() * frequency, ctx.z() * frequency)) / frequency - 30;

                frequency = 1.0F / 128.0F;
                float aquiferWallHoles = ctx.noise(1).sample( ctx.x() * frequency, ctx.z() * frequency) / frequency;
                aquiferWallHoles = 30 - Math.abs(aquiferWallHoles);

                float aquiferWallHolesSmall = ctx.noise(2).sample( ctx.x() * frequency * 2, ctx.z() * frequency * 2) / (frequency * 2);
                aquiferWallHolesSmall = Math.max(0, 20 - Math.abs(aquiferWallHolesSmall));

                aquiferWallHoles = aquiferWallHoles + aquiferWallHolesSmall * 8;

                return Math.max(aquiferWall, aquiferWallHoles);
            });
    public static final Synthesizer AQUIFER = Synthesizer.builder()
            .withDependencies(AQUIFER_CEILING_HEIGHT, AQUIFER_ISLANDS, AQUIFER_WALLS, SPELEOTHEMS)
            .withRange(-Integer.MAX_VALUE, 0, -100)
            .build(InterpolatingFieldResolution.COARSE, (ctx) -> {
                float seaLevel = -40;
                float ceilingHeight = ctx.dependentValue(0);

                float heightDensity = ctx.y() > seaLevel ?
                        Mth.map(ctx.y(), seaLevel, ceilingHeight, 40, 0) :
                        Mth.map(ctx.y(), -55, seaLevel, 0, 40);

                float islands = ctx.dependentValue(1) * 12 + 6;
                float density = Math.min(heightDensity, ctx.y() - (seaLevel - islands));

                float aquiferWall = ctx.dependentValue(2);

                density = MathUtils.smoothMin(density, aquiferWall, 5);

                float speleothem = ctx.dependentValue(3) - 1;
                speleothem = MathUtils.smoothMin(speleothem, 0, 3);
                float ceilingSpeleothems = speleothem * Mth.clampedMap(ctx.y(), seaLevel, ceilingHeight, Math.max(0, -islands), 1);
                float floorSpeleothems = speleothem * Mth.clampedMap(ctx.y(), -55, seaLevel - 2, 1, 0);

                return density + ceilingSpeleothems * 7 + floorSpeleothems * 5;
            });

    public static final Synthesizer CAVES = Synthesizer.builder()
            .withDependencies(NOODLES, AQUIFER)
            .withDefaultValue(-100)
            .build(InterpolatingFieldResolution.COARSE, (ctx) -> {
                return Math.max(ctx.dependentValue(0), ctx.dependentValue(1));
            });
}
