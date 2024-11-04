package birsy.clinker.common.world.level.gen;

import birsy.clinker.core.Clinker;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.function.BiFunction;

// todo: cubic interpolation?
public class InterpolatedNoiseField implements NoiseFieldWithOffset {
    int posOffsetX, posOffsetY, posOffsetZ;
    final int resolutionX, resolutionY, resolutionZ;
    //final float unitToGridX, unitToGridY, unitToGridZ;
    final int sizeX, sizeY, sizeZ;
    final float[] values;

    public InterpolatedNoiseField(int sizeX, int sizeY, int sizeZ, int resolutionX, int resolutionY, int resolutionZ) {
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
        this.resolutionZ = resolutionZ;
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
        this.values = new float[this.resolutionX * this.resolutionY * this.resolutionZ];
    }

    public double localToWorldX(int localX) { return (float) localX / (resolutionX-1) * sizeX + posOffsetX; }
    public double localToWorldY(int localY) { return (float) localY / (resolutionY-1) * sizeY + posOffsetY; }
    public double localToWorldZ(int localZ) { return (float) localZ / (resolutionZ-1) * sizeZ + posOffsetZ; }

    public double worldToLocalX(double worldX) { return ((worldX - posOffsetX) / sizeX) * (resolutionX-1); }
    public double worldToLocalY(double worldY) { return ((worldY - posOffsetY) / sizeY) * (resolutionY-1); }
    public double worldToLocalZ(double worldZ) { return ((worldZ - posOffsetZ) / sizeZ) * (resolutionZ-1); }

    @Override
    public void setPosOffset(int posOffsetX, int posOffsetY, int posOffsetZ) {
        this.posOffsetX = posOffsetX;
        this.posOffsetY = posOffsetY;
        this.posOffsetZ = posOffsetZ;
    }

    public void fill(NoiseFillFunction fillFunction, Object... params) {
        for (int y = 0; y < this.resolutionY; y++) {
            for (int z = 0; z < this.resolutionZ; z++) {
                for (int x = 0; x < this.resolutionX; x++) {
                    int index = index(x, y, z);
                    values[index] = fillFunction.apply(values[index], localToWorldX(x), localToWorldY(y), localToWorldZ(z), params);
                }
            }
        }
    }

    public void fillFrom(double fromX, double fromY, double fromZ, double toX, double toY, double toZ, NoiseFillFunction fillFunction, Object... params) {
        int fX = Mth.floor(worldToLocalX(fromX)), tX = Mth.ceil(worldToLocalX(toX));
        int fY = Mth.floor(worldToLocalY(fromY)), tY = Mth.ceil(worldToLocalY(toY));
        int fZ = Mth.floor(worldToLocalZ(fromZ)), tZ = Mth.ceil(worldToLocalZ(toZ));
        this.fillFromLocal(fX, fY, fZ, tX, tY, tZ, fillFunction, params);
    }

    public void fillFromLocal(int fromX, int fromY, int fromZ, int toX, int toY, int toZ, NoiseFillFunction fillFunction, Object... params) {
        for (int y = fromX; y < toY; y++) {
            for (int z = fromY; z < toZ; z++) {
                for (int x = fromZ; x < toX; x++) {
                    double wX = localToWorldX(x),
                            wY = localToWorldY(y),
                            wZ = localToWorldZ(z);
                    int index = index(x, y, z);
                    values[index] = fillFunction.apply(values[index], wX, wY, wZ, params);
                }
            }
        }
    }


    @Override
    public float getValue(double x, double y, double z) {
        double lX = worldToLocalX(x),
               lY = worldToLocalY(y),
               lZ = worldToLocalZ(z);


        int fX = Mth.floor(lX), cX = Mth.ceil(lX);
        float dX = (float) Mth.frac(lX);

        int fY = Mth.floor(lY), cY = Mth.ceil(lY);
        float dY = (float) Mth.frac(lY);

        int fZ = Mth.floor(lZ), cZ = Mth.ceil(lZ);
        float dZ = (float) Mth.frac(lZ);

        if (fX >= resolutionX || fX < 0 || fY >= resolutionY-1 || fY < 0 || fZ >= resolutionZ || fZ < 0) return 0;
        if (cX >= resolutionX || cX < 0 || cY >= resolutionY-1 || cY < 0 || cZ >= resolutionZ || cZ < 0) return 0;

        float ooo = getValueLocal(fX, fY, fZ);
        float loo = getValueLocal(cX, fY, fZ);
        float olo = getValueLocal(fX, cY, fZ);
        float llo = getValueLocal(cX, cY, fZ);
        float ool = getValueLocal(fX, fY, cZ);
        float lol = getValueLocal(cX, fY, cZ);
        float oll = getValueLocal(fX, cY, cZ);
        float lll = getValueLocal(cX, cY, cZ);

        return (float) Mth.lerp3(
                dX, dY, dZ,
                ooo, loo,
                olo, llo,
                ool, lol,
                oll, lll
        );
    }

    private static final double EPSILON = 0.01;
    @Override
    public Vector3f getGradient(double x, double y, double z, Vector3f set) {
        return set;
//        double lX = worldToLocalX(x),
//               lY = worldToLocalY(y),
//               lZ = worldToLocalZ(z);
//
//        if (Mth.frac(lX) < EPSILON) lX = lX + 0.5 * (lX - this.resolutionX < EPSILON ? -1 : 1);
//        if (Mth.frac(lY) < EPSILON) lY = lY + 0.5 * (lY - this.resolutionY < EPSILON ? -1 : 1);
//        if (Mth.frac(lZ) < EPSILON) lZ = lZ + 0.5 * (lZ - this.resolutionZ < EPSILON ? -1 : 1);
//
//        int fX = Mth.floor(lX), cX = Mth.ceil(lX);
//        int fY = Mth.floor(lY), cY = Mth.ceil(lY);
//        int fZ = Mth.floor(lZ), cZ = Mth.ceil(lZ);
//
//        return set.set((getValueLocal(fX, fY, fZ) - getValueLocal(cX, fY, fZ)) / this.unitToGridX,
//                       (getValueLocal(fX, fY, fZ) - getValueLocal(fX, cY, fZ)) / this.unitToGridY,
//                       (getValueLocal(fX, fY, fZ) - getValueLocal(fX, fY, cZ)) / this.unitToGridZ
//        );
    }

    @Override
    public void mix(NoiseField otherField, BiFunction<Float, Float, Float> mixFunction) {
        for (int y = 0; y < this.resolutionY; y++) {
            for (int z = 0; z < this.resolutionZ; z++) {
                for (int x = 0; x < this.resolutionX; x++) {
                    int index = index(x, y, z);
                    values[index] = mixFunction.apply(values[index], otherField.getValue(localToWorldX(x), localToWorldY(y), localToWorldZ(z)));
                }
            }
        }
    }

    @Override
    public int xResolution() { return resolutionX; }
    @Override
    public int yResolution() { return resolutionY; }
    @Override
    public int zResolution() { return resolutionZ; }

    @Override
    public int offsetX() { return posOffsetX; }
    @Override
    public int offsetY() { return posOffsetY; }
    @Override
    public int offsetZ() { return posOffsetZ; }

    @Override
    public float getValueLocal(int x, int y, int z) {
        return values[index(x, y, z)];
    }
}
