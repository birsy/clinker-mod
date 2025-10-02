package birsy.clinker.common.world.level.gen.worldfeature;

import birsy.clinker.common.world.level.gen.fluid.FluidLevel;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.biome.Biome;

import java.util.Set;

public abstract class WorldFeature {
    public final int depth, separationRadius;

    public WorldFeature(int depth, int separationRadius) {
        this.depth = depth;
        this.separationRadius = separationRadius;
    }

    public abstract int getCenterX();
    public abstract int getCenterZ();

    public abstract boolean within(int minX, int minZ, int maxX, int maxZ);

    public abstract boolean plan(MetaChunk metaChunk, RandomSource randomSource, NoiseComputerContext context);

    public void addChildFeatures(int childDepth, Set<WorldFeature> worldFeatures) {

    }

    public double modifyTerrain(int x, int y, int z, double currentNoiseValue, NoiseComputerContext context) {
        return currentNoiseValue;
    }

    public Holder<Biome> modifyBiome(int x, int y, int z, Holder<Biome> currentBiome, NoiseComputerContext context) {
        return currentBiome;
    }

    public FluidLevel modifyFluidLevel(int x, int y, int z, FluidLevel currentFluidLevel, NoiseComputerContext context) {
        return currentFluidLevel;
    }
}
