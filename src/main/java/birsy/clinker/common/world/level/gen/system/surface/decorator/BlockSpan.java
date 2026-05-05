package birsy.clinker.common.world.level.gen.system.surface.decorator;

import net.minecraft.world.level.block.state.BlockState;

import java.util.List;

public record BlockSpan(int bottomY, int topY, boolean solid) {
    public int height() { return topY - bottomY + 1; }

    public static int spanIndexAtY(List<BlockSpan> column, int y) {
        for (int i = 0; i < column.size(); i++) {
            if (column.get(i).bottomY <= y) return i;
        }
        return column.size() - 1;
    }
    public static BlockSpan spanAtY(List<BlockSpan> column, int y) {
        return column.get(spanIndexAtY(column, y));
    }
}
