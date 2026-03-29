package birsy.clinker.common.world.entity.gnomad.gnomind;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

import static birsy.clinker.common.world.entity.gnomad.gnomind.LastKnownEntityPosition.State.*;

public class LastKnownEntityPositionsTracker {
    public static final Codec<LastKnownEntityPositionsTracker> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            LastKnownEntityPosition.CODEC.listOf()
                    .fieldOf("positions")
                    .forGetter(t -> new ArrayList<>(t.lastKnownEnemyPositions.values())),
            Codec.INT.optionalFieldOf("uncertaintyTime", 10 * 20)
                    .forGetter(t -> t.uncertaintyTime),
            Codec.INT.optionalFieldOf("forgetTime", 3 * 60 * 20)
                    .forGetter(t -> t.forgetTime)
    ).apply(instance, (positions, uncertaintyTime, forgetTime) -> {
        LastKnownEntityPositionsTracker tracker = new LastKnownEntityPositionsTracker()
                .uncertaintyTime(uncertaintyTime)
                .forgetTime(forgetTime);
        for (LastKnownEntityPosition pos : positions) tracker.lastKnownEnemyPositions.put(pos.entityId, pos);
        return tracker;
    }));

    final Object2ObjectMap<UUID, LastKnownEntityPosition> lastKnownEnemyPositions = new Object2ObjectOpenHashMap<>();
    int uncertaintyTime = 5 * 20, // default value: 5 seconds
        forgetTime = 3 * 60 * 20; // default value: 3 minutes

    // configuration
    public LastKnownEntityPositionsTracker forgetTime(int time) {
        forgetTime = time;
        return this;
    }
    public LastKnownEntityPositionsTracker uncertaintyTime(int time) {
        uncertaintyTime = time;
        return this;
    }

    public void update(ServerLevel level) {
        long currentTime = level.getGameTime();
        // forget old stuff
        lastKnownEnemyPositions.values().removeIf(
                (pos) -> currentTime - pos.lastSeenTimestamp > forgetTime
        );
        // update uncertainty
        for (LastKnownEntityPosition pos : lastKnownEnemyPositions.values()) {
            if (pos.state == KNOWN && currentTime - pos.lastSeenTimestamp > uncertaintyTime) {
                pos.updateState(level, UNCERTAIN);
            }
        }
    }

    // returns the previous state
    public LastKnownEntityPosition.State updateTracking(ServerLevel level, Entity entity) {
        LastKnownEntityPosition position = this.lastKnownEnemyPositions.computeIfAbsent(entity.getUUID(),
                id -> new LastKnownEntityPosition(entity.getUUID())
        ).update(level.getGameTime(), entity.getBlockX(), entity.getBlockY(), entity.getBlockZ());
        LastKnownEntityPosition.State previousState = position.state;
        position.updateState(level, KNOWN);
        return previousState;
    }

    public void updateTrackingFromOtherTracker(LastKnownEntityPositionsTracker otherTracker) {
        for (LastKnownEntityPosition position : otherTracker.lastKnownEnemyPositions.values()) {
            this.lastKnownEnemyPositions
                    .computeIfAbsent(position.uuid(),
                            id -> new LastKnownEntityPosition(position.uuid())
                    ).update(position);
        }
    }

    public Collection<LastKnownEntityPosition> locations() {
        return Collections.unmodifiableCollection(lastKnownEnemyPositions.values());
    }

    // search stuff
//    public UUID findGoodSearchCandidate() {
//        return lastKnownEnemyPositions.values().stream()
//                // only search positions that we don't already know
//                .filter(pos -> pos.state == UNCERTAIN)
//                // try to search older positions first
//                .min(Comparator.comparingLong(LastKnownEntityPosition::timestamp))
//                .map(pos -> pos.entityId)
//                .orElse(null);
//    }
}
