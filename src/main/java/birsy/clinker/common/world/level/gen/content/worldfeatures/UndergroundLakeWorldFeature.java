package birsy.clinker.common.world.level.gen.content.worldfeatures;

import birsy.clinker.common.world.level.gen.system.fluid.FluidLevel;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureType;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.PaddedNoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.UncachedNoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.metachunk.MetaChunk;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeature;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesFluids;
import birsy.clinker.core.registry.worldgen.ClinkerNoiseComputers;
import birsy.clinker.core.registry.worldgen.ClinkerWorldFeatures;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Optional;

public class UndergroundLakeWorldFeature extends WorldFeature implements ModifiesFluids {
    final int centerX, centerZ;
    final int radius;
    final int waterLevel;
    final BlockState fluid;

    public UndergroundLakeWorldFeature(int centerX, int centerZ, int radius, int waterLevel, BlockState fluid) {
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.radius = radius;
        this.waterLevel = waterLevel;
        this.fluid = fluid;
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

    public static Optional<UndergroundLakeWorldFeature> realize(@Nullable BlockPos center,
                                                             LevelAccessor level,
                                                             int minX, int minZ, int maxX, int maxZ, int metaChunkDepth,
                                                             RandomSource randomSource,
                                                             UncachedNoiseContext context,
                                                             WorldFeatureContext worldContext) {
        int centerX, centerZ;
        if (center != null) {
            centerX = center.getX();
            centerZ = center.getZ();
        } else {
            centerX = randomSource.nextIntBetweenInclusive(minX, maxX);
            centerZ = randomSource.nextIntBetweenInclusive(minZ, maxZ);
        }

        double[] biomeWeights = worldContext.biomeBlender().getBiomeBlendingWeights(new double[worldContext.biomeList().maxId() + 1], centerX, centerZ);
        double surfaceHeight = worldContext.surfaceShaperSystem().getHeight(biomeWeights, centerX, centerZ, context);
        int maxFluidLevel = Mth.floor(surfaceHeight - 15);
        if (maxFluidLevel < 4) return Optional.empty();

        int radius = randomSource.nextInt(10, 30) + randomSource.nextInt(10, 30);
        int waterLevel = randomSource.nextInt(4, maxFluidLevel);
        // todo: replace with weighted list
        BlockState fluid = randomSource.nextInt(3) != 0 ? Blocks.WATER.defaultBlockState() : Blocks.LAVA.defaultBlockState();
        return Optional.of(new UndergroundLakeWorldFeature(centerX, centerZ, radius, waterLevel, fluid));
    }

    @Override
    public void prefillFluidNoiseFields(int chunkX, int chunkZ, PaddedNoiseFieldCache cache, WorldFeatureContext worldContext) {
        cache.fillNoiseField(ClinkerNoiseComputers.BASE_NOISE_2D[6]);
        cache.fillNoiseField(ClinkerNoiseComputers.BASE_NOISE_2D_ALT[6]);
    }

    @Override
    public FluidLevel modifyFluidLevel(int x, int y, int z, int minX, int minY, int minZ, FluidLevel currentFluidLevel, NoiseContext context, NoiseField heightmap) {
        if (currentFluidLevel.height() > this.waterLevel)
            return currentFluidLevel;
        if (y > this.waterLevel + 15 || y < this.waterLevel - 25)
            return currentFluidLevel;

        int localX = x - minX, localZ = z - minZ;
        double surfaceY = heightmap.retrieve(localX, 0, localZ);
        if (y > surfaceY - 10)
            return currentFluidLevel;

        double offsetX = x + context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D[6], x, y, z) * this.radius * 0.25,
               offsetZ = z + context.retrieve(ClinkerNoiseComputers.BASE_NOISE_2D_ALT[6], x, y, z) * this.radius * 0.25;
        double horizontalDistance = Math.sqrt((offsetX - centerX) * (offsetX - centerX) + (offsetZ - centerZ) * (offsetZ - centerZ));
        if (horizontalDistance - radius > 0)
            return currentFluidLevel;

        double lowerBound = Mth.clampedMap(horizontalDistance, 0, radius, this.waterLevel - 20, this.waterLevel - 5);
        if (y < lowerBound - 4) return currentFluidLevel;

        return new FluidLevel(this.waterLevel, this.fluid);
    }
}
