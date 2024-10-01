package birsy.clinker.common.world.level.gen;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;

import java.util.Arrays;

public class ChunkFluidField {
    public static final FluidCell EMPTY = new FluidCell(Fluids.EMPTY, 0);
    private static final Direction[] FLUID_PROPAGATION_DIRECTIONS = new Direction[]{Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST};
    final FluidCell[] fluidField;
    final int horizontalResolution, verticalResolution;

    public ChunkFluidField(int horizontalResolution, int verticalResolution) {
        this.fluidField = new FluidCell[horizontalResolution * horizontalResolution * verticalResolution];
        Arrays.fill(this.fluidField, EMPTY);
        this.horizontalResolution = horizontalResolution;
        this.verticalResolution = verticalResolution;
    }

    protected int getIndex(int x, int y, int z) {
        return y * (horizontalResolution * verticalResolution) + z * (horizontalResolution) + x;
    }


    public void fillBlockStates(ChunkAccess chunk, ChunkFluidField northNeighbor, ChunkFluidField southNeighbor, ChunkFluidField westNeighbor, ChunkFluidField eastNeighbor) {
        int cellHeight = chunk.getHeight() / this.verticalResolution;
        for (int y = 0; y < this.verticalResolution; y++) {
            for (int z = 0; z < this.horizontalResolution; z++) {
                for (int x = 0; x < this.horizontalResolution; x++) {
                    FluidCell currentCell = getFluidInCell(x, y, z);
                    FluidCell aboveCell = getFluidInCell(x, y + 1, z, northNeighbor, southNeighbor, eastNeighbor, westNeighbor);
                    this.placeFluid(chunk, x, y, z, currentCell);
                    for (Direction dir : FLUID_PROPAGATION_DIRECTIONS) {
                        float difference = currentCell.difference(getFluidInCell(x + dir.getStepX(), y + dir.getStepY(), z + dir.getStepZ(), northNeighbor, southNeighbor, eastNeighbor, westNeighbor), dir == Direction.DOWN);
                        if (difference != 0.0F) this.placeFluidBarrier(chunk, x, y, z, (int) (difference * cellHeight), dir);
                    }
                }
            }
        }
    }

    protected FluidCell getFluidInCell(int x, int y, int z, ChunkFluidField northNeighbor, ChunkFluidField southNeighbor, ChunkFluidField eastNeighbor, ChunkFluidField westNeighbor) {
        if (y < 0 || y >= verticalResolution) return EMPTY;
        if (x < 0) return westNeighbor.getFluidInCell(horizontalResolution - 1, y, z);
        if (x >= horizontalResolution) return eastNeighbor.getFluidInCell(0, y, z);
        if (z < 0) return northNeighbor.getFluidInCell(horizontalResolution - 1, y, z);
        if (z >= horizontalResolution) return southNeighbor.getFluidInCell(0, y, z);
        return this.getFluidInCell(x, y, z);
    }

    protected FluidCell getFluidInCell(int x, int y, int z) {
        return fluidField[getIndex(x, y, z)];
    }

    protected void placeFluidBarrier(ChunkAccess chunk, int cellX, int cellY, int cellZ, int height, Direction direction) {
        
    }

    protected void placeFluid(ChunkAccess chunk, int cellX, int cellY, int cellZ, FluidCell fluid) {
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        int horizontalSize = 16 / this.horizontalResolution;
        int verticalSize = chunk.getHeight() / this.verticalResolution;

        for (int x = 0; x < 16/this.horizontalResolution; x++) {
            for (int z = 0; z < 16/this.horizontalResolution; z++) {
                for (int y = 0; y < chunk.getHeight()/this.verticalResolution; z++) {
                    transformPosition(chunk, x + horizontalSize*cellX, y + verticalSize*cellY, z + horizontalSize*cellZ, pos);
                    chunk.setBlockState(pos, fluid.fluid.defaultFluidState().createLegacyBlock(), false);
                }
            }
        }
    }

    protected BlockPos.MutableBlockPos transformPosition(ChunkAccess chunk, int localX, int localY, int localZ, BlockPos.MutableBlockPos pos) {
        pos.set(localX + chunk.getPos().getMinBlockX(), localY + chunk.getMinBuildHeight(), localZ + chunk.getPos().getMinBlockZ());
        return pos;
    }

    public static final class FluidCell {
        final Fluid fluid;
        final float height;

        public FluidCell(Fluid fluid, float height) {
            this.fluid = fluid;
            this.height = height;
        }

        public float difference(FluidCell other, boolean below) {
            if (below) {
                return fluid.isSame(other.fluid) && other.height == 1.0F ? 0 : 1;
            }

            if (other.isEmpty()) return this.height;
            if (this.isEmpty()) return other.height;
            if (fluid.isSame(other.fluid)) return Math.max(this.height, other.height);
            return -Math.abs(this.height - other.height);
        }

        public boolean isEmpty() {
            return this.fluid.isSame(EMPTY.fluid) || this.height == 0;
        }
    }
}
