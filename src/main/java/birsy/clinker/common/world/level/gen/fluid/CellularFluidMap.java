package birsy.clinker.common.world.level.gen.fluid;

import birsy.clinker.common.world.level.gen.noise.NoiseComputer;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
import birsy.clinker.common.world.level.gen.worldfeature.MetaChunkMapHolder;
import birsy.clinker.common.world.level.gen.worldfeature.WorldFeature;
import birsy.clinker.core.Clinker;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

// fluid map with cells, but nothing to compute borders
// not really needed but i'm trying out a couple different strategies for
// computing container borders right now so it's useful to have the base
// cell logic separated.
public class CellularFluidMap implements FluidMap {
    final int cellWidth, cellHeight;
    final int halfCellWidth, halfCellHeight;
    final int paddingCells;

    final int cellCountXZ, cellCountY;
    final int minCellX, minCellY, minCellZ;
    final FluidCell[] cells;
    final List<Integer>[] cellNeighborIndices;

    final int minX, minY, minZ;
    final int paddingBlocksXZ, paddingBlocksY;
    final int blockCountXZ, blockCountY;
    final BlockState[] fluidStates;

    final PositionalRandomFactory aquiferRandom;
    final NoiseComputerContext noiseContext;
    final FluidFiller fluidFiller;

    final Collection<WorldFeature> worldFeatures;

