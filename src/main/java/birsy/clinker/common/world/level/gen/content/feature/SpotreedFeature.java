package birsy.clinker.common.world.level.gen.content.feature;

import birsy.clinker.common.world.block.plant.SpotreedBlock;
import birsy.clinker.core.registry.ClinkerBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.material.Fluids;

public class SpotreedFeature extends Feature<SpotreedFeature.SpotreedFeatureConfiguration> {

    public SpotreedFeature(Codec<SpotreedFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<SpotreedFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource random = context.random();
        BlockPos origin = context.origin();
        int length = context.config().length.sample(context.random());
        Direction growthDirection = context.config().upsideDown ? Direction.DOWN : Direction.UP;

        // don't let it place if there's a block there already
        if (!level.getBlockState(origin).canBeReplaced()) return false;

        BlockPos.MutableBlockPos mPos = origin.mutable();
        mPos.move(growthDirection.getOpposite());

        // don't let it place if it can't actually survive there
        BlockState state = ClinkerBlocks.SPOTREED.get().defaultBlockState()
                .setValue(SpotreedBlock.VERTICAL_DIRECTION, growthDirection);
        if (!state.canSurvive(level, origin)) return false;

        // don't let it place on top of itself
        BlockState rootState = level.getBlockState(mPos);
        if (rootState.is(ClinkerBlocks.SPOTREED)) return false;

        mPos.move(growthDirection);

        int age = 1;
        for (int i = 0; i < length - 1; i++) {
            BlockState stateAtTip = level.getBlockState(mPos);
            mPos.move(growthDirection);
            BlockState stateAboveTip = level.getBlockState(mPos);
            mPos.move(growthDirection.getOpposite());

            boolean canContinueGrowing = stateAboveTip.canBeReplaced();
            level.setBlock(mPos,
                    state.setValue(SpotreedBlock.AGE, age)
                         .setValue(SpotreedBlock.WATERLOGGED, stateAtTip.getFluidState().is(Fluids.WATER))
                         .setValue(SpotreedBlock.LIT, !canContinueGrowing),
                    2
            );
            if (!canContinueGrowing) return true;

            age = Math.min(age + random.nextIntBetweenInclusive(0, 2), 3);
            mPos.move(growthDirection);
        }

        // place tip
        level.setBlock(mPos,
                state.setValue(SpotreedBlock.AGE, 4)
                        .setValue(SpotreedBlock.WATERLOGGED, level.getBlockState(mPos).getFluidState().is(Fluids.WATER))
                        .setValue(SpotreedBlock.LIT, true),
                2
        );

        return true;
    }

    public record SpotreedFeatureConfiguration(IntProvider length, boolean upsideDown) implements FeatureConfiguration {
        public static final Codec<SpotreedFeature.SpotreedFeatureConfiguration> CODEC = RecordCodecBuilder.create(
                codec -> codec.group(
                        IntProvider.CODEC.fieldOf("length").forGetter(config -> config.length),
                        Codec.BOOL.fieldOf("upside_down").orElse(false).forGetter(config -> config.upsideDown)
                ).apply(codec, SpotreedFeature.SpotreedFeatureConfiguration::new)
        );
    }
}
