package birsy.clinker.client.render.world.gas;

import net.minecraft.core.SectionPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

// temporary
public class GasSectionManager {
    private final Map<SectionPos, GasSection> map;
    final Level level;

    public GasSectionManager(Level level) {
        this.level = level;
        this.map = new HashMap<>();
    }

    public GasSection getGasSection(SectionPos sectionPos) {
        if (!this.map.containsKey(sectionPos)) {
            GasSection newSection = new GasSection(level, sectionPos);
            this.map.put(sectionPos, newSection);
            return newSection;
        }

        return this.map.get(sectionPos);
    }
}
