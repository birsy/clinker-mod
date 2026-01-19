package birsy.clinker.common.world.level.gen.system.fluid;

import birsy.clinker.common.world.level.gen.system.noise.PaddedNoiseFieldCache;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import birsy.clinker.common.world.level.gen.system.worldfeature.WorldFeature;
import net.minecraft.core.SectionPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.Arrays;
import java.util.Collection;

// state map with cells, but nothing to compute borders
// not really needed but i'm trying out a couple different strategies for
// computing container borders right now so it's useful to have the base
// cell logic separated.
public class CellularFluidField implements FluidField {
    static final int[][] NEIGHBOR_OFFSETS = {
            // faces
            { 1,  0,  0}, {-1,  0,  0}, { 0,  1,  0}, { 0, -1,  0}, { 0,  0,  1}, { 0,  0, -1},
            // edges
            { 1,  1,  0}, { 1, -1,  0}, {-1,  1,  0}, {-1, -1,  0},
            { 1,  0,  1}, { 1,  0, -1}, {-1,  0,  1}, {-1,  0, -1},
            { 0,  1,  1}, { 0,  1, -1}, { 0, -1,  1}, { 0, -1, -1},
            // corners
            { 1,  1,  1}, { 1,  1, -1}, { 1, -1,  1}, { 1, -1, -1},
            {-1,  1,  1}, {-1,  1, -1}, {-1, -1,  1}, {-1, -1, -1},
    };

    final int cellWidth, cellHeight;
    final int halfCellWidth, halfCellHeight;
    final int paddingCells;

    final int cellCountXZ, cellCountY;
    final int cellLayerSize;
    final int minCellX, minCellY, minCellZ;
    final FluidCell[] cells;

    final int minX, minY, minZ;
    final int paddingBlocksXZ, paddingBlocksY;
    final int blockCountXZ, blockCountY;
    final int blockLayerSize;
    final BlockState[] fluidStates;

    final PositionalRandomFactory aquiferRandom;
    final PaddedNoiseFieldCache noiseCache;
    final FluidFieldFiller fluidFieldFiller;

    final Collection<WorldFeature> worldFeatures;

    public CellularFluidField(
            RandomState randomState,
            ChunkAccess chunk,
            PaddedNoiseFieldCache noiseCache,
            FluidFieldFiller baseFluidFieldFiller,
            Collection<WorldFeature> worldFeatures,
            int cellWidth, int cellHeight,
            int paddingCells) {
        this.aquiferRandom = randomState.aquiferRandom();
        this.noiseCache = noiseCache;
        this.fluidFieldFiller = baseFluidFieldFiller;

        this.cellWidth = cellWidth;
        this.cellHeight = cellHeight;
        this.halfCellWidth = cellWidth / 2;
        this.halfCellHeight = cellHeight / 2;
        this.paddingCells = paddingCells;

        this.minX = chunk.getPos().getMinBlockX();
        this.minY = chunk.getMinBuildHeight();
        this.minZ = chunk.getPos().getMinBlockZ();
        this.paddingBlocksXZ = this.paddingCells * this.cellWidth;
        this.paddingBlocksY = this.paddingCells * this.cellHeight;
        this.blockCountXZ = 16 + this.paddingBlocksXZ * 2;
        this.blockCountY = chunk.getHeight() + this.paddingBlocksY * 2;
        this.blockLayerSize = this.blockCountXZ * this.blockCountXZ;
        this.fluidStates = new BlockState[this.blockLayerSize * this.blockCountY];
        Arrays.fill(this.fluidStates, AIR);

        this.minCellX = Math.floorDiv(this.minX, this.cellWidth);
        this.minCellY = Math.floorDiv(this.minY, this.cellHeight);
        this.minCellZ = Math.floorDiv(this.minZ, this.cellWidth);
        this.cellCountXZ = (16 / this.cellWidth) + this.paddingCells * 2;
        this.cellCountY = (chunk.getHeight() / this.cellHeight) + this.paddingCells * 2;
        this.cellLayerSize = this.cellCountXZ * this.cellCountXZ;
        this.cells = new FluidCell[this.cellLayerSize * this.cellCountY];

        this.worldFeatures = worldFeatures;
    }

    @Override
    public BlockState getFluidState(int x, int y, int z) {
        int bX = x - this.minX + this.paddingBlocksXZ,
            bY = y - this.minY + this.paddingBlocksY,
            bZ = z - this.minZ + this.paddingBlocksXZ;
        int blockIndex = index(bX, bY, bZ, this.blockCountXZ, this.blockCountY);
        return this.fluidStates[blockIndex];
    }

