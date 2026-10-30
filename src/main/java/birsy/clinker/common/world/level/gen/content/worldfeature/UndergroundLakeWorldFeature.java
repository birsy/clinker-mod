package birsy.clinker.common.world.level.gen.content.worldfeature;

import birsy.clinker.common.world.level.gen.system.biome.BiomeGenerationInfo;
import birsy.clinker.common.world.level.gen.system.fluid.FluidLevel;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeature;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesFluids;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.DependencyRetriever;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
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

    public static Optional<UndergroundLakeWorldFeature> realize(@Nullable BlockPos center, int minX, int minZ, int maxX, int maxZ, int metaChunkDepth, RandomSource randomSource, WorldFeatureContext worldContext) {
        int centerX, centerZ;
        if (center != null) {
            centerX = center.getX();
            centerZ = center.getZ();
        } else {
            centerX = randomSource.nextIntBetweenInclusive(minX, maxX);
            centerZ = randomSource.nextIntBetweenInclusive(minZ, maxZ);
        }

        Holder<Biome> biome = worldContext.biomeSource().getSurfaceBiome(centerX, centerZ);
        BiomeGenerationInfo generationInfo = BiomeGenerationInfo.fromBiome(biome);
        int biomeHeight = generationInfo.shaper().baseHeight - 20;

        int radius = randomSource.nextInt(10, 30) + randomSource.nextInt(10, 30);
        int waterLevel = randomSource.nextInt(4, biomeHeight);
        // todo: replace with weighted list
        BlockState fluid = randomSource.nextInt(3) != 0 ? Blocks.WATER.defaultBlockState() : Blocks.LAVA.defaultBlockState();
        return Optional.of(new UndergroundLakeWorldFeature(centerX, centerZ, radius, waterLevel, fluid));
    }

    @Override
    public FluidLevel modifyFluidLevel(double x, double y, double z,
                                       FluidLevel currentFluidLevel,
                                       WorldFeatureContext context,
                                       DependencyRetriever seaFloorHeightRetriever) {
        if (currentFluidLevel.height() > this.waterLevel)
            return currentFluidLevel;
        if (y > this.waterLevel + 15 || y < this.waterLevel - 25)
            return currentFluidLevel;

        double surfaceY = seaFloorHeightRetriever.retrieve((int) x, 0, (int) z);
        if (y > surfaceY - 20)
            return currentFluidLevel;

        double horizontalDistance = Mth.length(x - centerX, z - centerZ);
        if (horizontalDistance - radius > 0)
            return currentFluidLevel;

        double lowerBound = Mth.clampedMap(horizontalDistance, 0, radius, this.waterLevel - 20, this.waterLevel - 5);
        if (y < lowerBound - 4) return currentFluidLevel;

        return new FluidLevel(this.waterLevel, this.fluid);
    }
}