package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.field.InterpolatedNoiseField;
import net.minecraft.util.Mth;

// trying to speed up interpolation by fetching cell results in advance...
public class InterpolationCache {
    final InterpolatedNoiseField field;
    final double[] array;

    // absolute cell coordinates, padding included.
    protected int cellX, cellY, cellZ;
    // corner of the current cell in local chunk space, padding not included.
    protected int cellBlockX, cellBlockY, cellBlockZ;
    // noise values
    protected double n000, n001, n010, n011, n100, n101, n110, n111;

    protected InterpolationCache(InterpolatedNoiseField field) {
        this.field = field;
        this.array = field.array();;
    }

    // todo: keep track of facX, facY, and facZ, and advance automatically
    public double sample(int x, int y, int z) {
        double facX = (double) (x - cellBlockX) / field.xzCellSize;
        double facY = (double) (y - cellBlockY) / field.yCellSize;
        double facZ = (double) (z - cellBlockZ) / field.xzCellSize;
        return Mth.lerp3(
                facX, facY, facZ,
                n000, n100, n010, n110,
                n001, n101, n011, n111
        );
    }

    // set the cell directly
    public void setCell(int cellX, int cellY, int cellZ) {
        this.cellX = cellX; this.cellY = cellY; this.cellZ = cellZ;
        fetchCellData();
    }

    // advance X only, reusing most data...
    public void advanceCellX() {
        cellX++;
        // scoot everything over
        n000 = n100;
        n001 = n101;
        n010 = n110;
        n011 = n111;

        // gather new data
        // todo: index better
        n100 = array[index(cellX + 1, cellY + 0, cellZ + 0)];
        n101 = array[index(cellX + 1, cellY + 0, cellZ + 1)];
        n110 = array[index(cellX + 1, cellY + 1, cellZ + 0)];
        n111 = array[index(cellX + 1, cellY + 1, cellZ + 1)];
    }
    // these two basically do the same thing
    public void advanceCellZ() {
        cellZ++;
        cellX = 0;
        fetchCellData();
    }
    public void advanceCellY() {
        cellY++;
        cellZ = 0;
        fetchCellData();
    }
    // fetches the data at each corner of a single cell, for interpolation.
    protected void fetchCellData() {
        n000 = array[index(cellX + 0, cellY + 0, cellZ + 0)];
        n001 = array[index(cellX + 0, cellY + 0, cellZ + 1)];
        n010 = array[index(cellX + 0, cellY + 1, cellZ + 0)];
        n011 = array[index(cellX + 0, cellY + 1, cellZ + 1)];
        n100 = array[index(cellX + 1, cellY + 0, cellZ + 0)];
        n101 = array[index(cellX + 1, cellY + 0, cellZ + 1)];
        n110 = array[index(cellX + 1, cellY + 1, cellZ + 0)];
        n111 = array[index(cellX + 1, cellY + 1, cellZ + 1)];
    }

    protected int index(int cellX, int cellY, int cellZ) {
        return cellX + cellZ * field.xzCellCount + cellY * field.xzCellStride;
    }
}
