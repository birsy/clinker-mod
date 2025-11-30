package birsy.clinker.common.world.level.gen.fluid;

import birsy.clinker.common.world.level.gen.noise.NoiseComputer;
import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
import birsy.clinker.common.world.level.gen.worldfeature.MetaChunkMapHolder;
import birsy.clinker.common.world.level.gen.worldfeature.WorldFeature;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.Arrays;
import java.util.Collection;

public class AxialBlurBorderFluidMap implements FluidMap {
    private static final int cellPadding = 2;
    private static final int cellWidth = 8, cellHeight = 16;
    private static final int cellCenterOffsetXZ = Math.floorDiv(cellWidth, 2), cellCenterOffsetY = Math.floorDiv(cellHeight, 2);

    private static final int maxBorderDistance = 4,
                             borderThickness = 1;
    final int minCellX, minCellY, minCellZ;
    final int cellCountXZ, cellCountY;
    final FluidCell[] cells;

    final PositionalRandomFactory aquiferRandom;
    final NoiseComputerContext noiseContext;
    final FluidFiller baseFluidFiller;

    final Collection<WorldFeature> worldFeatures;

    final int minX, minY, minZ;
    final int chunkHeight;
    final BlockState[] fluidStates;
    final int[] borderDistances;

    public AxialBlurBorderFluidMap(RandomState randomState, ChunkAccess chunk, NoiseComputerContext noiseContext, FluidFiller baseFluidFiller) {
        this.noiseContext = noiseContext;
        this.baseFluidFiller = baseFluidFiller;
        this.cellCountXZ = 16 / cellWidth;
        this.cellCountY = chunk.getHeight() / cellHeight;
        this.cells = new FluidCell[(this.cellCountXZ + cellPadding * 2) * (this.cellCountXZ + cellPadding * 2) * (this.cellCountY + cellPadding * 2)];

        this.minX = chunk.getPos().getMinBlockX();
        this.minY = chunk.getMinBuildHeight();
        this.minZ = chunk.getPos().getMinBlockZ();
        this.chunkHeight = chunk.getHeight();

        this.minCellX = Math.floorDiv(this.minX, cellWidth);
        this.minCellY = Math.floorDiv(this.minY, cellHeight);
        this.minCellZ = Math.floorDiv(this.minZ, cellWidth);

        this.aquiferRandom = randomState.aquiferRandom();
        this.worldFeatures = ((MetaChunkMapHolder) (Object) randomState).clinker$metaChunkMap()
                .getWorldFeatures(chunk.getLevel(), chunk.getPos().getMinBlockX(), chunk.getPos().getMinBlockZ());

        this.fluidStates = new BlockState[(16 + maxBorderDistance * 2 + 2) * (16 + maxBorderDistance * 2 + 2) * (this.chunkHeight + maxBorderDistance * 2 + 2)];
        this.borderDistances = new int[(16 + maxBorderDistance * 2) * (16 + maxBorderDistance * 2) * (this.chunkHeight + maxBorderDistance * 2)];
        Arrays.fill(this.borderDistances, maxBorderDistance + 1);
    }

    @Override
    public void precomputeValues(NoiseComputer finalDensityComputer, NoiseComputer waterfallPresenceComputer) {
        this.fillBorderArray(finalDensityComputer, waterfallPresenceComputer);
    }

    @Override
    public double getBorderDensity(int localX, int localY, int localZ) {
        int indexX = localX + maxBorderDistance,
            indexY = localY + maxBorderDistance,
            indexZ = localZ + maxBorderDistance;
        int index = indexX +
                    indexY * (16 + maxBorderDistance * 2) +
                    indexZ * (16 + maxBorderDistance * 2) * (this.chunkHeight + maxBorderDistance * 2);
        return borderDistances[index] - borderThickness;
    }

