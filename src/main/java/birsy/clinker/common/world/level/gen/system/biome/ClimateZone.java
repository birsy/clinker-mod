package birsy.clinker.common.world.level.gen.system.biome;

import birsy.clinker.common.world.level.gen.system.noise.NoiseFieldCache;
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
            NoiseFieldCache noiseExecutor, RandomState randomState
    );
}
