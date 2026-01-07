package birsy.clinker.common.world.level.gen.system.surface;

import com.mojang.datafixers.util.Either;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public record BiomeSurfaceDecorator(Either<ResourceKey<Biome>, TagKey<Biome>> biome, SurfaceDecorator decorator) {
    public BiomeSurfaceDecorator(ResourceKey<Biome> biome, SurfaceDecorator decorator) {
        this(Either.left(biome), decorator);
    }
    public BiomeSurfaceDecorator(TagKey<Biome> biomeTag, SurfaceDecorator decorator) {
        this(Either.right(biomeTag), decorator);
    }
}
