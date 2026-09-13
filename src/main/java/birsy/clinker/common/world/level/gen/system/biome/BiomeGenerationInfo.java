package birsy.clinker.common.world.level.gen.system.biome;

import birsy.clinker.common.world.level.gen.OthershoreGenerationConstants;
import birsy.clinker.common.world.level.gen.content.surface.decoration.NoOpSurfaceDecorator;
import birsy.clinker.common.world.level.gen.content.surface.shape.FlatWithCliffsSurfaceShape;
import birsy.clinker.common.world.level.gen.system.surface.decoration.SurfaceDecorator;
import birsy.clinker.common.world.level.gen.system.surface.shape.SurfaceShape;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public record BiomeGenerationInfo(ResourceKey<Biome> biomeKey,
                                  Supplier<BlockState> seaBlock, int seaLevel,
                                  SurfaceShape shaper,
                                  SurfaceDecorator decorator) {
    public static final BiomeGenerationInfo DEFAULT = new BiomeGenerationInfo(
            Biomes.THE_VOID,
            Blocks.WATER::defaultBlockState, OthershoreGenerationConstants.SEA_HEIGHT,
            new FlatWithCliffsSurfaceShape(OthershoreGenerationConstants.SEA_HEIGHT + 20, OthershoreGenerationConstants.SEA_HEIGHT),
            new NoOpSurfaceDecorator()
    );

    private static final Map<ResourceKey<Biome>, BiomeGenerationInfo> GEN_INFO_BY_BIOME = new HashMap<>();
    public static BiomeGenerationInfo fromBiome(Holder<Biome> biome) {
        return GEN_INFO_BY_BIOME.getOrDefault(biome.getKey(), DEFAULT);
    }
    public static void bakeFromRegistry(Registry<BiomeGenerationInfo> registry) {
        for (BiomeGenerationInfo genInfo : registry) GEN_INFO_BY_BIOME.put(genInfo.biomeKey(), genInfo);
    }

    public static BiomeGenerationInfo.Builder builder() {
        return new BiomeGenerationInfo.Builder();
    }

    public static class Builder {
        private Supplier<BlockState> seaBlock = DEFAULT.seaBlock();
        int seaLevel = DEFAULT.seaLevel();
        private SurfaceShape shaper = DEFAULT.shaper();
        private SurfaceDecorator decorator = DEFAULT.decorator();

        public Builder seaFluid(Supplier<BlockState> state) {
            this.seaBlock = state;
            return this;
        }
        public Builder seaLevel(int level) {
            this.seaLevel = level;
            return this;
        }
        public Builder surfaceShape(SurfaceShape shaper) {
            this.shaper = shaper;
            return this;
        }
        public Builder placeholderSurfaceShape(int height, int expectedNeighborHeight) {
            return this.surfaceShape(new FlatWithCliffsSurfaceShape(height, expectedNeighborHeight));
        }
        public Builder placeholderSurfaceShape(int height) {
            return this.surfaceShape(new FlatWithCliffsSurfaceShape(height, OthershoreGenerationConstants.SEA_HEIGHT));
        }
        public Builder decorator(SurfaceDecorator decorator) {
            this.decorator = decorator;
            return this;
        }

        public BiomeGenerationInfo build(ResourceKey<Biome> biomeKey) {
            return new BiomeGenerationInfo(biomeKey, seaBlock, seaLevel, shaper, decorator);
        }
    }
}
