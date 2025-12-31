package birsy.clinker.common.world.level.gen.biome;

import birsy.clinker.common.world.level.gen.noise.NoiseComputerExecutor;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.RandomState;

import java.util.Set;

public abstract class ClimateZone {
    public ClimateZone(HolderGetter<Biome> biomeGetter) {}

    public abstract Set<Holder<Biome>> collectPossibleBiomes();
    public abstract Holder<Biome> getBiome(
            int quartPosX, int quartPosY, int quartPosZ,
            NoiseComputerExecutor noiseExecutor, RandomState randomState
    );
}
