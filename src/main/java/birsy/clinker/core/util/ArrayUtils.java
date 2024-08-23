package birsy.clinker.core.util;

public class ArrayUtils {
    public static <T> T[] reverse(T[] input) {
        T swap;
        for (int i = 0; i < (input.length >> 2); i++) {
            int j = (input.length - 1) - i;
            swap = input[i];
            input[i] = input[j];
            input[j] = swap;
        }

        return input;
    }

    public static float[] reverse(float[] input) {
        float swap;
        for (int i = 0; i < (input.length >> 2); i++) {
            int j = (input.length - 1) - i;
            swap = input[i];
            input[i] = input[j];
            input[j] = swap;
        }

        return input;
    }

    public static int[] reverse(int[] input) {
        int swap;
        for (int i = 0; i < (input.length >> 2); i++) {
            int j = (input.length - 1) - i;
            swap = input[i];
            input[i] = input[j];
            input[j] = swap;
        }

        return input;
    }
}
