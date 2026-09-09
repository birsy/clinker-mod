package birsy.clinker.common.world.level.gen.content.synthesizers;

import birsy.clinker.common.world.level.gen.system.sampling.Synthesizer;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingFieldResolution;
import birsy.clinker.common.world.level.gen.system.sampling.noise.FNLNoiseProvider;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;

public class OthershoreCaveSynthesizers {
    public static final Synthesizer ENTRANCE_MASK = Synthesizer.builder()
            .withNoises(FNLNoiseProvider.create("cave_entrance"))
            .build(InterpolatingFieldResolution.COARSE_2D, (x, y, z, dependencyValues, noiseSamplers) ->
                    Mth.clampedMap(noiseSamplers[0].sample( x / 128.0, z / 128.0), 0.55, 0.8, 0.0, 1.0)
            );

    public static final Synthesizer SPELEOTHEMS = Synthesizer.builder()
            .withNoises(FNLNoiseProvider.create("speleothem"))
            .build(InterpolatingFieldResolution.COARSE_Y, (x, y, z, dependencyValues, noiseSamplers) -> {
                double frequency = 1 / 12.0;
                double value = noiseSamplers[0].sample(x * frequency, y * frequency * 0.08, z * frequency) / frequency;
                value += 8;
                return value;
            });

    public static final Synthesizer NOODLES = Synthesizer.builder()
            .withNoises(FNLNoiseProvider.create("cave_noodle_a"), FNLNoiseProvider.create("cave_noodle_b"),
                       FNLNoiseProvider.create("cave_noodle_c"), FNLNoiseProvider.create("cave_noodle_d"))
            .withDependencies(SPELEOTHEMS)
            .withRange(0, Integer.MAX_VALUE, -100)
            .build(InterpolatingFieldResolution.COARSE, (x, y, z, dependencyValues, noiseSamplers) -> {
                double frequency = 1.0 / 150.0;
                double sX = x * frequency, sZ = z * frequency, sY0 = y * frequency, sY1 = sY0 * 2;

                double caveNoiseA = noiseSamplers[0].sample(sX, sY0, sZ);
                double caveNoiseB = noiseSamplers[1].sample(sX, sY1, sZ);

                double caveNoiseC = noiseSamplers[2].sample(sX, sY0, sZ);
                double caveNoiseD = noiseSamplers[3].sample(sX, sY1, sZ);

                double sumOfSquaresA = Math.sqrt(caveNoiseA * caveNoiseA + caveNoiseB * caveNoiseB) / frequency;
                sumOfSquaresA = 30 - sumOfSquaresA;
                double sumOfSquaresB = Math.sqrt(caveNoiseC * caveNoiseC + caveNoiseD * caveNoiseD) / frequency;
                sumOfSquaresB = 25 - sumOfSquaresB;

                double noodleCaves = Math.max(sumOfSquaresA, sumOfSquaresB);

                double speleothem = dependencyValues[0];
                speleothem = MathUtils.smoothMinExpo(speleothem, 0, 3);

                return MathUtils.smoothMinExpo(noodleCaves + speleothem * 5, y, 5);
            });

    public static final Synthesizer AQUIFER_CEILING_HEIGHT = Synthesizer.builder()
            .withNoises(FNLNoiseProvider.create("aquifer_ceiling_height"))
            .build(InterpolatingFieldResolution.COARSE_2D, (x, y, z, dependencyValues, noiseSamplers) ->
                    Mth.clampedMap(
                            noiseSamplers[0].sample( x / 64.0, z / 64.0),
                            -1, 1, -15, 3
                    )
            );
    public static final Synthesizer AQUIFER_ISLANDS = Synthesizer.builder()
            .withNoises(FNLNoiseProvider.create("aquifer_islands"))
            .build(InterpolatingFieldResolution.COARSE_2D, (x, y, z, dependencyValues, noiseSamplers) ->
                    noiseSamplers[0].sample( x / 128.0, z / 128.0)
            );
    public static final Synthesizer AQUIFER_WALLS = Synthesizer.builder()
            .withNoises(FNLNoiseProvider.create("aquifer_wall"), FNLNoiseProvider.create("aquifer_wall_holes"), FNLNoiseProvider.create("aquifer_wall_holes_small"))
            .build(InterpolatingFieldResolution.COARSE, (x, y, z, dependencyValues, noiseSamplers) -> {
                double frequency = 1.0 / 190.0;
                double aquiferWall = Math.abs(noiseSamplers[0].sample( x * frequency, z * frequency)) / frequency - 30;

                frequency = 1.0 / 128.0;
                double aquiferWallHoles = noiseSamplers[1].sample( x * frequency, z * frequency) / frequency;
                aquiferWallHoles = 30 - Math.abs(aquiferWallHoles);

                double aquiferWallHolesSmall = noiseSamplers[2].sample( x * frequency * 2, z * frequency * 2) / (frequency * 2);
                aquiferWallHolesSmall = Math.max(0, 20 - Math.abs(aquiferWallHolesSmall));

                aquiferWallHoles = aquiferWallHoles + aquiferWallHolesSmall * 8;

                return Math.max(aquiferWall, aquiferWallHoles);
            });
    public static final Synthesizer AQUIFER = Synthesizer.builder()
            .withDependencies(AQUIFER_CEILING_HEIGHT, AQUIFER_ISLANDS, AQUIFER_WALLS, SPELEOTHEMS)
            .withRange(-Integer.MAX_VALUE, 0, -100)
            .build(InterpolatingFieldResolution.COARSE, (x, y, z, dependencyValues, noiseSamplers) -> {
                double seaLevel = -40;
                double ceilingHeight = dependencyValues[0];

                double heightDensity = y > seaLevel ?
                        Mth.map(y, seaLevel, ceilingHeight, 40, 0) :
                        Mth.map(y, -55, seaLevel, 0, 40);

                double islands = dependencyValues[1] * 12 + 6;
                double density = Math.min(heightDensity, y - (seaLevel - islands));

                double aquiferWall = dependencyValues[2];

                density = MathUtils.smoothMinExpo(density, aquiferWall, 5);

                double speleothem = dependencyValues[3] - 1;
                speleothem = MathUtils.smoothMinExpo(speleothem, 0, 3);
                double ceilingSpeleothems = speleothem * Mth.clampedMap(y, seaLevel, ceilingHeight, Math.max(0, -islands), 1);
                double floorSpeleothems = speleothem * Mth.clampedMap(y, -55, seaLevel - 2, 1, 0);

                return density + ceilingSpeleothems * 7 + floorSpeleothems * 5;
            });

    public static final Synthesizer CAVES = Synthesizer.builder()
            .withDependencies(NOODLES, AQUIFER)
            .withDefaultValue(-100)
            .build(InterpolatingFieldResolution.COARSE, (x, y, z, dependencyValues, noiseSamplers) -> {
                return Math.max(dependencyValues[0], dependencyValues[1]);
            });
}
