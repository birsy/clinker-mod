package birsy.clinker.common.world.level.gen.system.noise.field;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;
import net.minecraft.util.Mth;

import java.util.BitSet;

public final class InterpolatedNoiseField extends NoiseField {
    public final int xzCellScale, xzCellSize, xzCellCount, xzCellStride;
    public final int yCellScale, yCellSize, yCellCount;
    final int yCellMask;
    final int xzCellMask;
    final double invXZCellSize, invYCellSize;
    final BitSet filledLayers, fillMask;
    final int chunkHeight;

    public final double[] field;

    public InterpolatedNoiseField(int chunkHeight, int xzCellScale, int yCellScale, int paddingCells) {
        super(chunkHeight - 1, paddingCells, paddingCells << xzCellScale);
        this.chunkHeight = chunkHeight;

        this.xzCellScale = xzCellScale;
        this.xzCellSize = 1 << xzCellScale;
        this.xzCellMask = this.xzCellSize - 1;
        this.invXZCellSize = 1.0 / this.xzCellSize;
        this.xzCellCount = (CHUNK_WIDTH >> xzCellScale) + 1 + paddingCells * 2; // padding
        this.xzCellStride = this.xzCellCount * this.xzCellCount;

        this.yCellScale = yCellScale;
        this.yCellSize = 1 << yCellScale;
        this.yCellMask = this.yCellSize - 1;
        this.invYCellSize = 1.0 / this.yCellSize;
        this.yCellCount = (chunkHeight >> yCellScale) + 1;

        this.field = new double[this.xzCellStride * this.yCellCount];
        this.filledLayers = new BitSet(this.yCellCount);
        this.fillMask = new BitSet(this.yCellCount);
    }

    public static InterpolatedNoiseField twoDimensional(int xzCellScale, int paddingCells) {
        return new InterpolatedNoiseField(1, xzCellScale, 32, paddingCells);
    }

    @Override
    public double[] array() {
        return field;
    }

    @Override
    public double retrieve(int x, int y, int z) {
        int paddedX = x + paddingBlocks,
            paddedZ = z + paddingBlocks;
        int cellX = paddedX >> xzCellScale,
            cellY = y >> yCellScale,
            cellZ = paddedZ >> xzCellScale;
        int localX = paddedX & xzCellMask,
            localY = y & yCellMask,
            localZ = paddedZ & xzCellMask;
        double interpX = localX * invXZCellSize,
               interpY = localY * invYCellSize,
               interpZ = localZ * invXZCellSize;
        int nextX = cellX + ((localX | -localX) >>> 31),
            nextY = cellY + ((localY | -localY) >>> 31),
            nextZ = cellZ + ((localZ | -localZ) >>> 31);
        return Mth.lerp3(interpX, interpZ, interpY,
                field[cellX + cellZ * xzCellCount + cellY * xzCellStride], field[nextX + cellZ * xzCellCount + cellY * xzCellStride],
                field[cellX + nextZ * xzCellCount + cellY * xzCellStride], field[nextX + nextZ * xzCellCount + cellY * xzCellStride],
                field[cellX + cellZ * xzCellCount + nextY * xzCellStride], field[nextX + cellZ * xzCellCount + nextY * xzCellStride],
                field[cellX + nextZ * xzCellCount + nextY * xzCellStride], field[nextX + nextZ * xzCellCount + nextY * xzCellStride]
        );
    }

    @Override
    public void fill(int minLocalY, int maxLocalY, int minX, int minY, int minZ, NoiseContext context, NoiseFieldFiller filler) {
        // find unfilled layers
        fillMask.clear();
        fillMask.set(Math.max(0, minLocalY >> yCellScale), Math.min(yCellCount - 1, maxLocalY >> yCellScale) + 1);
        fillMask.andNot(filledLayers);
        // fill them
        for (int startCellY = fillMask.nextSetBit(0); startCellY >= 0; startCellY = fillMask.nextSetBit(startCellY + 1)) {
            int endCellY = fillMask.nextClearBit(startCellY);
            if (endCellY == -1) endCellY = yCellCount;
            fillInternal(startCellY, endCellY - 1, minX, minY, minZ, filler, context);
        }
        // finally, set filled layers
        filledLayers.or(fillMask);
    }

    public void fillInternal(int startCellY, int endCellY, int minX, int minY, int minZ, NoiseFieldFiller filler, NoiseContext context) {
        int index = startCellY * xzCellStride;
        for (int cellY = startCellY; cellY <= endCellY; cellY++) {
            int globalY = (cellY << yCellScale) + minY;
            for (int cellZ = 0; cellZ < xzCellCount; cellZ++) {
                int globalZ = (cellZ << xzCellScale) + minZ - paddingBlocks;
                for (int cellX = 0; cellX < xzCellCount; cellX++) {
                    int globalX = (cellX << xzCellScale) + minX - paddingBlocks;
                    field[index++] = filler.compute(globalX, globalY, globalZ, context);
                }
            }
        }
    }

    @Override
    public void byIndex(int minLocalY, int maxLocalY, NoiseFieldVisitors.IndexVisitor visitor) {
        int cellYStart = Math.max(0, minLocalY >> yCellScale),
            cellYEnd = Math.min(yCellCount - 1, maxLocalY >> yCellScale);
        int index = cellYStart * xzCellStride;
        for (int cellY = cellYStart; cellY <= cellYEnd; cellY++) {
            for (int cellZ = 0; cellZ < xzCellCount; cellZ++) {
                for (int cellX = 0; cellX < xzCellCount; cellX++) {
                    visitor.visit(index++);
                }
            }
        }
    }
    @Override
    public void byBlock(int minLocalY, int maxLocalY, NoiseFieldVisitors.PositionVisitor visitor) {
        int cellYStart = Math.max(0, minLocalY >> yCellScale),
                cellYEnd = Math.min(yCellCount - 1, maxLocalY >> yCellScale);
        int index = cellYStart * xzCellStride;
        for (int cellY = cellYStart; cellY <= cellYEnd; cellY++) {
            int bY = cellY << yCellScale;
            for (int cellZ = 0; cellZ < xzCellCount; cellZ++) {
                int bZ = (cellZ << xzCellScale) - paddingBlocks;
                for (int cellX = 0; cellX < xzCellCount; cellX++) {
                    int bX = (cellX << xzCellScale) - paddingBlocks;
                    visitor.visit(index++, bX, bY, bZ);
                }
            }
        }
    }
}
