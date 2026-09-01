package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.field.InterpolatedNoiseField;
import net.minecraft.util.Mth;

// trying to speed up sequential interpolation by fetching cell data in advance...
public class InterpolationCache {
    final InterpolatedNoiseField srcField;
    final double[] srcArray;
    final int srcSizeXZ, srcSizeY;
    final int dstSizeXZ, dstSizeY;
    final double facAddendXZ, facAddendY;
    // yzx bit order
    // sample indexing offsets
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
    // interpolation factors when trilerping
    protected double facX, facY, facZ;

    protected InterpolationCache(InterpolatedNoiseField sourceField, InterpolatedNoiseField destinationField) {
        // the math here only really works out when the scales are correct. So, make sure that's always true.
        assert sourceField.xzCellSize >= destinationField.xzCellSize && sourceField.yCellSize >= destinationField.yCellSize;

        this.srcField = sourceField;
        this.srcArray = sourceField.array();
        this.srcSizeXZ = sourceField.xzCellSize;
        this.srcSizeY = sourceField.yCellSize;

        this.dstSizeXZ = destinationField.xzCellSize;
        this.dstSizeY = destinationField.yCellSize;

        this.facAddendXZ = (double) dstSizeXZ / srcSizeXZ;
        this.facAddendY = (double) dstSizeY / srcSizeY;

        int xStride = 1, yStride = srcField.xzCellStride, zStride = srcField.xzCellCount;
        for (int i = 0; i < indexOffset.length; i++) {
            int xOffset = (i & 0b001) > 0 ? xStride : 0,
                yOffset = (i & 0b010) > 0 ? yStride : 0,
                zOffset = (i & 0b100) > 0 ? zStride : 0;
            indexOffset[i] = xOffset + yOffset + zOffset;
        }
    }

    public double sample() {
        return Mth.lerp3(facX, facY, facZ,
                data[0b000], data[0b100], data[0b010], data[0b110],
                data[0b001], data[0b101], data[0b011], data[0b111]
        );
    }

    public void advanceX() {
        dstCellX++;
        dstCellBlockX += dstSizeXZ;
        facX += facAddendXZ;
        if (facX >= 1) {
            facX -= 1;
            advanceCellX();
        }
    }
    // we index in x -> z -> y order, so these scoot the previous axis back over.
    // so, advancing z implicitly sets x back to zero, and advancing y sets x and z to zero.
    public void advanceZ() {
        dstCellX = 0;
        facX = 0;

        dstCellZ++;
        dstCellBlockZ += dstSizeXZ;
        facZ += facAddendXZ;
        if (facZ >= 1) {
            facZ -= 1;
            advanceCellZ();
        }
    }
    public void advanceY() {
        dstCellZ = 0;
        facZ = 0;

        dstCellY++;
        dstCellBlockY += dstSizeY;
        facY += facAddendY;
        if (facY >= 1) {
            facY -= 1;
            advanceCellY();
        }
    }

    // advance X only, reusing most data...
    public void advanceCellX() {
        srcCellX++;
        srcCellBlockX += srcSizeXZ;

        // scoot everything over
        int startIndex = srcCellX + srcCellZ * srcField.xzCellCount + srcCellY * srcField.xzCellStride;
        for (int i = 0; i < 8; i += 2) {
            data[i] = data[i + 1];
            data[i + 1] = srcArray[startIndex + indexOffset[i + 1]];
        }
    }
    // these two basically do the same thing
    // new column, can't reuse any data because of iteration order. see advanceZ's comment
    public void advanceCellZ() {
        srcCellX = 0;

        srcCellZ++;
        srcCellZ += srcSizeXZ;
        fetchCellData();
    }
    // new layer, can't reuse any data because of iteration order. see advanceZ's comment
    public void advanceCellY() {
        srcCellZ = 0;

        srcCellY++;
        srcCellY += srcSizeY;
        fetchCellData();
    }

    // fetches the data at each corner of a single cell for interpolation.
    protected void fetchCellData() {
        int startIndex = srcCellX + srcCellZ * srcField.xzCellCount + srcCellY * srcField.xzCellStride;
        for (int i = 0; i < indexOffset.length; i++) data[i] = srcArray[startIndex + indexOffset[i]];
    }
}