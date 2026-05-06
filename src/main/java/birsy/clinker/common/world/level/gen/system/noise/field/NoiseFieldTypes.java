package birsy.clinker.common.world.level.gen.system.noise.field;

public class NoiseFieldTypes {
    public static final NoiseFieldType<Direct2DNoiseField> DIRECT_2D =
            (chunkHeight, paddingBlocks) ->
                    new Direct2DNoiseField(paddingBlocks);
    public static final NoiseFieldType<DirectNoiseField> DIRECT =
            (chunkHeight, paddingBlocks) ->
                    new DirectNoiseField(chunkHeight, paddingBlocks);

    public static final NoiseFieldType<Interpolated2DNoiseField> FINE_2D =
            (chunkHeight, paddingBlocks) ->
                    new Interpolated2DNoiseField(1, smallestCellScale(paddingBlocks));
    public static final NoiseFieldType<Interpolated2DNoiseField> COARSE_2D =
            (chunkHeight, paddingBlocks) ->
                    new Interpolated2DNoiseField(2, smallestCellScale(paddingBlocks));
    public static final NoiseFieldType<Interpolated2DNoiseField> VERY_COARSE_2D =
            (chunkHeight, paddingBlocks) ->
                    new Interpolated2DNoiseField(4, smallestCellScale(paddingBlocks));

    public static final NoiseFieldType<InterpolatedNoiseField> FINE =
            (chunkHeight, paddingBlocks) ->
                    new InterpolatedNoiseField(chunkHeight, 2, 1, smallestCellScale(paddingBlocks));
    public static final NoiseFieldType<InterpolatedNoiseField> COARSE =
            (chunkHeight, paddingBlocks) ->
                    new InterpolatedNoiseField(chunkHeight, 2, 2, smallestCellScale(paddingBlocks));
    public static final NoiseFieldType<InterpolatedNoiseField> VERY_COARSE =
            (chunkHeight, paddingBlocks) ->
                    new InterpolatedNoiseField(chunkHeight, 4, 4, smallestCellScale(paddingBlocks));

    public static final NoiseFieldType<InterpolatedNoiseField> FINE_Y =
            (chunkHeight, paddingBlocks) ->
                    new InterpolatedNoiseField(chunkHeight, 4, 1, smallestCellScale(paddingBlocks));
    public static final NoiseFieldType<InterpolatedNoiseField> COARSE_Y =
            (chunkHeight, paddingBlocks) ->
                    new InterpolatedNoiseField(chunkHeight, 2, 4, smallestCellScale(paddingBlocks));
    public static int smallestCellScale(int targetSize) {
        if (targetSize <= 1) return 0;
        return 32 - Integer.numberOfLeadingZeros(targetSize - 1);
    }
}
