package birsy.clinker.mixin.common;

import birsy.clinker.common.world.level.gen.worldfeature.MetaChunkMap;
import birsy.clinker.common.world.level.gen.worldfeature.MetaChunkMapOwner;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RandomState.class)
public class RandomStateMixin implements MetaChunkMapOwner {
    @Unique
    MetaChunkMap clinker$metaChunkMap;

    @Inject(method = "<init>",
            at = @At("TAIL"))
    private void clinker$initRandomState(NoiseGeneratorSettings settings, HolderGetter noiseParametersGetter, long levelSeed, CallbackInfo ci) {
        clinker$metaChunkMap = new MetaChunkMap((RandomState)(Object) this);
    }

    @Override
    public MetaChunkMap clinker$metaChunkMap() {
        return clinker$metaChunkMap;
    }
}
