package birsy.clinker.core.registry.entity;

import birsy.clinker.core.Clinker;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.schedule.Activity;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ClinkerActivities {
    public static final DeferredRegister<Activity> ACTIVITIES = DeferredRegister.create(BuiltInRegistries.ACTIVITY, Clinker.MOD_ID);

    //public static final Supplier<Activity> RELAX = ACTIVITIES.register("relax", () -> new Activity("relax"));
}
