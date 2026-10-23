package birsy.clinker.common.world.level.gen.system.fluid;

import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesFluids;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities.ModifiesWaterfallSynthesizer;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingField;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.DependencyRetriever;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public interface FluidField {
    BlockState AIR = Blocks.AIR.defaultBlockState();

    void fill(InterpolatingField waterfallPresence,
              DependencyRetriever seaFloorHeightRetriever,
              List<ModifiesFluids> fluidModifyingWorldFeatures,
              WorldFeatureContext worldFeatureContext);
    double getBorderDistance(int localX, int localY, int localZ);
    BlockState getFluidState(int x, int y, int z);
}
