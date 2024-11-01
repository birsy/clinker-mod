package birsy.clinker.common.world.level.gen.legacy;

import net.minecraft.world.phys.Vec2;

import java.util.HashMap;
import java.util.Map;

/**
 * caches values, such as generation regions, that are currently being used in worldgen.
 * this is not saved!
 */
public class GenerationCache {
    private Map<Vec2, GenerationRegion> regionMap;

    public GenerationCache() {
        this.regionMap = new HashMap<>(128);
    }
}
