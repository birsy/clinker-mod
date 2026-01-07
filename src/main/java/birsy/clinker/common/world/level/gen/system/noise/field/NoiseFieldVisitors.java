package birsy.clinker.common.world.level.gen.system.noise.field;

public final class NoiseFieldVisitors {
    public interface IndexVisitor {
        void visit(int index);
    }

    public interface PositionVisitor {
        void visit(int index, int x, int y, int z);
    }

    public interface BigVisitor {
        void visit(int index, int blockX, int blockY, int blockZ, int cellX, int cellY, int cellZ);
    }
}
