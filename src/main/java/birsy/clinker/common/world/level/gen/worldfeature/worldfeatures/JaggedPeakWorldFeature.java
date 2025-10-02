package birsy.clinker.common.world.level.gen.worldfeature.worldfeatures;

import birsy.clinker.common.world.level.gen.OthershoreNoiseComputers;
import birsy.clinker.common.world.level.gen.noise.*;
import birsy.clinker.common.world.level.gen.worldfeature.MetaChunk;
import birsy.clinker.common.world.level.gen.worldfeature.WorldFeature;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;

public class JaggedPeakWorldFeature extends WorldFeature {
    int centerX, centerZ;
    int radius = 16;
    double leanDirX, leanDirZ;

    private NoiseComputer tiltedNoiseComputer;

    public JaggedPeakWorldFeature(int depth, int separationRadius) {
        super(depth, separationRadius);
    }

    @Override
    public int getCenterX() {
        return centerX;
    }

    @Override
    public int getCenterZ() {
        return centerZ;
    }

    @Override
    public boolean within(int minX, int minZ, int maxX, int maxZ) {
        int checkRadius = this.radius * 2;
        return centerX > minX - checkRadius &&
               centerX < maxX + checkRadius &&
               centerZ > minZ - checkRadius &&
               centerZ < maxZ + checkRadius;
    }

    @Override
    public boolean plan(MetaChunk metaChunk, RandomSource randomSource, NoiseComputerContext context) {
        this.radius = randomSource.nextInt(100, 180);
        this.centerX = randomSource.nextInt(metaChunk.minX(), metaChunk.maxX());
        this.centerZ = randomSource.nextInt(metaChunk.minZ(), metaChunk.maxZ());

        Clinker.LOGGER.info("Jagged peak generated at {}, {}", this.centerX, this.centerZ);

        NoiseComputerExecutor executor = context.noiseComputerExecutor();
        double minSurfaceHeight = Double.MAX_VALUE;
        // find the most striking direction.
        for (double angle = 0; angle < 2 * Math.PI; angle+= (2 * Math.PI) / 16.0) {
            double leanDirX = Math.sin(angle),
                   leanDirZ = Math.cos(angle);
            int xPos = this.centerX + (int) (leanDirX * this.radius * 1.2),
                zPos = this.centerX + (int) (leanDirZ * this.radius * 1.2);
            double surfaceHeight = executor.compute(xPos, 0, zPos, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER);
            if (surfaceHeight < minSurfaceHeight) {
                minSurfaceHeight = surfaceHeight;
                this.leanDirX = leanDirX * 0.5;
                this.leanDirZ = leanDirZ * 0.5;
            }
        }

        this.tiltedNoiseComputer = new NoiseComputer("jagged_peak_" + randomSource.nextInt(), CacheType.INTERPOLATED_COARSE,
            (x, y, z, noiseContext) -> {
                NoiseHolder noiseHolder = context.noiseHolder();
                NoiseComputerExecutor noiseExecutor = context.noiseComputerExecutor();
                noiseHolder.registerNoise("jagged_peak", 2, 2, 0.5, 0.0);
                noiseHolder.registerNoise("jagged_peak_big");

                double verticalDistance = y - noiseExecutor.compute(x, y, z, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER);
                int leaningX = x - (int) (leanDirX * verticalDistance),
                    leaningZ = z - (int) (leanDirZ * verticalDistance);
                double val = noiseHolder.sample("jagged_peak", leaningX / 32.0, leaningZ / 32.0);
                val = Mth.map(val, -1, 1, 0, 1);
//                val *= Mth.map(noiseHolder.sample("jagged_peak_big", leaningX / 48.0, leaningZ / 48.0),
//                        -1, 1, 0, 1);
                return val;
            }
        );

        return true;
    }

    @Override
    public double modifyTerrain(int x, int y, int z, double currentNoiseValue, NoiseComputerContext context) {
        NoiseComputerExecutor executor = context.noiseComputerExecutor();
        double centerY = executor.compute(x, y, z, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER);
        double verticalDistance = y - centerY;
        int leaningX = x - (int) (leanDirX * verticalDistance),
            leaningZ = z - (int) (leanDirZ * verticalDistance);
        double horizontalDistance = (leaningX - centerX) * (leaningX - centerX) + (leaningZ - centerZ) * (leaningZ - centerZ);

        double noiseValue = context.noiseComputerExecutor().compute(x, y, z, this.tiltedNoiseComputer);
        //noiseValue *= noiseValue;
        noiseValue = Mth.lerp(noiseValue, 2, 1);

        double distance = y > centerY ?
                Math.abs(verticalDistance * noiseValue * 0.8) + Math.sqrt(horizontalDistance) :
                Math.sqrt(horizontalDistance + verticalDistance * verticalDistance);

        return MathUtils.smoothMinExpo(currentNoiseValue, distance - radius, 2);
    }
}
