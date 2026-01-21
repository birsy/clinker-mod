package birsy.clinker.common.world.level.gen.system;

import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.WorldGenerationContext;

public record VerticalRange(int min, int max) {
    public static VerticalRange fromHeights(int y1, int y2) {
        if (y1 > y2) return new VerticalRange(y2, y1);
        return new VerticalRange(y1, y2);
    }
}
