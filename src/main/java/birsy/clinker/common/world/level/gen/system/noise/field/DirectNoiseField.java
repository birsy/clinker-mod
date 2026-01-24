package birsy.clinker.common.world.level.gen.system.noise.field;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;

import java.util.BitSet;

public final class DirectNoiseField extends NoiseField3D {
    final int paddedWidth;
    final int xzStride;
    final BitSet filledLayers, fillMask;
    public final double[] field;

    public DirectNoiseField(int chunkHeight, int padding) {
        super(chunkHeight, padding, padding);
        this.paddedWidth = CHUNK_WIDTH + paddingBlocks * 2;
        this.xzStride = paddedWidth * paddedWidth;
        this.field = new double[xzStride * chunkHeight];
        this.filledLayers = new BitSet(chunkHeight);
        this.fillMask = new BitSet(chunkHeight);
    }

    @Override
    public double[] array() {
        return field;
    }

    @Override
    public double retrieve(int x, int y, int z) {
        int paddedX = x + paddingBlocks,
            paddedZ = z + paddingBlocks;
        return field[paddedX + paddedZ * paddedWidth + y * xzStride];
    }

    @Override
    public void fill(int minLocalY, int maxLocalY, int minX, int minY, int minZ, NoiseContext context, NoiseFieldFiller filler) {
        // find unfilled layers
        fillMask.clear();
        fillMask.set(Math.max(0, minLocalY), Math.min(chunkHeight - 1, maxLocalY) + 1);
        fillMask.andNot(filledLayers);
        // fill them
        for (int startY = fillMask.nextSetBit(0); startY >= 0; startY = fillMask.nextSetBit(startY + 1)) {
            int endY = fillMask.nextClearBit(startY);
            if (endY == -1) endY = chunkHeight;
            fillInternal(startY, endY - 1, minX, minY, minZ, filler, context);
        }
        // finally, set filled layers
        filledLayers.or(fillMask);
    }

    private void fillInternal(int startY, int endY, int minX, int minY, int minZ, NoiseFieldFiller filler, NoiseContext context) {
        int index = startY * xzStride;
        for (int y = startY; y <= endY; y++) {
            int globalY = y + minY;
            for (int z = 0; z < paddedWidth; z++) {
                int globalZ = z + minZ - paddingBlocks;
                for (int x = 0; x < paddedWidth; x++) {
                    int globalX = x + minX - paddingBlocks;
                    field[index++] = filler.compute(globalX, globalY, globalZ, context);
                }
            }
        }
    }

    @Override
    public void byIndex(int minLocalY, int maxLocalY, NoiseFieldVisitors.IndexVisitor visitor) {
        int startY = Math.max(0, minLocalY), endY = Math.min(chunkHeight - 1, maxLocalY);
        int index = startY * xzStride;
        for (int y = startY; y <= endY; y++) {
            for (int z = 0; z < paddedWidth; z++) {
                for (int x = 0; x < paddedWidth; x++) {
                    visitor.visit(index++);
                }
            }
        }
    }
    @Override
    public void byBlock(int minLocalY, int maxLocalY, NoiseFieldVisitors.PositionVisitor visitor) {
        int startY = Math.max(0, minLocalY), endY = Math.min(chunkHeight - 1, maxLocalY);
        int index = startY * xzStride;
        for (int y = startY; y <= endY; y++) {
            for (int z = 0; z < paddedWidth; z++) {
                for (int x = 0; x < paddedWidth; x++) {
                    visitor.visit(index++, x, y, z);
                }
            }
        }
    }
    @Override
    public void byCell(int minLocalY, int maxLocalY, NoiseFieldVisitors.PositionVisitor visitor) {
        this.byBlock(minLocalY, maxLocalY, visitor);
    }
    @Override
    public void visit(int minLocalY, int maxLocalY, NoiseFieldVisitors.BigVisitor visitor) {
        int startY = Math.max(0, minLocalY), endY = Math.min(chunkHeight - 1, maxLocalY);
        int index = startY * xzStride;
        for (int y = startY; y <= endY; y++) {
            for (int z = 0; z < paddedWidth; z++) {
                for (int x = 0; x < paddedWidth; x++) {
                    visitor.visit(index++, x, y, z, x, y, z);
                }
            }
        }
    }
    @Override
    public void byBlockPadded(int minLocalY, int maxLocalY, NoiseFieldVisitors.PositionVisitor visitor) {
        int startY = Math.max(0, minLocalY), endY = Math.min(chunkHeight - 1, maxLocalY);
        int index = startY * xzStride;
        for (int y = startY; y <= endY; y++) {
            for (int z = 0; z < paddedWidth; z++) {
                int bZ = z - paddingBlocks;
                for (int x = 0; x < paddedWidth; x++) {
                    int bX = x - paddingBlocks;
                    visitor.visit(index++, bX, y, bZ);
                }
            }
        }
    }
    @Override
    public void byCellPadded(int minLocalY, int maxLocalY, NoiseFieldVisitors.PositionVisitor visitor) {
        this.byBlockPadded(minLocalY, maxLocalY, visitor);
    }
    @Override
    public void visitPadded(int minLocalY, int maxLocalY, NoiseFieldVisitors.BigVisitor visitor) {
        int startY = Math.max(0, minLocalY), endY = Math.min(chunkHeight - 1, maxLocalY);
        int index = startY * xzStride;
        for (int y = startY; y <= endY; y++) {
            for (int z = 0; z < paddedWidth; z++) {
                int bZ = z - paddingBlocks;
                for (int x = 0; x < paddedWidth; x++) {
                    int bX = x - paddingBlocks;
                    visitor.visit(index++, bX, y, bZ, bX, y, bZ);
                }
            }
        }
    }
}
