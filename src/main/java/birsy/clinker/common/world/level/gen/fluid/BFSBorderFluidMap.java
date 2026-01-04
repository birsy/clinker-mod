package birsy.clinker.common.world.level.gen.fluid;

import birsy.clinker.common.world.level.gen.noise.NoiseComputer;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
import birsy.clinker.common.world.level.gen.worldfeature.WorldFeature;
import birsy.clinker.core.Clinker;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.PriorityQueue;

public class BFSBorderFluidMap extends CellularFluidMap {
    // offset x, offset y, offset z, weight
    protected static final int FACE_WEIGHT = 10,
                               EDGE_WEIGHT = 14,
                               CORNER_WEIGHT = 17;
    // distances multiplied by two, if above. this makes shorelines flatter...
    protected static final int[][] BORDER_NEIGHBORS = {
            // faces
            { 1, 0, 0, FACE_WEIGHT}, {-1, 0, 0, FACE_WEIGHT},
            { 0, 1, 0, FACE_WEIGHT*2}, { 0,-1, 0, FACE_WEIGHT},
            { 0, 0, 1, FACE_WEIGHT}, { 0, 0,-1, FACE_WEIGHT},
            // edges
            { 1, 1, 0, EDGE_WEIGHT*2}, { 1,-1, 0, EDGE_WEIGHT}, {-1, 1, 0, EDGE_WEIGHT*2}, {-1,-1, 0, EDGE_WEIGHT},
            { 1, 0, 1, EDGE_WEIGHT}, { 1, 0,-1, EDGE_WEIGHT}, {-1, 0, 1, EDGE_WEIGHT}, {-1, 0,-1, EDGE_WEIGHT},
            { 0, 1, 1, EDGE_WEIGHT*2}, { 0, 1,-1, EDGE_WEIGHT*2}, { 0,-1, 1, EDGE_WEIGHT}, { 0,-1,-1, EDGE_WEIGHT},
            // corners
            { 1, 1, 1, CORNER_WEIGHT*2}, { 1, 1,-1, CORNER_WEIGHT*2}, { 1,-1, 1, CORNER_WEIGHT}, { 1,-1,-1, CORNER_WEIGHT},
            {-1, 1, 1, CORNER_WEIGHT*2}, {-1, 1,-1, CORNER_WEIGHT*2}, {-1,-1, 1, CORNER_WEIGHT}, {-1,-1,-1, CORNER_WEIGHT},
    };

    final int[] borderDistances;

    public BFSBorderFluidMap(
            RandomState randomState,
            ChunkAccess chunk,
            NoiseComputerContext noiseContext,
            FluidFiller baseFluidFiller,
            Collection<WorldFeature> worldFeatures,
            int cellWidth, int cellHeight, int paddingCells) {
        super(randomState, chunk, noiseContext, baseFluidFiller, worldFeatures, cellWidth, cellHeight, paddingCells);
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
        return (this.borderDistances[blockIndex] / (double)FACE_WEIGHT) - 1;
    }

    @Override
    public void precomputeValues(NoiseComputer finalDensityComputer, NoiseComputer waterfallPresenceComputer) {
        super.precomputeValues(finalDensityComputer, waterfallPresenceComputer);
        this.initializeFluidBorders(finalDensityComputer, waterfallPresenceComputer);
        this.computeBorderDistances();
        int maxDistance = Integer.MIN_VALUE;
        for (int bX = 0; bX < this.blockCountXZ; bX++) {
            for (int bY = 0; bY < this.blockCountY; bY++) {
                for (int bZ = 0; bZ < this.blockCountXZ; bZ++) {
                    int borderDist = this.borderDistances[index(bX, bY, bZ, this.blockCountXZ, this.blockCountY)];
                    if (borderDist < 1000)
                        maxDistance = Math.max(maxDistance, borderDist);
                }
            }
        }
    }

