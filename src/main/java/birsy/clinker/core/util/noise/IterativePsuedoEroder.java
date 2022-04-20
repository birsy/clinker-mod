package birsy.clinker.core.util.noise;

import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec2;

import java.util.function.BiFunction;

public class IterativePsuedoEroder {
    private float GRIDSIZE = 50.0F;
    private float BASE_HEIGHT = 0.7F;
    private float EROSION_HEIGHT = 0.6F;
    private BiFunction<Vec2, Long, Float> heightmap;
    private long seed;

    public IterativePsuedoEroder(BiFunction<Vec2, Long, Float> heightmap, long seed) {
        this.heightmap = heightmap;
        this.seed = seed;
    }

    public float at (float x, float y) {
        return pseudoErosionFractal(new Vec2(x, y));
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    Vec2 hash2(Vec2 p ) {
        return frac(multiply(sin(multiply(new Vec2(793.34F, 934.78F), p.x + p.y)), new Vec2(12345.0F, 5432.0F))).add(-0.5F);
    }

    static float sdLine(Vec2 p, Vec2 a, Vec2 b ) {
        Vec2 pa = p.add(a.negated());
        Vec2 ba = b.add(a.negated());
        float h = Mth.clamp( pa.dot(ba) / ba.dot(ba), 0.0F, 1.0F);
        if (Float.isNaN(h)) {
            h = 0.0F;
        }
        return pa.add(multiply(ba, h).negated()).length();
    }

    float pseudoErosion(Vec2 pos, float gridSize) {
        Vec2 pi = round(divide(pos, gridSize).add(0.5F));

        float minh = Float.MAX_VALUE;
        for(int j = -1; j <= 1 ; j++ )
            for (int i = -1; i <= 1 ; i++ )
            {
                Vec2 pa = pi.add(new Vec2(i,j));
                Vec2 p1 = multiply((pa.add(hash2(pa))), gridSize);
                Vec2 p2 = null;

                float lowestNeighbor = Float.MAX_VALUE;

                for(int n = -1; n <= 1; n++)
                    for(int m = -1; m <= 1; m++)
                    {
                        Vec2 pb = pa.add(new Vec2(m, n));
                        Vec2 candidate = multiply((pb.add(hash2(pb))), gridSize);

                        float height = heightmap.apply(candidate, this.seed);
                        if( height < lowestNeighbor )
                        {
                            p2 = candidate;
                            lowestNeighbor = height;
                        }
                    }

                float h = sdLine( pos, p1, p2);
                minh = Math.min(h, minh);
            }

        minh = minh / gridSize;
        return minh;
    }

    float pseudoErosionFractal( Vec2 wsPos ) {
        float baseHeight = heightmap.apply(wsPos, this.seed);
        float gridSize = 100.0F;
        float r1 = pseudoErosion( wsPos, gridSize );
        float erosion = r1 * r1;

        float r2 = pseudoErosion( wsPos, gridSize / 2.0F );
        erosion += r1 * r2 / 2.0;

        float r3 = pseudoErosion( wsPos, gridSize / 4.0F );
        erosion += Math.sqrt(r1*r2)*r3 / 3.0;

        return baseHeight * 0.0F + erosion * 1.0F;
    }

    static Vec2 multiply(Vec2 x, Vec2 y) {
        return new Vec2(x.x * y.x, x.y * y.y);
    }
    static Vec2 multiply(Vec2 x, float y) {
        return new Vec2(x.x * y, x.y * y);
    }
    static Vec2 divide(Vec2 x, Vec2 y) {
        return new Vec2(x.x / y.x, x.y / y.y);
    }
    static Vec2 divide(Vec2 x,float y) {
        return new Vec2(x.x / y, x.y / y);
    }
    static Vec2 round(Vec2 x) {
        return new Vec2(Math.round(x.x), Math.round(x.y));
    }
    static Vec2 sin(Vec2 x) {
        return new Vec2(Mth.sin(x.x), Mth.sin(x.y));
    }
    static Vec2 frac(Vec2 x) {
        return new Vec2(Mth.frac(x.x), Mth.frac(x.y));
    }
}
