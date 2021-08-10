package birsy.clinker.common.world.feature.enviornment;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Random;

/**
 * TODO - get this not terrible.
 * Using ice spikes stuff for now.
 */
public class SpeleothemFeature extends Feature<NoneFeatureConfiguration> {
    public SpeleothemFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, NoneFeatureConfiguration config) {
        //Makes larger values less likely to occur.
        int biasedRadius = (int) MathUtils.mapRange(0, 1, 2, 8, MathUtils.bias(rand.nextFloat(), 0.3f));
        int biasedHeight = (int) MathUtils.mapRange(0, 1, biasedRadius * 2, 48, MathUtils.bias(rand.nextFloat(), 0.3f));

        Direction direction = rand.nextBoolean() ? Direction.UP : Direction.DOWN;

        //Checks to make sure the speleothem doesn't go above or below the world.
        boolean canGenerate = (direction == Direction.UP ?
                (pos.getY() + biasedHeight < 100) && (pos.getY() - biasedRadius > 0) :
                (pos.getY() + biasedRadius < 100) && (pos.getY() - biasedHeight > 0));


        if (canGenerate && reader.getBlockState(pos.relative(direction.getOpposite())).canOcclude() && !reader.getBlockState(pos.relative(direction)).canOcclude()) {
            createSpeleothem(reader, pos, rand, biasedRadius, biasedHeight, direction);
            for (int i = 0; i < rand.nextInt(3); i++) {
                createSpeleothem(reader, pos.offset(rand.nextInt(biasedRadius) - (biasedRadius / 2), 0, rand.nextInt(biasedRadius) - (biasedRadius / 2)), rand, rand.nextInt(biasedRadius / 2), rand.nextInt(biasedHeight / 2), direction);
            }
            return true;
        } else {
            return false;
        }
    }

    private void createSpeleothem(WorldGenLevel worldIn, BlockPos pos, Random rand, int radius, int height, Direction direction) {
        /**
         * Generates the cone.
         */
        //Iterates through every blockpos in the cone to actually place them.
        BlockPos.MutableBlockPos iterablePos = pos.mutable();
        //Records the center of the cone.
        BlockPos.MutableBlockPos centerPos = pos.mutable();

        for (int y = 0; y < height; y++) {
            for (int x = -radius * 2; x < radius * 2; x++) {
                for (int z = -radius * 2; z < radius * 2; z++) {
                    iterablePos.set(pos.offset(x, 0, z));
                    iterablePos.setY(centerPos.getY());

                    float percentage = MathUtils.mapRange(0, height, 1, 0, y);
                    float trueRadius = radius * percentage;

                    //The second clause makes it so they don't keep stretching on as 1 block thick pillars. Cleans up the aesthetic.
                    if (iterablePos.closerThan(centerPos, (trueRadius)) && trueRadius > 0.5) {
                        this.placeBlock(worldIn, iterablePos);
                    }
                }
            }
            centerPos.move(direction);
        }

        /**
         * Generates the bulb that the cone connects to.
         */
        BlockPos.betweenClosedStream(pos.getX() - radius, pos.getY() - radius, pos.getZ() - radius, pos.getX() + radius, pos.getY() + radius, pos.getZ() + radius).forEach((bulbPos) -> {
            if (bulbPos.closerThan(pos, radius)) {
                this.placeBlock(worldIn, bulbPos);
            }
        });
    }

    private void placeBlock(WorldGenLevel worldIn, BlockPos pos) {
        if (worldIn.getBlockState(pos).getMaterial().isReplaceable() && !worldIn.getBlockState(pos).getMaterial().isSolid()) {
            if (worldIn.canSeeSkyFromBelowWater(pos)) {
                this.setBlock(worldIn, pos.above((int) (Mth.sin((pos.getX() * 0.3F) * 4))), ClinkerBlocks.BRIMSTONE.get().defaultBlockState());
            }
        }
    }
}
