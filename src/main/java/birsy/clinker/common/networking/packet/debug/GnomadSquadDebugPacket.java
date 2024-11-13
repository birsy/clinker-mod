package birsy.clinker.common.networking.packet.debug;

import birsy.clinker.client.render.debug.ClinkerDebugRenderers;
import birsy.clinker.common.networking.packet.ClientboundPacket;
import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.common.world.entity.gnomad.squad.GnomadSquad;
import birsy.clinker.common.world.entity.gnomad.squad.GnomadSquadTask;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GnomadSquadDebugPacket extends ClientboundPacket {
    public final UUID squadID;
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
            StringBuilder taskName = new StringBuilder(task.getClass().getSimpleName());
            taskName.append("\nTime Remaining: ").append(task.remainingTime());
            taskName.append("\nTaskmaster: ").append(DebugEntityNameGenerator.getEntityName(task.taskmaster));
            taskName.append("\nVolunteers: ");
            for (int j = 0; j < Math.max(Math.max(task.volunteers.size(), task.minVolunteers), task.maxVolunteers); j++) {
                taskName.append("\n");
                if (j > task.volunteers.size() - 1 || task.volunteers.get(j) == null) {
                    taskName.append(j < task.minVolunteers ? "[AWAITING...]" : "[EMPTY]");
                } else {
                    taskName.append(DebugEntityNameGenerator.getEntityName(task.volunteers.get(j)));
                }
                taskName.append(",");
            }
            this.taskNames.add(taskName.toString());
        }
    }

    public GnomadSquadDebugPacket(FriendlyByteBuf buffer) {
        this.squadID = buffer.readUUID();
        this.squadCenter = buffer.readVec3();
        this.squadMemberIDs = buffer.readList(FriendlyByteBuf::readInt);
        this.taskNames = buffer.readList(FriendlyByteBuf::readUtf);
    }

    @Override
    public void write(FriendlyByteBuf buffer) {
        buffer.writeUUID(this.squadID);
        buffer.writeVec3(this.squadCenter);
        buffer.writeCollection(this.squadMemberIDs, FriendlyByteBuf::writeInt);
        buffer.writeCollection(this.taskNames, FriendlyByteBuf::writeUtf);
    }

    @Override
    public void run(PlayPayloadContext context) {
        ClinkerDebugRenderers.gnomadSquadDebugRenderer.dumpSquad(this);
    }
}
