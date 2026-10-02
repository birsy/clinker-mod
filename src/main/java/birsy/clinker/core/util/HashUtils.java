package birsy.clinker.core.util;

public class HashUtils {
    public static int hash(long seed, int x, int y, int z) {
        int h = Long.hashCode(seed);
        h ^= x * 0x1bf52cf3;
        h ^= y * 0x2b415a74;
        h ^= z * 0x4f3d1b82;
        h *= 0x85ebca6b;
        h ^= h >>> 15;
        h *= 0xc2b2ae35;
        h ^= h >>> 13;
        return h;
    }

    // converts some random integer into a unit float using evil bit hacks
    public static float bitsToUnitFloat(int h) {
        int floatBits = (h & 0x7FFFFF) | 0x40000000;
        return Float.intBitsToFloat(floatBits) - 3.0f;
    }

    // converts some *portion* of a random integer into a unit float using more evil bit hacks
    public static float bandToUnitFloat(int h, int shift, int bits) {
        int mask = (1 << bits) - 1;
        int v = (h >>> shift) & mask;
        return v * (2f / mask) - 1f;
    }

    // returns -1.0 to 1.0
    public static float hash3d(long seed, int x, int y, int z) {
        int h = (int) seed;
        int k = (int) (seed >>> 32);

        h ^= x * 0x1bf52cf3;
        k ^= y * 0x2b415a74;
        h ^= z * 0x4f3d1b82;

        h ^= k;
        h = (h ^ (h >>> 16)) * 0x85ebca6b;
        h = (h ^ (h >>> 13)) * 0xc2b2ae35;
        h ^= h >>> 16;

        int floatBits = (h & 0x7FFFFF) | 0x40000000;
        return Float.intBitsToFloat(floatBits) - 3.0f;
    }

    // returns -1.0 to 1.0
    public static float hash2d(long seed, int x, int y) {
        int h = (int) seed;
        int k = (int) (seed >>> 32);

        h ^= x * 0x1bf52cf3;
        k ^= y * 0x2b415a74;

        h ^= k;
        h = (h ^ (h >>> 16)) * 0x85ebca6b;
        h = (h ^ (h >>> 13)) * 0xc2b2ae35;
        h ^= h >>> 16;

        int floatBits = (h & 0x7FFFFF) | 0x40000000;
        return Float.intBitsToFloat(floatBits) - 3.0f;
    }
}
