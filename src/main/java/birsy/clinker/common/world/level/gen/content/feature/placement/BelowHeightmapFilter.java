package birsy.clinker.common.world.level.gen.content.feature.placement;

import birsy.clinker.core.registry.worldgen.ClinkerPlacementModifierTypes;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementFilter;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

public class BelowHeightmapFilter extends PlacementFilter {
    public static final MapCodec<BelowHeightmapFilter> CODEC = RecordCodecBuilder.mapCodec(
            obj -> obj.group(
                    Heightmap.Types.CODEC.fieldOf("heightmap").forGetter(filter -> filter.heightmap)
                    ).apply(obj, BelowHeightmapFilter::new)
    );

    private final Heightmap.Types heightmap;

    public BelowHeightmapFilter(Heightmap.Types heightmap) {
        this.heightmap = heightmap;
    }

    @Override
    protected boolean shouldPlace(PlacementContext context, RandomSource random, BlockPos pos) {
        int y = context.getHeight(this.heightmap, pos.getX(), pos.getZ());
        return pos.getY() < y;
    }

    @Override
    public PlacementModifierType<?> type() {
        return ClinkerPlacementModifierTypes.HEIGHTMAP_STEEPNESS_FILTER.get();
    }
}
