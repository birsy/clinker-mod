package birsy.clinker.core.registry.entity;

import birsy.clinker.common.world.entity.gnomad.SuppliesHolder;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.Squad;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.SquadTask;
import birsy.clinker.core.Clinker;
import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class ClinkerMemoryModules {
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES = DeferredRegister.create(BuiltInRegistries.MEMORY_MODULE_TYPE, Clinker.MOD_ID);

    public static final Supplier<MemoryModuleType<GlobalPos>>
            NEAREST_SUPPLY_DEPOT = MEMORY_MODULE_TYPES.register("nearest_supply_depot", () -> new MemoryModuleType<>(Optional.empty()));
    public static final Supplier<MemoryModuleType<SuppliesHolder>>
            DELIVERY_TARGET = MEMORY_MODULE_TYPES.register("delivery_target", () -> new MemoryModuleType<>(Optional.empty()));

    public static final Supplier<MemoryModuleType<Squad>> SQUAD =
            MEMORY_MODULE_TYPES.register("squad", () -> new MemoryModuleType<>(Optional.empty()));
    public static final Supplier<MemoryModuleType<SquadTask>> CURRENTLY_ASSIGNED_SQUAD_TASK =
            MEMORY_MODULE_TYPES.register("currently_assigned_squad_task", () -> new MemoryModuleType<>(Optional.empty()));
    public static final Supplier<MemoryModuleType<Unit>> IS_CURRENTLY_ASSIGNED_SQUAD_TASK_ACTIVE =
            MEMORY_MODULE_TYPES.register("is_currently_assigned_squad_task_active", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
    public static final Supplier<MemoryModuleType<List<SquadTask>>> SQUAD_TASKS_I_HAVE_POSTED =
            MEMORY_MODULE_TYPES.register("squad_tasks_i_have_posted", () -> new MemoryModuleType<>(Optional.empty()));

    public static final Supplier<MemoryModuleType<Unit>> ATTACK_WINDUP = MEMORY_MODULE_TYPES.register("attack_windup", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
}
