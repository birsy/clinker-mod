package birsy.clinker.common.world.level.gen.system.fluid;

import birsy.clinker.common.world.level.gen.system.noise.PaddedNoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.worldfeature.WorldFeature;
import birsy.clinker.common.world.level.gen.system.worldfeature.WorldFeatureContext;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.Arrays;
import java.util.Collection;

public class BFSBorderFluidField extends CellularFluidField {
    // really approximate euclidean distance
    // trying to keep this as small as possible since the smaller it is the faster dial's algorithm runs
    static final int ADJACENT_COST = 3, DIAGONAL_2_COST = 4, DIAGONAL_3_COST = 5;
    static final float UP_COST_MULTIPLIER = 1.5F;
    static final int[] NEIGHBOR_COSTS = Util.make(() -> {
        int[] costs = new int[NEIGHBOR_OFFSETS.length];
        for (int i = 0; i < NEIGHBOR_OFFSETS.length; i++) {
            int[] neighborOffsets = NEIGHBOR_OFFSETS[i];
            // determine what kind of corner it is
            int zeroCount = 0;
            for (int neighborOffset : neighborOffsets) if (neighborOffset == 0) zeroCount++;
            // assign cost accordingly
            int cost = 0;
            if (zeroCount >= 2) cost = ADJACENT_COST;
            else if (zeroCount >= 1) cost = DIAGONAL_2_COST;
            else if (zeroCount >= 0) cost = DIAGONAL_3_COST;
            // going up costs a little extra
            if (neighborOffsets[1] >= 1) cost = Math.round(cost * UP_COST_MULTIPLIER);
            costs[i] = cost;
        }
        return costs;
    });

    final int[] borderDistances;

    public BFSBorderFluidField(
            RandomState randomState,
            ChunkAccess chunk,
            PaddedNoiseFieldCache cache,
            FluidFieldFiller baseFluidFieldFiller,
            Collection<WorldFeature> worldFeatures,
            WorldFeatureContext worldFeatureContext,
            NoiseField heightmap,
            int cellWidth, int cellHeight, int paddingCells) {
        super(randomState, chunk, cache, baseFluidFieldFiller, worldFeatures, worldFeatureContext, heightmap, cellWidth, cellHeight, paddingCells);
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
        return (this.borderDistances[blockIndex] / (double) ADJACENT_COST) - 2;
    }

    @Override
    public void precomputeValues(NoiseField finalDensityField, NoiseField waterfallPresenceField) {
        super.precomputeValues(finalDensityField, waterfallPresenceField);
        this.initializeFluidBordersByCell();
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

                    int yIndex = index(bX, prevY, bZ, this.blockCountXZ, this.blockCountY);
                    BlockState belowState = this.fluidStates[yIndex];
                    // only fluids check the block below them
                    // because only fluids flow down...
                    if (!(state == null || state.isAir()) && state != belowState) {
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

    // todo: benchmark if this is actually faster
    protected void initializeFluidBordersByCell() {
        for (int cY = 0; cY < this.cellCountY; cY++) {
            for (int cZ = 0; cZ < this.cellCountXZ; cZ++) {
                for (int cX = 0; cX < this.cellCountXZ; cX++) {
                    int cellIndex = index(cX, cY, cZ, this.cellCountXZ, this.cellCountY);
                    FluidCell cell = this.cells[cellIndex];
                    int blockStartX = cX * this.cellWidth,
                        blockStartY = cY * this.cellHeight,
                        blockStartZ = cZ * this.cellWidth;

                    // only non-homogenous cells will have borders
                    if (!cell.homogenousWithNeighbors) {
                        for (int bY = blockStartY; bY < blockStartY + cellHeight; bY++) {
                            int prevY = Math.max(0, bY - 1);
                            for (int bZ = blockStartZ; bZ < blockStartZ + cellWidth; bZ++) {
                                int prevZ = Math.max(0, bZ - 1);
                                for (int bX = blockStartX; bX < blockStartX + cellWidth; bX++) {
                                    int blockIndex = index(bX, bY, bZ, this.blockCountXZ, this.blockCountY);
                                    BlockState state = this.fluidStates[blockIndex];

                                    int yIndex = index(bX, prevY, bZ, this.blockCountXZ, this.blockCountY);
                                    BlockState belowState = this.fluidStates[yIndex];
                                    // only fluids check the block below them
                                    // because only fluids flow down...
                                    if (!(state == null || state.isAir()) && state != belowState) {
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
                    if (!(state == null || state.isAir()) || distance <= 0) continue;

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
        final int maxDistance = Math.min(this.paddingBlocksXZ, this.paddingBlocksY) * ADJACENT_COST + 1;

        IntArrayList[] buckets = new IntArrayList[maxDistance];
        for (int i = 0; i < maxDistance; i++) buckets[i] = new IntArrayList();

        for (int i = 0; i < borderDistances.length; i++) {
            if (borderDistances[i] == 0) buckets[0].add(i);
        }

        for (int curDist = 0; curDist < maxDistance; curDist++) {
            IntArrayList bucket = buckets[curDist];
            for (int bucketIndex = 0; bucketIndex < bucket.size(); bucketIndex++) {
                int currentIndex = bucket.getInt(bucketIndex);
                int currentDistance = borderDistances[currentIndex];
                if (currentDistance != curDist) continue; // out of date

                // decode position from index
                int bY = currentIndex / this.blockLayerSize;
                int rem = currentIndex % this.blockLayerSize;
                int bZ = rem / this.blockCountXZ;
                int bX = rem % this.blockCountXZ;

                for (int i = 0; i < NEIGHBOR_OFFSETS.length; i++) {
                    int[] neighborOffsets = NEIGHBOR_OFFSETS[i];
                    int nX = bX + neighborOffsets[0],
                        nY = bY + neighborOffsets[1],
                        nZ = bZ + neighborOffsets[2];
                    if (outOfRange(nX, nY, nZ, blockCountXZ, blockCountY)) continue;

                    int neighborIndex = index(nX, nY, nZ, blockCountXZ, blockCountY);
                    int newDistance = currentDistance + NEIGHBOR_COSTS[i];
                    if (newDistance < borderDistances[neighborIndex]) {
                        borderDistances[neighborIndex] = newDistance;
                        if (newDistance < maxDistance) buckets[newDistance].add(neighborIndex);
                    }
                }
            }
        }
    }
}
