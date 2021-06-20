package birsy.clinker.core.util;

import net.minecraft.util.math.MathHelper;

import java.util.Random;

public class MathUtils {
    /**
     * Maps one range of numbers to another. Incredibly useful function for lazy people like me.
     * @param fromMin The minimum of the range you're mapping from.
     * @param fromMax The maximum of the range you're mapping from.
     * @param toMin The minimum of the range you're mapping to.
     * @param toMax The maximum of the range you're mapping to.
     * @param value The value you're mapping.
     * @return The value, mapped to the second range.
     */
    public static float mapRange(float fromMin, float fromMax, float toMin, float toMax, float value) {
        return toMin + (((value - fromMin) * (toMax - toMin))/(fromMax - fromMin));
    }

    /**
     * Biases an input so that smaller numbers are more likely to occur.
     * @param value The input value. Should range for 0 - 1; any more or less will break it.
     * @param bias The amount of biasing applied to the function. I like to keep it around 0.5.
     * @return The input with biasing applied.
     */
    public static float bias (float value, float bias) {
        float b = (float) Math.pow(1-bias, 3);
        return (value*b)/(value*b-value+1);
    }

    /**
     * Biases an input so that smaller numbers are more likely to occur.
     * @param value The input value. Should range for 0 - 1; any more or less will break it.
     * @param bias The amount of biasing applied to the function. I like to keep it around 0.5.
     * @return The input with biasing applied.
     */
    public static double bias (double value, double bias) {
        double b = Math.pow(1-bias, 3);
        return (value*b)/(value*b-value+1);
    }

    /**
     * Determines if a value is within range of another.
     * @param value The value you want to check the closeness of.
     * @param input The value you want to check it against.
     * @param margin The range to check within.
     * @return If the value is within range of the input.
     */
    public static boolean within (float value, float input, float margin) {
        float difference = Math.abs(value - input);
        return difference <= Math.abs(margin);
    }

    /**
     * Get a random float between two numbers. Rather self-explanatory.
     * @param rand The RNG used to determine the float.
     * @param min The minimum value of the random number.
     * @param max The maximum value of the random number.
     * @return A random float between the minimum and the maximum.
     */
    public static float getRandomFloatBetween (Random rand, float min, float max) {
        return mapRange(0, 1, min, max, rand.nextFloat());
    }

    /**
     * Applies a series of distinct stairs (terraces) to a number. Very useful for terrain.
     * @param height A float between 0 and 1. The input height that you want to be terraced.
     * @param width A float between 0 and 1. The width of each "step" on the terrace.
     * @param erosion A float between 0 and 1. The factor of influence for the terrace.
     * @return The inputted height with the terraced step effect applied.
     */
    public static float terrace (float height, float width, float erosion) {
        float terraceWidth = width * 0.5f;
        if (terraceWidth == 0) {
            terraceWidth += 0.0001f;
        }

        float k = (float) Math.floor(height / terraceWidth);
        float f = (height - k * terraceWidth) / terraceWidth;
        float s = Math.min(2.0f * f, 1.0f);
        return MathHelper.lerp(erosion,(k + s) * terraceWidth, height);
    }

    public static float minMaxSin (float value, float min, float max) {
        return (((MathHelper.sin(value) + 1) * 0.5F) * (max - min)) + min;
    }

    public static float smoothMin (float value1, float value2, float smoothness) {
        float h = Math.max(smoothness - Math.abs(value1-value2), 0) / smoothness;
        return Math.min(value1, value2) - h * h * h * smoothness * 1 / 6.0F;
    }
}
