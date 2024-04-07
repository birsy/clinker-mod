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
    public final int id;
    private final Level level;
    public boolean markedForRemoval = false;
    List<GnomadEntity> members;
    List<GnomadSquadTask> tasks;

    public static final float SQUAD_SEARCH_RADIUS = 10.0F;

    public GnomadSquad(int id, ServerLevel level) {
        this.id = id;
        this.level = level;
        this.members = new ArrayList<>();
        this.tasks = new ArrayList<>();
    }

    public void tick() {
        this.updateTasks();
        this.cullDeadOrRemovedMembers();
        this.searchForNewMembers(10.0F);

        if (ClinkerDebugRenderers.shouldRender) ClinkerPacketHandler.sendToAllClients(new GnomadSquadDebugPacket(this));
    }

    private void cullDeadOrRemovedMembers() {
        Iterator<GnomadEntity> memberIterator = this.members.iterator();
        while (memberIterator.hasNext()) {
            GnomadEntity member = memberIterator.next();
            if (member == null || member.isRemoved() || member.isDeadOrDying() ) memberIterator.remove();
        }
    }

    private void updateTasks() {
        Iterator<GnomadSquadTask> taskIterator = this.tasks.iterator();
        while (taskIterator.hasNext()) {
            GnomadSquadTask task = taskIterator.next();

            if (!task.isInitialized()) task.initialize();

            task.ticksExisted++;
            task.attemptInvalidate();

            if (task.shouldRemove) {
                taskIterator.remove();
                continue;
            }

            // check if this task should be updated this tick
            task.ticksSinceLastUpdate++;
            if (task.ticksSinceLastUpdate > task.tickRate) {
                task.ticksSinceLastUpdate = 0;
                task.tick();
            }
        }
    }

    private int searchIndex = 0;
    private void searchForNewMembers(double searchRadius) {
        this.searchIndex = (this.searchIndex + 1) % (this.members.size() - 1);
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
            if (entity.squad == null) {
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
        for (GnomadSquadTask task : squad.tasks) {
            this.tasks.add(task);
        }
        squad.members.clear();
        squad.markedForRemoval = true;
    }

    public void addMember(GnomadEntity entity) {
        if (entity.squad != null) {
            entity.squad.removeMember(entity);
        }
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
        this.tasks.add(task);
    }

    public List<GnomadSquadTask> getTasksImmutable() {
        return Collections.unmodifiableList(this.tasks);
    }
}
