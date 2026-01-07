package birsy.clinker.common.world.level.gen.system.surface.decorator;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Map;

public class SurfaceDecorators {
    public static final Map<ResourceKey<Biome>, SurfaceDecorator> decoratorByBiome = new Object2ObjectOpenHashMap<>();
    public static final Map<TagKey<Biome>, SurfaceDecorator> decoratorByBiomeTag = new Object2ObjectOpenHashMap<>();
}
