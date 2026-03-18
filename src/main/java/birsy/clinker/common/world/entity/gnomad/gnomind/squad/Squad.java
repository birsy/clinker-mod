package birsy.clinker.common.world.entity.gnomad.gnomind.squad;

import birsy.clinker.core.Clinker;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class Squad {
    static final int TIME_UNTIL_REMOVAL = 20 * 10;
    public final UUID uuid;
    final ServerLevel level;
    final List<SquadMember<?>> members = new ArrayList<>();
    final List<SquadTask> tasks = new ArrayList<>();
    int ticksUntilRemoval = TIME_UNTIL_REMOVAL;

    Squad(UUID uuid, ServerLevel level) {
        this.uuid = uuid;
        this.level = level;
    }

    public boolean addMember(SquadMember<?> member) {
        if (isMemberInvalid(member)) return false;
        Clinker.LOGGER.info("squad {} added member {}", this.uuid, DebugEntityNameGenerator.getEntityName(member.asEntity().getUUID()));
        return members.add(member);
    }
    public boolean removeMember(SquadMember<?> member) {
        return members.remove(member);
    }
    public List<SquadMember<?>> getMembers() {
        return members.stream().filter(Predicate.not(this::isMemberInvalid)).toList();
    }
    public int size() {
        return this.members.size();
    }

    public void addTask(SquadTask task) {
        // sorted insert
        for (int i = 0; i < tasks.size(); i++) {
            int lastPriority = tasks.get(i).priority;
            if (task.priority >= lastPriority) {
                tasks.add(i, task);
                return;
            }
        }
        this.tasks.add(task);
    }
    public Optional<SquadTask> findTask(SquadMember<?> member, Predicate<SquadTask> predicate) {
        return tasks.stream().filter(predicate).filter(task -> task.canBeAssigned(member)).findFirst();
    }
    public List<SquadTask> getTasksPostedBy(SquadMember<?> member) {
        return tasks.stream().filter(task -> task.taskMaster.equals(member)).toList();
    }

    boolean shouldBeRemoved() { return members.isEmpty(); }
    boolean isMemberInvalid(SquadMember<?> member) {
        LivingEntity entity = member.asEntity();
        return entity.isDeadOrDying() ||
               entity.isRemoved() ||
               entity.level() != level;
    }

    void tick() {
        members.removeIf(this::isMemberInvalid);
        for (SquadTask task : tasks) { task.tick(); }
        tasks.removeIf(SquadTask::isFinished);
    }

    public void cleanup() {
        Clinker.LOGGER.info("removing squad {}", this.uuid);
        for (SquadTask task : tasks) task.fail(SquadTask.FailureType.SQUAD_DISBANDED);
        tasks.clear();
    }

    public Vec3 getCenter(@Nullable SquadMember<?> memberToExclude) {
        double x = 0, y = 0, z = 0;
        int count = 0;
        for (SquadMember<?> member : this.members) {
            if (member == memberToExclude) continue;
            if (isMemberInvalid(member)) continue;
            count++;
            x += member.asEntity().getX();
            y += member.asEntity().getY();
            z += member.asEntity().getZ();
        }
        return new Vec3(x / count, y / count, z / count);
    }
}
