package birsy.clinker.core.registry.entity;

import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.schedule.Activity;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerActivities {
    public static final DeferredRegister<Activity> ACTIVITIES = DeferredRegister.create(BuiltInRegistries.ACTIVITY, Clinker.MOD_ID);

    public static final Supplier<Activity> DELIVER_SUPPLIES = register("deliver_supplies");

    private static DeferredHolder<Activity, Activity> register(String name) {
        return ACTIVITIES.register(name, () -> new Activity(name));
    }
}
