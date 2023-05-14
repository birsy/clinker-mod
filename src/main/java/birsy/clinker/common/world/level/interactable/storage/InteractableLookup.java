package birsy.clinker.common.world.level.interactable.storage;

import birsy.clinker.common.world.level.interactable.Interactable;
import birsy.clinker.core.Clinker;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class InteractableLookup {
    private final Map<UUID, Interactable> byUuid = Maps.newHashMap();

    public Iterable<Interactable> getAllInteractables() {
        List<Interactable> list = new ArrayList<>();
        this.byUuid.forEach((key, interactable) -> {
            list.add(interactable);
        });
        return list;
    }

    public void add(Interactable interactable) {
        UUID uuid = interactable.uuid;
        if (this.byUuid.containsKey(uuid)) {
            Clinker.LOGGER.warn("Duplicate interactable UUID {}: {}", uuid, interactable);
        } else {
            this.byUuid.put(uuid, interactable);
        }
    }

    public void remove(Interactable interactable) {
        this.byUuid.remove(interactable.uuid);
    }

    public void remove(UUID id) {
        Interactable thing = this.byUuid.remove(id);
        Clinker.LOGGER.info(this.byUuid.size());
    }

    public void clear() {
        this.byUuid.clear();
    }

    public boolean isEmpty() {
        return byUuid.isEmpty();
    }

    public Stream<Interactable> stream() {
        return byUuid.values().stream();
    }

    @Nullable
    public Interactable getInteractable(UUID pUuid) {
        return this.byUuid.get(pUuid);
    }

    public int count() {
        return this.byUuid.size();
    }
}
