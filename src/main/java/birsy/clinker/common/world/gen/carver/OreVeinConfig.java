package birsy.clinker.common.world.gen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.carver.CarverConfiguration;

public class OreVeinConfig implements CarverConfiguration {
    public static final Codec<OreVeinConfig> CODEC = RecordCodecBuilder.create((builder) -> {
        return builder.group(BlockState.CODEC.fieldOf("ore").forGetter((config) -> {
            return config.ore;
        }), BlockState.CODEC.fieldOf("filler").forGetter((config) -> {
            return config.filler;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter((config) -> {
            return config.probability;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("vein_size").forGetter((config) -> {
            return config.veinSize;
        }), Codec.floatRange(0.0F, 1.0F).fieldOf("density").forGetter((config) -> {
            return config.density;
        }), Codec.intRange(0, 255).fieldOf("minHeight").forGetter((config) -> {
            return config.minHeight;
        }), Codec.intRange(0, 255).fieldOf("minFalloffHeight").forGetter((config) -> {
            return config.minFalloff;
        }), Codec.intRange(0, 255).fieldOf("maxHeight").forGetter((config) -> {
            return config.maxHeight;
        }), Codec.intRange(0, 255).fieldOf("maxFalloffHeight").forGetter((config) -> {
            return config.maxFalloff;
        })).apply(builder, OreVeinConfig::new);
    });

    public final BlockState ore;
    public final BlockState filler;

    public final float probability;
    public final float veinSize;
    public final float density;

    public final int minHeight;
    public final int minFalloff;
    public final int maxHeight;
    public final int maxFalloff;


    /**
     * Config for ore veins!
     * Used exclusively by lead, for now.
     *
     * @param ore The ore that occurs within the vein.
     * @param filler The "filler" block that occurs within the vein to signify its existence.
     * @param probability The probability of a vein occurring in the world
     * @param veinSize The thickness of the vein's roots
     * @param density The density of the ore within the vein - 1.0f will have all ore and no filler, 0.0f will have zero ore and all filler.
     * @param minHeight The minimum height of the vein.
     * @param minFalloff The falloff of the minimum height - determines the gradient from nothing to all veins.
     * @param maxHeight The maximum height of the vein.
     * @param maxFalloff The falloff of the maximum height - determines the gradient from all veins to nothing.
     */
    public OreVeinConfig(BlockState ore, BlockState filler, float probability, float veinSize, float density, int minHeight, int minFalloff, int maxHeight, int maxFalloff) {
        this.ore = ore;
        this.filler = filler;
        this.probability = probability;
        this.veinSize = veinSize;
        this.density = density;
        this.minHeight = minHeight;
        this.minFalloff = minFalloff;
        this.maxHeight = maxHeight;
        this.maxFalloff = maxFalloff;
    }
}
