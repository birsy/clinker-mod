package birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.capabilities;

import birsy.clinker.common.world.level.gen.system.fluid.FluidLevel;
import birsy.clinker.common.world.level.gen.system.sampling.field.InterpolatingField;
import birsy.clinker.common.world.level.gen.system.metachunk.worldfeature.WorldFeatureContext;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.DependencyRetriever;
import birsy.clinker.common.world.level.gen.system.sampling.synthesizer.SynthesizerCache;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.RandomState;

public interface ModifiesFluids extends WorldFeatureCapability {
    FluidLevel modifyFluidLevel(double x, double y, double z,
                                FluidLevel currentFluidLevel,
                                WorldFeatureContext context,
                                DependencyRetriever seaFloorHeightRetriever);
}
