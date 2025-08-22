package birsy.clinker.common.world.level.gen;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.QuartPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.*;
import net.minecraft.world.level.levelgen.DensityFunction;

import java.util.stream.Stream;

public class OthershoreBiomeSource extends BiomeSource {
    public static final MapCodec<OthershoreBiomeSource> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(RegistryOps.retrieveGetter(Registries.BIOME))
                        .apply(instance, instance.stable(OthershoreBiomeSource::new))
    );

    public OthershoreBiomeSource(HolderGetter<Biome> biomeGetter) {

    }

    @Override
    protected Stream<Holder<Biome>> collectPossibleBiomes() {
        return Stream.empty();
    }

    @Override
    public Holder<Biome> getNoiseBiome(int x, int y, int z, Climate.Sampler sampler) {
        int blockX = QuartPos.toBlock(x);
        int blockY = QuartPos.toBlock(y);
        int blockZ = QuartPos.toBlock(z);
        int sectionX = SectionPos.blockToSectionCoord(blockX);
        int sectionZ = SectionPos.blockToSectionCoord(blockZ);


        if ((long)sectionX * (long)sectionX + (long)sectionZ * (long)sectionZ <= 4096L) {
            return this.end;
        } else {
            int j1 = (SectionPos.blockToSectionCoord(blockX) * 2 + 1) * 8;
            int k1 = (SectionPos.blockToSectionCoord(blockZ) * 2 + 1) * 8;
            double d0 = sampler.erosion().compute(new DensityFunction.SinglePointContext(j1, blockY, k1));
            if (d0 > 0.25) {
                return this.highlands;
            } else if (d0 >= -0.0625) {
                return this.midlands;
            } else {
                return d0 < -0.21875 ? this.islands : this.barrens;
            }
        }
    }

    @Override
    protected MapCodec<? extends BiomeSource> codec() {
        return CODEC;
    }
}
