package birsy.clinker.core.registry.entity;

import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.common.world.entity.gnomad.GnomadSupplyDepot;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.squadtasks.GnomadSquadTask;
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

    public static final Supplier<MemoryModuleType<List<GnomadSupplyDepot>>> NEARBY_SUPPLY_DEPOTS = MEMORY_MODULE_TYPES.register("nearby_supply_depots", () -> new MemoryModuleType<>(Optional.empty()));
    public static final Supplier<MemoryModuleType<GnomadSupplyDepot>> TARGETED_SUPPLY_DEPOT = MEMORY_MODULE_TYPES.register("targeted_supply_depot", () -> new MemoryModuleType<>(Optional.empty()));
    public static final Supplier<MemoryModuleType<GnomadEntity>> SUPPLY_TARGET = MEMORY_MODULE_TYPES.register("supply_target", () -> new MemoryModuleType<>(Optional.empty()));

    public static final Supplier<MemoryModuleType<List<GnomadEntity>>> GNOMADS_IN_SQUAD = MEMORY_MODULE_TYPES.register("gnomads_in_squad", () -> new MemoryModuleType<>(Optional.empty()));

    public static final Supplier<MemoryModuleType<GlobalPos>> RELAXATION_SPOT = MEMORY_MODULE_TYPES.register("relaxation_spot", () -> new MemoryModuleType<>(Optional.of(GlobalPos.CODEC)));

    public static final Supplier<MemoryModuleType<GnomadSquadTask>> ACTIVE_SQUAD_TASK = MEMORY_MODULE_TYPES.register("active_squad_task", () -> new MemoryModuleType<>(Optional.empty()));

    public static final Supplier<MemoryModuleType<Unit>> ATTACK_WINDUP = MEMORY_MODULE_TYPES.register("attack_windup", () -> new MemoryModuleType<>(Optional.of(Codec.unit(Unit.INSTANCE))));
}
