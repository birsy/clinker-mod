package birsy.clinker.common.world.level.gen.system.noise.field;

public class NoiseFieldTypes {
    public static final NoiseFieldType<InterpolatedNoiseField> DIRECT_2D =
            (chunkHeight, paddingBlocks) -> InterpolatedNoiseField.twoDimensional(0, paddingBlocks);
    public static final NoiseFieldType<InterpolatedNoiseField> DIRECT =
            (chunkHeight, paddingBlocks) -> new InterpolatedNoiseField(chunkHeight, 0, 0, paddingBlocks);

    public static final NoiseFieldType<InterpolatedNoiseField> FINE_2D =
            (chunkHeight, paddingBlocks) -> InterpolatedNoiseField.twoDimensional(1, smallestCellScale(paddingBlocks));
    public static final NoiseFieldType<InterpolatedNoiseField> COARSE_2D =
            (chunkHeight, paddingBlocks) -> InterpolatedNoiseField.twoDimensional(2, smallestCellScale(paddingBlocks));
    public static final NoiseFieldType<InterpolatedNoiseField> VERY_COARSE_2D =
            (chunkHeight, paddingBlocks) -> InterpolatedNoiseField.twoDimensional(4, smallestCellScale(paddingBlocks));

    public static final NoiseFieldType<InterpolatedNoiseField> FINE =
            (chunkHeight, paddingBlocks) -> new InterpolatedNoiseField(chunkHeight, 2, 1, smallestCellScale(paddingBlocks));
    public static final NoiseFieldType<InterpolatedNoiseField> COARSE =
            (chunkHeight, paddingBlocks) -> new InterpolatedNoiseField(chunkHeight, 2, 2, smallestCellScale(paddingBlocks));
    public static final NoiseFieldType<InterpolatedNoiseField> VERY_COARSE =
            (chunkHeight, paddingBlocks) -> new InterpolatedNoiseField(chunkHeight, 4, 4, smallestCellScale(paddingBlocks));

    public static final NoiseFieldType<InterpolatedNoiseField> FINE_Y =
            (chunkHeight, paddingBlocks) -> new InterpolatedNoiseField(chunkHeight, 4, 1, smallestCellScale(paddingBlocks));
    public static final NoiseFieldType<InterpolatedNoiseField> COARSE_Y =
            (chunkHeight, paddingBlocks) -> new InterpolatedNoiseField(chunkHeight, 2, 4, smallestCellScale(paddingBlocks));

    public static int smallestCellScale(int targetSize) {
        if (targetSize <= 1) return 0;
        return 32 - Integer.numberOfLeadingZeros(targetSize - 1);
    }
}