    public void initializeFluidBorders(NoiseComputer finalDensityComputer, NoiseComputer waterfallPresenceComputer) {
        // place initial borders
        for (int bX = 0; bX < this.blockCountXZ; bX++) {
            int globalBlockX = bX - this.paddingBlocksXZ + this.minX;
            for (int bY = 0; bY < this.blockCountY; bY++) {
                int globalBlockY = bY - this.paddingBlocksY + this.minY;
                NEXT_BLOCK:
                for (int bZ = 0; bZ < this.blockCountXZ; bZ++) {
                    int globalBlockZ = bZ - this.paddingBlocksXZ + this.minZ;

                    // if there's already blocks going to be here, we can safely skip.
                    if (finalDensityComputer.compute(globalBlockX, globalBlockY, globalBlockZ, this.noiseContext) < 0)
                        continue NEXT_BLOCK;

                    int blockIndex = index(bX, bY, bZ, this.blockCountXZ, this.blockCountY);
                    BlockState state = this.fluidStates[blockIndex];
                    boolean isBorder = false;

                    // vertical
                    if (state.isAir()) {
                        // air blocks only check above
                        int nY = bY + 1;
                        if (nY < blockCountY) {
                            BlockState neighborState = this.fluidStates[index(bX, nY, bZ, this.blockCountXZ, this.blockCountY)];
                            if (neighborState != state) {
                                borderDistances[blockIndex] = 0;
                                continue NEXT_BLOCK;
                            }
                        }
                    } else {
                        // fluid blocks only check below
                        int nY = bY - 1;
                        if (nY < 0) {
                            // bottom of the world, should always be contained.
                            borderDistances[blockIndex] = 0;
                            continue NEXT_BLOCK;
                        } else {
                            BlockState neighborState = this.fluidStates[index(bX, nY, bZ, this.blockCountXZ, this.blockCountY)];
                            if (neighborState != state) {
                                borderDistances[blockIndex] = 0;
                                continue NEXT_BLOCK;
                            }
                        }
                    }

                    // horizontal
                    if (!isBorder) {
                        for (Direction direction : Direction.Plane.HORIZONTAL) {
                            int nX = bX + direction.getStepX(),
                                    nY = bY + direction.getStepY(),
                                    nZ = bZ + direction.getStepZ();
                            if (nX < 0 || nX >= blockCountXZ || nY < 0 || nY >= blockCountY || nZ < 0 || nZ >= blockCountXZ)
                                continue;
                            BlockState neighborState = this.fluidStates[index(nX, nY, nZ, this.blockCountXZ, this.blockCountY)];
                            if (neighborState != state) {
                                borderDistances[blockIndex] = 0;
                                continue NEXT_BLOCK;
                            }
                        }
                    }
                }
            }
        }

        // remove where waterfalls
        for (int bX = 0; bX < this.blockCountXZ; bX++) {
            int globalBlockX = bX - this.paddingBlocksXZ + this.minX;
            for (int bY = 1; bY < this.blockCountY; bY++) {
                int globalBlockY = bY - this.paddingBlocksY + this.minY;
                for (int bZ = 0; bZ < this.blockCountXZ; bZ++) {
                    int globalBlockZ = bZ - this.paddingBlocksXZ + this.minZ;

                    int blockIndex = index(bX, bY, bZ, this.blockCountXZ, this.blockCountY);
                    BlockState state = this.fluidStates[blockIndex];
                    int distance = this.borderDistances[blockIndex];
                    if (!state.isAir() || distance <= 0) continue;

                    int belowBlockIndex = index(bX, bY - 1, bZ, this.blockCountXZ, this.blockCountY);
                    int belowDistance = this.borderDistances[belowBlockIndex];
                    if (belowDistance > 0) continue;

                    double waterfallPresence = waterfallPresenceComputer.compute(globalBlockX, globalBlockY, globalBlockZ, this.noiseContext);
                    if (waterfallPresence <= 0) continue;

                    // "smear" the air downwards.
                    this.borderDistances[belowBlockIndex] = distance;
                    for (int i = 2; i < 1 + waterfallPresence; i++) {
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
        final int maxDistance = Math.min(this.paddingBlocksXZ, this.paddingBlocksY) * FACE_WEIGHT * 2 + 1;

        IntArrayList[] buckets = new IntArrayList[maxDistance];
        for (int i = 0; i < maxDistance; i++) buckets[i] = new IntArrayList();

        for (int i = 0; i < borderDistances.length; i++) {
            if (borderDistances[i] == 0) buckets[0].add(i);
        }

        for (short curDist = 0; curDist < maxDistance; curDist++) {
            IntArrayList bucket = buckets[curDist];
            for (int idx = 0; idx < bucket.size(); idx++) {
                int currentIndex = bucket.getInt(idx);
                int currentDistance = borderDistances[currentIndex];
                if (currentDistance != curDist) continue; // out of date

                // decode from index
                int bX = currentIndex % this.blockCountXZ;
                int temp = currentIndex / this.blockCountXZ;
                int bY = temp % this.blockCountY;
                int bZ = temp / this.blockCountY;

                for (int[] neighbor : BORDER_NEIGHBORS) {
                    int nX = bX + neighbor[0], nY = bY + neighbor[1], nZ = bZ + neighbor[2];
                    if (nX < 0 || nX >= this.blockCountXZ || nY < 0 || nY >= this.blockCountY || nZ < 0 || nZ >= this.blockCountXZ) {
                        continue;
                    }
                    int neighborIndex = index(nX, nY, nZ, blockCountXZ, blockCountY);
                    int newDistance = currentDistance + neighbor[3];
                    if (newDistance < borderDistances[neighborIndex]) {
                        borderDistances[neighborIndex] = newDistance;
                        if (newDistance < maxDistance) buckets[newDistance].add(neighborIndex);
                    }
                }
            }
        }
    }
}
