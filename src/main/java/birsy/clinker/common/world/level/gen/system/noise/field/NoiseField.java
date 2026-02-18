package birsy.clinker.common.world.level.gen.system.noise.field;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;

public abstract class NoiseField {
    public static final int CHUNK_WIDTH = 16;
    public final int maxY;
    public final int paddingBlocks, paddingCells;

    protected NoiseField(int maxY, int paddingBlocks, int paddingCells) {
        this.maxY = maxY;
        this.paddingBlocks = paddingBlocks;
        this.paddingCells = paddingCells;
    }

    public abstract double[] array();

    public abstract double retrieve(int x, int y, int z);

    public abstract void fill(int minLocalY, int maxLocalY, int minX, int minY, int minZ, NoiseContext context, NoiseFieldFiller function);
    public void fill(int minX, int minY, int minZ, NoiseContext context, NoiseFieldFiller function) {
        this.fill(0, maxY, minX, minY, minZ, context, function);
    }

    public abstract void byIndex(int minLocalY, int maxLocalY, NoiseFieldVisitors.IndexVisitor visitor);
    public void byIndex(NoiseFieldVisitors.IndexVisitor visitor) {
        this.byIndex(0, maxY, visitor);
    }
    public abstract void byBlock(int minLocalY, int maxLocalY, NoiseFieldVisitors.PositionVisitor visitor);
    public void byBlock(NoiseFieldVisitors.PositionVisitor visitor) {
        this.byBlock(0, maxY, visitor);
    }
    public abstract void byCell(int minLocalY, int maxLocalY, NoiseFieldVisitors.PositionVisitor visitor);
    public void byCell(NoiseFieldVisitors.PositionVisitor visitor) {
        this.byCell(0, maxY, visitor);
    }
    public abstract void visit(int minLocalY, int maxLocalY, NoiseFieldVisitors.BigVisitor visitor);
    public void visit(NoiseFieldVisitors.BigVisitor visitor) {
        this.visit(0, maxY, visitor);
    }
}
