package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.noise.*;
import birsy.clinker.common.world.level.gen.worldfeature.MetaChunkMap;
import birsy.clinker.common.world.level.gen.worldfeature.MetaChunkMapHolder;
import birsy.clinker.common.world.level.gen.worldfeature.WorldFeature;
import birsy.clinker.core.Clinker;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.List;

public class LocalFluidLevelMap {
    private static final int cellWidth = 8, cellHeight = 12;
    private static final int borderRadius = 5;
    private static final int[][] borderOffsets = {
            {-1, 0, 0},
            {1, 0, 0},
            {0, 0, -1},
            {0, 0, 1},
            {0, 1, 0},
            {0, -1, 0}
    };
    final int cellsH, cellsV;
    final int mapSizeH, mapSizeV;

    final int minX, minY, minZ;
    final FluidLevel[] cellMap;
    final boolean[] homogenousnessMap;
    final int[] cellCenterOffsets;
    final PositionalRandomFactory random;
    final MetaChunkMap metaChunkMap;
    final FluidFiller fluidFiller;

    public final NoiseComputer noiseComputer;

    public LocalFluidLevelMap(ChunkAccess chunk, RandomState randomState, FluidFiller fluidFiller) {
        this.minX = chunk.getPos().getMinBlockX();
        this.minY = chunk.getMinBuildHeight();
        this.minZ = chunk.getPos().getMinBlockZ();
        this.fluidFiller = fluidFiller;
        this.cellsH = 16 / cellWidth;
        this.cellsV = chunk.getHeight() / cellHeight;
        this.mapSizeH = this.cellsH + 2;
        this.mapSizeV = this.cellsV;
        this.cellMap = new FluidLevel[this.mapSizeH * this.mapSizeH * this.mapSizeV];
        this.cellCenterOffsets = new int[this.cellMap.length * 3];
        this.homogenousnessMap = new boolean[this.cellMap.length];
        this.random = randomState.aquiferRandom();
        this.metaChunkMap = ((MetaChunkMapHolder)(Object) randomState).clinker$metaChunkMap();
        this.noiseComputer = new NoiseComputer("aquifer_border", CacheType.INTERPOLATED_FINE, this::computeFluidBorderDensity);
    }

    public BlockState getFluidState(int localX, int localY, int localZ) {
        FluidLevel currentFluidLevel = getFluidLevel(localX, localY, localZ);
        return currentFluidLevel.resolveFluid(localX + minX, localY + minY, localZ + minZ);
    }

    public void fillFluidMap(NoiseComputerExecutor executor, NoiseHolder holder) {
        NoiseComputerContext context = new NoiseComputerContext(executor, holder);
        for (int cellX = -1; cellX < this.cellsH + 1; cellX++) {
            int blockX = cellX * cellWidth + minX;
            for (int cellY = 0; cellY < this.cellsV; cellY++) {
                int blockY = cellY * cellHeight + minY;
                for (int cellZ = -1; cellZ < this.cellsH + 1; cellZ++) {
                    int blockZ = cellZ * cellWidth + minZ;
                    RandomSource aquiferRandom = random.at(blockX, blockY, blockZ);

                    int centerOffsetX = cellWidth/2 + aquiferRandom.nextInt(-4, 4),
                        centerOffsetY = cellHeight/2 + aquiferRandom.nextInt(-2, 2),
                        centerOffsetZ = cellWidth/2 + aquiferRandom.nextInt(-4, 4);
                    int cellCenterX = blockX + centerOffsetX,
                        cellCenterY = blockY + centerOffsetY,
                        cellCenterZ = blockZ + centerOffsetZ;

                    FluidLevel fluidLevel = this.fluidFiller.compute(cellCenterX, cellCenterY, cellCenterZ, context);
                    List<WorldFeature> worldFeatures = metaChunkMap.getWorldFeatures(cellCenterX, cellCenterZ);
                    for (WorldFeature worldFeature : worldFeatures) {
                        fluidLevel = worldFeature.modifyFluidLevel(cellCenterX, cellCenterY, cellCenterZ, fluidLevel, context);
                    }

                    int index = getIndexFromCell(cellX, cellY, cellZ);
                    this.cellCenterOffsets[index * 3 + 0] = centerOffsetX;
                    this.cellCenterOffsets[index * 3 + 1] = centerOffsetY;
                    this.cellCenterOffsets[index * 3 + 2] = centerOffsetZ;
                    this.cellMap[index] = fluidLevel;
                }
            }
        }

        for (int cellX = -1; cellX < this.cellsH + 1; cellX++) {
            for (int cellY = 0; cellY < this.cellsV; cellY++) {
                for (int cellZ = -1; cellZ < this.cellsH + 1; cellZ++) {
                    int index = getIndexFromCell(cellX, cellY, cellZ);
                    this.homogenousnessMap[index] = calculateHomogenousness(cellX, cellY, cellZ);
                }
            }
        }
    }

