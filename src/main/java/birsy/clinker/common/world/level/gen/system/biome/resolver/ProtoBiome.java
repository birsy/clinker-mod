package birsy.clinker.common.world.level.gen.system.biome.resolver;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;

import javax.annotation.Nullable;
import java.util.Optional;

public final class ProtoBiome {
    public int id = -1; // DO NOT TOUCH EVER. assigned on deferred registration
    public final Optional<ResourceKey<Biome>> biome;

    private ProtoBiome(Optional<ResourceKey<Biome>> biome) {
        this.biome = biome;
    }
    public ProtoBiome() {
        this(Optional.empty());
    }
    public ProtoBiome(@Nullable ResourceKey<Biome> biome) {
        this(Optional.ofNullable(biome));
    }
}
