package birsy.clinker.common.world.gen.surfacebuilder;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public abstract class ClinkerSurfaceBuilder<C extends ISurfaceBuilderConfig> extends SurfaceBuilder<SurfaceBuilderConfig> {
    private float steepness;

    public ClinkerSurfaceBuilder(Codec<SurfaceBuilderConfig> codec) {
        super(codec);
    }

    public void setSteepness(float steepness) {
        this.steepness = steepness;
    }
}
