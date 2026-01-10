package birsy.clinker.common.world.level.gen.content.worldfeatures;

import birsy.clinker.common.world.level.gen.system.fluid.FluidLevel;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.PaddedNoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.worldfeature.MetaChunk;
import birsy.clinker.common.world.level.gen.system.worldfeature.WorldFeature;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class UndergroundLakeWorldFeature extends WorldFeature {
    int centerX, centerZ;
    int radius = 16;
    int waterLevel = 0;
    BlockState fluid;

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
        int checkRadius = Mth.ceil(this.radius * 1.5) + 32;
        return centerX > minX - checkRadius &&
               centerX < maxX + checkRadius &&
               centerZ > minZ - checkRadius &&
               centerZ < maxZ + checkRadius;
    }

    @Override
    public boolean plan(LevelAccessor level, MetaChunk metaChunk, RandomSource randomSource, NoiseContext context) {
        this.radius = randomSource.nextInt(10, 30) + randomSource.nextInt(10, 30);
        this.centerX = randomSource.nextInt(metaChunk.minX(), metaChunk.maxX());
        this.centerZ = randomSource.nextInt(metaChunk.minZ(), metaChunk.maxZ());

        double surfaceHeight = context.retrieve(ClinkerNoiseComputers.BASE_SURFACE_HEIGHT, this.centerX, 0, this.centerZ);
        int maxFluidLevel = (int) surfaceHeight - 20;
        if (maxFluidLevel <= 0) return false;
        this.waterLevel = randomSource.nextInt(4, maxFluidLevel);
        this.fluid = randomSource.nextInt(3) != 0 ? Blocks.WATER.defaultBlockState() : Blocks.LAVA.defaultBlockState();
        return true;
    }

    @Override
    public void prefillFluidNoiseFields(int chunkX, int chunkZ, PaddedNoiseFieldCache cache) {
        cache.fillNoiseField(ClinkerNoiseComputers.BASE_SURFACE_HEIGHT);
        cache.fillNoiseField(ClinkerNoiseComputers.BASE_NOISE_2D[6]);
        cache.fillNoiseField(ClinkerNoiseComputers.BASE_NOISE_2D_ALT[6]);
    }
    @Override
    public FluidLevel modifyFluidLevel(int x, int y, int z, FluidLevel currentFluidLevel, NoiseContext context) {
        if (currentFluidLevel.height() > this.waterLevel)
            return currentFluidLevel;

        double surfaceY = context.retrieve(ClinkerNoiseComputers.BASE_SURFACE_HEIGHT, x, y, z);
        if (y > surfaceY - 10)
            return currentFluidLevel;

        double offsetX = x + context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[6], x, y, z) * this.radius * 0.25,
               offsetZ = z + context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D_ALT[6], x, y, z) * this.radius * 0.25;
        double horizontalDistance = Math.sqrt((offsetX - centerX) * (offsetX - centerX) + (offsetZ - centerZ) * (offsetZ - centerZ));
        if (horizontalDistance - radius > 0)
            return currentFluidLevel;

        double minY = Mth.clampedMap(horizontalDistance, 0, radius, this.waterLevel - 20, this.waterLevel - 5);
        if (y > this.waterLevel + 15 || y < minY - 5)
            return currentFluidLevel;

        return new FluidLevel(this.waterLevel, this.fluid);
    }
}
