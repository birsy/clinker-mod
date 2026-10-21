package birsy.clinker.client;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.util.FastColor;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jetbrains.annotations.Nullable;

public class RandomizedTintHandler implements BlockColor {
    public static final RandomizedTintHandler INSTANCE = new RandomizedTintHandler();
    private static final int WHITE = FastColor.ARGB32.colorFromFloat(1, 1, 1, 1);
    private static final NormalNoise NOISE = NormalNoise.create(RandomSource.create(345678), -2, 0.5F, 1F, 1F);

    @Override
    public int getColor(BlockState state, @Nullable BlockAndTintGetter level, @Nullable BlockPos pos, int tintIndex) {
        if (level == null || pos == null)
            return WHITE;
        float noise = (float) NOISE.getValue(pos.getX(), 0F, pos.getZ()) * 0.5F + 0.5F;
        noise = Mth.sqrt(noise);
        float brightness = Mth.lerp(noise, 0.5F, 1.0F);
        return FastColor.ARGB32.colorFromFloat(1, brightness, brightness, brightness);
    }
}
