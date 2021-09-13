package birsy.clinker.common.world.feature.enviornment;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.noise.FastNoiseLite;
import com.mojang.serialization.Codec;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class SpeleothemFeature extends Feature<NoFeatureConfig> {
    private FastNoiseLite placementNoise;
    private FastNoiseLite speleothemNoise;
    public SpeleothemFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        if (placementNoise == null || speleothemNoise == null) {
            initNoise((int) reader.getSeed(), rand);
        }

        int range = 12 + rand.nextInt(12);
        BlockPos.getAllInBox(pos.add(range, range, range), pos.add(-range, -range, -range)).forEach((placementPos) -> {
            float noise = Math.abs(placementNoise.GetNoise(placementPos.getX(), placementPos.getY(), placementPos.getZ()));
            noise *= MathUtils.invert(MathUtils.lazyPow(placementPos.distanceSq(pos) / range, 2));

            boolean canGenerate = rand.nextFloat() < noise;

            if (canGenerate) {
                //Makes larger values less likely to occur.
                int biasedRadius = (int) MathUtils.mapRange(0, 1, 2, 8, MathUtils.bias(rand.nextFloat(), 0.3f) * noise);
                int biasedHeight = (int) MathUtils.mapRange(0, 1, biasedRadius * 2, 48, MathUtils.bias(rand.nextFloat(), 0.3f) * noise);

                Direction direction = rand.nextBoolean() ? Direction.UP : Direction.DOWN;

                //Checks to make sure the speleothem doesn't go above or below the world.
                canGenerate &= (direction == Direction.UP ?
                        (pos.getY() + biasedHeight < 100) && (pos.getY() - biasedRadius > 0) :
                        (pos.getY() + biasedRadius < 100) && (pos.getY() - biasedHeight > 0));

                if (canGenerate && reader.getBlockState(pos.offset(direction.getOpposite())).isSolid() && !reader.getBlockState(pos.offset(direction)).isSolid()) {
                    createSpeleothem(reader, pos, rand, biasedRadius, biasedHeight, direction);
                }
            }
        });

        return true;
    }

    private void createSpeleothem(ISeedReader worldIn, BlockPos pos, Random rand, int radius, int height, Direction direction) {
        int xNoiseOffset = rand.nextInt();
        int yNoiseOffset = rand.nextInt();
        int zNoiseOffset = rand.nextInt();

        /**
         * Generates the cone.
         */
        for (int i = 0; i < 2; i++) {
            //Iterates through every blockpos in the cone to actually place them.
            BlockPos.Mutable iterablePos = pos.toMutable();
            //Records the center of the cone.
            Vector3d centerPos = new Vector3d(pos.getX(), pos.getY(), pos.getZ());
            //BlockPos.Mutable centerPos = pos.toMutable();
            for (int y = 0; y < height; y++) {
                for (int x = -radius * 2; x < radius * 2; x++) {
                    for (int z = -radius * 2; z < radius * 2; z++) {
                        iterablePos.setPos(pos.add(x, 0, z));
                        iterablePos.setY((int) centerPos.getY());

                        float percentage = MathUtils.mapRange(0, height, 1, 0, y);
                        float trueRadius = radius * percentage + speleothemNoise.GetNoise(x + xNoiseOffset, y + yNoiseOffset, z + zNoiseOffset);

                        //The second clause makes it so they don't keep stretching on as 1 block thick pillars. Cleans up the aesthetic.
                        if (iterablePos.withinDistance(centerPos, (trueRadius)) && trueRadius > 0.5) {
                            this.placeBlock(worldIn, iterablePos);
                        }
                    }
                }

                centerPos.add(0, direction == Direction.UP ? 1 : -1, placementNoise.GetNoise((float) centerPos.getX() + xNoiseOffset, y + yNoiseOffset, (float) centerPos.getZ() + zNoiseOffset) * 0.5F);
            }

            direction = direction.getOpposite();
            height = (int) (height * 0.5);
        }
    }

    private void placeBlock(ISeedReader worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos).getMaterial().isReplaceable() && !worldIn.getBlockState(pos).getMaterial().isSolid()) {
            if (worldIn.canBlockSeeSky(pos)) {
                this.setBlockState(worldIn, pos.up((int) (MathHelper.sin((pos.getX() * 0.3F) * 4))), ClinkerBlocks.BRIMSTONE.get().getDefaultState());
            }
        }
    }

    private void initNoise (int seed, Random rand) {
        this.placementNoise = new FastNoiseLite(seed);
        this.placementNoise.SetNoiseType(FastNoiseLite.NoiseType.Value);
        this.placementNoise.SetFrequency(0.04F);
        this.placementNoise.SetFractalType(FastNoiseLite.FractalType.None);

        this.speleothemNoise = new FastNoiseLite(seed + rand.nextInt());
        this.speleothemNoise.SetNoiseType(FastNoiseLite.NoiseType.Value);
        this.speleothemNoise.SetFrequency(0.04F);
        this.speleothemNoise.SetFractalType(FastNoiseLite.FractalType.None);
    }
}
