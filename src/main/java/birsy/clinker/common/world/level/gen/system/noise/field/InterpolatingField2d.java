package birsy.clinker.common.world.level.gen.system.noise.field;

import birsy.clinker.common.world.level.gen.system.noise.Synthesizer;
import birsy.clinker.core.util.noise.FastNoiseLite;
import net.minecraft.util.Mth;

public class InterpolatingField2d extends InterpolatingField {
    boolean filled = false;
    public InterpolatingField2d(int xzCellScale, int paddingBlocks) {
        super(xzCellScale, 0, 1, paddingBlocks);
        this.field = new double[this.sliceCellCount];
    }

    @Override
    public double retrieve(int x, int y, int z) {
        int paddedX = x + paddingBlocks,
            paddedZ = z + paddingBlocks;
        int cellX = paddedX >> xzCellScale,
            cellZ = paddedZ >> xzCellScale;
        int localX = paddedX & xzCellMask,
            localZ = paddedZ & xzCellMask;
        double interpX = localX * invXZCellSize,
               interpZ = localZ * invXZCellSize;
        int nextX = cellX + 1, nextZ = cellZ + 1;
        return Mth.lerp2(interpX, interpZ,
                field[cellX + cellZ * xzCellCount], field[nextX + cellZ * xzCellCount],
                field[cellX + nextZ * xzCellCount], field[nextX + nextZ * xzCellCount]
        );
    }

    public void fill(int fromY, int toY, int minX, int minY, int minZ, Synthesizer.Function filler, Synthesizer.Context context) {
        if (filled) return;
        context.setSlice(0);
        FastNoiseLite[] noises = context.noises();
        int index = 0;
        for (int cellZ = 0; cellZ < xzCellCount; cellZ++) {
            int globalZ = (cellZ << xzCellScale) + minZ - paddingBlocks;
            for (int cellX = 0; cellX < xzCellCount; cellX++) {
                int globalX = (cellX << xzCellScale) + minX - paddingBlocks;
                field[index++] = filler.compute(globalX, 0, globalZ, context.sampleDependencyValues(), noises);
                context.advanceX();
            }
            context.advanceZ();
        }
        filled = true;
    }

    public void visit(int minLocalY, int maxLocalY, InterpolatingField.Visitor visitor) {
        int index = 0;
        for (int cellZ = 0; cellZ < xzCellCount; cellZ++) {
            int bZ = (cellZ << xzCellScale) - paddingBlocks;
            for (int cellX = 0; cellX < xzCellCount; cellX++) {
                int bX = (cellX << xzCellScale) - paddingBlocks;
                visitor.visit(index++, bX, 0, bZ);
            }
        }
    }
}