    @Override
    public void precomputeValues(NoiseField finalDensityField, NoiseField waterfallPresenceField) {
        for (WorldFeature worldFeature : worldFeatures)
            worldFeature.prefillFluidNoiseFields(
                    SectionPos.blockToSectionCoord(minX),
                    SectionPos.blockToSectionCoord(minZ),
                    this.noiseCache
            );
        this.initializeCells();
        this.computeNeighborHomogeneity();
        this.fillFluidStateMapByCell();
    }

    @Override
    public double getBorderDensity(int localX, int localY, int localZ) {
        return -1;
    }

    protected void initializeCells() {
        for (int cY = 0; cY < this.cellCountY; cY++) {
            int globalCellY = cY + this.minCellY - this.paddingCells;
            for (int cZ = 0; cZ < this.cellCountXZ; cZ++) {
                int globalCellZ = cZ + this.minCellZ - this.paddingCells;
                for (int cX = 0; cX < this.cellCountXZ; cX++) {
                    int globalCellX = cX + this.minCellX - this.paddingCells;
                    FluidCell cell = createCell(globalCellX, globalCellY, globalCellZ);
                    this.cells[index(cX, cY, cZ, this.cellCountXZ, this.cellCountY)] = cell;
                }
            }
        }
    }

    protected FluidCell createCell(int cellX, int cellY, int cellZ) {
        RandomSource cellRandom = aquiferRandom.at(cellX, cellY, cellZ);
        double centerX = cellX * this.cellWidth +  this.halfCellWidth +  cellRandom.triangle(0, this.halfCellWidth),
               centerY = cellY * this.cellHeight + this.halfCellHeight + cellRandom.triangle(0, this.halfCellWidth),
               centerZ = cellZ * this.cellWidth +  this.halfCellWidth +  cellRandom.triangle(0, this.halfCellWidth);
        FluidLevel fluidLevel = this.fluidFieldFiller.compute((int)centerX, (int)centerY, (int)centerZ, this.noiseCache.context);
        for (WorldFeature worldFeature : this.worldFeatures)
            fluidLevel = worldFeature.modifyFluidLevel((int)centerX, (int)centerY, (int)centerZ, fluidLevel, this.noiseCache.context);
        return new FluidCell(centerX, centerY, centerZ, fluidLevel, this.cellHeight);
    }

    protected void computeNeighborHomogeneity() {
        for (int cY = 1; cY < this.cellCountY - 1; cY++) {
            for (int cZ = 1; cZ < this.cellCountXZ - 1; cZ++) {
                NEXT_CELL:
                for (int cX = 1; cX < this.cellCountXZ - 1; cX++) {
                    int cellIndex = index(cX, cY, cZ, this.cellCountXZ, this.cellCountY);
                    FluidCell cell = this.cells[cellIndex];
                    switch (cell.cellType) {
                        case FULL:
                            // if the cell is completely filled, check if all neighbors are completely full of the same state.
                            // if so, the cell is homogenous.
                            for (int[] neighborOffset : NEIGHBOR_OFFSETS) {
                                int nX = cX + neighborOffset[0], nY = cY + neighborOffset[1], nZ = cZ + neighborOffset[2];
                                if (outOfRange(nX, nY, nZ, cellCountXZ, cellCountY)) continue;
                                int neighborIndex = index(nX, nY, nZ, this.cellCountXZ, this.cellCountY);

                                FluidCell neighborCell = this.cells[neighborIndex];
                                if (neighborCell.fluidType() != cell.fluidType() ||
                                        neighborCell.cellType != CellType.FULL) {
                                    continue NEXT_CELL;
                                }
                            }
                            break;
                        case EMPTY:
                            // if the cell is completely empty, check if all neighbors are empty.
                            // if so, the cell is homogenous.
                            for (int[] neighborOffset : NEIGHBOR_OFFSETS) {
                                int nX = cX + neighborOffset[0], nY = cY + neighborOffset[1], nZ = cZ + neighborOffset[2];
                                if (outOfRange(nX, nY, nZ, cellCountXZ, cellCountY)) continue;
                                int neighborIndex = index(nX, nY, nZ, this.cellCountXZ, this.cellCountY);

                                FluidCell neighborCell = this.cells[neighborIndex];
                                if (neighborCell.cellType != CellType.EMPTY) {
                                    continue NEXT_CELL;
                                }
                            }
                            break;
                        case SURFACE:
                            continue NEXT_CELL;
                            // still haven't quite worked out surface homogeneity...
                    }
                    // if it passes all tests, it's homogenous.
                    cell.homogenousWithNeighbors = true;
                }
            }
        }
    }

