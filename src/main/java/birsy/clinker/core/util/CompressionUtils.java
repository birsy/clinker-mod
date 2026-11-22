package birsy.clinker.core.util;

public class CompressionUtils {
    public static final double
            COMPRESSION_SCALE_MAX_1 = 32768.0,
            COMPRESSION_SCALE_MAX_2 = 16384.0,
            COMPRESSION_SCALE_MAX_8 = 4096.0;

    public static short writeCompressed(double num, double scale) {
        return (short) Math.round(num * scale);
    }
    public static double readCompressed(short num, double scale) {
        return num / scale;
    }
}
