package birsy.clinker.common.world.feature.enviornment;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import com.mojang.serialization.Codec;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class RotatableSpeleothemFeature extends Feature<NoFeatureConfig> {
    public RotatableSpeleothemFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        float radius = (int) MathUtils.mapRange(0, 1, 2, 8, MathUtils.bias(rand.nextFloat(), 0.3f));
        float length = (int) MathUtils.mapRange(0, 1, radius * 2, 48, MathUtils.bias(rand.nextFloat(), 0.3f));
        Vector3d direction = new Vector3d(MathUtils.getRandomFloatBetween(rand, -1, 1), MathUtils.getRandomFloatBetween(rand, -1, 1), MathUtils.getRandomFloatBetween(rand, -1, 1));

            createSpeleothem(reader, pos, rand, radius, length, direction);
            return true;


    }

    private void createSpeleothem(ISeedReader reader, BlockPos pos, Random rand, float radius, float length, Vector3d direction) {
        Vector3d blockPos = new Vector3d(pos.getX(), pos.getY(), pos.getZ());
        for (int l = 0; l < length; l++) {
            float percentage = MathUtils.mapRange(0, length, 1, l, 0);
            float trueRadius = radius * percentage;

            createSphere(reader, blockPos, trueRadius);

            blockPos.add(direction);
        }
    }

    private void createSphere(ISeedReader reader, Vector3d position, float radius) {
        BlockPos pos = new BlockPos(position.getX(), position.getY(), position.getZ());
        int intRadius = (int) radius;

        BlockPos.getAllInBox(pos.getX() - intRadius, pos.getY() - intRadius, pos.getZ() - intRadius, pos.getX() + intRadius, pos.getY() + intRadius, pos.getZ() + intRadius).forEach((bulbPos) -> {
            if (bulbPos.withinDistance(position, radius)) {
                this.placeBlock(reader, bulbPos);
            }
        });
    }

    private void placeBlock(ISeedReader worldIn, BlockPos pos) {
        if (pos.getY() > 1 && pos.getY() < worldIn.getDimensionType().getLogicalHeight()) {
            if (worldIn.getBlockState(pos).getMaterial().isReplaceable() && !worldIn.getBlockState(pos).getMaterial().isSolid()) {
                this.setBlockState(worldIn, pos, Blocks.DIAMOND_BLOCK.getDefaultState());
            }
        }
    }
}
