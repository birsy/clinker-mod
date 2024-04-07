package birsy.clinker.common.world.level.gen.chunk.noiseproviders;

import birsy.clinker.common.world.level.gen.chunk.OthershoreNoiseSampler;
import birsy.clinker.core.util.MathUtils;
import net.minecraft.Util;
import net.minecraft.util.Mth;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class TerrainNoiseProvider extends NoiseProvider {
    private final OthershoreNoiseSampler s;

    public TerrainNoiseProvider(ChunkAccess chunk, long seed, int horizontalSamples, int verticalSamples) {
        super(chunk, seed, Util.make(new ArrayList<>(2), (cD) -> {cD.add(true);cD.add(false);}), Util.make(new ArrayList<>(2), (eY) -> {eY.add(true);eY.add(false);}), horizontalSamples, verticalSamples);
        this.s = new OthershoreNoiseSampler(0);
        fillNoiseSampleArrays();
    }

    @Override
    public void initNoise() {
        s.setSeed(seed);
    }

    @Override
    public void createSampleArrayList() {
        sampleArrays = new ArrayList<>(2);
        sampleArrays.add(new double[this.horizontalSamples + 1][this.verticalSamples + 1][this.horizontalSamples + 1]);
        sampleArrays.add(new double[this.horizontalSamples + 1][this.verticalSamples + 1][this.horizontalSamples + 1]);
        derivativeSampleArrays = new ArrayList<>(2);
        derivativeSampleArrays.add(new Vec3[this.horizontalSamples + 1][this.verticalSamples + 1][this.horizontalSamples + 1]);
        derivativeSampleArrays.add(new Vec3[this.horizontalSamples + 1][this.verticalSamples + 1][this.horizontalSamples + 1]);
    }

    @Override
    public List<Double> getNoiseAtPosition(double x, double y, double z, long seed) {
        int maxHeight = 240;
        int minHeight = 64;

        double terrainShape;
        double duneShape;

        if (y > maxHeight + 10) {
            terrainShape = -100000.0;
            duneShape = -100000.0;
        } else if (y < minHeight - 30) {
            terrainShape = 100000.0;
            duneShape = 100000.0;
        } else {
            double frequency = 0.05;

            double detailSize = 1.5;
            double detailNoise = s.detailNoise.GetNoise(x * detailSize, y * 0.3, z * detailSize);

            double plateauSize = 2.0;
            double cliffsideSize = 1.5;
            double erosion = MathUtils.biasTowardsExtreme(s.largeNoise.GetNoise((x + 4233) * frequency * 3, (z + 3234) * frequency) * 3, 0.1F, 1);
            erosion = (erosion + 1) * 0.5;
            erosion = Mth.clamp(erosion, 0.0, 1.0);
            erosion = MathUtils.bias(erosion, 0.3F);
            erosion = MathUtils.mapRange(0.0, 1.0, 0.2, 0.8, erosion);
            double tBaseNoise = s.largeNoise.GetNoise(x * frequency, z * frequency);
            tBaseNoise = MathUtils.smoothMinExpo((tBaseNoise * plateauSize + plateauSize) - 1, 1, 0.0);
            tBaseNoise = tBaseNoise < 0.0 ? MathUtils.smoothMinExpo(tBaseNoise * cliffsideSize, -1, -0.1) : tBaseNoise;
            double baseNoise = MathUtils.biasTowardsExtreme(tBaseNoise, erosion, 2);

            double basicNoise = baseNoise;
            basicNoise = MathUtils.mapRange(-1.0, 1.0, minHeight, maxHeight, basicNoise);
            basicNoise -= y;

            double overhangStepSize = 0.07;
            double overhangStepNoise = (s.largeNoise.GetNoise(x * overhangStepSize, 22.0, z * overhangStepSize) + 1.0F) / 2.0F;
            overhangStepNoise = MathUtils.map(4.0F, 8.0F, (float) overhangStepNoise);
            double detailIntensityO = 0.3;//0.23;
            double overhangNoise = (MathUtils.biasTowardsExtreme(tBaseNoise, 0.9F, 2) * (1 - detailIntensityO)) + (detailNoise * detailIntensityO);
            overhangNoise = MathUtils.mapRange(-1.0, 1.0, minHeight, maxHeight, overhangNoise);
            overhangNoise -= overhang(y, overhangStepNoise, 64, 240);

            double stepSize = 0.15;
            double terrainStepNoise = (s.largeNoise.GetNoise(x * stepSize, 2342.0, z * stepSize) + 1.0F) / 2.0F;
            double detailIntensityT = 0.04;
            double terracedNoise = MathUtils.terrace((float) ((baseNoise * (1 - detailIntensityT)) + (detailNoise * detailIntensityT)), 1.0F / (MathUtils.map(3.3F, 1.2F, (float) MathUtils.bias(terrainStepNoise, -0.2F))), 0.08F).first;
            terracedNoise = MathUtils.mapRange(-1.0, 1.0, minHeight, maxHeight, terracedNoise);
            terracedNoise -= y;

            double smoothingFactor = baseNoise * baseNoise * baseNoise * baseNoise * baseNoise * baseNoise;
            smoothingFactor = MathUtils.bias(smoothingFactor, -0.1F);
            smoothingFactor = MathUtils.map(0.0F, 0.9F, (float) smoothingFactor);

            double shapeScale = 0.4;
            double shapeNoise = MathUtils.bias((MathUtils.biasTowardsExtreme(s.largeNoise.GetNoise(x * shapeScale, -1234.0, z * shapeScale), 0.8F, 1) + 1.0F) / 2.0F, 0.35F);
            terrainShape = Mth.lerp(shapeNoise, terracedNoise, overhangNoise);
            terrainShape = Mth.lerp(smoothingFactor, terrainShape, basicNoise);

            double duneSize = 0.7;
            duneShape = (1 - Math.abs(s.detailNoise.GetNoise(x * duneSize, z * duneSize)));
            duneShape *= duneShape;
            duneShape *= 1 + (1 - smoothingFactor);
            duneShape *= MathUtils.mapRange(-1.0, 1.0, 3.0, 5.0, tBaseNoise);
            duneShape += basicNoise;
            duneShape += ((terrainStepNoise * 2) - 1) * 6;
            duneShape -= MathUtils.map(2, 15, /*(float) Math.min(shapeNoise, 1 - smoothingFactor)*/ (float)shapeNoise);
            duneShape += MathUtils.mapRange(-1.0, 1.0, 6.0, 0.0, tBaseNoise);
            duneShape += (smoothingFactor) * 1.5;
            duneShape -= (1 - smoothingFactor) * 3.5;
        }

        List<Double> list =  new ArrayList<>(2);
        list.add(terrainShape);
        list.add(duneShape);
        return list;
    }

    public double overhang(double y, double overhangAmount, int minHeight, int maxHeight) {
        double dist = MathUtils.mapRange(minHeight, maxHeight, 0, 1, y);
        double a = Mth.frac(dist * overhangAmount);

        double topSteepness = 0.08;
        double overhangDepth = 4.1;

        double i = MathUtils.smoothClampExpo((a - 1)/topSteepness + 1, 0, 1, 0.2);
        double o = ((a - 1)*(a - 1)*(a - 1) + (a - 1)*(a - 1)) * overhangDepth;
        double s = MathUtils.bias(a, 0.7);
        a = Mth.lerp(i, o, s);

        //a = OVERHANG_CURVE.bezierPoint(a).y();
        //a = (7.466 * a * a * a - 11.2 * a * a + 4.733 * a);
        double b = Math.floor(dist * overhangAmount);

        double overhang = (a + b) / overhangAmount;
        return MathUtils.map(64, 240, (float) overhang);
    }
}
