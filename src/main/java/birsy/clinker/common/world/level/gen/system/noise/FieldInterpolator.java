package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.field.NoiseField;
import net.minecraft.util.Mth;

// trying to speed up sequential interpolation by fetching cell data in advance
public class FieldInterpolator {
    final NoiseField srcField;
    final boolean source2d;
    final double[] srcArray;
    final int srcCellSizeXZ, srcCellSizeY;

    final NoiseField dstField;
    final int dstCellSizeXZ, dstCellSizeY;
    final double facAddendXZ, facAddendY;
    // yzx bit order - as in, y is the most significant bit, and x the least. damn you arabic numerals.
    // sample indexing offsets. this is static for every cell scale but w/e...
    protected final int[] indexOffset = new int[8];
    // noise values in the current cell
    protected final double[] data = new double[8];

    // info about the current cell in the source array
    // absolute cell coordinates, padding included.
    protected int srcCellX, srcCellY, srcCellZ;
    // corner of the current cell in local chunk space, padding not included.
    protected int srcCellBlockX, srcCellBlockY, srcCellBlockZ;

    // info about the current cell in the destination array
    // see above
    protected int dstCellX, dstCellY, dstCellZ;
    protected int dstCellBlockX, dstCellBlockY, dstCellBlockZ;
    // interpolation factors when tri-lerping
    protected double facX, facY, facZ;

    protected FieldInterpolator(NoiseField sourceField, NoiseField destinationField) {
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

        this.source2d = sourceField.yCellCount <= 1;
        int xStride = 1, yStride = srcField.xzCellStride, zStride = srcField.xzCellCount;
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
            srcCellBlockY = srcCellY << srcField.yCellScale;
            facY = (double) (dstCellBlockY % srcCellSizeY) / srcCellSizeY;
        }

        // reset xz
        srcCellX = 0; srcCellZ = 0;
        srcCellBlockX = -srcField.paddingBlocks; srcCellBlockZ = -srcField.paddingBlocks;
        dstCellX = 0; dstCellZ = 0;
        dstCellBlockX = -dstField.paddingBlocks; dstCellBlockZ = -dstField.paddingBlocks;
        facX = 0; facZ = 0;

        fetchCellData();
    }

    // moves the destination cell one forward.
    public void advanceX() {
        dstCellX++;
        dstCellBlockX += dstCellSizeXZ;
        facX += facAddendXZ;
        // new source cell
        if (facX >= 1) {
            facX -= 1;
            srcCellX++;
            srcCellBlockX += srcCellSizeXZ;
            // scoot everything over
            int startIndex = srcCellX + srcCellZ * srcField.xzCellCount + srcCellY * srcField.xzCellStride;
            for (int i = 0; i < 8; i += 2) {
                data[i] = data[i + 1];
                data[i + 1] = srcArray[startIndex + indexOffset[i + 1]];
            }
        }
    }
    // we index in x -> z -> y order, so these scoot the previous axis back over.
    // so, advancing z implicitly sets x back to zero, and advancing y sets x and z to zero.
    // moves the destination cell to a new row
    public void advanceZ() {
        // reset x
        dstCellX = 0;
        dstCellBlockX = -dstField.paddingBlocks;
        facX = 0;
        srcCellX = 0;
        srcCellBlockX = -srcField.paddingBlocks;

        dstCellZ++;
        dstCellBlockZ += dstCellSizeXZ;
        facZ += facAddendXZ;
        // new source cell
        if (facZ >= 1) {
            facZ -= 1;
            srcCellZ++;
            srcCellBlockZ += srcCellSizeXZ;
        }
        fetchCellData();
    }
    // moves the destination cell to a new xz layer
    public void advanceY() {
        // reset z
        dstCellZ = 0;
        dstCellBlockZ = -dstField.paddingBlocks;
        facZ = 0;
        srcCellZ = 0;
        srcCellBlockZ = -srcField.paddingBlocks;

        dstCellY++;
        dstCellBlockY += dstCellSizeY;
        facY += facAddendY;
        // new source cell
        if (facY >= 1 && !source2d) {
            facY -= 1;
            srcCellY++;
            srcCellBlockY += srcCellSizeY;
        }
        fetchCellData();
    }

    // fetches the data at each corner of a single cell for interpolation.
    protected void fetchCellData() {
        int startIndex = srcCellX + srcCellZ * srcField.xzCellCount + srcCellY * srcField.xzCellStride;
        for (int i = 0; i < indexOffset.length; i++) data[i] = srcArray[startIndex + indexOffset[i]];
    }
}