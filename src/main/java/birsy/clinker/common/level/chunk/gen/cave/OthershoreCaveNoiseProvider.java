package birsy.clinker.common.level.chunk.gen.cave;

import birsy.clinker.core.util.BezierCurve;
import birsy.clinker.core.util.noise.VoronoiGenerator;
import com.mojang.datafixers.util.Pair;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class OthershoreCaveNoiseProvider extends CaveNoiseProvider {
    private final BridgeCaveNoiseProvider bridgeCaveNoise;
    public OthershoreCaveNoiseProvider(ChunkAccess chunk, long seed) {
        super(chunk, seed);
        bridgeCaveNoise = new BridgeCaveNoiseProvider(chunk, seed);
    }

    @Override
    public double[][][] getNoiseInChunk(int chunkX, int chunkZ) {
        return new double[16][chunk.getHeight()][16];
    }

    public class BridgeCaveNoiseProvider extends CaveNoiseProvider {
        private final VoronoiGenerator bridgeCaveNoise;
        private Pair<Double, Vec3>[] cornerVoronoiInfo;
        public BridgeCaveNoiseProvider(ChunkAccess chunk, long seed) {
            super(chunk, seed);
            this.bridgeCaveNoise = new VoronoiGenerator(seed);
        }

        public double sampleBridgeCavernCaveNoise(double x, double y, double z, long seed) {
            Vec3 position = new Vec3(x, y, z);
            Pair<double[], Vec3> voronoiInfo = bridgeCaveNoise.get3(x, y, z);
            Vec3 localPosition = position.subtract(voronoiInfo.getSecond());
            BezierCurve bridgeCurve = generateBridgeCurve(seed, 1.0F, 1.0F, 3);

            return localPosition.distanceTo(bridgeCurve.closestPoint(localPosition));
        }

        private BezierCurve generateBridgeCurve(long seed, float cavernRadius, float cavernHeight, int bridgeAmount) {
            List<Vec3> bridgePoints = new ArrayList<>();

            float vBridgeOffset = cavernHeight / (float)bridgeAmount;
            float bridgeRadius = cavernRadius * 1.5F;
            int sign = 1;
            bridgePoints.add(new Vec3(-bridgeRadius, -vBridgeOffset, 0.0F));
            for (int i = 0; i < bridgeAmount; i++) {
                float bridgeHeight = i * vBridgeOffset;
                bridgePoints.add(new Vec3(-bridgeRadius * sign, bridgeHeight, 0.0F));
                bridgePoints.add(new Vec3(bridgeRadius * sign, bridgeHeight, 0.0F));

                sign *= -1;
            }
            bridgePoints.add(new Vec3(-bridgeRadius * sign, -vBridgeOffset, 0.0F));

            return new BezierCurve((Vec3[]) bridgePoints.toArray());
        }

        @Override
        public double[][][] getNoiseInChunk(int chunkX, int chunkZ) {
            return new double[16][chunk.getHeight()][16];
        }
    }
}
