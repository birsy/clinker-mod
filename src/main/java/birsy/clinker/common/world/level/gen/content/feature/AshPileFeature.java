package birsy.clinker.common.world.level.gen.content.feature;

import birsy.clinker.common.block.FallingLayerBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import net.minecraft.world.level.material.Fluids;

public class AshPileFeature extends Feature<AshPileFeature.Configuration> {
    public AshPileFeature(Codec<Configuration> config) {
        super(config);
    }

    public boolean place(FeaturePlaceContext<Configuration> featureContext) {
        WorldGenLevel level = featureContext.level();
        BlockPos origin = featureContext.origin();
        RandomSource random = featureContext.random();

        int height = featureContext.config().height().sample(random);
        int radius = featureContext.config().radius.sample(random) * 2;
        int placementRadius = radius + 2;

        NormalNoise noise = NormalNoise.create(random, -3, 1.0, 0.25);

        BlockPos.MutableBlockPos cursor = origin.mutable();
        for (int x = -placementRadius; x <= placementRadius; x++) {
            cursor.setX(x + origin.getX());
            for (int z = -placementRadius; z <= placementRadius; z++) {
                cursor.setZ(z + origin.getZ());

                double distance = Mth.length(x, z);
                distance += (Math.abs(noise.getValue(cursor.getX(), 0, cursor.getZ())) * 2.0 - 1.0) * 2.0;
                distance /= radius;

                if (distance > 1) continue;
                distance = 1.0 - distance;
                cursor.setY(origin.getY());
                placeAshLayer(level, cursor, (int) Math.round(distance * height));
            }
        }

        return true;
    }

    void placeAshLayer(WorldGenLevel level, BlockPos.MutableBlockPos pos, int levels) {
        if (levels <= 0) return;

        pos.move(Direction.DOWN);
        if (!level.getBlockState(pos).isFaceSturdy(level, pos, Direction.UP, SupportType.FULL)) return;
        pos.move(Direction.UP);

        int workingLevels = levels;
        while (workingLevels > 0 && pos.getY() < level.getMaxBuildHeight()) {
            BlockState stateToReplace = level.getBlockState(pos);

            if (stateToReplace.is(ClinkerBlocks.ASH_LAYER) && stateToReplace.getValue(FallingLayerBlock.LAYERS) >= workingLevels) return;
            if (!stateToReplace.canBeReplaced()) return;

            int layersToPlace = Math.min(workingLevels, FallingLayerBlock.MAX_HEIGHT);
            boolean fullBlock = layersToPlace == FallingLayerBlock.MAX_HEIGHT;

            BlockState state;
            if (fullBlock) {
                state = ClinkerBlocks.ASH.get().defaultBlockState();
            } else {
                boolean waterlogged = stateToReplace.getFluidState().isSourceOfType(Fluids.WATER);
                state = ClinkerBlocks.ASH_LAYER.get().defaultBlockState()
                        .setValue(FallingLayerBlock.LAYERS, layersToPlace)
                        .setValue(FallingLayerBlock.WATERLOGGED, waterlogged);
            }

            level.setBlock(pos, state, 2);

            pos.move(Direction.UP);
            workingLevels -= layersToPlace;
        }
    }

    public record Configuration(IntProvider height, IntProvider radius) implements FeatureConfiguration {
        public static final Codec<Configuration> CODEC = RecordCodecBuilder.create(
                codec -> codec.group(
                        IntProvider.CODEC.fieldOf("height").forGetter(config -> config.height),
                        IntProvider.CODEC.fieldOf("radius").forGetter(config -> config.radius)
                ).apply(codec, Configuration::new));
    }
}