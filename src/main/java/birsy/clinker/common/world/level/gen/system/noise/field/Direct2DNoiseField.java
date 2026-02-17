package birsy.clinker.common.world.level.gen.system.noise.field;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;

public final class Direct2DNoiseField extends NoiseField2D {
    final int paddedWidth;
    boolean filled = false;
    public final double[] field;

    public Direct2DNoiseField(int padding) {
        super(padding, padding);
        this.paddedWidth = CHUNK_WIDTH + padding * 2;
        this.field = new double[paddedWidth * paddedWidth];
    }

    @Override
    public double[] array() {
        return field;
    }

    @Override
    public double retrieve(int x, int y, int z) {
        int paddedX = x + paddingBlocks,
            paddedZ = z + paddingBlocks;
        return field[paddedX + paddedZ * paddedWidth];
    }
    
    @Override
    public void fill(int startY, int endY, int minX, int minY, int minZ, NoiseContext context, NoiseFieldFiller filler) {
        if (filled) return;
        int index = 0;
        for (int z = 0; z < paddedWidth; z++) {
            int globalZ = z + minZ - paddingBlocks;
            for (int x = 0; x < paddedWidth; x++) {
                int globalX = x + minX - paddingBlocks;
                field[index++] = filler.compute(globalX, 0, globalZ, context);
            }
        }
        filled = true;
    }

    @Override
    public void byIndex(int minLocalY, int maxLocalY, NoiseFieldVisitors.IndexVisitor visitor) {
        for (int i = 0; i < field.length; i++) visitor.visit(i);
    }
    @Override
    public void byBlock(int minLocalY, int maxLocalY, NoiseFieldVisitors.PositionVisitor visitor) {
        int index = 0;
        for (int z = 0; z < paddedWidth; z++) {
            int bZ = z - paddingBlocks;
            for (int x = 0; x < paddedWidth; x++) {
                int bX = z - paddingBlocks;
                visitor.visit(index++, bX, 0, bZ);
            }
        }
    }
    @Override
    public void byCell(int minLocalY, int maxLocalY, NoiseFieldVisitors.PositionVisitor visitor) {
        int index = 0;
        for (int z = 0; z < paddedWidth; z++) {
            for (int x = 0; x < paddedWidth; x++) {
                visitor.visit(index++, x, 0, z);
            }
        }
    }
    @Override
    public void visit(int minLocalY, int maxLocalY, NoiseFieldVisitors.BigVisitor visitor) {
        int index = 0;
        for (int z = 0; z < paddedWidth; z++) {
            int bZ = z - paddingBlocks;
            for (int x = 0; x < paddedWidth; x++) {
                int bX = z - paddingBlocks;
                visitor.visit(index++, bX, 0, bZ, x, 0, z);
            }
        }
    }
}
