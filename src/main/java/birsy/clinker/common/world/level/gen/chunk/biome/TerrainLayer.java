package birsy.clinker.common.world.level.gen.chunk.biome;

import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Iterator;

public class TerrainLayer implements Iterable<TerrainLayer.Gexel> {
    final ChunkAccess chunk;
    final int horizontalResolution;
    final int verticalResolution;

    final float[] values;
    final Gexel[] gexels;

    public TerrainLayer(ChunkAccess chunk, int horizontalResolution, int verticalResolution) {
        this.chunk = chunk;

        this.horizontalResolution = horizontalResolution;
        this.verticalResolution = verticalResolution;

        this.values = new float[this.horizontalResolution * this.horizontalResolution * this.verticalResolution];
        this.gexels = new Gexel[this.horizontalResolution * this.horizontalResolution * this.verticalResolution];
        for (int x = 0; x < this.horizontalResolution; x++) {
            for (int z = 0; z < this.horizontalResolution; z++) {
                for (int y = 0; y < this.verticalResolution; y++) {
                    int index = index(x, y, z);
                    this.gexels[index] = new Gexel(
                            index,
                            ((float)x / (float)this.horizontalResolution) * 16 + chunk.getPos().getMinBlockX(),
                            ((float)y / (float)this.verticalResolution)   * chunk.getHeight() + chunk.getMinBuildHeight(),
                            ((float)z / (float)this.horizontalResolution) * 16 + chunk.getPos().getMinBlockZ()
                    );
                }
            }
        }
    }

    // interpolates gexel values into world space
    public float interpolate(double worldspaceX, double worldspaceY, double worldspaceZ) {
        // position in local space
        double x = worldspaceX - this.chunk.getPos().getMinBlockX();
        double y = worldspaceY - this.chunk.getMinBuildHeight();
        double z = worldspaceZ - this.chunk.getPos().getMinBlockZ();

        // find its position within the grid
        x = (x / 15.0F) * this.horizontalResolution;
        y = (y / this.chunk.getHeight()) * this.verticalResolution;
        z = (z / 15.0F) * this.horizontalResolution;

        // split into fractional and whole components
        int ix = Mth.floor(x);
        x = Mth.frac(x);
        int iy = Mth.floor(y);
        y = Mth.frac(y);
        int iz = Mth.floor(z);
        z = Mth.frac(z);

        // perform a trilinear interpolation on our data points
        return (float) Mth.lerp(z, Mth.lerp(y, Mth.lerp(x, this.values[index(ix + 0, iy + 0, iz + 0)], this.values[index(ix + 1, iy + 0, iz + 0)]),
                                               Mth.lerp(x, this.values[index(ix + 0, iy + 1, iz + 0)], this.values[index(ix + 1, iy + 1, iz + 0)])),
                                   Mth.lerp(y, Mth.lerp(x, this.values[index(ix + 0, iy + 0, iz + 1)], this.values[index(ix + 1, iy + 0, iz + 1)]),
                                               Mth.lerp(x, this.values[index(ix + 0, iy + 1, iz + 1)], this.values[index(ix + 1, iy + 1, iz + 1)])));
    }

    protected int index(int x, int y, int z) {
        return x +
              (z * this.horizontalResolution) +
              (y * this.horizontalResolution * this.horizontalResolution);
    }

    @NotNull
    @Override
    public Iterator<Gexel> iterator() {
        return Arrays.stream(this.gexels).iterator();
    }

    // "generation element"
    public class Gexel {
        private final int index;
        public final double x;
        public final double y;
        public final double z;

        private Gexel(int index, double wsX, double wsY, double wsZ) {
            this.index = index;

            this.x = wsX;
            this.y = wsY;
            this.z = wsZ;
        }

        public void set(float value) {
            values[this.index] = value;
        }
        public float get() {
            return values[this.index];
        }
    }
}
