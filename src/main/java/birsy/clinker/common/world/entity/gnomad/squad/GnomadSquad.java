package birsy.clinker.common.world.entity.gnomad.squad;

import birsy.clinker.client.render.debug.ClinkerDebugRenderers;
import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.debug.GnomadSquadDebugPacket;
import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
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
        this.updateTasks();
        this.searchForNewMembers(10.0F);
        this.cullDeadOrRemovedMembers();

        if (ClinkerDebugRenderers.shouldRender) ClinkerPacketHandler.sendToAllClients(new GnomadSquadDebugPacket(this));
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
        if (this.markedForRemoval) return;
        Iterator<GnomadSquadTask> taskIterator = this.taskboard.iterator();
        while (taskIterator.hasNext()) {
            GnomadSquadTask task = taskIterator.next();

            task.ticksExisted++;

            // check if this task should be updated this tick
            task.ticksSinceLastUpdate++;
            if (task.ticksSinceLastUpdate >= task.ticksPerUpdate) {
                task.ticksSinceLastUpdate = 0;

                task.volunteers.removeIf(task::shouldRemoveVolunteer);
                Optional<GnomadSquadTask.FailureType> failureType = task.shouldFail();
                if (failureType.isPresent()) {
                    task.fail(failureType.get());
                    taskIterator.remove();
                    continue;
                }

                if (!task.active) {
                    if (task.shouldBegin()) {
                        task.active = true;
                        task.begin();
                    }
                }
                if (task.shouldSucceed() && task.active) task.succeed();

                task.update(task.active);
            }
        }

        this.taskboard.sort(Comparator.comparingInt(GnomadSquadTask::remainingTime));
    }

    private int searchIndex = 0;
    private void searchForNewMembers(double searchRadius) {
        if (this.markedForRemoval) return;
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
}
