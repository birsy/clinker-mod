package birsy.clinker.common.world.level.gen.system.fluid;

import birsy.clinker.common.world.level.gen.system.noise.FluidFieldNoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.NoiseComputer;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.worldfeature.WorldFeature;
import birsy.clinker.core.Clinker;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.Util;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.Arrays;
import java.util.Collection;

public class BFSBorderFluidField extends CellularFluidField {
    static final double VERTICAL_WEIGHT_MULTIPLIER = 1.25;
    static final int WEIGHT_PER_BLOCK = 10;
    static final int MAX_WEIGHT = (int) Math.round(Math.sqrt(3) * WEIGHT_PER_BLOCK * VERTICAL_WEIGHT_MULTIPLIER);
    static final int[] NEIGHBOR_WEIGHTS = Util.make(() -> {
        int[] weights = new int[NEIGHBOR_OFFSETS.length];
        for (int i = 0; i < NEIGHBOR_OFFSETS.length; i++) {
            int[] neighborOffsets = NEIGHBOR_OFFSETS[i];
            double distance = Mth.length(neighborOffsets[0], neighborOffsets[1], neighborOffsets[2]);
            if (neighborOffsets[1] >= 1) distance *= VERTICAL_WEIGHT_MULTIPLIER;
            weights[i] = (int) Math.round(distance * WEIGHT_PER_BLOCK);
        }
        return weights;
    });

    final int[] borderDistances;

    public BFSBorderFluidField(
            RandomState randomState,
            ChunkAccess chunk,
            FluidFieldNoiseFieldCache cache,
            FluidFieldFiller baseFluidFieldFiller,
            Collection<WorldFeature> worldFeatures,
            int cellWidth, int cellHeight, int paddingCells) {
        super(randomState, chunk, cache, baseFluidFieldFiller, worldFeatures, cellWidth, cellHeight, paddingCells);
        this.borderDistances = new int[this.fluidStates.length];
        // fill with maximum possible distance
        Arrays.fill(this.borderDistances, 1000);
    }

    @Override
    public double getBorderDensity(int localX, int localY, int localZ) {
        int bX = localX + this.paddingBlocksXZ;
        int bY = localY + this.paddingBlocksY;
        int bZ = localZ + this.paddingBlocksXZ;
        int blockIndex = index(bX, bY, bZ, this.blockCountXZ, this.blockCountY);
        return (this.borderDistances[blockIndex] / (double)WEIGHT_PER_BLOCK) - 2;
    }

    @Override
    public void precomputeValues(NoiseField finalDensityField, NoiseField waterfallPresenceField) {
        super.precomputeValues(finalDensityField, waterfallPresenceField);
        this.initializeFluidBorders(finalDensityField);
        this.computeWaterfalls(waterfallPresenceField);
        this.computeBorderDistances();
    }

    public void initializeFluidBorders(NoiseField finalDensityField) {
        // place initial borders
        for (int bY = 0; bY < this.blockCountY; bY++) {
            int prevY = Math.max(0, bY - 1);
            for (int bZ = 0; bZ < this.blockCountXZ; bZ++) {
                int prevZ = Math.max(0, bZ - 1);
                for (int bX = 0; bX < this.blockCountXZ; bX++) {
                    int blockIndex = index(bX, bY, bZ, this.blockCountXZ, this.blockCountY);
                    BlockState state = this.fluidStates[blockIndex];

                    // vertical
                    int yIndex = index(bX, prevY, bZ, this.blockCountXZ, this.blockCountY);
                    BlockState belowState = this.fluidStates[yIndex];
                    // air blocks don't consider fluid blocks below them
                    if (!state.isAir() && state != belowState) {
                        borderDistances[blockIndex] = 0;
                        continue;
                    }

                    int zIndex = index(bX, bY, prevZ, this.blockCountXZ, this.blockCountY);
                    BlockState zState = this.fluidStates[zIndex];
                    if (state != zState) {
                        borderDistances[blockIndex] = 0;
                        continue;
                    }

                    int prevX = Math.max(0, bX - 1);
                    int xIndex = index(prevX, bY, bZ, this.blockCountXZ, this.blockCountY);
                    BlockState xState = this.fluidStates[xIndex];
                    if (state != xState) {
                        borderDistances[blockIndex] = 0;
                        continue;
                    }
                }
            }
        }
    }

