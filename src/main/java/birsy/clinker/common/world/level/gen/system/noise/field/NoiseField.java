package birsy.clinker.common.world.level.gen.system.noise.field;

import birsy.clinker.common.world.level.gen.system.noise.NoiseContext;

public abstract class NoiseField {
    public static final int CHUNK_WIDTH = 16;
    final int maxY;
    final int paddingBlocks, paddingCells;

    protected NoiseField(int maxY, int paddingBlocks, int paddingCells) {
        this.maxY = maxY;
        this.paddingBlocks = paddingBlocks;
        this.paddingCells = paddingCells;
    }

    public abstract double[] array();

    public abstract double retrieve(int x, int y, int z);

    public abstract void fill(int startY, int endY, int minX, int minY, int minZ, NoiseFieldFiller function, NoiseContext context);
    public void fill(int minX, int minY, int minZ, NoiseFieldFiller function, NoiseContext context) {
        this.fill(0, maxY, minX, minY, minZ, function, context);
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
