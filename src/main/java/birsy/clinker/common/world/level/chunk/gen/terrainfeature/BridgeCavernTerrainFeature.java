package birsy.clinker.common.world.level.chunk.gen.terrainfeature;

import birsy.clinker.common.world.level.chunk.gen.GenerationRegion;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class BridgeCavernTerrainFeature extends TerrainFeature {
    private Vec3 caveCenter;
    private double cavernRadius = 0;
    private List<Bridge> bridges;

    public BridgeCavernTerrainFeature() {
        this.bridges = new ArrayList<>();
    }

    public void generate(GenerationRegion region) {
        long seed = region.worldSeed;
        Vec2 regionPosition = region.position;
        RandomSource random = region.random;
        ChunkAccess chunkHelper = region.getChunkAccess();

        this.cavernRadius = 32.0;

        this.caveCenter = new Vec3(regionPosition.x, 0, regionPosition.y);
        this.caveCenter = this.caveCenter.add(((random.nextFloat() * 2) - 1) * GenerationRegion.REGION_SIZE,
                random.nextInt((int) (chunkHelper.getMinBuildHeight() + cavernRadius), 64),
                ((random.nextFloat() * 2) - 1) * GenerationRegion.REGION_SIZE);

        float bridgeNumber = 4;
        double bridgeHeight = caveCenter.y() - cavernRadius;

        for (int i = 0; i < bridgeNumber; i++) {
            bridgeHeight += ((cavernRadius * 2.0) / bridgeNumber);
            float rotation = (float) (random.nextDouble() * 2 * Math.PI);
            Vec3 point1 = new Vec3(Mth.sin(rotation), 0, Mth.cos(rotation)).scale(cavernRadius * 2).add(caveCenter.x(), bridgeHeight, caveCenter.z());
            Vec3 point2 = new Vec3(Mth.sin(rotation + Mth.PI), 0, Mth.cos(rotation + Mth.PI)).scale(cavernRadius * 2).add(caveCenter.x(), bridgeHeight, caveCenter.z());
            Vec3 randomOffset = new Vec3((random.nextDouble() * 2.0) - 1.0, 0, (random.nextDouble() * 2.0) - 1.0).scale(16);

            this.bridges.add(new Bridge(point1.add(randomOffset), point2.add(randomOffset)));
        }
    }

    @Override
    public double transformDensity(float initialDensity, BlockPos pos) {
        Vec3 position = new Vec3(pos.getX(), pos.getY(), pos.getZ());
        Vec3 localPos = position.subtract(caveCenter);

        double cavernS = localPos.multiply(1.0, 0.5, 1.0).length() - cavernRadius;
        double shellS = MathUtils.mapRange(cavernRadius, cavernRadius + 10.0, 1.0, -1.0, localPos.length());
        double bridgeS = 1.0 / GenerationRegion.REGION_SIZE;
        double tunnelS = 1.0 / GenerationRegion.REGION_SIZE;

        for (Bridge bridge : bridges) {
            double bDistance = getBridgeDistance(position, bridge.point1, bridge.point2, 4);
            double tDistance = getBridgeDistance(position, bridge.point1.add(0, 10, 0), bridge.point2.add(0, 10, 0), 3.5);

            bridgeS = Math.min(bridgeS, bDistance);
            tunnelS = Math.min(tunnelS, tDistance);
        }

        shellS += Math.min((tunnelS - 5) * 2, 0);
        shellS -= Math.min(bridgeS * 2, 0);

        cavernS = MathUtils.smoothMinExpo(MathUtils.smoothMinExpo((float) -bridgeS, (float) cavernS, -5.0F), tunnelS, 2.0F) * 0.05;

        return MathUtils.smoothMinExpo(initialDensity + Math.max(shellS * 0.8, 0), cavernS, 0.06);
    }

    record Bridge(Vec3 point1, Vec3 point2) {}

    // actually just a cylinder SDF
    private double getBridgeDistance(Vec3 position, Vec3 bridgeStart, Vec3 bridgeEnd, double radius) {
        Vec3  ba = bridgeEnd.subtract(bridgeStart);
        Vec3  pa = position.subtract(bridgeStart);
        double baba = ba.dot(ba);
        double paba = pa.dot(ba);
        double x = pa.scale(baba).subtract(ba.scale(paba)).length() - radius * baba;
        double y = Math.abs(paba-baba*0.5)-baba*0.5;
        double x2 = x*x;
        double y2 = y*y*baba;

        double d = (Math.max(x,y)<0.0) ? -Math.min(x2,y2) : ( ( (x > 0.0) ? x2 : 0.0) + ( (y > 0.0) ? y2 : 0.0) );

        return Mth.sign(d) * Math.sqrt(Math.abs(d)) / baba;
    }
}
