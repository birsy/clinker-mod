package birsy.clinker.common.world.level.gen;

import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.function.BiFunction;

public class BasicNoiseField implements NoiseFieldWithOffset {
    int posOffsetX, posOffsetY, posOffsetZ;
    final int sizeX, sizeY, sizeZ;
    final float[] values;

    public BasicNoiseField(int sizeX, int sizeY, int sizeZ) {
        this.values = new float[(sizeY + 1) * sizeX * sizeZ];
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    protected int getIndex(int x, int y, int z) {
        return y * (sizeX * sizeZ) + z * (sizeX) + x;
    }

    @Override
    public void setPosOffset(int posOffsetX, int posOffsetY, int posOffsetZ) {
        this.posOffsetX = posOffsetX;
        this.posOffsetY = posOffsetY;
        this.posOffsetZ = posOffsetZ;
    }

    @Override
    public void fill(NoiseFillFunction fillFunction, Object... params) {
        for (int y = 0; y < this.sizeY; y++) {
            for (int z = 0; z < this.sizeZ; z++) {
                for (int x = 0; x < this.sizeX; x++) {
                    int i = getIndex(x, y, z);
                    values[i] = fillFunction.apply(values[i], x + this.posOffsetX, y + this.posOffsetY, z + this.posOffsetZ, params);
                }
            }
        }
    }

    @Override
    public void fillFrom(double fromX, double fromY, double fromZ, double toX, double toY, double toZ, NoiseFillFunction fillFunction, Object... params) {
        int fX = Mth.floor(fromX) - this.posOffsetX, tX = Math.min(Mth.ceil(toX) - this.posOffsetX, this.sizeX);
        int fY = Mth.floor(fromY) - this.posOffsetY, tY = Math.min(Mth.ceil(toY) - this.posOffsetY, this.sizeY);
        int fZ = Mth.floor(fromZ) - this.posOffsetZ, tZ = Math.min(Mth.ceil(toZ) - this.posOffsetZ, this.sizeZ);
        this.fillFromLocal(fX, fY, fZ, tX, tY, tZ, fillFunction, params);
    }

    @Override
    public void fillFromLocal(int fromX, int fromY, int fromZ, int toX, int toY, int toZ, NoiseFillFunction fillFunction, Object... params) {
        for (int y = fromX; y < toY; y++) {
            for (int z = fromY; z < toZ; z++) {
                for (int x = fromZ; x < toX; x++) {
                    int i = getIndex(x, y, z);
                    values[i] = fillFunction.apply(values[i], x + this.posOffsetX, y + this.posOffsetY, z + this.posOffsetZ, params);
                }
            }
        }
    }

    @Override
    public float getValue(double x, double y, double z) {
        int lX = Mth.clamp(Mth.floor(x) - this.posOffsetX, 0, this.sizeX),
            lY = Mth.clamp(Mth.floor(y) - this.posOffsetY, 0, this.sizeY),
            lZ = Mth.clamp(Mth.floor(z) - this.posOffsetZ, 0, this.sizeZ);
        return getValueLocal(lX, lY, lZ);
    }

    private static final double EPSILON = 0.01;
    @Override
    public Vector3f getGradient(double x, double y, double z, Vector3f set) {
        double lX = x - this.posOffsetX,
               lY = y - this.posOffsetY,
               lZ = z - this.posOffsetZ;

        if (Mth.frac(lX) < EPSILON) lX = lX + 0.5 * (lX - sizeX < EPSILON ? -1 : 1);
        if (Mth.frac(lY) < EPSILON) lY = lY + 0.5 * (lY - sizeY < EPSILON ? -1 : 1);
        if (Mth.frac(lZ) < EPSILON) lZ = lZ + 0.5 * (lZ - sizeX < EPSILON ? -1 : 1);

        int fX = Mth.floor(lX), cX = Mth.ceil(lX);
        int fY = Mth.floor(lY), cY = Mth.ceil(lY);
        int fZ = Mth.floor(lZ), cZ = Mth.ceil(lZ);

        return set.set((getValueLocal(fX, fY, fZ) - getValueLocal(cX, fY, fZ)),
                       (getValueLocal(fX, fY, fZ) - getValueLocal(fX, cY, fZ)),
                       (getValueLocal(fX, fY, fZ) - getValueLocal(fX, fY, cZ))
        );
    }

    @Override
    public void mix(NoiseField otherField, BiFunction<Float, Float, Float> mixFunction) {
        for (int y = 0; y < this.sizeY; y++) {
            for (int z = 0; z < this.sizeZ; z++) {
                for (int x = 0; x < this.sizeX; x++) {
                    int i = getIndex(x, y, z);
                    values[i] = mixFunction.apply(values[i], otherField.getValue(x + this.posOffsetX, y + this.posOffsetY, z + this.posOffsetZ));
                }
            }
        }
    }

    @Override
    public float getValueLocal(int x, int y, int z) {
        return values[getIndex(x, y, z)];
    }
}
