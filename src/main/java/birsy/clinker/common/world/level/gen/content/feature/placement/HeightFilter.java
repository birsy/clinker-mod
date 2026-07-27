package birsy.clinker.common.world.level.gen.content.feature.placement;

import birsy.clinker.core.registry.worldgen.ClinkerPlacementModifierTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class HeightFilter extends PlacementFilter {
    public static final MapCodec<HeightFilter> CODEC = RecordCodecBuilder.mapCodec(
            obj -> obj.group(VerticalAnchor.CODEC.optionalFieldOf("min_inclusive", VerticalAnchor.bottom()).forGetter(filter -> filter.min),
                             VerticalAnchor.CODEC.optionalFieldOf("max_inclusive", VerticalAnchor.top()).forGetter(filter -> filter.max))
                      .apply(obj, HeightFilter::new)
    );
    final VerticalAnchor min, max;

    public HeightFilter(VerticalAnchor min, VerticalAnchor max) {
        this.min = min;
        this.max = max;
    }

    @Override
    protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
        return pos.getY() >= min.resolveY(context) && pos.getY() <= max.resolveY(context);
    }

    @Override
    public PlacementModifierType<?> type() {
        return ClinkerPlacementModifierTypes.HEIGHT_FILTER.get();
    }
}
