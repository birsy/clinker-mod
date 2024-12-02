package birsy.clinker.common.world.entity.gnomad.gnomind.squad;

import birsy.clinker.client.render.debug.ClinkerDebugRenderers;
import birsy.clinker.common.networking.packet.debug.GnomadSquadDebugPacket;
import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.squadtasks.GnomadSquadTask;
import birsy.clinker.core.registry.entity.ClinkerMemoryModules;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.PacketDistributor;
import net.tslat.smartbrainlib.util.BrainUtils;
import net.tslat.smartbrainlib.util.EntityRetrievalUtil;
import net.tslat.smartbrainlib.util.SensoryUtils;

import java.util.*;
import java.util.function.Predicate;

public class GnomadSquad {
    public final UUID id;
    private final Level level;
    public boolean markedForRemoval = false;
    List<GnomadEntity> members;
    List<GnomadSquadTask> taskboard;

    public static final float SQUAD_SEARCH_RADIUS = 10.0F;

    public GnomadSquad(UUID id, ServerLevel level) {
        this.id = id;
        this.level = level;
        this.members = new ArrayList<>();
        this.taskboard = new ArrayList<>();
    }

    public void tick() {
        if (this.markedForRemoval) return;
        this.updateTasks();
        this.searchForNewMembers(10.0F);
        this.cullDeadOrRemovedMembers();

        if (ClinkerDebugRenderers.shouldRender) PacketDistributor.sendToAllPlayers(new GnomadSquadDebugPacket(this));
    }

    private void cullDeadOrRemovedMembers() {
        this.members.removeIf(member ->
                member == null ||
                member.isRemoved() ||
                member.isDeadOrDying() ||
                member.squad != this ||
                member.level() != this.level);
        if (this.members.isEmpty()) this.markedForRemoval = true;
    }

    private void updateTasks() {
        Iterator<GnomadSquadTask> taskIterator = this.taskboard.iterator();
        while (taskIterator.hasNext()) {
            GnomadSquadTask task = taskIterator.next();
            if (task.markedForRemoval) {
                taskIterator.remove();
                continue;
            }

            task.ticksExisted++;

            // check if this task should be updated this tick
            task.ticksSinceLastUpdate++;
            if (task.ticksSinceLastUpdate >= task.ticksPerUpdate) {
                task.ticksSinceLastUpdate = 0;

                // check for failures
                Optional<GnomadSquadTask.FailureType> failureType = task.shouldFail();
                if (failureType.isPresent()) {
                    task.fail(failureType.get());
                    task.markedForRemoval = true;
                    continue;
                }
                // check if we should begin
                if (!task.hasBegun) {
                    if (task.shouldBegin()) {
                        task.begin();
                        task.hasBegun = true;
                    }
                }
                // check for successes
                if (task.shouldSucceed() && task.hasBegun) {
                    task.succeed();
                    task.markedForRemoval = true;
                    continue;
                }

                // cull any invalid members
                task.volunteers.values().removeIf(task::shouldRemoveVolunteer);
                // set the current task for the rest
                for (GnomadEntity entity : task.volunteers.values()) BrainUtils.setForgettableMemory(entity, ClinkerMemoryModules.ACTIVE_SQUAD_TASK.get(), task, task.remainingTime());
                // update the task
                task.update(task.hasBegun);
            }
        }
    }

    private int searchIndex = 0;
    private void searchForNewMembers(double searchRadius) {
        if (this.members.isEmpty()) return;
        this.searchIndex = (this.searchIndex + 1) % this.members.size();
        this.searchForNewMembersAroundGnomad(this.members.get(this.searchIndex), searchRadius);
    }

    public static List<GnomadEntity> getNearbyGnomads(GnomadEntity member, double searchRadius) {
        return getNearbyGnomads(member, searchRadius, (entity) -> true);
    }

    public static List<GnomadEntity> getNearbyGnomads(GnomadEntity member, double searchRadius, Predicate<GnomadEntity> filter) {
        return EntityRetrievalUtil.getEntities(member, searchRadius, (entity -> entity instanceof GnomadEntity && SensoryUtils.hasLineOfSight(member, entity) && filter.test((GnomadEntity) entity)));
    }

    private void searchForNewMembersAroundGnomad(GnomadEntity member, double searchRadius) {
        List<GnomadEntity> entities = getNearbyGnomads(member, searchRadius);
        for (GnomadEntity entity : entities) {
            if (entity.squad == this) continue;
            if (entity.squad.size() <= 1) {
                this.addMember(entity);
                continue;
            }
            // this means that the gnomad belongs to a different squad, so we should merge these squads
            this.mergeWithSquad(entity.squad);
        }
    }

    private void mergeWithSquad(GnomadSquad squad) {
        if (squad.size() > this.size()) {
            squad.mergeWithSquad(this);
            return;
        }

        // transfer all the members over
        for (GnomadEntity member : squad.members) {
            this.members.add(member);
            member.squad = this;
        }
        // transfer all the tasks over too
        this.taskboard.addAll(squad.taskboard);
        squad.members.clear();
        squad.markedForRemoval = true;
    }

    public void addMember(GnomadEntity entity) {
        if (entity.squad != null) entity.squad.removeMember(entity);
        this.members.add(entity);
        entity.squad = this;
    }

    public void removeMember(GnomadEntity entity) {
        entity.squad = null;
        this.members.remove(entity);
    }

    public List<GnomadEntity> getMembersImmutable() {
        return Collections.unmodifiableList(this.members);
    }

    public int size() {
        return members.size();
    }

    public void postTask(GnomadSquadTask task) {
        this.taskboard.add(task);
    }

    public void volunteerForTask(GnomadEntity entity, GnomadSquadTask task) {
        task.volunteer(entity);
    }

    public List<GnomadSquadTask> getTasksImmutable() {
        return Collections.unmodifiableList(this.taskboard);
    }

    public Vec3 getCenter() {
        Vec3 average = new Vec3(0, 0, 0);
        for (GnomadEntity member : members) average = average.add(member.position());
        return average.scale(1.0 / this.size());
    }
}
