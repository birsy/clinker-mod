package birsy.clinker.common.world.feature.enviornment;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.UniformInt;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;

public class SpeleothemConfig implements FeatureConfiguration {
    public static final Codec<birsy.clinker.common.world.feature.enviornment.SpeleothemConfig> CODEC = RecordCodecBuilder.create((speleothemConfig) -> {
        return speleothemConfig.group(BlockState.CODEC.fieldOf("state").forGetter((speleothemConfigState) -> {
            return speleothemConfigState.state;
        }), UniformInt.codec(0, 4, 4).fieldOf("radius").forGetter((speleothemConfigRadius) -> {
            return speleothemConfigRadius.radius;
        }), Codec.intRange(0, 64).fieldOf("minHeight").forGetter((speleothemConfigMinHeight) -> {
            return speleothemConfigMinHeight.minHeight;
        }), Codec.intRange(0, 64).fieldOf("maxHeight").forGetter((speleothemConfigMaxHeight) -> {
            return speleothemConfigMaxHeight.maxHeight;
        })).apply(speleothemConfig, birsy.clinker.common.world.feature.enviornment.SpeleothemConfig::new);
    });
    public final BlockState state;
    public final UniformInt radius;
    public final int minHeight;
    public final int maxHeight;

    public SpeleothemConfig(BlockState state, UniformInt radius, int minHeight, int maxHeight) {
        this.state = state;
        this.radius = radius;
        this.minHeight = minHeight;
        this.maxHeight = maxHeight;
    }
}
