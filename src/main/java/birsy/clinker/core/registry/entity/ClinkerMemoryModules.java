package birsy.clinker.core.registry.entity;

import birsy.clinker.common.entity.gnomad.SuppliesHolder;
import birsy.clinker.common.entity.gnomad.gnomind.LastKnownEntityPositionsTracker;
import birsy.clinker.common.entity.system.squad.Squad;
import birsy.clinker.common.entity.system.squad.SquadTask;
import birsy.clinker.core.Clinker;
import com.mojang.serialization.Codec;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

public class ClinkerMemoryModules {
    public static final DeferredRegister<MemoryModuleType<?>> MEMORY_MODULE_TYPES = DeferredRegister.create(BuiltInRegistries.MEMORY_MODULE_TYPE, Clinker.MOD_ID);

    public static final Supplier<MemoryModuleType<GlobalPos>> COVER_POSITION =
            MEMORY_MODULE_TYPES.register("cover_position", () -> new MemoryModuleType<>(Optional.empty()));
    public static final Supplier<MemoryModuleType<GlobalPos>> NEAREST_SUPPLY_DEPOT =
            MEMORY_MODULE_TYPES.register("nearest_supply_depot", () -> new MemoryModuleType<>(Optional.empty()));

    public static final Supplier<MemoryModuleType<SuppliesHolder>> DELIVERY_TARGET =
            MEMORY_MODULE_TYPES.register("delivery_target", () -> new MemoryModuleType<>(Optional.empty()));
    public static final Supplier<MemoryModuleType<GlobalPos>> RELAXATION_POSITION =
            MEMORY_MODULE_TYPES.register("relaxation_position", () -> new MemoryModuleType<>(Optional.empty()));

    public static final Supplier<MemoryModuleType<Squad>> SQUAD =
            MEMORY_MODULE_TYPES.register("squad", () -> new MemoryModuleType<>(Optional.empty()));
    public static final Supplier<MemoryModuleType<Set<SquadTask>>> POSTED_SQUAD_TASKS =
            MEMORY_MODULE_TYPES.register("posted_squad_tasks", () -> new MemoryModuleType<>(Optional.empty()));
    public static final Supplier<MemoryModuleType<SquadTask>> ASSIGNED_SQUAD_TASK =
            MEMORY_MODULE_TYPES.register("assigned_squad_task", () -> new MemoryModuleType<>(Optional.empty()));

    public static final Supplier<MemoryModuleType<LastKnownEntityPositionsTracker>> LAST_KNOWN_ENEMY_POSITIONS =
            MEMORY_MODULE_TYPES.register("last_known_enemy_positions", () -> new MemoryModuleType<>(Optional.empty()));

    public static final Supplier<MemoryModuleType<Unit>> ATTACK_WINDUP = MEMORY_MODULE_TYPES.register("attack_windup", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
}
