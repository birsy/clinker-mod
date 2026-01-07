package birsy.clinker.common.world.level.gen.system.noise.field;

public class NoiseFieldTypes {
    public static final NoiseFieldType DIRECT_2D =
            (chunkHeight, paddingBlocks) ->
                    new Direct2DNoiseField(paddingBlocks);
    public static final NoiseFieldType DIRECT =
            (chunkHeight, paddingBlocks) ->
                    new DirectNoiseField(chunkHeight, paddingBlocks);

    public static final NoiseFieldType FINE_2D =
            (chunkHeight, paddingBlocks) ->
                    new Interpolated2DNoiseField(1, smallestCellScale(paddingBlocks));
    public static final NoiseFieldType COARSE_2D =
            (chunkHeight, paddingBlocks) ->
                    new Interpolated2DNoiseField(2, smallestCellScale(paddingBlocks));
    public static final NoiseFieldType VERY_COARSE_2D =
            (chunkHeight, paddingBlocks) ->
                    new Interpolated2DNoiseField(4, smallestCellScale(paddingBlocks));

    public static final NoiseFieldType FINE =
            (chunkHeight, paddingBlocks) ->
                    new InterpolatedNoiseField(chunkHeight, 1, 2, smallestCellScale(paddingBlocks));
    public static final NoiseFieldType COARSE =
            (chunkHeight, paddingBlocks) ->
                    new InterpolatedNoiseField(chunkHeight, 2, 3, smallestCellScale(paddingBlocks));
    public static final NoiseFieldType VERY_COARSE =
            (chunkHeight, paddingBlocks) ->
                    new InterpolatedNoiseField(chunkHeight, 4, 4, smallestCellScale(paddingBlocks));

    public static int smallestCellScale(int targetSize) {
        if (targetSize <= 1) return 0;
        return 32 - Integer.numberOfLeadingZeros(targetSize - 1);
    }
}
