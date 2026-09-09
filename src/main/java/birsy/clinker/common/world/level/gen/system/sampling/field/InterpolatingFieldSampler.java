package birsy.clinker.common.world.level.gen.system.sampling.field;

import birsy.clinker.core.Clinker;
import net.minecraft.util.Mth;

// trying to speed up sequential interpolation by fetching cell data in advance
public interface InterpolatingFieldSampler {
    double sample();
    void setSlice(int sliceY);
    void advanceX();
    void advanceY();
    void advanceZ();

    static InterpolatingFieldSampler create(InterpolatingField source, InterpolatingField destination) {
        if (source.xzCellSize == destination.xzCellSize
            && source.yCellSize  == destination.yCellSize
            && source.paddingBlocks == destination.paddingBlocks) {
            return new SameResolutionSampler(source);
        }
        return new MismatchedResolutionSampler(source, destination);
    }

    // if the two fields are the same resolution, we can skip all the
    // weird fancy interpolation magic and just sample directly.
    class SameResolutionSampler implements InterpolatingFieldSampler {
        private final double[] srcArray;
        private final int sliceCellCount, xzCellCount;
        private int index, sliceStart;
        private int cellZ, cellY;

        public SameResolutionSampler(InterpolatingField field) {
            this.srcArray = field.array();
            this.sliceCellCount = field.sliceCellCount;
            this.xzCellCount = field.xzCellCount;
        }

        @Override
        public double sample() {
            return srcArray[index];
        }

        @Override
        public void setSlice(int cellY) {
            this.cellY = cellY;
            this.sliceStart = cellY * sliceCellCount;
            this.cellZ = 0;
            this.index = sliceStart;
        }

        @Override
        public void advanceX() {
            index++;
        }
        @Override
        public void advanceZ() {
            cellZ++;
            index = sliceStart + cellZ * xzCellCount;
        }
        @Override
        public void advanceY() {
            cellY++;
            sliceStart = cellY * sliceCellCount;
            cellZ = 0;
            index = sliceStart;
        }
    }

    // the aforementioned weird fancy interpolation magic
    class MismatchedResolutionSampler implements InterpolatingFieldSampler {
        public final InterpolatingField srcField;
        final boolean source2d;
        final double[] srcArray;
        final int srcCellSizeXZ, srcCellSizeY;

        public final InterpolatingField dstField;
        final int dstCellSizeXZ, dstCellSizeY;
        final double facAddendXZ, facAddendY;
        // yzx bit order - as in, y is the most significant bit, and x the least. damn you arabic numerals.
        // sample indexing offsets. this is static for every cell scale but w/e...
        protected final int[] indexOffset = new int[8];
        // noise values in the current cell
        protected final double[] data = new double[8];

        // info about the current cell in the source array
        // absolute cell coordinates, padding included.
        public int srcCellX, srcCellY, srcCellZ;
        // corner of the current cell in local chunk space, padding not included.
        public int srcCellBlockX, srcCellBlockY, srcCellBlockZ;

        // info about the current cell in the destination array
        // see above
        public int dstCellX, dstCellY, dstCellZ;
        public int dstCellBlockX, dstCellBlockY, dstCellBlockZ;
        // interpolation factors when tri-lerping
        protected double facX, facY, facZ;

        public MismatchedResolutionSampler(InterpolatingField sourceField, InterpolatingField destinationField) {
            // the math here only really works out when the scales are correct. So, make sure that's always true.
            assert sourceField.xzCellSize >= destinationField.xzCellSize && sourceField.yCellSize >= destinationField.yCellSize;

            this.srcField = sourceField;
            this.srcArray = sourceField.array();
            this.srcCellSizeXZ = sourceField.xzCellSize;
            this.srcCellSizeY = sourceField.yCellSize;

            this.dstField = destinationField;
            this.dstCellSizeXZ = destinationField.xzCellSize;
            this.dstCellSizeY = destinationField.yCellSize;

            this.facAddendXZ = (double) dstCellSizeXZ / srcCellSizeXZ;
            this.facAddendY = (double) dstCellSizeY / srcCellSizeY;

            this.source2d = sourceField instanceof InterpolatingField2d;
            boolean destination2d = destinationField instanceof InterpolatingField2d;
            // we can only go from 2d -> 3d, never from 3d -> 2d
            assert ((source2d == destination2d) || source2d);

            int xStride = 1, yStride = srcField.sliceCellCount, zStride = srcField.xzCellCount;
            for (int i = 0; i < indexOffset.length; i++) {
                int xOffset = (i & 0b001) > 0 ? xStride : 0,
                        zOffset = (i & 0b010) > 0 ? zStride : 0;
                // if the source is 2d, there should never be any y offsets.
                int yOffset = source2d ? 0 : (i & 0b100) > 0 ? yStride : 0;
                indexOffset[i] = xOffset + zOffset + yOffset;
            }
        }

