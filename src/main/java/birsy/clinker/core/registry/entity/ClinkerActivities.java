package birsy.clinker.core.registry.entity;

import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.common.world.entity.gnomad.GnomadSupplyDepot;
import birsy.clinker.core.Clinker;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ClinkerActivities {
    public static final DeferredRegister<Activity> ACTIVITIES = DeferredRegister.create(BuiltInRegistries.ACTIVITY, Clinker.MOD_ID);

    //public static final Supplier<Activity> RELAX = ACTIVITIES.register("relax", () -> new Activity("relax"));
}
