package birsy.clinker.common.entity.module;

import net.minecraft.world.entity.Entity;

public interface ModuleHolder<E extends Entity & ModuleHolder<E>> {
    Module.Set<E> modules();

    default E moduleMe() { return (E) this; }
}
