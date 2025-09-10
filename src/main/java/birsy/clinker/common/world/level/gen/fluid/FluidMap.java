package birsy.clinker.common.world.level.gen.fluid;

import birsy.clinker.common.world.level.gen.noise.NoiseComputerContext;
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

import java.util.Collection;

public class FluidMap {
    private static final int cellPadding = 1;
    private static final int cellWidth = 16, cellHeight = 16;

    final int minCellX, minCellY, minCellZ;
    final int cellCountXZ, cellCountY;
    final FluidCell[] cells;

    final PositionalRandomFactory aquiferRandom;
    final NoiseComputerContext noiseContext;
    final FluidFiller baseFluidFiller;

    final Collection<WorldFeature> worldFeatures;

    public FluidMap(RandomState randomState, ChunkAccess chunk, NoiseComputerContext noiseContext, FluidFiller baseFluidFiller) {
        this.noiseContext = noiseContext;
        this.baseFluidFiller = baseFluidFiller;
        this.cellCountXZ = 16 / cellWidth;
        this.cellCountY = chunk.getHeight() / cellHeight;
        this.cells = new FluidCell[(this.cellCountXZ + cellPadding * 2) * (this.cellCountXZ + cellPadding * 2) * (this.cellCountY + cellPadding * 2)];
        this.minCellX = Math.floorDiv(chunk.getPos().getMinBlockX(), cellWidth);
        this.minCellY = Math.floorDiv(chunk.getMinBuildHeight(), cellHeight);
        this.minCellZ = Math.floorDiv(chunk.getPos().getMinBlockZ(), cellWidth);
        this.aquiferRandom = randomState.aquiferRandom();
        this.worldFeatures = ((MetaChunkMapHolder) (Object) randomState).clinker$metaChunkMap()
                .getWorldFeatures(chunk.getPos().getMiddleBlockX(), chunk.getPos().getMiddleBlockZ());
    }

    public BlockState getFluidState(int x, int y, int z) {
        int cellX = Math.floorDiv(x, cellWidth),
            cellY = Math.floorDiv(y, cellHeight),
            cellZ = Math.floorDiv(z, cellWidth);
        // check the neighboring cells of each block for the closest cell center,
        // use that as the fluid.
        FluidCell closestCell = null;
        int closestCellDistance = Integer.MAX_VALUE;
        for (int xOffset = -1; xOffset < 1; xOffset++) {
            int offsetCellX = cellX + xOffset;
            for (int yOffset = -1; yOffset <= 1; yOffset++) {
                int offsetCellY = cellY + yOffset;
                for (int zOffset = -1; zOffset <= 1; zOffset++) {
                    int offsetCellZ = cellZ + zOffset;
                    FluidCell offsetCell = getCell(offsetCellX, offsetCellY, offsetCellZ);
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

    private FluidCell createNewCell(int cellX, int cellY, int cellZ) {
        RandomSource cellRandom = aquiferRandom.at(cellX * cellWidth, cellY * cellHeight, cellZ * cellWidth);
        // offset the cell center randomly, in order for more natural results.
        int cellCenterX = cellX * cellWidth +  Math.floorDiv(cellWidth, 2),// + cellRandom.nextIntBetweenInclusive(-2, 2),
            cellCenterY = cellY * cellHeight + Math.floorDiv(cellHeight, 2),// + cellRandom.nextIntBetweenInclusive(-2, 2),
            cellCenterZ = cellZ * cellWidth +  Math.floorDiv(cellWidth, 2);// + cellRandom.nextIntBetweenInclusive(-2, 2);
        FluidLevel fluidLevel = this.baseFluidFiller.compute(cellCenterX, cellCenterY, cellCenterZ, this.noiseContext);
        for (WorldFeature worldFeature : this.worldFeatures)
            fluidLevel = worldFeature.modifyFluidLevel(cellCenterX, cellCenterY, cellCenterZ, fluidLevel, noiseContext);
        return new FluidCell(cellCenterX, cellCenterY, cellCenterZ, fluidLevel);
    }

    private record FluidCell(int centerX, int centerY, int centerZ, FluidLevel fluidLevel) {
        BlockState resolve(int x, int y, int z) {
            return y < this.fluidLevel.height() ? fluidLevel.fluid() : Blocks.AIR.defaultBlockState();
        }
    }
}
