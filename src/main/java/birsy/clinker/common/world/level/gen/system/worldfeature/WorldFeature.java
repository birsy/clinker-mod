package birsy.clinker.common.world.level.gen.system.worldfeature;

import birsy.clinker.common.world.level.gen.system.noise.FluidFieldNoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;

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

    public abstract boolean plan(LevelAccessor level, MetaChunk metaChunk, RandomSource randomSource);

    public void addChildFeatures(int childDepth, Set<WorldFeature> worldFeatures) {}

    public void modifySurfaceDensityField(int minX, int minY, int minZ, NoiseFieldCache cache, NoiseField field) {}
    public void modifyCaveDensityField(int minX, int minY, int minZ, NoiseFieldCache cache, NoiseField field) {}
    public void modifyFinalDensityField(int minX, int minY, int minZ, NoiseFieldCache cache, NoiseField field) {}
    public void modifyWaterfallPresenceField(int minX, int minY, int minZ, FluidFieldNoiseFieldCache cache, NoiseField field) {}

//    public Holder<Biome> modifyBiome(int x, int y, int z, Holder<Biome> currentBiome, NoiseComputerContext context) {
//        return currentBiome;
//    }
//
//    public FluidLevel modifyFluidLevel(int x, int y, int z, FluidLevel currentFluidLevel, NoiseComputerContext context) {
//        return currentFluidLevel;
//    }
//
//    public double modifyWaterfallPresence(int x, int y, int z, double currentValue, NoiseComputerContext context) {
//        return currentValue;
//    }
}