    @Override
    public BlockState getFluidState(int x, int y, int z) {
        // clamp y values such that they always lie inside
        // used for waterfall border computation...
        y = Math.clamp(y, minY, chunkHeight - minY);
        int indexX = (x - this.minX) + (maxBorderDistance + 1),
            indexY = (y - this.minY) + (maxBorderDistance + 1),
            indexZ = (z - this.minZ) + (maxBorderDistance + 1);

        // only retrieve cached value if fluid state is actually inside the cache.
        if (indexX < 16 + maxBorderDistance * 2 + 2 && indexX > 0 &&
            indexY < this.chunkHeight + maxBorderDistance * 2 + 2 && indexY > 0 &&
            indexZ < 16 + maxBorderDistance * 2 + 2 && indexZ > 0) {
            int index = indexX +
                        indexY * (16 + maxBorderDistance * 2 + 2) +
                        indexZ * (16 + maxBorderDistance * 2 + 2) * (this.chunkHeight + maxBorderDistance * 2 + 2);
            // calculate it and add it to the cache if it hasn't been computed already.
            if (fluidStates[index] == null) fluidStates[index] = computeFluidState(x, y, z);
            return fluidStates[index];
        } else {
            return computeFluidState(x, y, z);
        }
    }

    private void fillBorderArray(NoiseComputer finalDensityComputer, NoiseComputer waterfallPresenceComputer) {
        for (int indexX = 0; indexX < 16 + maxBorderDistance * 2; indexX++) {
            int localX = indexX - maxBorderDistance,
                     x = localX + this.minX,
                 cellX = Math.floorDiv(x, cellWidth);
            for (int indexY = 0; indexY < this.chunkHeight + maxBorderDistance * 2; indexY++) {
                int localY = indexY - maxBorderDistance,
                         y = localY + this.minY,
                     cellY = Math.floorDiv(y, cellHeight);
                NEXT_CELL:
                for (int indexZ = 0; indexZ < 16 + maxBorderDistance * 2; indexZ++) {
                    int localZ = indexZ - maxBorderDistance,
                             z = localZ + this.minZ,
                         cellZ = Math.floorDiv(z, cellWidth);
                    // skip homogenous cells
                    if (isHomogenous(cellX, cellY, cellZ))
                        continue;

                    // skip full values, if the index is in range.
//                    if (localX > 0 && localX < 16 && localY > 0 && localY < this.chunkHeight && localZ > 0 && localZ < 16 &&
//                        this.noiseContext.noiseComputerExecutor().compute(x, y, z, finalDensityComputer) < 0)
//                        continue;

                    BlockState state = getFluidState(x, y, z);

                    CHECK_NEIGHBORS:
                    for (Direction.Axis axis : Direction.Axis.VALUES) {
                        if (!state.isAir() && axis == Direction.Axis.Y) continue;
                        BlockState neighborState = getFluidState(
                                x + axis.choose(1, 0, 0),
                                y + axis.choose(0, 1, 0),
                                z + axis.choose(0, 0, 1)
                        );
                        if (state != neighborState) {
                            // waterfall stuffs
                            // run only if waterfalls are requested for this block.
                            double waterfallPresence = this.noiseContext.noiseComputerExecutor().compute(x, y, z, waterfallPresenceComputer);
                            if (waterfallPresence > 0) {
                                // search upwards for air blocks. if there's air along the way, this is a waterfall-affected block.
                                BlockState previousState = !state.isAir() ? state : neighborState;
                                for (int i = 1; i < 1 + waterfallPresence; i++) {
                                    BlockState aboveState = !state.isAir() ?
                                            getFluidState(x, y + i, z) :
                                            getFluidState(
                                                    x + axis.choose(1, 0, 0),
                                                    y + axis.choose(0, 1, 0) + i,
                                                    z + axis.choose(0, 0, 1)
                                            );
                                    if (!previousState.isAir() && aboveState.isAir()) break CHECK_NEIGHBORS;
                                    previousState = aboveState;
                                }
                            }
                            int index = indexX +
                                        indexY * (16 + maxBorderDistance * 2) +
                                        indexZ * (16 + maxBorderDistance * 2) * (this.chunkHeight + maxBorderDistance * 2);
                            borderDistances[index] = 0;
                            break;
                        }
                    }
                }
            }
        }
        smearBorderArrayAxially(Direction.Axis.X, finalDensityComputer);
        smearBorderArrayAxially(Direction.Axis.Z, finalDensityComputer);
        smearBorderArrayAxially(Direction.Axis.Y, finalDensityComputer);
    }

