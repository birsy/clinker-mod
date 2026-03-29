package birsy.clinker.common.world.entity.gnomad.gnomind.squad;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.DebugEntityNameGenerator;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.IntFunction;

public record SquadDebugDataDump(UUID id, int leaderId, 
                                 double centerX, double centerY, double centerZ,
                                 List<MemberData> members, List<TaskData> tasks, List<EnemyPositionData> enemyPositions) {
    public static final StreamCodec<FriendlyByteBuf, SquadDebugDataDump> STREAM_CODEC = StreamCodec.of(
            (buffer, data) -> {
                buffer.writeUUID(data.id); buffer.writeInt(data.leaderId);
                buffer.writeDouble(data.centerX); buffer.writeDouble(data.centerY); buffer.writeDouble(data.centerZ);
                MemberData.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, data.members);
                TaskData.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, data.tasks);
                EnemyPositionData.STREAM_CODEC.apply(ByteBufCodecs.list()).encode(buffer, data.enemyPositions);
            },
            buffer -> new SquadDebugDataDump(
                    buffer.readUUID(), buffer.readInt(),
                    buffer.readDouble(), buffer.readDouble(), buffer.readDouble(),
                    MemberData.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer),
                    TaskData.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer),
                    EnemyPositionData.STREAM_CODEC.apply(ByteBufCodecs.list()).decode(buffer)
            )
    );

    public static SquadDebugDataDump of(Squad squad) {
        int leaderId = squad.hasLeader() ? squad.getLeader().asEntity().getId() : -1;
        List<MemberData> members = squad.getMembers().stream()
                .map(member -> new MemberData(
                        member.asEntity().getId(),
                        DebugEntityNameGenerator.getEntityName(member.asEntity()),
                        member.asEntity().getX(), member.asEntity().getY(), member.asEntity().getZ(),
                        member == squad.getLeader()
                ))
                .toList();
        List<TaskData> tasks = squad.tasks.stream()
                .map(task -> new TaskData(
                        task.getClass().getSimpleName(), task.status.name(),
                        task.assignees.size(), task.minAssignees, task.maxAssignees,
                        task.ticksExisted, task.priority
                ))
                .toList();
        List<EnemyPositionData> enemies = squad.lastKnownEnemyPositions
                .locations().stream()
                .map(pos -> new EnemyPositionData(
                        pos.getX(), pos.getY(), pos.getZ(),
                        pos.state().getSerializedName(),
                        pos.timestamp(),
                        squad.level.getEntity(pos.entityId) != null ?
                                DebugEntityNameGenerator.getEntityName(squad.level.getEntity(pos.entityId)) :
                                "unknown"
                ))
                .toList();
        Vec3 center = squad.getCenter(null);
        return new SquadDebugDataDump(squad.uuid, leaderId, center.x, center.y, center.z, members, tasks, enemies);
    }

    public record MemberData(int entityId, String name, double x, double y, double z, boolean isLeader) {
        static final StreamCodec<FriendlyByteBuf, MemberData> STREAM_CODEC = StreamCodec.of(
                (buffer, data) -> {
                    buffer.writeInt(data.entityId);
                    buffer.writeUtf(data.name);
                    buffer.writeDouble(data.x);
                    buffer.writeDouble(data.y);
                    buffer.writeDouble(data.z);
                    buffer.writeBoolean(data.isLeader);
                },
                buffer -> new MemberData(buffer.readInt(), buffer.readUtf(), buffer.readDouble(), buffer.readDouble(), buffer.readDouble(), buffer.readBoolean())
        );
    }

    public record TaskData(String className, String status,
                           int assigneeCount, int minAssignees, int maxAssignees,
                           int ticksExisted, int priority) {
        static final StreamCodec<FriendlyByteBuf, TaskData> STREAM_CODEC = StreamCodec.of(
                (buffer, data) -> {
                    buffer.writeUtf(data.className);
                    buffer.writeUtf(data.status);
                    buffer.writeInt(data.assigneeCount);
                    buffer.writeInt(data.minAssignees);
                    buffer.writeInt(data.maxAssignees);
                    buffer.writeInt(data.ticksExisted);
                    buffer.writeInt(data.priority);
                },
                buffer -> new TaskData(
                        buffer.readUtf(), buffer.readUtf(),
                        buffer.readInt(), buffer.readInt(), buffer.readInt(),
                        buffer.readInt(), buffer.readInt()
                )
        );
    }

    public record EnemyPositionData(int x, int y, int z, String state, long timestamp, String name) {
        static final StreamCodec<FriendlyByteBuf, EnemyPositionData> STREAM_CODEC = StreamCodec.of(
                (buffer, data) -> {
                    buffer.writeInt(data.x); buffer.writeInt(data.y); buffer.writeInt(data.z);
                    buffer.writeUtf(data.state);
                    buffer.writeLong(data.timestamp);
                    buffer.writeUtf(data.name);
                },
                buffer -> new EnemyPositionData(
                        buffer.readInt(), buffer.readInt(), buffer.readInt(),
                        buffer.readUtf(), buffer.readLong(), buffer.readUtf()
                )
        );
    }
}
