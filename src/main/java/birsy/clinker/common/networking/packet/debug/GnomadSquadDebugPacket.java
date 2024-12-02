package birsy.clinker.common.networking.packet.debug;

import birsy.clinker.client.render.debug.ClinkerDebugRenderers;
import birsy.clinker.common.world.entity.gnomad.GnomadEntity;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.GnomadSquad;
import birsy.clinker.common.world.entity.gnomad.gnomind.squad.squadtasks.GnomadSquadTask;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.codecs.ExtraByteBufCodecs;
import io.netty.buffer.ByteBuf;
import net.minecraft.Util;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record GnomadSquadDebugPacket(UUID squadID, Vec3 squadCenter, List<Integer> squadMemberIDs, List<String> taskNames) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<GnomadSquadDebugPacket> TYPE = new CustomPacketPayload.Type<>(Clinker.resource("debug/gnomadsquad"));
    public static final StreamCodec<ByteBuf, GnomadSquadDebugPacket> STREAM_CODEC = StreamCodec.composite(
            ExtraByteBufCodecs.UUID, GnomadSquadDebugPacket::squadID,
            ExtraByteBufCodecs.VEC3, GnomadSquadDebugPacket::squadCenter,
            ByteBufCodecs.INT.apply(ByteBufCodecs.collection(ArrayList::new)), GnomadSquadDebugPacket::squadMemberIDs,
            ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.collection(ArrayList::new)), GnomadSquadDebugPacket::taskNames,
            GnomadSquadDebugPacket::new
    );

    public GnomadSquadDebugPacket(GnomadSquad squad) {
        this(
             squad.id,
             squad.getCenter(),
             Util.make(() -> {
                 List<GnomadEntity> members = squad.getMembersImmutable();
                 List<Integer> squadMemberIDs = new ArrayList<>(squad.size());
                 for (GnomadEntity member : members) squadMemberIDs.add(member.getId());
                 return squadMemberIDs;
             }),
             Util.make(() -> {
                 List<GnomadSquadTask> tasks = squad.getTasksImmutable();
                 List<String> taskNames = new ArrayList<>(tasks.size());
                 for (GnomadSquadTask task : tasks) {
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
                     taskNames.add(taskName.toString());
                 }
                 return taskNames;
             })
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }


    public void handle(final IPayloadContext context) {
        ClinkerDebugRenderers.gnomadSquadDebugRenderer.dumpSquad(this);
    }
}
