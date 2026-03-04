package birsy.clinker.core.registration;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public interface DataValue<T> {
    ResourceKey<T> key();
    T create(BootstrapContext<T> context);

    default Holder<T> register(BootstrapContext<T> context) {
        return context.register(key(), create(context));
    }

    default Holder<T> holder(HolderGetter<T> getter) {
        return getter.getOrThrow(key());
    }
}
