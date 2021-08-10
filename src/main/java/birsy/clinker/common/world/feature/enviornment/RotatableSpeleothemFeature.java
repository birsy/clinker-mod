package birsy.clinker.common.world.feature.enviornment;

import birsy.clinker.core.registry.ClinkerBlocks;
import birsy.clinker.core.util.MathUtils;
import com.mojang.serialization.Codec;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.Random;

public class RotatableSpeleothemFeature extends Feature<NoneFeatureConfiguration> {
    public RotatableSpeleothemFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(WorldGenLevel reader, ChunkGenerator generator, Random rand, BlockPos pos, NoneFeatureConfiguration config) {
        float radius = (int) MathUtils.mapRange(0, 1, 2, 8, MathUtils.bias(rand.nextFloat(), 0.3f));
        float length = (int) MathUtils.mapRange(0, 1, radius * 2, 48, MathUtils.bias(rand.nextFloat(), 0.3f));
        Vec3 direction = new Vec3(MathUtils.getRandomFloatBetween(rand, -1, 1), MathUtils.getRandomFloatBetween(rand, -1, 1), MathUtils.getRandomFloatBetween(rand, -1, 1));

            createSpeleothem(reader, pos, rand, radius, length, direction);
            return true;


    }

    private void createSpeleothem(WorldGenLevel reader, BlockPos pos, Random rand, float radius, float length, Vec3 direction) {
        Vec3 blockPos = new Vec3(pos.getX(), pos.getY(), pos.getZ());
        for (int l = 0; l < length; l++) {
            float percentage = MathUtils.mapRange(0, length, 1, l, 0);
            float trueRadius = radius * percentage;

            createSphere(reader, blockPos, trueRadius);

            blockPos.add(direction);
        }
    }

    private void createSphere(WorldGenLevel reader, Vec3 position, float radius) {
        BlockPos pos = new BlockPos(position.x(), position.y(), position.z());
        int intRadius = (int) radius;

        BlockPos.betweenClosedStream(pos.getX() - intRadius, pos.getY() - intRadius, pos.getZ() - intRadius, pos.getX() + intRadius, pos.getY() + intRadius, pos.getZ() + intRadius).forEach((bulbPos) -> {
            if (bulbPos.closerThan(position, radius)) {
                this.placeBlock(reader, bulbPos);
            }
        });
    }

    private void placeBlock(WorldGenLevel worldIn, BlockPos pos) {
        if (pos.getY() > 1 && pos.getY() < worldIn.dimensionType().logicalHeight()) {
            if (worldIn.getBlockState(pos).getMaterial().isReplaceable() && !worldIn.getBlockState(pos).getMaterial().isSolid()) {
                this.setBlock(worldIn, pos, Blocks.DIAMOND_BLOCK.defaultBlockState());
            }
        }
    }
}
