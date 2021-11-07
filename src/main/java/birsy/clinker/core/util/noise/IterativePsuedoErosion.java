package birsy.clinker.core.util.noise;

import net.minecraft.util.Mth;

public class IterativePsuedoErosion {
    public float[][] evaluateHeightMap(float[][] heightmap, int sampleRes, int iterations) {
        float[][] evaluatedHeightmap = heightmap;
        float[][][] samples = getSamples(heightmap, sampleRes);
        float[][][] pairedSamples = pairNearbySamples(samples);



        return iterations == 0 ? evaluatedHeightmap : evaluateHeightMap(evaluatedHeightmap, sampleRes, iterations - 1);
    }

    //First array = sampleX
    //Second array = sampleY
    //Third array = actual x, actual y, and sampled height.
    private static float[][][] getSamples(float[][] heightmap, int sampleRes) {
        float[][][] samples = new float[sampleRes][sampleRes][3];

        float distBetweenSamplesX = heightmap.length / sampleRes;
        float distBetweenSamplesY = heightmap[0].length / sampleRes;

        for (int x = 0; x < sampleRes; x++) {
            for (int y = 0; y < sampleRes; y++) {
                float sampleX = x * distBetweenSamplesX;
                float sampleY = y * distBetweenSamplesY;
                samples[x][y][0] = distBetweenSamplesX;
                samples[x][y][1] = distBetweenSamplesY;

                if (Mth.frac(sampleX) == 0 && Mth.frac(sampleY) == 0) {
                    samples[x][y][2] = heightmap[(int) sampleX][(int) sampleY];
                } else {
                    float lerp1 = Mth.lerp(Mth.frac(sampleX), heightmap[(int) Math.floor(sampleX)][(int) Math.ceil(sampleY)], heightmap[(int) Math.ceil(sampleX)][(int) Math.ceil(sampleY)]);
                    float lerp2 = Mth.lerp(Mth.frac(sampleX), heightmap[(int) Math.floor(sampleX)][(int) Math.floor(sampleY)], heightmap[(int) Math.ceil(sampleX)][(int) Math.floor(sampleY)]);
                    samples[x][y][2] = Mth.lerp(Mth.frac(sampleY), lerp1, lerp2);
                }
            }
        }

        return samples;
    }

    //"Connects" each sample to the lowest nearby sample. The value isn't stored here 'cause we don't actually need it, and can just look it up in the above sample array anyway.
    //First array = sampleX.
    //Second array = sampleY
    //Third array = coordinates of said sample.
    private static float[][][] pairNearbySamples(float[][][] samples) {
        for (int x = 0; x < samples.length; x++) {
            for (int y = 0; y < samples[x].length; y++) {

            }
        }
    }

    private float evaluatePoint(float x, float y, Float[][]... nearbyPairs) {
        float evaluation = Float.NaN;

        for (Float[][] nearbyPair : nearbyPairs) {
            float result;

            //Coordinates of the points being checked.
            float x1 = nearbyPair[0][0], y1 = nearbyPair[0][1];
            //Coordinates of what they point is connected to.
            float x2 = nearbyPair[1][0], y2 = nearbyPair[1][1];

            float f1 = (float) (((y1 - y2) * (y - y1) + (x1 - x2) * (x - x1)) / (Math.pow(y1 - y2, 2.0F) + Math.pow(x1 - x2, 2.0F)));
            float f2 = (float) Math.abs(((y1 - y2) * (x - x1) - (x1 - x2) * (y - y1)) / Math.sqrt(Math.pow(x1 - x2, 2.0F) + Math.pow(y1-y2, 2.0F)));

            if (f1 > 0) {
                result = (float) Math.sqrt(Math.pow(x - x1, 2.0F) + Math.pow(y - y1, 2.0F));
            } else if (f1 < -1) {
                result =  (float) Math.sqrt(Math.pow(x - x2, 2.0F) + Math.pow(y - y2, 2.0F));
            } else if (0 > f1 && f1 > -1){
                result =  f2;
            } else {
                //Should be impossible? Who knows!
                result =  Float.NaN;
            }

            evaluation = evaluation == Float.NaN ? result : Math.min(evaluation, result);
        }

        return evaluation;
    }
}
