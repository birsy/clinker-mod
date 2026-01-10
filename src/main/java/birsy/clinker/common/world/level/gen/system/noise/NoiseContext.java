package birsy.clinker.common.world.level.gen.system.noise;

import java.util.function.Supplier;

public interface NoiseContext extends NoiseProvider {
    double retrieve(NoiseComputer computer, int x, int y, int z);
    default double retrieve(Supplier<NoiseComputer> computer, int x, int y, int z) {
        return retrieve(computer.get(), x, y, z);
    }
}
