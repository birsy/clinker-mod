package birsy.clinker.common.entity.gnomad.gnomind;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Position;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.StringRepresentable;

import java.util.UUID;

public class LastKnownEntityPosition extends Vec3i implements Position {
    public static final Codec<LastKnownEntityPosition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            UUIDUtil.CODEC.fieldOf("id").forGetter(LastKnownEntityPosition::uuid),
            Codec.LONG.fieldOf("timestamp").forGetter(LastKnownEntityPosition::timestamp),
            Codec.INT.fieldOf("x").forGetter(LastKnownEntityPosition::getX),
            Codec.INT.fieldOf("y").forGetter(LastKnownEntityPosition::getY),
            Codec.INT.fieldOf("z").forGetter(LastKnownEntityPosition::getZ),
            State.CODEC.optionalFieldOf("state", State.KNOWN).forGetter(pos -> pos.state)
    ).apply(instance, (id, timestamp, x, y, z, state) -> {
        LastKnownEntityPosition pos = new LastKnownEntityPosition(id, timestamp, x, y, z);
        pos.state = state;
        return pos;
    }));

    public final UUID entityId;
    long lastSeenTimestamp;
    State state;

    LastKnownEntityPosition(UUID entityId) {
        this(entityId, Long.MIN_VALUE, 0, 0, 0);
    }

    LastKnownEntityPosition(UUID entityId, long lastSeenTimestamp, int x, int y, int z) {
        super(x, y, z);
        this.entityId = entityId;
        this.lastSeenTimestamp = lastSeenTimestamp;
        this.state = State.MISSING;
    }

    public LastKnownEntityPosition update(LastKnownEntityPosition position) {
        if (position.lastSeenTimestamp > this.lastSeenTimestamp) this.state = position.state;
        this.update(position.lastSeenTimestamp, position.getX(), position.getY(), position.getZ());
        return this;
    }

    public LastKnownEntityPosition update(long timestamp, int newX, int newY, int newZ) {
        // only update if this one is more recent
        if (timestamp > this.lastSeenTimestamp) {
            this.setX(newX); this.setY(newY); this.setZ(newZ);
            this.lastSeenTimestamp = timestamp;
        }
        return this;
    }

    public LastKnownEntityPosition updateState(ServerLevel level, State state) {
        this.state = state;
        return this;
    }

    public BlockPos asBlockPos() {
        return new BlockPos(this);
    }

    public UUID uuid() { return entityId; }
    public long timestamp() { return lastSeenTimestamp; }
    public State state() { return state; }
    @Override public double x() { return this.getX(); }
    @Override public double y() { return this.getY(); }
    @Override public double z() { return this.getZ(); }

    public enum State implements StringRepresentable {
        // we've been updated recently, and know
        // pretty much exactly where they are.
        KNOWN("known"),
        // this position hasn't been searched yet,
        // but we think they're nearby.
        UNCERTAIN("uncertain"),
        // we've searched this position,
        // and the entity is not nearby...
        MISSING("missing");
        public static final Codec<State> CODEC = StringRepresentable.fromEnum(State::values);
        final String name;
        State(String name) { this.name = name; }
        @Override public String getSerializedName() { return name; }
    }
}
