package birsy.clinker.common.entity.system.squad;

import birsy.clinker.common.entity.gnomad.gnomind.LastKnownEntityPositionsTracker;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.util.BrainUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class Squad {
    static final int TIME_UNTIL_REMOVAL = 20 * 10;
    public final UUID uuid;
    public final LastKnownEntityPositionsTracker lastKnownEnemyPositions;
    final ServerLevel level;
    final Set<SquadMember<?>> members = new HashSet<>();
    final List<SquadTask> tasks = new ArrayList<>();

    SquadMember<?> leader;
    int ticksUntilRemoval = TIME_UNTIL_REMOVAL;

    Squad(UUID uuid, ServerLevel level) {
        this.uuid = uuid;
        this.level = level;
        this.lastKnownEnemyPositions = new LastKnownEntityPositionsTracker();
    }

    // lifecycle
    void tick() {
        if (isMemberInvalid(leader)) leader = null;
        members.removeIf(this::isMemberInvalid);
        for (SquadTask task : tasks) { task.tick(); }
        tasks.removeIf(SquadTask::isFinished);
        lastKnownEnemyPositions.update(this.level);
    }
    boolean shouldBeRemoved() { return members.isEmpty(); }
    public void cleanup() {
        Clinker.LOGGER.info("removing squad {}", this.uuid);
        for (SquadTask task : tasks) task.fail(SquadTask.FailureReason.SQUAD_DISBANDED);
        tasks.clear();
        List<SquadMember<?>> toRemove = new ArrayList<>(members);
        toRemove.forEach(this::removeMember);
    }

    // leader
    public void setLeader(SquadMember<?> newLeader) {
        if (this.leader == newLeader) return;
        if (this.leader != null) oustLeader();
        // make sure the leader is a member
        addMember(newLeader);
        this.leader = newLeader;
    }
    public void oustLeader() { this.leader = null; }
    public boolean hasLeader() {
        return this.leader != null && !isMemberInvalid(this.leader);
    }
    @Nullable public SquadMember getLeader() {
        return hasLeader() ? this.leader : null;
    }

    // members
    public boolean addMember(SquadMember<?> member) {
        if (isMemberInvalid(member)) return false;
        Clinker.LOGGER.info("squad {} added member {}", this.uuid, DebugEntityNameGenerator.getEntityName(member.asEntity().getUUID()));
        if (member instanceof LivingEntity livingEntity)
            BrainUtils.setMemory(livingEntity, ClinkerMemoryModules.SQUAD.get(), this);
        return members.add(member);
    }
    public boolean removeMember(SquadMember<?> member) {
        if (member instanceof LivingEntity livingEntity && BrainUtils.getMemory(livingEntity, ClinkerMemoryModules.SQUAD.get()) == this)
            BrainUtils.clearMemory(livingEntity, ClinkerMemoryModules.SQUAD.get());
        return members.remove(member);
    }
    public List<SquadMember<?>> getMembers() {
        return members.stream().filter(Predicate.not(this::isMemberInvalid)).toList();
    }
    boolean isMemberInvalid(SquadMember<?> member) {
        if (member == null) return true;
        LivingEntity entity = member.asEntity();
        return entity.isDeadOrDying() ||
               entity.isRemoved() ||
               entity.level() != level;
    }
    public int size() {
        return this.members.size();
    }

    // tasks
    public void addTask(SquadTask task) {
        // sorted insert
        for (int i = 0; i < tasks.size(); i++) {
            int lastPriority = tasks.get(i).priority;
            if (task.priority >= lastPriority) {
                tasks.add(i, task);
                task.post();
                return;
            }
        }
        this.tasks.add(task);
        task.post();
    }
    public Optional<SquadTask> findTask(SquadMember<?> member, Predicate<SquadTask> predicate) {
        return tasks.stream().filter(predicate).filter(task -> task.canBeAssigned(member)).findFirst();
    }
    public List<SquadTask> getTasksPostedBy(SquadMember<?> member) {
        return tasks.stream().filter(task -> task.taskMaster.equals(member)).toList();
    }

    // utilities
    public Vec3 getCenter(@Nullable SquadMember<?> memberToExclude) {
        // compute in two passes to avoid floating point precision problems

        // compute total weight
        float totalWeight = 0;
        for (SquadMember<?> member : this.members) {
            if (member == memberToExclude) continue;
            if (isMemberInvalid(member)) continue;
            float weight = member.squadPositionWeight();
            totalWeight += weight;
        }
        // add together positions
        double x = 0, y = 0, z = 0;
        for (SquadMember<?> member : this.members) {
            if (member == memberToExclude) continue;
            if (isMemberInvalid(member)) continue;
            float mult = member.squadPositionWeight() / totalWeight;
            x += member.asEntity().getX() * mult;
            y += member.asEntity().getY() * mult;
            z += member.asEntity().getZ() * mult;
        }
        return new Vec3(x, y, z);
    }
}
