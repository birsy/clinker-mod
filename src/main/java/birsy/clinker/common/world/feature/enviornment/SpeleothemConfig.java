package birsy.clinker.common.world.feature.enviornment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.world.gen.feature.FeatureSpread;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class SpeleothemConfig implements IFeatureConfig {
    public static final Codec<birsy.clinker.common.world.feature.enviornment.SpeleothemConfig> CODEC = RecordCodecBuilder.create((speleothemConfig) -> {
        return speleothemConfig.group(BlockState.CODEC.fieldOf("state").forGetter((speleothemConfigState) -> {
            return speleothemConfigState.state;
        }), FeatureSpread.func_242254_a(0, 4, 4).fieldOf("radius").forGetter((speleothemConfigRadius) -> {
            return speleothemConfigRadius.radius;
        }), Codec.intRange(0, 64).fieldOf("minHeight").forGetter((speleothemConfigMinHeight) -> {
            return speleothemConfigMinHeight.minHeight;
        }), Codec.intRange(0, 64).fieldOf("maxHeight").forGetter((speleothemConfigMaxHeight) -> {
            return speleothemConfigMaxHeight.maxHeight;
        })).apply(speleothemConfig, birsy.clinker.common.world.feature.enviornment.SpeleothemConfig::new);
    });
    public final BlockState state;
    public final FeatureSpread radius;
    public final int minHeight;
    public final int maxHeight;

    public SpeleothemConfig(BlockState state, FeatureSpread radius, int minHeight, int maxHeight) {
        this.state = state;
        this.radius = radius;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }
}
