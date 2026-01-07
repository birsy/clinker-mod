package birsy.clinker.common.world.level.gen.system.surface.shaper;

import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public record BiomeSurfaceShaper(Either<ResourceKey<Biome>, TagKey<Biome>> biome, SurfaceShaper shaper) {
    public BiomeSurfaceShaper(ResourceKey<Biome> biome, SurfaceShaper shaper) {
        this(Either.left(biome), shaper);
    }
    public BiomeSurfaceShaper(TagKey<Biome> biomeTag, SurfaceShaper shaper) {
        this(Either.right(biomeTag), shaper);
    }
}