    public void computeWaterfalls(NoiseField waterfallPresence) {
        for (int bY = 1; bY < this.blockCountY; bY++) {
            int localBlockY = bY - this.paddingBlocksY;
            for (int bZ = 0; bZ < this.blockCountXZ; bZ++) {
                int localBlockZ = bZ - this.paddingBlocksXZ;
                for (int bX = 0; bX < this.blockCountXZ; bX++) {
                    int localBlockX = bX - this.paddingBlocksXZ;

                    int blockIndex = index(bX, bY, bZ, this.blockCountXZ, this.blockCountY);
                    BlockState state = this.fluidStates[blockIndex];
                    int distance = this.borderDistances[blockIndex];
                    if (!state.isAir() || distance <= 0) continue;

                    int belowBlockIndex = index(bX, bY - 1, bZ, this.blockCountXZ, this.blockCountY);
                    int belowDistance = this.borderDistances[belowBlockIndex];
                    if (belowDistance > 0) continue;

                    double waterfallPresenceValue = waterfallPresence.retrieve(localBlockX, localBlockY, localBlockZ);
                    if (waterfallPresenceValue <= 0) continue;

                    // "smear" the air downwards.
                    this.borderDistances[belowBlockIndex] = distance;
                    for (int i = 2; i < 2 + waterfallPresenceValue; i++) {
                        int belowY = bY - i;
                        if (belowY < 0) break;
                        belowBlockIndex = index(bX, belowY, bZ, this.blockCountXZ, this.blockCountY);
                        belowDistance = this.borderDistances[belowBlockIndex];
                        if (belowDistance > 0) break;
                        this.borderDistances[belowBlockIndex] = distance;
                    }
                }
            }
        }
    }

    public void computeBorderDistances() {
        // dial's algorithms dijkstra bfs
        // scaled by face cost
        final int maxDistance = Math.min(this.paddingBlocksXZ, this.paddingBlocksY) * MAX_WEIGHT + 1;

        IntArrayList[] buckets = new IntArrayList[maxDistance];
        for (int i = 0; i < maxDistance; i++) buckets[i] = new IntArrayList();

        for (int i = 0; i < borderDistances.length; i++) {
            if (borderDistances[i] == 0) buckets[0].add(i);
        }

        for (int curDist = 0; curDist < maxDistance; curDist++) {
            IntArrayList bucket = buckets[curDist];
            for (int idx = 0; idx < bucket.size(); idx++) {
                int currentIndex = bucket.getInt(idx);
                int currentDistance = borderDistances[currentIndex];
                if (currentDistance != curDist) continue; // out of date

                // decode from index
                int layerSize = this.blockCountXZ * this.blockCountXZ;
                int bY = currentIndex / layerSize;
                int rem = currentIndex % layerSize;
                int bZ = rem / this.blockCountXZ;
                int bX = rem % this.blockCountXZ;

                for (int i = 0; i < NEIGHBOR_OFFSETS.length; i++) {
                    int[] neighborOffsets = NEIGHBOR_OFFSETS[i];
                    int nX = bX + neighborOffsets[0],
                        nY = bY + neighborOffsets[1],
                        nZ = bZ + neighborOffsets[2];
                    if (outOfRange(nX, nY, nZ, blockCountXZ, blockCountY)) continue;

                    int neighborIndex = index(nX, nY, nZ, blockCountXZ, blockCountY);
                    int newDistance = currentDistance +  NEIGHBOR_WEIGHTS[i];
                    if (newDistance < borderDistances[neighborIndex]) {
                        borderDistances[neighborIndex] = newDistance;
                        if (newDistance < maxDistance) buckets[newDistance].add(neighborIndex);
                    }
                }
            }
        }
    }
}
