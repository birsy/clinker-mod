package birsy.clinker.common.world.level.gen.system.surface.shaper;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

import java.util.Map;

public class SurfaceShapers {
    // filled automatically after registration
    public static final Map<ResourceKey<Biome>, SurfaceShaper> shaperByBiome = new Object2ObjectOpenHashMap<>();
    public static final Map<TagKey<Biome>, SurfaceShaper> shaperByBiomeTag = new Object2ObjectOpenHashMap<>();
}
