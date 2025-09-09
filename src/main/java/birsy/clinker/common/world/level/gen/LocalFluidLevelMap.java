package birsy.clinker.common.world.level.gen;

import birsy.clinker.common.world.level.gen.noise.*;
import birsy.clinker.common.world.level.gen.worldfeature.MetaChunkMap;
import birsy.clinker.common.world.level.gen.worldfeature.MetaChunkMapHolder;
import birsy.clinker.common.world.level.gen.worldfeature.WorldFeature;
import birsy.clinker.core.Clinker;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.PositionalRandomFactory;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.Collection;

public class LocalFluidLevelMap {
    private static final int bufferCells = 2;
    private static final int cellWidth = 8, cellHeight = 12;
    private static final int fluidBoundaryBufferSize = cellWidth, maxFluidBoundaryDistance = 5;
    final int cellsH, cellsV;
    final int chunkHeight;

    final int minX, minY, minZ;
    final FluidLevel[] cellMap;
    final boolean[] homogenousnessMap;
    final int[] cellCenterOffsets;
    final PositionalRandomFactory random;
    final MetaChunkMap metaChunkMap;
    final FluidFiller fluidFiller;
    final int[] borderDistance;

    public final NoiseComputer borderDensityComputer;

    public LocalFluidLevelMap(ChunkAccess chunk, RandomState randomState, FluidFiller fluidFiller) {
        this.minX = chunk.getPos().getMinBlockX();
        this.minY = chunk.getMinBuildHeight();
        this.minZ = chunk.getPos().getMinBlockZ();
        this.chunkHeight = chunk.getHeight();
        this.fluidFiller = fluidFiller;
        this.cellsH = 16 / cellWidth;
        this.cellsV = chunk.getHeight() / cellHeight;

        this.cellMap = new FluidLevel[(this.cellsH + bufferCells * 2) * (this.cellsH + bufferCells * 2) * this.cellsV];
        this.cellCenterOffsets = new int[this.cellMap.length * 3];
        this.homogenousnessMap = new boolean[this.cellMap.length];
        this.random = randomState.aquiferRandom();
        this.metaChunkMap = ((MetaChunkMapHolder)(Object) randomState).clinker$metaChunkMap();

        this.borderDistance = new int[(16 + fluidBoundaryBufferSize * 2) * (16 + fluidBoundaryBufferSize * 2) * (this.chunkHeight + fluidBoundaryBufferSize * 2)];
        this.borderDensityComputer = new NoiseComputer("aquifer_border_density", CacheType.NONE, this::computeFluidBorderDensity);
    }

    public BlockState getFluidState(int localX, int localY, int localZ) {
        FluidLevel currentFluidLevel = getFluidLevel(localX, localY, localZ);
        return currentFluidLevel.resolveFluid(localX + minX, localY + minY, localZ + minZ);
    }

