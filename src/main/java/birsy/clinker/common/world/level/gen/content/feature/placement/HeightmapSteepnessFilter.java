package birsy.clinker.common.world.level.gen.content.feature.placement;

import birsy.clinker.core.registry.worldgen.ClinkerPlacementModifierTypes;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class HeightmapSteepnessFilter extends PlacementFilter {
    public static final MapCodec<HeightmapSteepnessFilter> CODEC = RecordCodecBuilder.mapCodec(
            obj -> obj.group(Heightmap.Types.CODEC.fieldOf("heightmap").forGetter(filter -> filter.heightmap),
                             IntProvider.CODEC.fieldOf("max_gradient_inclusive").forGetter(filter -> filter.maxGradient),
                             Codec.INT.fieldOf("radius").orElse(1).forGetter(filter -> filter.radius)
                    ).apply(obj, HeightmapSteepnessFilter::new)
    );
    private static final int[] OFFSETS = {
            -1, -1,
            -1,  1,
             1, -1,
             1,  1,
            -1,  0,
             0, -1,
             0,  1,
             1,  0,
    };

    private final Heightmap.Types heightmap;
    private final IntProvider maxGradient;
    private final int radius;

    public HeightmapSteepnessFilter(Heightmap.Types heightmap, IntProvider maxGradient, int radius) {
        this.heightmap = heightmap;
        this.maxGradient = maxGradient;
        this.radius = radius;
    }

    @Override
    protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
        int y = context.getHeight(this.heightmap, pos.getX(), pos.getZ());
        int maxSteepness = this.maxGradient.sample(random);
        for (int i = radius - 1; i >= 0; i--) {
            for (int j = 0; j < OFFSETS.length; j += 2) {
                int offsetY = context.getHeight(this.heightmap, pos.getX() + OFFSETS[j] * i, pos.getZ() + OFFSETS[j + 1] * i);
                if (Math.abs(offsetY - y) > maxSteepness) return false;
            }
        }
        return true;
    }

    @Override
    public PlacementModifierType<?> type() {
        return ClinkerPlacementModifierTypes.HEIGHTMAP_STEEPNESS_FILTER.get();
    }
}
