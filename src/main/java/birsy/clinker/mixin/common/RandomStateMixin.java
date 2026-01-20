package birsy.clinker.mixin.common;

import birsy.clinker.common.world.level.gen.system.noise.SeededNoiseHolder;
import birsy.clinker.common.world.level.gen.system.noise.SeededNoiseHolderHolder;
import birsy.clinker.common.world.level.gen.system.WorldSeedHolder;
import birsy.clinker.common.world.level.gen.system.worldfeature.MetaChunkMap;
import birsy.clinker.common.world.level.gen.system.worldfeature.MetaChunkMapHolder;
import net.minecraft.core.HolderGetter;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.RandomState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RandomState.class)
public class RandomStateMixin implements MetaChunkMapHolder, WorldSeedHolder, SeededNoiseHolderHolder {
    @Unique MetaChunkMap clinker$metaChunkMap;
    @Unique SeededNoiseHolder clinker$noiseHolder;
    @Unique long clinker$worldSeed;

    @Inject(method = "<init>",
            at = @At("TAIL"))
    private void clinker$initRandomState(NoiseGeneratorSettings settings, HolderGetter noiseParametersGetter, long levelSeed, CallbackInfo ci) {
        clinker$metaChunkMap = new MetaChunkMap((RandomState)(Object)this);
        clinker$noiseHolder = new SeededNoiseHolder(((RandomState)(Object)this).random);
        clinker$worldSeed = levelSeed;
    }

    @Override
    public MetaChunkMap clinker$metaChunkMap() {
        return clinker$metaChunkMap;
    }

    @Override
    public long clinker$getWorldSeed() { return clinker$worldSeed; }

    @Override
    public SeededNoiseHolder clinker$noiseHolder() { return clinker$noiseHolder; }
}
