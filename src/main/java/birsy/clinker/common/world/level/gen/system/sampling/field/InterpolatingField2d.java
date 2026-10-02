package birsy.clinker.common.world.level.gen.system.sampling.field;

import net.minecraft.util.Mth;

public class InterpolatingField2d extends InterpolatingField {
    boolean filled = false;
    public InterpolatingField2d(int xzCellScale, int paddingBlocks) {
        super(xzCellScale, 0, 1, paddingBlocks);
        this.field = new float[this.sliceCellCount];
    }

    @Override
    public float retrieve(int x, int y, int z) {
        int paddedX = x + paddingBlocks,
            paddedZ = z + paddingBlocks;
        int cellX = paddedX >> xzCellScale,
            cellZ = paddedZ >> xzCellScale;
        int localX = paddedX & xzCellMask,
            localZ = paddedZ & xzCellMask;
        float facX = localX * invXZCellSize,
              facZ = localZ * invXZCellSize;
        int nextX = cellX + 1, nextZ = cellZ + 1;

        float x0 = Mth.lerp(facX, field[cellX + cellZ * xzCellCount], field[nextX + cellZ * xzCellCount]),
              x1 = Mth.lerp(facX, field[cellX + nextZ * xzCellCount], field[nextX + nextZ * xzCellCount]);
        return Mth.lerp(facZ, x0, x1);
    }

    @Override
    public void fill(int fromY, int toY, InterpolatingFieldFiller filler) {
        if (filled) return;
        filler.setSlice(0);
        int index = 0;
        for (int cellZ = 0; cellZ < xzCellCount; cellZ++) {
            int z = (cellZ << xzCellScale) - paddingBlocks;
            for (int cellX = 0; cellX < xzCellCount; cellX++) {
                int x = (cellX << xzCellScale) - paddingBlocks;
                field[index++] = filler.compute(x, 0, z);
                filler.advanceX();
            }
            filler.advanceZ();
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
