package birsy.clinker.core.util;

import com.ibm.icu.impl.Pair;
import net.minecraft.util.Mth;

import java.lang.reflect.Array;
import java.util.Arrays;
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
    public static Pair<Float, Boolean> terrace (float height, float width, float erosion, float terraceThreshold) {
        float terraceWidth = width * 0.5f;
        if (terraceWidth == 0) {
            terraceWidth += 0.0001f;
        }

        float k = (float) Math.floor(height / terraceWidth);
        float f = (height - k * terraceWidth) / terraceWidth;
        float s = Math.min(2.0f * f, 1.0f);

        float secondTerraceThreshold = Math.abs(terraceThreshold - 1);
        boolean isTerrace = s >= terraceThreshold || s <= secondTerraceThreshold;

        return Pair.of(Mth.lerp(erosion,(k + s) * terraceWidth, height), isTerrace);
    }

    public static float minMaxSin (float value, float min, float max) {
        return (((Mth.sin(value) + 1) * 0.5F) * (max - min)) + min;
    }

    public static float smoothMin (float value1, float value2, float smoothness) {
        float h = Math.max(smoothness - Math.abs(value1-value2), 0) / smoothness;
        return Math.min(value1, value2) - h * h * h * smoothness * 1 / 6.0F;
    }

    public static int[] getValidIndexes(Object array, int... excludedIndexes) {
        try {
            int[] retArray = new int[Array.getLength(array) - excludedIndexes.length];
            int index = 0;
            for (int i = 0; i < Array.getLength(array); i++) {
                if (!Arrays.asList(excludedIndexes).contains(i)) {
                    retArray[index] = i;
                    index++;
                }
            }

            return retArray;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    //Refer to https://easings.net/.
    //X should usually be between zero and one, but it doesn't need to be.
    public static float ease(float x, EasingType easingType) {
        return easingType.ease(x);
    }

    public enum EasingType implements IEasingFunction {
        linear {
            public float ease(float x) { return x;}
        },
        easeInQuad {
            public float ease(float x) {
                return x * x;
            }
        },
        easeOutQuad {
            public float ease(float x) {
                return 1 - (1 - x) * (1 - x);
            }
        },
        easeInOutQuad {
            public float ease(float x) {
                return x < 0.5 ? 2 * x * x : (float) (1 - Math.pow(-2 * x + 2, 2) / 2);
            }
        },
        easeInCubic {
            public float ease(float x) {
                return x * x * x;
            }
        },
        easeOutCubic {
            public float ease(float x) {
                return (float) (1 - Math.pow(1 - x, 3));
            }
        },
        easeInOutCubic {
            public float ease(float x) {
                return x < 0.5 ? 4 * x * x * x : (float) (1 - Math.pow(-2 * x + 2, 3) / 2);
            }
        },
        easeInQuart {
            public float ease(float x) {
                return x * x * x * x;
            }
        },
        easeOutQuart {
            public float ease(float x) {
                return (float) (1 - Math.pow(1 - x, 4));
            }
        },
        easeInOutQuart {
            public float ease(float x) {
                return x < 0.5 ? 8 * x * x * x * x : (float) (1 - Math.pow(-2 * x + 2, 4) / 2);
            }
        },
        easeInQuint {
            public float ease(float x) {
                return x * x * x * x * x;
            }
        },
        easeOutQuint {
            public float ease(float x) {
                return (float) (1 - Math.pow(1 - x, 5));
            }
        },
        easeInOutQuint {
            public float ease(float x) {
                return x < 0.5 ? 16 * x * x * x * x * x : (float) (1 - Math.pow(-2 * x + 2, 5) / 2);
            }
        },
        easeInSine {
            public float ease(float x) {
                return 1 - Mth.cos((float) ((x * Math.PI) / 2));
            }
        },
        easeOutSine {
            public float ease(float x) {
                return Mth.sin((float) ((x * Math.PI) / 2));
            }
        },
        easeInOutSine {
            public float ease(float x) {
                return -(Mth.cos((float) (Math.PI * x)) - 1) / 2;
            }
        },
        easeInExpo {
            public float ease(float x) {
                return x == 0 ? 0 : (float) Math.pow(2, 10 * x - 10);
            }
        },
        easeOutExpo {
            public float ease(float x) {
                return x == 1 ? 1 : (float) (1 - Math.pow(2, -10 * x));
            }
        },
        easeInOutExpo {
            public float ease(float x) {
                return x == 0
                        ? 0
                        : (float) (x == 1
                        ? 1
                        : x < 0.5
                        ? Math.pow(2, 20 * x - 10) / 2
                        : (2 - Math.pow(2, -20 * x + 10)) / 2);
            }
        },
        easeInCirc {
            public float ease(float x) {
                return (float) (1 - Math.sqrt(1 - Math.pow(x, 2)));
            }
        },
        easeOutCirc {
            public float ease(float x) {
                return (float) Math.sqrt(1 - Math.pow(x - 1, 2));
            }
        },
        easeInOutCirc {
            public float ease(float x) {
                return (float) (x < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2);
            }
        },
        easeInBack {
            public float ease(float x) {
                return 2.70158F * x * x * x - 1.70158F * x * x;
            }
        },
        easeOutBack {
            public float ease(float x) {
                return (float) (1 + 2.70158F * Math.pow(x - 1, 3) + 1.70158F * Math.pow(x - 1, 2));
            }
        },
        easeInOutBack {
            public float ease(float x) {
                return (float) (x < 0.5
                        ? (Math.pow(2 * x, 2) * ((2.5949095F + 1) * 2 * x - 2.5949095F)) / 2
                        : (Math.pow(2 * x - 2, 2) * ((2.5949095F + 1) * (x * 2 - 2) + 2.5949095F) + 2) / 2);
            }
        },
        easeInElastic {
            public float ease(float x) {
                return x == 0
                        ? 0
                        : (float) (x == 1
                        ? 1
                        : -Math.pow(2, 10 * x - 10) * Mth.sin((float) ((x * 10 - 10.75) * ((2 * Math.PI) / 3))));
            }
        },
        easeOutElastic {
            public float ease(float x) {
                return x == 0
                        ? 0
                        : (float) (x == 1
                        ? 1
                        : Math.pow(2, -10 * x) * Mth.sin((float) ((x * 10 - 0.75) * ((2 * Math.PI) / 3))) + 1);
            }
        },
        easeInOutElastic {
            public float ease(float x) {
                return x == 0
                        ? 0
                        : (float) (x == 1
                        ? 1
                        : x < 0.5
                        ? -(Math.pow(2, 20 * x - 10) * Mth.sin((float) ((20 * x - 11.125) * ((2 * Math.PI) / 4.5)))) / 2
                        : (Math.pow(2, -20 * x + 10) * Mth.sin((float) ((20 * x - 11.125) * ((2 * Math.PI) / 4.5)))) / 2 + 1);
            }
        },
        easeInBounce {
            public float ease(float x) {
                return 1 - bounceOut(1 - x);
            }
        },
        easeOutBounce {
                public float ease(float x) {
                    return 1 - bounceOut(1 - x);
                }
        },
        easeInOutBounce {
            public float ease(float x) {
                return x < 0.5
                        ? (1 - bounceOut(1 - 2 * x)) / 2
                        : (1 + bounceOut(2 * x - 1)) / 2;
            }
        };

        private static float bounceOut(float x) {
            float n1 = 7.5625F;
            float d1 = 2.75F;

            if (x < 1 / d1) {
                return n1 * x * x;
            } else if (x < 2 / d1) {
                return n1 * (x -= 1.5 / d1) * x + 0.75F;
            } else if (x < 2.5 / d1) {
                return n1 * (x -= 2.25 / d1) * x + 0.9375F;
            } else {
                return n1 * (x -= 2.625 / d1) * x + 0.984375F;
            }
        }
    }
    
    public interface IEasingFunction {
        float ease(float x);
    }
}
