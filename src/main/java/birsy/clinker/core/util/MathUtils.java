package birsy.clinker.core.util;

public class MathUtils {
    public static float mapRange(float a1, float a2, float b1, float b2, float s) {
        return  b1+(((s-a1) * (b2-b1))/(a2-a1));
    }

}
