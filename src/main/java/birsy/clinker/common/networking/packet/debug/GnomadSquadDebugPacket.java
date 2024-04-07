package birsy.clinker.common.networking.packet.debug;

import birsy.clinker.client.render.debug.ClinkerDebugRenderers;
import birsy.clinker.common.networking.packet.ClientboundPacket;
import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.common.world.entity.gnomad.squad.GnomadSquad;
import birsy.clinker.common.world.entity.gnomad.squad.GnomadSquadTask;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.ArrayList;
import java.util.List;

public class GnomadSquadDebugPacket extends ClientboundPacket {
    public final int squadID;
    public final Vec3 squadCenter;
    public final List<Integer> squadMemberIDs;
    public final List<String> taskNames;

    public GnomadSquadDebugPacket(GnomadSquad squad) {
        this.squadID = squad.id;

        List<GnomadEntity> members = squad.getMembersImmutable();

        Vec3 average = new Vec3(0, 0, 0);
        for (GnomadEntity member : members) average = average.add(member.getEyePosition());
        average = average.scale(1.0 / squad.size());
        squadCenter = average;

        this.squadMemberIDs = new ArrayList<>(squad.size());
        for (int i = 0; i < squad.size(); i++) {
            this.squadMemberIDs.add(members.get(i).getId());
        }

        List<GnomadSquadTask> tasks = squad.getTasksImmutable();
        this.taskNames = new ArrayList<>(tasks.size());
        for (int i = 0; i < tasks.size(); i++) {
            GnomadSquadTask task = tasks.get(i);
            this.taskNames.add(task.getClass().getSimpleName() + " " + task.remainingTime()  + ", assignee: " + (task.isAssigned() ? (task.getAssignee().hasCustomName() ? task.getAssignee().getCustomName().getString() : task.getAssignee().getId()) : "NONE"));
        }
    }

    public GnomadSquadDebugPacket(FriendlyByteBuf buffer) {
        this.squadID = buffer.readInt();
        this.squadCenter = buffer.readVec3();
        this.squadMemberIDs = buffer.readList(FriendlyByteBuf::readInt);
        this.taskNames = buffer.readList(FriendlyByteBuf::readUtf);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeInt(this.squadID);
        buffer.writeVec3(this.squadCenter);
        buffer.writeCollection(this.squadMemberIDs, FriendlyByteBuf::writeInt);
        buffer.writeCollection(this.taskNames, FriendlyByteBuf::writeUtf);
    }

    @Override
    public void run(PlayPayloadContext context) {
        ClinkerDebugRenderers.gnomadSquadDebugRenderer.dumpSquad(this);
    }
}