        // tri-lerp
        public double sample() {
            double x0 = Mth.lerp(facX, data[0b000], data[0b001]),
                   x1 = Mth.lerp(facX, data[0b010], data[0b011]),
                   x2 = Mth.lerp(facX, data[0b100], data[0b101]),
                   x3 = Mth.lerp(facX, data[0b110], data[0b111]);
            double z0 = Mth.lerp(facZ, x0, x1),
                   z1 = Mth.lerp(facZ, x2, x3);
            return Mth.lerp(facY, z0, z1);
        }

        // sets the xz slice directly
        public void setSlice(int destinationCellY) {
            if (!source2d) {
                // set y
                dstCellY = destinationCellY;
                dstCellBlockY = dstCellY << dstField.yCellScale;

                // which source y cell contains this destination cell?
                srcCellY = dstCellBlockY >> srcField.yCellScale;
                if (srcCellY >= srcField.yCellCount - 1) {
                    srcCellY = srcField.yCellCount - 2;
                    srcCellBlockY = srcCellY << srcField.yCellScale;
                    facY = 1.0;
                } else {
                    srcCellBlockY = srcCellY << srcField.yCellScale;
                    facY = (double) (dstCellBlockY % srcCellSizeY) / srcCellSizeY;
                }
            }

            resetX(); resetZ();
            fetchCellData();
        }

        // moves the destination cell one forward.
        public void advanceX() {
            dstCellX++;
            dstCellBlockX += dstCellSizeXZ;
            facX += facAddendXZ;
            // new source cell
            if (facX >= 1 && srcCellX < srcField.xzCellCount - 2) {
                facX -= 1;
                srcCellX++;
                srcCellBlockX += srcCellSizeXZ;
                // scoot everything over
                int startIndex = srcCellX + srcCellZ * srcField.xzCellCount + srcCellY * srcField.sliceCellCount;
                for (int i = 0; i < 8; i += 2) {
                    data[i] = data[i + 1];
                    data[i + 1] = srcArray[startIndex + indexOffset[i + 1]];
                }
            }
        }
        private void resetX() {
            dstCellX = 0;
            dstCellBlockX = -dstField.paddingBlocks;
            facX = 0;
            srcCellX = 0;
            srcCellBlockX = -srcField.paddingBlocks;
        }

        // we index in x -> z -> y order, so these scoot the previous axis back over.
        // so, advancing z implicitly sets x back to zero, and advancing y sets x and z to zero.
        // moves the destination cell to a new row
        public void advanceZ() {
            resetX();

            dstCellZ++;
            dstCellBlockZ += dstCellSizeXZ;
            facZ += facAddendXZ;
            // new source cell
            if (facZ >= 1 && srcCellZ < srcField.xzCellCount - 2) {
                facZ -= 1;
                srcCellZ++;
                srcCellBlockZ += srcCellSizeXZ;
            }
            fetchCellData();
        }
        private void resetZ() {
            dstCellZ = 0;
            dstCellBlockZ = -dstField.paddingBlocks;
            facZ = 0;
            srcCellZ = 0;
            srcCellBlockZ = -srcField.paddingBlocks;
        }

        // moves the destination cell to a new xz layer
        public void advanceY() {
            resetX(); resetZ();

            dstCellY++;
            dstCellBlockY += dstCellSizeY;
            facY += facAddendY;
            // new source cell
            if (facY >= 1 && srcCellY < srcField.yCellCount - 2 && !source2d) {
                facY -= 1;
                srcCellY++;
                srcCellBlockY += srcCellSizeY;
            }
            fetchCellData();
        }

        // fetches the data at each corner of a single cell for interpolation.
        protected void fetchCellData() {
            int startIndex = srcCellX + srcCellZ * srcField.xzCellCount + srcCellY * srcField.sliceCellCount;
            for (int i = 0; i < indexOffset.length; i++) data[i] = srcArray[startIndex + indexOffset[i]];
        }
    }
}