    public void fillFluidMap(NoiseComputerExecutor executor, NoiseHolder holder) {
        NoiseComputerContext context = new NoiseComputerContext(executor, holder);
        for (int cellX = -bufferCells; cellX < this.cellsH + bufferCells; cellX++) {
            int blockX = cellX * cellWidth + minX;
            for (int cellY = 0; cellY < this.cellsV; cellY++) {
                int blockY = cellY * cellHeight + minY;
                for (int cellZ = -bufferCells; cellZ < this.cellsH + bufferCells; cellZ++) {
                    int blockZ = cellZ * cellWidth + minZ;
                    RandomSource aquiferRandom = random.at(blockX, blockY, blockZ);

                    int centerOffsetX = cellWidth / 2,// + aquiferRandom.nextInt(-4, 4),
                        centerOffsetY = cellHeight / 2,// + aquiferRandom.nextInt(-2, 2),
                        centerOffsetZ = cellWidth / 2;// + aquiferRandom.nextInt(-4, 4);
                    int cellCenterX = blockX + centerOffsetX,
                        cellCenterY = blockY + centerOffsetY,
                        cellCenterZ = blockZ + centerOffsetZ;

                    FluidLevel fluidLevel = this.fluidFiller.compute(cellCenterX, cellCenterY, cellCenterZ, context);
                    Collection<WorldFeature> worldFeatures = metaChunkMap.getWorldFeatures(cellCenterX, cellCenterZ);
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

        for (int cellX = -bufferCells; cellX < this.cellsH + bufferCells; cellX++) {
            for (int cellY = 0; cellY < this.cellsV; cellY++) {
                for (int cellZ = -bufferCells; cellZ < this.cellsH + bufferCells; cellZ++) {
                    int index = getIndexFromCell(cellX, cellY, cellZ);
                    //this.homogenousnessMap[index] = calculateHomogenousness(cellX, cellY, cellZ);
                }
            }
        }

        fillFluidBorders();
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
        int maxYCellOffset = cellY < this.cellsV ? 2 : 0;

        FluidLevel closest = cellMap[0];
        int closestDistance = Integer.MAX_VALUE;
        for (int xCellOffset = -1; xCellOffset < 2; xCellOffset++) {
            int offsetCellX = cellX + xCellOffset;

            for (int yCellOffset = minYCellOffset; yCellOffset < maxYCellOffset; yCellOffset++) {
                int offsetCellY = cellY + yCellOffset;

                for (int zCellOffset = -1; zCellOffset < 2; zCellOffset++) {
                    int offsetCellZ = cellZ + zCellOffset;

                    int index = getIndexFromCell(offsetCellX, offsetCellY, offsetCellZ);
                    int cellCenterX = offsetCellX * cellWidth  + this.cellCenterOffsets[index * 3 + 0],
                        cellCenterY = offsetCellY * cellHeight + this.cellCenterOffsets[index * 3 + 1],
                        cellCenterZ = offsetCellZ * cellWidth  + this.cellCenterOffsets[index * 3 + 2];
                    int distance = (localX - cellCenterX) * (localX - cellCenterX) +
                                   (localY - cellCenterY) * (localY - cellCenterY) +
                                   (localZ - cellCenterZ) * (localZ - cellCenterZ);
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
        int localX = x - this.minX,
            localY = y - this.minY,
            localZ = z - this.minZ;

        int indexX = localX + fluidBoundaryBufferSize,
            indexY = localY + fluidBoundaryBufferSize,
            indexZ = localZ + fluidBoundaryBufferSize;

        int index = indexX +
                    indexY * (16 + 2 * fluidBoundaryBufferSize) +
                    indexZ * (16 + 2 * fluidBoundaryBufferSize) * (this.chunkHeight + 2 * fluidBoundaryBufferSize);

        return borderDistance[index] - 2;
    }

    private final Direction[] directions = Direction.values();
    // todo: only compute fluid borders in air blocks
    private void fillFluidBorders() {
        // initialize fluid borders - 0 at boundaries, maxFluidBoundaryDistance everywhere else
        // i think i might only need to check in one direction? it would get it on both sides...
        // especially since it's computed per-block now.
        for (int indexX = 0; indexX < 16 + fluidBoundaryBufferSize * 2; indexX++) {
            int localX = indexX - fluidBoundaryBufferSize;
            int worldX = localX + minX;
            int cellX = Math.floorDiv(localX, cellWidth);

            for (int indexY = 0; indexY < this.chunkHeight + fluidBoundaryBufferSize * 2; indexY++) {
                int localY = indexY - fluidBoundaryBufferSize;
                int worldY = localY + minY;
                int cellY = Math.floorDiv(localY, cellHeight);

                for (int indexZ = 0; indexZ < 16 + fluidBoundaryBufferSize * 2; indexZ++) {
                    int localZ = indexZ - fluidBoundaryBufferSize;
                    int worldZ = localZ + minZ;
                    int cellZ = Math.floorDiv(localZ, cellWidth);

                    int cellIndex = getIndexFromCell(cellX, cellY, cellZ);
                    // don't bother calculating borders if the fluid is completely homogenous
                    //if (homogenousnessMap[cellIndex]) continue;

                    BlockState currentFluidState = getFluidLevel(localX, localY, localZ).resolveFluid(worldX, worldY, worldZ);

                    int borderDist = Integer.MAX_VALUE;
                    for (Direction.Axis axis : Direction.Axis.VALUES) {
                        if (axis == Direction.Axis.Y && !currentFluidState.isAir()) continue;
                        int offsetX = axis.choose(1, 0, 0),
                            offsetY = axis.choose(0, 1, 0),
                            offsetZ = axis.choose(0, 0, 1);
                        BlockState neighboringFluidState = getFluidLevel(localX + offsetX, localY + offsetY, localZ + offsetZ)
                                .resolveFluid(worldX + offsetX, worldY + offsetY, worldZ + offsetZ);
                        if (currentFluidState.getBlock() != neighboringFluidState.getBlock()) {
                            borderDist = 0;
                            break;
                        }
                    }

                    int index = indexX +
                                indexY * (16 + 2 * fluidBoundaryBufferSize) +
                                indexZ * (16 + 2 * fluidBoundaryBufferSize) * (this.chunkHeight + 2 * fluidBoundaryBufferSize);
                    borderDistance[index] = borderDist;
                }
            }
        }

        // smears out the fluid directionally
        // kind of turns each border pixel into a diamond gradient
        // allows for blending to properly take over.
        for (Direction.Axis axis : Direction.Axis.VALUES) {
            for (int i = 0; i < maxFluidBoundaryDistance; i++)
                fluidBorderSmearPass(axis);
        }
    }

    private void fluidBorderSmearPass(Direction.Axis axis) {
        for (int localX = 0; localX < 16; localX++) {
            int indexX = localX + fluidBoundaryBufferSize;
            int cellX = Math.floorDiv(localX, cellWidth);

            for (int localZ = 0; localZ < 16; localZ++) {
                int indexZ = localZ + fluidBoundaryBufferSize;
                int cellZ = Math.floorDiv(localZ, cellWidth);

                for (int localY = 0; localY < this.chunkHeight; localY++) {
                    int indexY = localY + fluidBoundaryBufferSize;
                    int cellY = Math.floorDiv(localY, cellHeight);

                    int cellIndex = getIndexFromCell(cellX, cellY, cellZ);
                    // don't bother calculating borders if the fluid is completely homogenous
                    //if (homogenousnessMap[cellIndex]) continue;

                    int index = indexX +
                                indexY * (16 + 2 * fluidBoundaryBufferSize) +
                                indexZ * (16 + 2 * fluidBoundaryBufferSize) * (this.chunkHeight + 2 * fluidBoundaryBufferSize);
                    int minDistance = borderDistance[index];
                    for (int i = -1; i < 2; i += 2) {
                        int offsetX = axis.choose(1, 0, 0) * i,
                            offsetY = axis.choose(0, 1, 0) * i,
                            offsetZ = axis.choose(0, 0, 1) * i;
                        int oIndex = (indexX + offsetX) +
                                     (indexY + offsetY) * (16 + 2 * fluidBoundaryBufferSize) +
                                     (indexZ + offsetZ) * (16 + 2 * fluidBoundaryBufferSize) * (this.chunkHeight + 2 * fluidBoundaryBufferSize);
                        minDistance = Math.min(minDistance, borderDistance[oIndex] + 1);
                    }
                    borderDistance[index] = minDistance;
                }
            }
        }
    }

    int getIndexFromCell(int cellX, int cellY, int cellZ) {
        int indexX = Math.clamp(cellX + bufferCells, 0, this.cellsH + bufferCells * 2 - 1),
            indexY = Math.clamp(cellY, 0, this.cellsV - 1),
            indexZ = Math.clamp(cellZ + bufferCells, 0, this.cellsH + bufferCells * 2 - 1);
        int index = indexX + indexY * (this.cellsH + bufferCells * 2) + indexZ * (this.cellsH + bufferCells * 2) * this.cellsV;
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
