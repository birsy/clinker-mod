package birsy.clinker.common.world.level.gen.system.noise;

import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldFiller;
import birsy.clinker.common.world.level.gen.system.noise.field.NoiseFieldType;

import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class NoiseComputer {
    public int id = -1; // DO NOT TOUCH EVER. assigned on deferred registration
    public final Supplier<NoiseFieldType> fieldType;
    public final BiConsumer<NoiseDependencyCollector, NoiseRegistry> dependencies;
    public final NoiseFieldFiller filler;

    public NoiseComputer(Supplier<NoiseFieldType> fieldType,
                         BiConsumer<NoiseDependencyCollector, NoiseRegistry> dependencies,
                         NoiseFieldFiller filler) {
        this.fieldType = fieldType;
        this.dependencies = dependencies;
        this.filler = filler;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (NoiseComputer) obj;
        return this.id == that.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