    // todo: make this for each direction? might be much faster.
    private boolean calculateHomogenousness(int cellX, int cellY, int cellZ) {
        int index = getIndexFromCell(cellX, cellY, cellZ);
        FluidLevel centerFluid = this.cellMap[index];
        boolean isCenterEmpty = centerFluid.height < ((cellY - 1) * cellHeight + minY);
        boolean isCenterFull = centerFluid.height > ((cellY + 1) * cellHeight + minY);
        for (int xCellOffset = -1; xCellOffset <= 1; xCellOffset++) {
            int offsetCellX = cellX + xCellOffset;
            for (int yCellOffset = -1; yCellOffset <= 1; yCellOffset++) {
                int offsetCellY = cellY + yCellOffset;
                for (int zCellOffset = -1; zCellOffset <= 1; zCellOffset++) {
                    int offsetCellZ = cellZ + zCellOffset;
                    int offsetCellIndex = getIndexFromCell(offsetCellX, offsetCellY, offsetCellZ);

                    FluidLevel offsetFluid = this.cellMap[offsetCellIndex];

                    boolean isOffsetEmpty = offsetFluid.height < ((offsetCellY - 1) * cellHeight + minY);
                    boolean isCenterOffsetFull = centerFluid.height > ((cellY + 1) * cellHeight + minY);

                    // if they're both empty, it is homogenous.
                    if (isCenterEmpty == isOffsetEmpty)
                        continue;
                    // if they're not empty and have different fluids, they are not homogenous.
                    if (centerFluid.fluid() != offsetFluid.fluid())
                        return false;
                    // if they're both totally full, it is homogenous.
                    if (isCenterFull == isCenterOffsetFull)
                        continue;
                    // if they have different fluid levels, then it is not homogenous
                    if (centerFluid.height() != offsetFluid.height())
                        return false;
                }
            }
        }

        return false;
    }

    private FluidLevel getFluidLevel(int localX, int localY, int localZ) {
        int cellX = Math.floorDiv(localX, cellWidth),
            cellY = Math.floorDiv(localY, cellHeight),
            cellZ = Math.floorDiv(localZ, cellWidth);

        // make sure that we don't check outside the world
        int minYCellOffset = cellY > 0 ? -1 : 0;
        int maxYCellOffset = cellY < mapSizeV ? 1 : 0;

        FluidLevel closest = cellMap[0];
        int closestDistance = Integer.MAX_VALUE;
        for (int xCellOffset = -1; xCellOffset <= 1; xCellOffset++) {
            int offsetCellX = cellX + xCellOffset;
            for (int yCellOffset = minYCellOffset; yCellOffset <= maxYCellOffset; yCellOffset++) {
                int offsetCellY = cellY + yCellOffset;
                for (int zCellOffset = -1; zCellOffset <= 1; zCellOffset++) {
                    int offsetCellZ = cellZ + zCellOffset;

                    int index = getIndexFromCell(offsetCellX, offsetCellY, offsetCellZ);
                    int cellCenterX = offsetCellX * cellWidth  + this.cellCenterOffsets[index * 3 + 0],
                        cellCenterY = offsetCellY * cellHeight + this.cellCenterOffsets[index * 3 + 1],
                        cellCenterZ = offsetCellZ * cellWidth  + this.cellCenterOffsets[index * 3 + 2];
                    int distance = Math.abs(localX - cellCenterX) +
                                   Math.abs(localY - cellCenterY) +
                                   Math.abs(localZ - cellCenterZ);
                    if (closestDistance > distance) {
                        closestDistance = distance;
                        closest = cellMap[index];
                    }
                }
            }
        }

        return closest;
    }

    private double computeFluidBorderDensity(int x, int y, int z, NoiseComputerContext context) {
        int localX = x - minX, localY = y - minY, localZ = z - minZ;
        int cellX = Math.floorDiv(localX, cellWidth), cellY = Math.floorDiv(localY, cellHeight), cellZ = Math.floorDiv(localZ, cellWidth);
        int cellIndex = getIndexFromCell(cellX, cellY, cellZ);
        // don't bother calculating borders if the fluid is completely homogenous
        if (homogenousnessMap[cellIndex])
            return borderRadius - 3;

        FluidLevel currentFluidLevel = getFluidLevel(x - minX, y - minY, z - minZ);
        BlockState currentFluidState = currentFluidLevel.resolveFluid(x, y, z);
        // don't check downwards if we're at the bottom of the world.
        //int finalIndex = y > -63 + borderRadius ? borderOffsets.length : borderOffsets.length - 1;
        for (int distance = 0; distance < borderRadius; distance++) {
            for (int i = 0; i < borderOffsets.length; i++) {
                // only check upwards if the current state is air.
                if (i == 4 && !currentFluidState.isAir()) continue;
                int xOffset = borderOffsets[i][0] * distance,
                    yOffset = borderOffsets[i][1] * distance,
                    zOffset = borderOffsets[i][2] * distance;
                FluidLevel neighboringFluid = getFluidLevel(localX + xOffset, localY + yOffset, localZ + zOffset);
                BlockState neighboringFluidState = neighboringFluid.resolveFluid(x + xOffset, y + yOffset, z + zOffset);
                if (currentFluidState.getBlock() != neighboringFluidState.getBlock())
                    return distance - 3;
            }
        }
        return borderRadius - 3;
    }

    int getIndexFromCell(int cellX, int cellY, int cellZ) {
        int indexX = Math.clamp(cellX + 1, 0, mapSizeH - 1),
            indexY = Math.clamp(cellY, 0, mapSizeV - 1),
            indexZ = Math.clamp(cellZ + 1, 0, mapSizeH - 1);
        int index = indexX + indexY * mapSizeH + indexZ * mapSizeH * mapSizeV;
        if (index < 0 || index >= cellMap.length )
            Clinker.LOGGER.error("cellPos = ({}, {}, {}), indexPos = ({}, {}, {}), index = {}", cellX, cellY, cellZ, indexX, cellY, indexZ, index);
        return index;
    }

    public interface FluidFiller {
        FluidLevel compute(int x, int y, int z, NoiseComputerContext context);
    }
    public record FluidLevel(BlockState fluid, int height) {
        public BlockState resolveFluid(int x, int y, int z) {
            return y > height ? Blocks.AIR.defaultBlockState() : fluid;
        }
    }
}
