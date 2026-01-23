package birsy.clinker.common.world.level.gen.system;

import java.util.Objects;

public final class VerticalRange {
    public int min;
    public int max;

    public VerticalRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public static VerticalRange fromHeights(int y1, int y2) {
        if (y1 > y2) return new VerticalRange(y2, y1);
        return new VerticalRange(y1, y2);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (VerticalRange) obj;
        return this.min == that.min && this.max == that.max;
    }

    @Override
    public int hashCode() {
        return Objects.hash(min, max);
    }

    @Override
    public String toString() {
        return "VerticalRange[" + "min=" + min + ", " + "max=" + max + ']';
    }
}
