package birsy.clinker.common.networking.packet.debug;

import birsy.clinker.client.render.debug.ClinkerDebugRenderers;
import birsy.clinker.common.networking.packet.ClientboundPacket;
import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.GnomadSquad;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.squadtasks.GnomadSquadTask;
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

        squadCenter = squad.getCenter();

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
            for (GnomadEntity volunteer : task.volunteers.values()) {
                taskName.append("\n");
                taskName.append(DebugEntityNameGenerator.getEntityName(volunteer));
                taskName.append(",");
            }
            int remainder = task.maxVolunteers - task.volunteers.size();
            if (remainder > 0) {
                for (int j = 0; j < remainder; j++) {
                    taskName.append("\n");
                    taskName.append(j + task.volunteers.size() < task.minVolunteers ? "[AWAITING...]" : "[EMPTY]");
                    taskName.append(",");
                }
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
