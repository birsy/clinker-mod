package birsy.clinker.common.world.level.gen.noise.field;

public class NoiseFieldTypes {
    public static final NoiseFieldType DIRECT_2D =
            (executor, paddingBlocks) ->
                    new Direct2DNoiseField(paddingBlocks);
    public static final NoiseFieldType DIRECT =
            (executor, paddingBlocks) ->
                    new DirectNoiseField(executor.chunkHeight, paddingBlocks);

    public static final NoiseFieldType FINE_2D =
            (executor, paddingBlocks) ->
                    new Interpolated2DNoiseField(1, smallestCellScale(paddingBlocks));
    public static final NoiseFieldType COARSE_2D =
            (executor, paddingBlocks) ->
                    new Interpolated2DNoiseField(2, smallestCellScale(paddingBlocks));
    public static final NoiseFieldType VERY_COARSE_2D =
            (executor, paddingBlocks) ->
                    new Interpolated2DNoiseField(4, smallestCellScale(paddingBlocks));

    public static final NoiseFieldType FINE =
            (executor, paddingBlocks) ->
                    new InterpolatedNoiseField(executor.chunkHeight, 1, 2, smallestCellScale(paddingBlocks));
    public static final NoiseFieldType COARSE =
            (executor, paddingBlocks) ->
                    new InterpolatedNoiseField(executor.chunkHeight, 2, 3, smallestCellScale(paddingBlocks));
    public static final NoiseFieldType VERY_COARSE =
            (executor, paddingBlocks) ->
                    new InterpolatedNoiseField(executor.chunkHeight, 4, 4, smallestCellScale(paddingBlocks));

    public static int smallestCellScale(int targetSize) {
        if (targetSize <= 1) return 0;
        return 32 - Integer.numberOfLeadingZeros(targetSize - 1);
    }
}