    public CellularFluidMap(
            RandomState randomState,
            ChunkAccess chunk,
            NoiseComputerContext noiseContext,
            FluidFiller baseFluidFiller,
            Collection<WorldFeature> worldFeatures,
            int cellWidth, int cellHeight,
            int paddingCells) {
        this.aquiferRandom = randomState.aquiferRandom();
        this.noiseContext = noiseContext;
        this.fluidFiller = baseFluidFiller;

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
        this.fluidStates = new BlockState[this.blockCountXZ * this.blockCountXZ * this.blockCountY];
        Arrays.fill(this.fluidStates, AIR);

        this.minCellX = Math.floorDiv(this.minX, this.cellWidth);
        this.minCellY = Math.floorDiv(this.minY, this.cellHeight);
        this.minCellZ = Math.floorDiv(this.minZ, this.cellWidth);
        this.cellCountXZ = (16 / this.cellWidth) + this.paddingCells * 2;
        this.cellCountY = (chunk.getHeight() / this.cellHeight) + this.paddingCells * 2;
        this.cells = new FluidCell[this.cellCountXZ * this.cellCountXZ * this.cellCountY];
        this.cellNeighborIndices = new List[this.cells.length];

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
    public void precomputeValues(NoiseComputer finalDensityComputer, NoiseComputer waterfallPresenceComputer) {
        this.initializeCells();
        this.computeCellNeighbors();
        this.computeNeighborHomogeneity();
        this.fillFluidStateMap();
    }

    @Override
    public double getBorderDensity(int localX, int localY, int localZ) {
        return -1;
    }

    protected void initializeCells() {
        for (int cX = 0; cX < this.cellCountXZ; cX++) {
            int globalCellX = cX + this.minCellX - this.paddingCells;
            for (int cY = 0; cY < this.cellCountY; cY++) {
                int globalCellY = cY + this.minCellY - this.paddingCells;
                for (int cZ = 0; cZ < this.cellCountXZ; cZ++) {
                    int globalCellZ = cZ + this.minCellZ - this.paddingCells;
                    FluidCell created = createCell(globalCellX, globalCellY, globalCellZ);
                    this.cells[index(cX, cY, cZ, this.cellCountXZ, this.cellCountY)] = created;
                }
            }
        }
    }

    protected FluidCell createCell(int cellX, int cellY, int cellZ) {
        RandomSource cellRandom = aquiferRandom.at(cellX, cellY, cellZ);
        int centerX = cellX * this.cellWidth +  this.halfCellWidth +  (int)Math.round(cellRandom.triangle(0, this.halfCellWidth));
        int centerY = cellY * this.cellHeight + this.halfCellHeight + (int)Math.round(cellRandom.triangle(0, this.halfCellHeight));
        int centerZ = cellZ * this.cellWidth +  this.halfCellWidth +  (int)Math.round(cellRandom.triangle(0, this.halfCellWidth));

        FluidLevel fluidLevel = this.fluidFiller.compute(centerX, centerY, centerZ, this.noiseContext);
        for (WorldFeature wf : this.worldFeatures) {
            fluidLevel = wf.modifyFluidLevel(centerX, centerY, centerZ, fluidLevel, this.noiseContext);
        }
        return new FluidCell(centerX, centerY, centerZ, fluidLevel);
    }

    protected void computeCellNeighbors() {
        for (int cX = 0; cX < this.cellCountXZ; cX++) {
            for (int cY = 0; cY < this.cellCountY; cY++) {
                for (int cZ = 0; cZ < this.cellCountXZ; cZ++) {
                    List<Integer> neighbors = new ArrayList<>(27);
                    for (int dX = -1; dX <= 1; dX++) {
                        int nX = cX + dX;
                        for (int dY = -1; dY <= 1; dY++) {
                            int nY = cY + dY;
                            for (int dZ = -1; dZ <= 1; dZ++) {
                                int nZ = cZ + dZ;
                                if (nX < 0 || nX >= cellCountXZ || nY < 0 || nY >= cellCountY || nZ < 0 || nZ >= cellCountXZ) continue;
                                if (dX == 0 && dY == 0 && dZ == 0) continue;
                                neighbors.add(index(nX, nY, nZ, this.cellCountXZ, this.cellCountY));
                            }
                        }
                    }
                    this.cellNeighborIndices[index(cX, cY, cZ, this.cellCountXZ, this.cellCountY)] = neighbors;
                }
            }
        }
    }

    protected void computeNeighborHomogeneity() {
        for (int cX = 1; cX < this.cellCountXZ - 1; cX++) {
            for (int cY = 1; cY < this.cellCountY - 1; cY++) {
                NEXT_CELL:
                for (int cZ = 1; cZ < this.cellCountXZ - 1; cZ++) {
                    int cellIndex = index(cX, cY, cZ, this.cellCountXZ, this.cellCountY);
                    FluidCell cell = this.cells[cellIndex];
                    boolean completelyFilled = cell.isCompletelyFilled(this.cellHeight),
                            completelyEmpty = cell.isCompletelyEmpty(this.cellHeight),
                            surface = !completelyFilled && !completelyEmpty;

                    if (completelyFilled) {
                        // if the cell is completely filled, check if all neighbors are completely full of the same fluid.
                        // if so, the cell is homogenous.
                        List<Integer> neighborIndices = this.cellNeighborIndices[cellIndex];
                        for (int neighborIndex : neighborIndices) {
                            FluidCell neighborCell = this.cells[neighborIndex];
                            if (neighborCell.fluidType() != cell.fluidType() ||
                                !neighborCell.isCompletelyFilled(this.cellHeight)) {
                                continue NEXT_CELL;
                            }
                        }
                        cell.homogenousWithNeighbors = true;
                    } else if (completelyEmpty) {
                        // if the cell is completely empty, check if all neighbors are empty.
                        // if so, the cell is homogenous.
                        List<Integer> neighborIndices = this.cellNeighborIndices[cellIndex];
                        for (int neighborIndex : neighborIndices) {
                            FluidCell neighborCell = this.cells[neighborIndex];
                            if (!neighborCell.isCompletelyEmpty(this.cellHeight)) {
                                continue NEXT_CELL;
                            }
                        }
                    } else if (surface) {
                        // if the cell is a surface, check that
                        // a) the cells above are completely empty
                        // b) the cells below are completely full of the same kind of fluid
                        // c) the cells surrounding are surfaces, with the same fluid and water height.
                        for (int dX = -1; dX <= 1; dX++) {
                            int nX = cX + dX;
                            for (int dY = -1; dY <= 1; dY++) {
                                int nY = cY + dY;
                                for (int dZ = -1; dZ <= 1; dZ++) {
                                    int nZ = cZ + dZ;
                                    if (dX == 0 && dY == 0 && dZ == 0) continue;
                                    int neighborIndex = index(nX, nY, nZ, this.cellCountXZ, this.cellCountY);
                                    FluidCell neighborCell = this.cells[neighborIndex];
                                    // condition A:
                                    if (dY == 1 &&
                                            !neighborCell.isCompletelyEmpty(this.cellHeight)) {
                                        continue NEXT_CELL;
                                    }
                                    // condition B:
                                    else if (dY == -1 &&
                                            (neighborCell.fluidType() != cell.fluidType() ||
                                             !neighborCell.isCompletelyFilled(this.cellHeight))) {
                                        continue NEXT_CELL;
                                    }
                                    // condition C:
                                    else if (neighborCell.level != cell.level) {
                                        continue NEXT_CELL;
                                    }
                                }
                            }
                        }
                    }
                    cell.homogenousWithNeighbors = true;
                }
            }
        }
    }

    protected void fillFluidStateMap() {
        for (int bX = 0; bX < this.blockCountXZ; bX++) {
            int cX = (bX - this.paddingBlocksXZ) / this.cellWidth + this.paddingCells;
            int globalBlockX = bX - this.paddingBlocksXZ + this.minX;
            for (int bY = 0; bY < this.blockCountY; bY++) {
                int cY = (bY - this.paddingBlocksY) / this.cellHeight + this.paddingCells;
                int globalBlockY = bY - this.paddingBlocksY + this.minY;
                for (int bZ = 0; bZ < this.blockCountXZ; bZ++) {
                    int cZ = (bZ - this.paddingBlocksXZ) / this.cellWidth + this.paddingCells;
                    int globalBlockZ = bZ - this.paddingBlocksXZ + this.minZ;

                    int cellIndex = index(cX, cY, cZ, this.cellCountXZ, this.cellCountY);
                    FluidCell bestCell = this.cells[cellIndex];

                    // only check neighbors if the fluid cell isn't homogenous
                    if (!bestCell.homogenousWithNeighbors) {
                        List<Integer> neighborIndices = this.cellNeighborIndices[cellIndex];
                        int bestDistance = bestCell.distanceSq(globalBlockX, globalBlockY, globalBlockZ);
                        for (int neighborIndex : neighborIndices) {
                            FluidCell neighborCell = this.cells[neighborIndex];
                            int neighborDistance = neighborCell.distanceSq(globalBlockX, globalBlockY, globalBlockZ);
                            if (neighborDistance < bestDistance) {
                                bestCell = neighborCell;
                                bestDistance = neighborDistance;
                            }
                        }
                    }
                    int blockIndex = index(bX, bY, bZ, this.blockCountXZ, this.blockCountY);
                    BlockState state = bestCell.resolve(globalBlockY);
                    this.fluidStates[blockIndex] = state;
                }
            }
        }
    }

    protected static int index(int x, int y, int z, int sizeX, int sizeY) {
        return x + y * sizeX + z * sizeX * sizeY;
    }

    protected static final class FluidCell {
        final int centerX, centerY, centerZ;
        final FluidLevel level;
        boolean homogenousWithNeighbors;

        FluidCell(int centerX, int centerY, int centerZ, FluidLevel level) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.centerZ = centerZ;
            this.level = level;
        }

        int fluidHeight() {
            return this.level.height();
        }
        BlockState fluidType() {
            return this.level.fluid();
        }

        int distanceSq(int x, int y, int z) {
            int dx = x - centerX, dy = y - centerY, dz = z - centerZ;
            return dx * dx + dy * dy + dz * dz;
        }
        BlockState resolve(int y) {
            return y < level.height() ? level.fluid() : AIR;
        }
        boolean isCompletelyFilled(int cellHeight) {
            return this.level.height() > this.centerY + cellHeight;
        }
        boolean isCompletelyEmpty(int cellHeight) {
            return this.level.height() < this.centerY - cellHeight;
        }
    }
}