    private final int[] cellNeighborIndices = new int[26];
    protected void fillFluidStateMapByCell() {
        for (int cY = 0; cY < this.cellCountY; cY++) {
            for (int cZ = 0; cZ < this.cellCountXZ; cZ++) {
                for (int cX = 0; cX < this.cellCountXZ; cX++) {
                    int cellIndex = index(cX, cY, cZ, this.cellCountXZ, this.cellCountY);
                    FluidCell cell = this.cells[cellIndex];
                    int blockStartX = cX * this.cellWidth,
                        blockStartY = cY * this.cellHeight,
                        blockStartZ = cZ * this.cellWidth;

                    if (cell.homogenousWithNeighbors && cell.cellType != CellType.EMPTY) {
                        int blockEndY = Math.min(blockStartY + this.cellHeight, this.blockCountY);
                        int localSurfaceHeight = Math.min(
                                cell.fluidHeight() - this.minY + this.paddingBlocksY,
                                blockEndY
                        );
                        BlockState fluid = cell.fluidType();

                        // fill blocks directly
                        for (int bY = blockStartY; bY < localSurfaceHeight; bY++) {
                            for (int z = blockStartZ; z < blockStartZ + cellWidth; z++) {
                                int rowStart = index(blockStartX, bY, z, blockCountXZ, blockCountY),
                                    rowEnd = rowStart + cellWidth;
                                Arrays.fill(fluidStates, rowStart, rowEnd, fluid);
                            }
                        }
                    } else {
                        // create cell neighbors array
                        int validCellNeighborCount = 0;
                        for (int[] neighborOffset : NEIGHBOR_OFFSETS) {
                            int nX = cX + neighborOffset[0], nY = cY + neighborOffset[1], nZ = cZ + neighborOffset[2];
                            if (outOfRange(nX, nY, nZ, this.cellCountXZ, this.cellCountY)) continue;
                            cellNeighborIndices[validCellNeighborCount++] = index(nX, nY, nZ, this.cellCountXZ, this.cellCountY);
                        }

                        // fill blocks with voronoi testing
                        for (int bY = blockStartY; bY < blockStartY + cellHeight; bY++) {
                            int globalBlockY = bY - this.paddingBlocksY + this.minY;
                            for (int bZ = blockStartZ; bZ < blockStartZ + cellWidth; bZ++) {
                                int globalBlockZ = bZ - this.paddingBlocksXZ + this.minZ;
                                for (int bX = blockStartX; bX < blockStartX + cellWidth; bX++) {
                                    int globalBlockX = bX - this.paddingBlocksXZ + this.minX;

                                    FluidCell bestCell = cell;
                                    double bestDistance = cell.distanceSq(globalBlockX, globalBlockY, globalBlockZ);
                                    for (int i = 0; i < validCellNeighborCount; i++) {
                                        int neighborIndex = cellNeighborIndices[i];
                                        FluidCell neighborCell = this.cells[neighborIndex];
                                        double neighborDistance = neighborCell.distanceSq(globalBlockX, globalBlockY, globalBlockZ);
                                        if (neighborDistance < bestDistance) {
                                            bestCell = neighborCell;
                                            bestDistance = neighborDistance;
                                        }
                                    }

                                    BlockState state = bestCell.resolve(globalBlockY);
                                    int blockIndex = index(bX, bY, bZ, this.blockCountXZ, this.blockCountY);
                                    this.fluidStates[blockIndex] = state;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    static int index(int x, int y, int z, int sizeXZ, int sizeY) {
        return x + z * sizeXZ + y * sizeXZ * sizeXZ;
    }
    static boolean outOfRange(int x, int y, int z, int sizeXZ, int sizeY) {
        return x < 0 || x >= sizeXZ || y < 0 || y >= sizeY || z < 0 || z >= sizeXZ;
    }

    protected static final class FluidCell {
        final double centerX, centerY, centerZ;
        final FluidLevel fluidLevel;
        final CellType cellType;
        boolean homogenousWithNeighbors;

        FluidCell(double centerX, double centerY, double centerZ, FluidLevel fluidLevel, int cellHeight) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.centerZ = centerZ;
            this.fluidLevel = fluidLevel;
            if (this.fluidLevel.height() > this.centerY + cellHeight) {
                this.cellType = CellType.FULL;
            } else if (this.fluidLevel.height() < this.centerY - cellHeight) {
                this.cellType = CellType.EMPTY;
            } else {
                this.cellType = CellType.SURFACE;
            }
        }
        int fluidHeight() {
            return this.fluidLevel.height();
        }
        BlockState fluidType() {
            return this.fluidLevel.fluid();
        }
        double distanceSq(int x, int y, int z) {
            double dx = x - centerX, dy = y - centerY, dz = z - centerZ;
            return dx * dx + dy * dy + dz * dz;
        }
        BlockState resolve(int y) {
            return y < fluidLevel.height() ? fluidLevel.fluid() : AIR;
        }
    }

    enum CellType {
        FULL, EMPTY, SURFACE
    }
}