    private void smearBorderArrayAxially(Direction.Axis axis, NoiseComputer finalDensityComputer) {
        int offsetX = axis.choose(1, 0, 0),
            offsetY = axis.choose(0, 1, 0),
            offsetZ = axis.choose(0, 0, 1);
        for (int indexX = maxBorderDistance; indexX < 16 + maxBorderDistance; indexX++) {
            int x = indexX - maxBorderDistance + this.minX,
                    cellX = Math.floorDiv(x, cellWidth);
            for (int indexY = maxBorderDistance; indexY < this.chunkHeight + maxBorderDistance; indexY++) {
                int y = indexX - maxBorderDistance + this.minY,
                        cellY = Math.floorDiv(y, cellHeight);
                for (int indexZ = maxBorderDistance; indexZ < 16 + maxBorderDistance; indexZ++) {
                    int z = indexX - maxBorderDistance + this.minZ,
                            cellZ = Math.floorDiv(z, cellWidth);
                    // skip homogenous cells
//                    if (isHomogenous(cellX, cellY, cellZ))
//                        continue;
                    // skip full values.
//                    if (this.noiseContext.noiseComputerExecutor().compute(x, y, z, finalDensityComputer) < 0)
//                        continue;
                    int index = indexX +
                                indexY * (16 + maxBorderDistance * 2) +
                                indexZ * (16 + maxBorderDistance * 2) * (this.chunkHeight + maxBorderDistance * 2);
                    int minBorderDistance = borderDistances[index];
                    for (int i = -maxBorderDistance; i <= maxBorderDistance; i++) {
                        if (i == 0) continue;
                        int neighborIndex = (indexX + offsetX * i) +
                                            (indexY + offsetY * i) * (16 + maxBorderDistance * 2) +
                                            (indexZ + offsetZ * i) * (16 + maxBorderDistance * 2) * (this.chunkHeight + maxBorderDistance * 2);
                        int neighborBorderDistance = borderDistances[neighborIndex];
                        minBorderDistance = Math.min(minBorderDistance, neighborBorderDistance + (int)(Math.abs(i) * (axis == Direction.Axis.Y ? 1.5 : 1)));
                        if (neighborBorderDistance == 0) break;
                    }

                    borderDistances[index] = minBorderDistance;
                }
            }
        }
    }

    private BlockState computeFluidState(int x, int y, int z) {
        int cellX = Math.floorDiv(x, cellWidth),
            cellY = Math.floorDiv(y, cellHeight),
            cellZ = Math.floorDiv(z, cellWidth);
        if (isHomogenous(cellX, cellY, cellZ))
            return getCell(cellX, cellY, cellZ).resolve(x, y, z);

        // check the neighboring cells of each block for the closest cell center,
        // use that as the fluid.
        FluidCell closestCell = getCell(cellX, cellY, cellZ);
        int closestCellDistance = (x - closestCell.centerX) * (x - closestCell.centerX) +
                (y - closestCell.centerY) * (y - closestCell.centerY) +
                (z - closestCell.centerZ) * (z - closestCell.centerZ);
        for (int xOffset = -1; xOffset <= 1; xOffset++) {
            for (int yOffset = -1; yOffset <= 1; yOffset++) {
                for (int zOffset = -1; zOffset <= 1; zOffset++) {
                    if (xOffset == 0 && yOffset == 0 && zOffset == 0) continue; // skip the center cell, as we already check it first by default.
                    FluidCell offsetCell = getCell(cellX + xOffset, cellY + yOffset, cellZ + zOffset);
                    int offsetCellDistance = (x - offsetCell.centerX) * (x - offsetCell.centerX) +
                            (y - offsetCell.centerY) * (y - offsetCell.centerY) +
                            (z - offsetCell.centerZ) * (z - offsetCell.centerZ);
                    if (offsetCellDistance < closestCellDistance) {
                        closestCell = offsetCell;
                        closestCellDistance = offsetCellDistance;
                    }
                }
            }
        }
        return closestCell.resolve(x, y, z);
    }

