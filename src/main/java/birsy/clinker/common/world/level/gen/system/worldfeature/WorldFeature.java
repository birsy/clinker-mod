package birsy.clinker.common.world.level.gen.system.worldfeature;

import birsy.clinker.common.world.level.gen.system.fluid.FluidLevel;
import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import birsy.clinker.common.world.level.gen.system.noise.PaddedNoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
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

    public abstract boolean plan(LevelAccessor level, MetaChunk metaChunk, RandomSource randomSource, NoiseContext context);

    public void addChildFeatures(int childDepth, Set<WorldFeature> worldFeatures) {}

    public void modifySurfaceHeight(int minX, int minZ, NoiseFieldCache cache, NoiseField field) {}
    public void modifySurfaceDensityField(int minX, int minY, int minZ, NoiseFieldCache cache, NoiseField field) {}
    public void modifyCaveDensityField(int minX, int minY, int minZ, NoiseFieldCache cache, NoiseField field, NoiseField maskField) {}
    public void modifyFinalDensityField(int minX, int minY, int minZ, NoiseFieldCache cache, NoiseField field) {}
    public void modifyWaterfallPresenceField(int minX, int minY, int minZ, PaddedNoiseFieldCache cache, NoiseField field) {}

    public void prefillBiomeNoiseFields(int chunkX, int chunkZ, NoiseFieldCache cache) {}
    public Holder<Biome> modifyBiome(int x, int y, int z, Holder<Biome> currentBiome, NoiseContext context) {
        return currentBiome;
    }

    public void prefillFluidNoiseFields(int chunkX, int chunkZ, PaddedNoiseFieldCache cache) {}
    public FluidLevel modifyFluidLevel(int x, int y, int z, FluidLevel currentFluidLevel, NoiseContext context) {
        return currentFluidLevel;
    }

}
