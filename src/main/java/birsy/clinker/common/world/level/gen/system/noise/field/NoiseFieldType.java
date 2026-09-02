package birsy.clinker.common.world.level.gen.system.noise.field;

public record NoiseFieldType(int xzScale, int yScale, boolean twoDimensional) {
    
    public static final NoiseFieldType DIRECT_2D = new NoiseFieldType(0);
    public static final NoiseFieldType DIRECT = new NoiseFieldType(0, 0);

    public static final NoiseFieldType FINE_2D = new NoiseFieldType(1);
    public static final NoiseFieldType COARSE_2D = new NoiseFieldType(2);
    public static final NoiseFieldType VERY_COARSE_2D = new NoiseFieldType(4);

    public static final NoiseFieldType FINE = new NoiseFieldType( 2, 1);
    public static final NoiseFieldType COARSE = new NoiseFieldType( 2, 2);
    public static final NoiseFieldType VERY_COARSE = new NoiseFieldType( 4, 4);

    public static final NoiseFieldType FINE_Y = new NoiseFieldType( 4, 1);
    public static final NoiseFieldType COARSE_Y = new NoiseFieldType( 2, 4);

    public NoiseFieldType(int xzScale, int yScale) { this(xzScale, yScale, true); }
    public NoiseFieldType(int xzScale) { this(xzScale, 32, false); }

    public NoiseField create(int chunkHeight, int paddingBlocks) {
        int paddingCells = Math.ceilDiv(paddingBlocks, xzScale);
        if (this.twoDimensional) return NoiseField.twoDimensional(xzScale, paddingCells);
        else return new NoiseField(chunkHeight, xzScale, yScale, paddingCells);
    }
}
