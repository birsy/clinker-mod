package birsy.clinker.common.world.level.chunk.gen.noiseproviders;

import birsy.clinker.core.util.MathUtils;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public abstract class NoiseProvider {
    protected final long seed;
    protected final ChunkAccess chunk;
    private final List<Boolean> easeY;

    private final List<Boolean> createsDerivatives;
    protected List<double[][][]> sampleArrays;
    protected List<Vec3[][][]> derivativeSampleArrays;

    protected final int horizontalSamples;
    protected final int verticalSamples;
    
    public NoiseProvider(ChunkAccess chunk, long seed, List<Boolean> createsDerivatives, List<Boolean> easeY, int horizontalSamples, int verticalSamples) {
        this.chunk = chunk;
        this.seed = seed;
        this.easeY = easeY;
        this.createsDerivatives = createsDerivatives;
        this.horizontalSamples = horizontalSamples;
        this.verticalSamples = verticalSamples;
    }

    public List<double[][][]> getNoiseInChunk() {
        List<double[][][]> returnList = new ArrayList<>();

        for (int i = 0; i < sampleArrays.size(); i++) {
            double[][][] sampleArray = sampleArrays.get(i);
            double[][][] chunkArray = new double[16][chunk.getHeight()][16];
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < chunk.getHeight(); y++) {
                        chunkArray[x][y][z] = lerpSample(sampleArray, 0, chunk.getMinBuildHeight(), 0, 16, chunk.getMaxBuildHeight(), 16, mod(x, 16), y, mod(z, 16), easeY.get(i));
                    }
                }
            }
            returnList.add(chunkArray);
        }

        return returnList;
    }

    public List<Vec3[][][]> getNoiseDerivativeInChunk() {
        List<Vec3[][][]> returnList = new ArrayList<>();

        for (Vec3[][][] derivativeSampleArray : derivativeSampleArrays) {
            Vec3[][][] chunkArray = new Vec3[16][chunk.getHeight()][16];
            for (int x = 0; x < 16; x++) {
                for (int z = 0; z < 16; z++) {
                    for (int y = 0; y < chunk.getHeight(); y++) {
                        chunkArray[x][y][z] = lerpSampleVec3(derivativeSampleArray, 0, chunk.getMinBuildHeight(), 0, 16, chunk.getMaxBuildHeight(), 16, mod(x, 16), y, mod(z, 16));
                    }
                }
            }
            returnList.add(chunkArray);
        }

        return returnList;
    }

    private double mod(double x, double m) {
        return x - (m * Math.floor(x / m));
    }

    public abstract void initNoise();

    public abstract List<Double> getNoiseAtPosition(double x, double y, double z, long seed);

    public abstract void createSampleArrayList();
    
    public void fillNoiseSampleArrays() {
        float hOffset = (16.0F / (float) horizontalSamples);
        float vOffset = ((float) chunk.getHeight() / (float) verticalSamples);

        this.createSampleArrayList();
        this.initNoise();
        for (int sX = 0; sX < horizontalSamples + 1; sX++) {
            for (int sZ = 0; sZ < horizontalSamples + 1; sZ++) {
                for (int sY = 0; sY < verticalSamples + 1; sY++) {

                    float cX = sX * hOffset;
                    float cY = sY * vOffset;
                    float cZ = sZ * hOffset;

                    float x = cX + chunk.getPos().getMinBlockX();
                    float z = cZ + chunk.getPos().getMinBlockZ();
                    float y = cY + chunk.getMinBuildHeight();

                    List<Double> noiseSamples = getNoiseAtPosition(x, y, z, this.seed);
                    for (int i = 0; i < sampleArrays.size(); i++) {
                        sampleArrays.get(i)[sX][sY][sZ] = noiseSamples.get(i);
                    }
                }
            }
        }

        for (int i = 0; i < sampleArrays.size(); i++) {
            double[][][] sampleArray = sampleArrays.get(i);
            Vec3[][][] derivativeSampleArray = derivativeSampleArrays.get(i);
            for (int sX = 0; sX < horizontalSamples + 1; sX++) {
                for (int sZ = 0; sZ < horizontalSamples + 1; sZ++) {
                    for (int sY = 0; sY < verticalSamples + 1; sY++) {
                        if (createsDerivatives.get(i)) {
                            double dX;
                            double dY;
                            double dZ;

                            if (sX == 0) {
                                dX = (sampleArray[sX][sY][sZ] - sampleArray[sX + 1][sY][sZ]) / hOffset;
                            } else {
                                dX = (sampleArray[sX - 1][sY][sZ] - sampleArray[sX][sY][sZ]) / hOffset;
                            }

                            if (sY == 0) {
                                dY = (sampleArray[sX][sY][sZ] - sampleArray[sX][sY + 1][sZ]) / vOffset;
                            } else {
                                dY = (sampleArray[sX][sY - 1][sZ] - sampleArray[sX][sY][sZ]) / vOffset;
                            }

                            if (sZ == 0) {
                                dZ = (sampleArray[sX][sY][sZ] - sampleArray[sX][sY][sZ + 1]) / hOffset;
                            } else {
                                dZ = (sampleArray[sX][sY][sZ - 1] - sampleArray[sX][sY][sZ]) / hOffset;
                            }

                            derivativeSampleArray[sX][sY][sZ] = new Vec3(dX, dY, dZ);
                        } else {
                            derivativeSampleArray[sX][sY][sZ] = Vec3.ZERO;
                        }
                    }
                }
            }
        }
    }

    public double lerpSample(double[][][] sampleGrid, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, double x, double y, double z, boolean easeY) {
        //non-negative
        double nNX = x - minX;
        double nNY = y - minY;
        double nNZ = z - minZ;
        int nNMaxX = maxX - minX;
        int nNMaxY = maxY - minY;
        int nNMaxZ = maxZ - minZ;

        double sX = (nNX / nNMaxX) * (sampleGrid.length - 1);
        double sY = (nNY / nNMaxY) * (sampleGrid[0].length - 1);
        double sZ = (nNZ / nNMaxZ) * (sampleGrid[0][0].length - 1);

        //Clinker.LOGGER.info("" + x +" "+ y +" "+ z + " -> " + nNX +" "+ nNY +" "+ nNZ + " -> " + sX +" "+ sY +" "+ sZ);

        double sFracX = Mth.frac(sX);
        double sFracY = Mth.frac(sY);
        double sFracZ = Mth.frac(sZ);

        int sMinX = Mth.floor(sX);
        int sMaxX = Mth.ceil(sX);
        int sMinY = Mth.floor(sY);
        int sMaxY = Mth.ceil(sY);
        int sMinZ = Mth.floor(sZ);
        int sMaxZ = Mth.ceil(sZ);

        double lerpX = Mth.lerp(sFracX, sampleGrid[sMinX][sMinY][sMinZ], sampleGrid[sMaxX][sMinY][sMinZ]);
        double lerpXZ = Mth.lerp(sFracX, sampleGrid[sMinX][sMinY][sMaxZ], sampleGrid[sMaxX][sMinY][sMaxZ]);

        double lerpXY = Mth.lerp(sFracX, sampleGrid[sMinX][sMaxY][sMinZ], sampleGrid[sMaxX][sMaxY][sMinZ]);
        double lerpXYZ = Mth.lerp(sFracX, sampleGrid[sMinX][sMaxY][sMaxZ], sampleGrid[sMaxX][sMaxY][sMaxZ]);

        return Mth.lerp(easeY ? MathUtils.ease((float) sFracY, MathUtils.EasingType.easeInOutQuad) : sFracY, Mth.lerp(sFracZ, lerpX, lerpXZ), Mth.lerp(sFracZ, lerpXY, lerpXYZ));
        /*return MathUtils.lerp3(sFracX, sFracY, sFracZ,
                sampleGrid[sMinX][sMinY][sMinZ], sampleGrid[sMinX][sMaxY][sMinZ],
                sampleGrid[sMaxX][sMinY][sMinZ], sampleGrid[sMaxX][sMaxY][sMinZ],
                sampleGrid[sMinX][sMinY][sMaxZ], sampleGrid[sMinX][sMaxY][sMaxZ],
                sampleGrid[sMaxX][sMinY][sMaxZ], sampleGrid[sMaxX][sMaxY][sMaxZ]);*/
    }

    public Vec3 lerpSampleVec3(Vec3[][][] sampleGrid, int minX, int minY, int minZ, int maxX, int maxY, int maxZ, double x, double y, double z) {
        //non-negative
        double nNX = x - minX;
        double nNY = y - minY;
        double nNZ = z - minZ;
        int nNMaxX = maxX - minX;
        int nNMaxY = maxY - minY;
        int nNMaxZ = maxZ - minZ;

        double sX = (nNX / nNMaxX) * (sampleGrid.length - 1);
        double sY = (nNY / nNMaxY) * (sampleGrid[0].length - 1);
        double sZ = (nNZ / nNMaxZ) * (sampleGrid[0][0].length - 1);

        //Clinker.LOGGER.info("" + x +" "+ y +" "+ z + " -> " + nNX +" "+ nNY +" "+ nNZ + " -> " + sX +" "+ sY +" "+ sZ);

        double sFracX = Mth.frac(sX);
        double sFracY = Mth.frac(sY);
        double sFracZ = Mth.frac(sZ);

        int sMinX = Mth.floor(sX);
        int sMaxX = Mth.ceil(sX);
        int sMinY = Mth.floor(sY);
        int sMaxY = Mth.ceil(sY);
        int sMinZ = Mth.floor(sZ);
        int sMaxZ = Mth.ceil(sZ);

        Vec3 lerpX = MathUtils.vec3Lerp(sFracX, sampleGrid[sMinX][sMinY][sMinZ], sampleGrid[sMaxX][sMinY][sMinZ]);
        Vec3 lerpXZ = MathUtils.vec3Lerp(sFracX, sampleGrid[sMinX][sMinY][sMaxZ], sampleGrid[sMaxX][sMinY][sMaxZ]);

        Vec3 lerpXY = MathUtils.vec3Lerp(sFracX, sampleGrid[sMinX][sMaxY][sMinZ], sampleGrid[sMaxX][sMaxY][sMinZ]);
        Vec3 lerpXYZ = MathUtils.vec3Lerp(sFracX, sampleGrid[sMinX][sMaxY][sMaxZ], sampleGrid[sMaxX][sMaxY][sMaxZ]);

        return MathUtils.vec3Lerp(MathUtils.ease((float) sFracY, MathUtils.EasingType.easeInOutQuad), MathUtils.vec3Lerp(sFracZ, lerpX, lerpXZ), MathUtils.vec3Lerp(sFracZ, lerpXY, lerpXYZ));
        /*return MathUtils.lerp3(sFracX, sFracY, sFracZ,
                sampleGrid[sMinX][sMinY][sMinZ], sampleGrid[sMinX][sMaxY][sMinZ],
                sampleGrid[sMaxX][sMinY][sMinZ], sampleGrid[sMaxX][sMaxY][sMinZ],
                sampleGrid[sMinX][sMinY][sMaxZ], sampleGrid[sMinX][sMaxY][sMaxZ],
                sampleGrid[sMaxX][sMinY][sMaxZ], sampleGrid[sMaxX][sMaxY][sMaxZ]);*/
    }
}
