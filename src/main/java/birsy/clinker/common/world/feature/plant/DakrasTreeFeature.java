package birsy.clinker.common.world.feature.plant;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import com.mojang.serialization.Codec;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import java.util.Random;

public class DakrasTreeFeature extends Feature<NoFeatureConfig> {
    private final BlockState TRUNK_STATE = Blocks.STRIPPED_CRIMSON_STEM.getDefaultState();

    public DakrasTreeFeature(Codec<NoFeatureConfig> codec) {
        super(codec);
    }

    @Override
    public boolean generate(ISeedReader reader, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        Clinker.LOGGER.info("placing a tree");
        boolean couldGenerate = false;

        int resolution = 16;
        float length = 15 + rand.nextFloat() * 15;
        final float maxTrunkRadius = 6;
        final float minTrunkRadius = 2;
        float trunkRadius = maxTrunkRadius;

        final float curveRadius = length / 2;
        BlockPos.Mutable trunkCenter = pos.toMutable();
        for (int i = 0; i < resolution; i += 1) {
            float distanceAlongPath = i / resolution;
            float f = (float) (distanceAlongPath < 0.5 ? (distanceAlongPath * 2 * Math.PI) - (0.5 * Math.PI) : (distanceAlongPath * -2 * Math.PI) + (0.5 * Math.PI));
            float yRotation = (float) (rand.nextFloat() * 2.0F * Math.PI);

            float xzOffset = MathHelper.sin(f + (distanceAlongPath < 0.5 ? curveRadius : curveRadius * 3));
            float yOffset = MathHelper.cos(f);

            float x = (xzOffset * MathHelper.sin(yRotation)) + pos.getX();
            float y = yOffset + pos.getY();
            float z = (xzOffset * MathHelper.cos(yRotation)) + pos.getZ();

            trunkCenter.setPos(x, y, z);
            reader.setBlockState(trunkCenter, TRUNK_STATE, 2);
            couldGenerate = true;

            trunkRadius = MathHelper.lerp(distanceAlongPath, maxTrunkRadius, minTrunkRadius);
        }

        return couldGenerate;
    }

}
