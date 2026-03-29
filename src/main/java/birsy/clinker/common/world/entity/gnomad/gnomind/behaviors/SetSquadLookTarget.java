package birsy.clinker.common.world.entity.gnomad.gnomind.behaviors;

import birsy.clinker.common.world.entity.gnomad.gnomind.squad.SquadMember;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour;
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget;
import net.tslat.smartbrainlib.object.MemoryTest;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.SensoryUtils;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class SetSquadLookTarget<E extends LivingEntity & SquadMember<E>> extends ExtendedBehaviour<E> {
    private static final MemoryTest MEMORY_REQUIREMENTS = MemoryTest.builder(2)
            .hasMemory(ClinkerMemoryModules.SQUAD.get())
            .noMemory(MemoryModuleType.LOOK_TARGET);

    protected BiPredicate<E, SquadMember<?>> lookPredicate = this::defaultPredicate;
    protected Predicate<SquadMember<?>> predicate = pl -> true;
    protected SquadMember<?> target = null;

    public SetSquadLookTarget<E> lookPredicate(BiPredicate<E, SquadMember<?>> predicate) {
        this.lookPredicate = predicate;
        return this;
    }

    @Override
    protected List<Pair<MemoryModuleType<?>, MemoryStatus>> getMemoryRequirements() {
        return MEMORY_REQUIREMENTS;
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel level, E entity) {
        List<SquadMember<?>> squadMembers = BrainUtils.getMemory(entity, ClinkerMemoryModules.SQUAD.get()).getMembers();
        squadMembers = Util.toShuffledList(squadMembers.stream(), entity.getRandom());
        for (SquadMember squadMember : squadMembers) {
            if (this.lookPredicate.test(entity, squadMember)) {
                this.target = squadMember;
                break;
            }
        }
        return this.target != null;
    }

    protected boolean defaultPredicate(E entity, SquadMember<?> squadMember) {
        LivingEntity squadEntity = squadMember.asEntity();

        if (entity.hasPassenger(squadEntity)) return false;
        if (entity instanceof Mob mob) {
            if (!mob.getSensing().hasLineOfSight(squadEntity)) return false;
        } else if (!entity.hasLineOfSight(squadEntity)) {
            return false;
        }
        double visibleDistance = Math.max(squadEntity.getVisibilityPercent(entity) * 16, 2);

        return entity.distanceToSqr(squadEntity) <= visibleDistance * visibleDistance;
    }

    @Override
    protected void start(E entity) {
        BrainUtils.setMemory(entity, MemoryModuleType.LOOK_TARGET, new EntityTracker(this.target.asEntity(), true));
    }

    @Override
    protected void stop(E entity) {
        this.target = null;
    }
}
