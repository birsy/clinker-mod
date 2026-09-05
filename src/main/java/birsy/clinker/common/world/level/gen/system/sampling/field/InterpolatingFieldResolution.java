package birsy.clinker.common.world.level.gen.system.sampling.field;

public record InterpolatingFieldResolution(int xzScale, int yScale, boolean twoDimensional) {
    public static final InterpolatingFieldResolution DIRECT_2D = new InterpolatingFieldResolution(0);
    public static final InterpolatingFieldResolution DIRECT = new InterpolatingFieldResolution(0, 0);

    public static final InterpolatingFieldResolution FINE_2D = new InterpolatingFieldResolution(1);
    public static final InterpolatingFieldResolution COARSE_2D = new InterpolatingFieldResolution(2);
    public static final InterpolatingFieldResolution VERY_COARSE_2D = new InterpolatingFieldResolution(4);

    public static final InterpolatingFieldResolution FINE = new InterpolatingFieldResolution( 2, 1);
    public static final InterpolatingFieldResolution COARSE = new InterpolatingFieldResolution( 2, 2);
    public static final InterpolatingFieldResolution VERY_COARSE = new InterpolatingFieldResolution( 4, 4);

    public static final InterpolatingFieldResolution FINE_Y = new InterpolatingFieldResolution( 4, 1);
    public static final InterpolatingFieldResolution COARSE_Y = new InterpolatingFieldResolution( 2, 4);

    public InterpolatingFieldResolution {
        // 4 = log2 of 16, the xz chunk size.
        assert (xzScale <= 4 && yScale < Integer.SIZE);
    }
    public InterpolatingFieldResolution(int xzScale, int yScale) {
        this(xzScale, yScale, false);
    }
    public InterpolatingFieldResolution(int xzScale) {
        this(xzScale, Integer.SIZE, true);
    }

    public InterpolatingField create(int chunkHeight, int paddingBlocks) {
        return fromResolution(this.xzScale, this.yScale, this.twoDimensional, chunkHeight, paddingBlocks);
    }

    // without intermediary
    public static InterpolatingField fromResolution(int xzScale, int yScale, boolean twoDimensional, int chunkHeight, int paddingBlocks) {
        return twoDimensional ?
                fromResolution(xzScale, paddingBlocks) :
                new InterpolatingField(xzScale, yScale, chunkHeight, paddingBlocks);
    }
    public static InterpolatingField2d fromResolution(int xzScale, int paddingBlocks) {
        return new InterpolatingField2d(xzScale, paddingBlocks);
    }
}