    private FluidCell getCell(int cellX, int cellY, int cellZ) {
        int cellArrayX = cellX - this.minCellX + cellPadding,
            cellArrayY = cellY - this.minCellY + cellPadding,
            cellArrayZ = cellZ - this.minCellZ + cellPadding;
        int cellIndex = cellArrayX +
                        cellArrayY * (this.cellCountXZ + cellPadding * 2) +
                        cellArrayZ * (this.cellCountXZ + cellPadding * 2) * (this.cellCountY + cellPadding * 2);

        // don't fill the array beforehand - instead,
        // only create cells when we need it...
        // will probably make it faster? since completely
        // solid fluid cells never need to be computed.
        if (cells[cellIndex] != null) {
            return cells[cellIndex];
        } else {
            FluidCell newCell = createNewCell(cellX, cellY, cellZ);
            cells[cellIndex] = newCell;
            return newCell;
        }
    }

    private boolean isHomogenous(int cellX, int cellY, int cellZ) {
        FluidCell cell = getCell(cellX, cellY, cellZ);
        if (cell.homogenous == 0) {
            for (int xOffset = -1; xOffset <= 1; xOffset++) {
                for (int yOffset = -1; yOffset <= 1; yOffset++) {
                    for (int zOffset = -1; zOffset <= 1; zOffset++) {
                        if (xOffset == 0 && yOffset == 0 && zOffset == 0) continue;
                        FluidCell offsetCell = getCell(cellX + xOffset, cellY + yOffset, cellZ + zOffset);

                        if (cell.isEmpty() && offsetCell.isEmpty()) continue;
                        if (cell.isEmpty() && offsetCell.fluidLevel.height() < cell.centerY - cellHeight) continue;
                        if (offsetCell.isEmpty() && cell.fluidLevel.height() < offsetCell.centerY - cellHeight) continue;
                        if (cell.fluidLevel.fluid() == offsetCell.fluidLevel.fluid()) {
                            if (cell.isFull() && offsetCell.isFull()) continue;
                            if (cell.isFull() && offsetCell.fluidLevel.height() > cell.centerY + cellHeight) continue;
                            if (cell.fluidLevel.height() == offsetCell.fluidLevel.height()) continue;
                        }

                        cell.homogenous = 2;
                        offsetCell.homogenous = 2;
                        return false;
                    }
                }
            }

            cell.homogenous = 1;
        }

        return cell.homogenous == 1;
    }

    private FluidCell createNewCell(int cellX, int cellY, int cellZ) {
        RandomSource cellRandom = aquiferRandom.at(cellX * cellWidth, cellY * cellHeight, cellZ * cellWidth);
        // offset the cell center randomly, in order for more natural results.
        int cellCenterX = cellX * cellWidth + cellCenterOffsetXZ + (int) Math.round(cellRandom.triangle(0, cellCenterOffsetXZ)),
            cellCenterY = cellY * cellHeight + cellCenterOffsetY + (int) Math.round(cellRandom.triangle(0, cellCenterOffsetY)),
            cellCenterZ = cellZ * cellWidth + cellCenterOffsetXZ + (int) Math.round(cellRandom.triangle(0, cellCenterOffsetXZ));
        FluidLevel fluidLevel = this.baseFluidFiller.compute(cellCenterX, cellCenterY, cellCenterZ, this.noiseContext);
        for (WorldFeature worldFeature : this.worldFeatures)
            fluidLevel = worldFeature.modifyFluidLevel(cellCenterX, cellCenterY, cellCenterZ, fluidLevel, noiseContext);
        return new FluidCell(cellCenterX, cellCenterY, cellCenterZ, fluidLevel);
    }

    private static final class FluidCell {
        private final int centerX, centerY, centerZ;
        private final FluidLevel fluidLevel;
        private byte homogenous;

        private FluidCell(int centerX, int centerY, int centerZ, FluidLevel fluidLevel) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.centerZ = centerZ;
            this.fluidLevel = fluidLevel;
        }

        BlockState resolve(int x, int y, int z) {
                return y < this.fluidLevel.height() ? fluidLevel.fluid() : Blocks.AIR.defaultBlockState();
        }

        boolean isEmpty() {
            return fluidLevel.fluid().isAir() || fluidLevel.height() < this.centerY - cellHeight;
        }

        boolean isFull() {
            return !fluidLevel.fluid().isAir() && fluidLevel.height() > this.centerY + cellHeight;
        }
    }
}
