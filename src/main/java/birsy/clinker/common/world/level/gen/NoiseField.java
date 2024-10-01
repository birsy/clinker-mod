package birsy.clinker.common.world.level.gen;

import org.joml.Vector3f;

import java.util.function.BiFunction;

public interface NoiseField {
    void fill(NoiseFillFunction fillFunction, Object... params);

    void fillFrom(double fromX, double fromY, double fromZ, double toX, double toY, double toZ, NoiseFillFunction fillFunction, Object... params);

    float getValue(double x, double y, double z);

    Vector3f getGradient(double x, double y, double z, Vector3f set);

    void mix(NoiseField otherField, BiFunction<Float, Float, Float> mixFunction);

    interface NoiseFillFunction {
        float apply(float currentValue, double x, double y, double z, Object... params);
    }
}
