package birsy.clinker.common.world.level.gen.worldfeature.worldfeatures;

import birsy.clinker.common.world.level.gen.fluid.FluidLevel;
import birsy.clinker.common.world.level.gen.OthershoreNoiseComputers;
import birsy.clinker.common.world.level.gen.noise.*;
import birsy.clinker.common.world.level.gen.worldfeature.MetaChunk;
import birsy.clinker.common.world.level.gen.worldfeature.WorldFeature;
import birsy.clinker.core.Clinker;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;

public class UndergroundLakeWorldFeature extends WorldFeature {
    int centerX, centerZ;
    int radius = 16;
    int waterLevel = 0;
    private static final NoiseComputer SEA_OFFSET_X =
            new NoiseComputer("underground_sea_offset_x", CacheType.INTERPOLATED_2D_COARSE, (x, y, z, context) -> {
               context.noiseHolder().registerNoise("underground_sea_offset_x");
               return context.noiseHolder().sample("underground_sea_offset_x", x / 64.0, z / 64.0);
            });
    private static final NoiseComputer SEA_OFFSET_Z =
            new NoiseComputer("underground_sea_offset_z", CacheType.INTERPOLATED_2D_COARSE, (x, y, z, context) -> {
                context.noiseHolder().registerNoise("underground_sea_offset_z");
                return context.noiseHolder().sample("underground_sea_offset_z", x / 64.0, z / 64.0);
            });

    public UndergroundLakeWorldFeature(int depth, int separationRadius) {
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
        int checkRadius = this.radius * 3;
        return centerX > minX - checkRadius &&
               centerX < maxX + checkRadius &&
               centerZ > minZ - checkRadius &&
               centerZ < maxZ + checkRadius;
    }

    @Override
    public boolean plan(MetaChunk metaChunk, RandomSource randomSource, NoiseComputerContext context) {
        this.radius = randomSource.nextInt(20, 70);
        this.centerX = randomSource.nextInt(metaChunk.minX(), metaChunk.maxX());
        this.centerZ = randomSource.nextInt(metaChunk.minZ(), metaChunk.maxZ());

        double surfaceHeight = context.noiseComputerExecutor().compute(this.centerX, 0, this.centerZ, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER);
        int maxFluidLevel = (int) surfaceHeight - 20;
        if (maxFluidLevel <= 0) return false;
        this.waterLevel = randomSource.nextInt(0, maxFluidLevel);

        return true;
    }

    @Override
    public FluidLevel modifyFluidLevel(int x, int y, int z, FluidLevel currentFluidLevel, NoiseComputerContext context) {
        if (currentFluidLevel.height() > this.waterLevel)
            return currentFluidLevel;

        NoiseComputerExecutor executor = context.noiseComputerExecutor();
        double surfaceY = executor.compute(x, y, z, OthershoreNoiseComputers.SURFACE_HEIGHT_COMPUTER);
        if (y > surfaceY - 20)
            return currentFluidLevel;

        double offsetX = x,// + executor.compute(x, y, z, SEA_OFFSET_X) * this.radius * 0.25,
               offsetZ = z;// + executor.compute(x, y, z, SEA_OFFSET_Z) * this.radius * 0.25;
        double horizontalDistance = Math.sqrt((offsetX - centerX) * (offsetX - centerX) + (offsetZ - centerZ) * (offsetZ - centerZ));
        if (horizontalDistance - radius > 0)
            return currentFluidLevel;

        double minY = Mth.clampedMap(horizontalDistance, 0, radius, this.waterLevel - 20, this.waterLevel - 5);
        if (y > this.waterLevel + 15 || y < minY - 5)
            return currentFluidLevel;

        return new FluidLevel(this.waterLevel, Blocks.WATER.defaultBlockState());
    }
}
