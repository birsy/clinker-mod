package birsy.clinker.common.world.level.gen.system.sampling.field;

import birsy.clinker.common.world.level.gen.system.sampling.Synthesizer;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.noise.FastNoiseLite;
import net.minecraft.util.Mth;

import java.util.BitSet;

public class InterpolatingField {
    public static final int CHUNK_WIDTH = 16;

    public final int maxY;
    public final int paddingBlocks, paddingCells;
    public final int xzCellScale, xzCellSize, xzCellCount, xzCellMask;
    public final int yCellScale, yCellSize, yCellCount, yCellMask;
    final double invXZCellSize, invYCellSize;
    final int sliceCellCount;
    final BitSet filledLayers, fillMask;
    final int chunkHeight;

    protected double[] field;

    public InterpolatingField(int xzCellScale, int yCellScale, int chunkHeight, int paddingBlocks) {
        this.maxY = chunkHeight - 1;

        this.xzCellScale = xzCellScale;
        this.xzCellSize = 1 << xzCellScale;
        this.xzCellMask = this.xzCellSize - 1;
        this.invXZCellSize = 1.0 / this.xzCellSize;

        this.paddingCells = Math.ceilDiv(paddingBlocks, this.xzCellSize);
        this.paddingBlocks = paddingCells << xzCellScale;
        this.chunkHeight = chunkHeight;

        this.xzCellCount = (CHUNK_WIDTH >> xzCellScale) + 1 + paddingCells * 2;
        this.sliceCellCount = this.xzCellCount * this.xzCellCount;

        this.yCellScale = yCellScale;
        this.yCellSize = 1 << yCellScale;
        this.yCellMask = this.yCellSize - 1;
        this.invYCellSize = 1.0 / this.yCellSize;
        this.yCellCount = (chunkHeight >> yCellScale) + 1;

        this.field = new double[this.sliceCellCount * this.yCellCount];
        this.filledLayers = new BitSet(this.yCellCount);
        this.fillMask = new BitSet(this.yCellCount);
    }

    public double[] array() { return field; }

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
        int nextX = cellX + 1, nextY = cellY + 1, nextZ = cellZ + 1;
        return Mth.lerp3(interpX, interpZ, interpY,
                field[cellX + cellZ * xzCellCount + cellY * sliceCellCount], field[nextX + cellZ * xzCellCount + cellY * sliceCellCount],
                field[cellX + nextZ * xzCellCount + cellY * sliceCellCount], field[nextX + nextZ * xzCellCount + cellY * sliceCellCount],
                field[cellX + cellZ * xzCellCount + nextY * sliceCellCount], field[nextX + cellZ * xzCellCount + nextY * sliceCellCount],
                field[cellX + nextZ * xzCellCount + nextY * sliceCellCount], field[nextX + nextZ * xzCellCount + nextY * sliceCellCount]
        );
    }

    public void fill(int fromY, int toY, int minX, int minY, int minZ, Synthesizer.Function filler, Synthesizer.Context context) {
        int fromLocalY = fromY - minY,
              toLocalY = toY - minY;
        FastNoiseLite[] noises = context.noises();
        // find unfilled layers
        fillMask.clear();
        fillMask.set(Math.max(0, fromLocalY >> yCellScale), Math.min(yCellCount - 1, toLocalY >> yCellScale) + 1);
        fillMask.andNot(filledLayers);
        // fill them
        for (int startCellY = fillMask.nextSetBit(0); startCellY >= 0; startCellY = fillMask.nextSetBit(startCellY + 1)) {
            int endCellY = fillMask.nextClearBit(startCellY);
            if (endCellY == -1) endCellY = yCellCount;
            context.setSlice(startCellY);
            fillInternal(startCellY, endCellY - 1, minX, minY, minZ, filler, context, noises);
        }
        // finally, set filled layers
        filledLayers.or(fillMask);
    }

    void fillInternal(int startCellY, int endCellY, int minX, int minY, int minZ, Synthesizer.Function filler, Synthesizer.Context context, FastNoiseLite[] noises) {
        int index = startCellY * sliceCellCount;
        for (int cellY = startCellY; cellY <= endCellY; cellY++) {
            int globalY = (cellY << yCellScale) + minY;
            for (int cellZ = 0; cellZ < xzCellCount; cellZ++) {
                int globalZ = (cellZ << xzCellScale) + minZ - paddingBlocks;
                for (int cellX = 0; cellX < xzCellCount; cellX++) {
                    int globalX = (cellX << xzCellScale) + minX - paddingBlocks;
                    field[index++] = filler.compute(globalX, globalY, globalZ, context.sampleDependencyValues(), noises);
                    context.advanceX();
                }
                context.advanceZ();
            }
            context.advanceY();
        }
    }

    public void visit(InterpolatingField.Visitor visitor) { this.visit(0, maxY, visitor); }
    public void visit(int minLocalY, int maxLocalY, InterpolatingField.Visitor visitor) {
        int cellYStart = Math.max(0, minLocalY >> yCellScale),
                cellYEnd = Math.min(yCellCount - 1, maxLocalY >> yCellScale);
        int index = cellYStart * sliceCellCount;
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

    public interface Visitor { void visit(int index, int x, int y, int z); }
}
