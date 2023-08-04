package birsy.clinker.common.world.alchemy.workstation.storage;

import birsy.clinker.common.world.alchemy.workstation.Workstation;
import birsy.clinker.core.Clinker;
import com.google.common.collect.Maps;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

public class WorkstationLookup {
    private final Map<UUID, Workstation> byUuid = Maps.newHashMap();

    public Collection<Workstation> getAllWorkstations() {
        List<Workstation> list = new ArrayList<>();
        this.byUuid.forEach((key, workstation) -> {
            list.add(workstation);
        });
        return list;
    }

    public void add(Workstation workstation) {
        UUID uuid = workstation.uuid;
        if (this.byUuid.containsKey(uuid)) {
            Clinker.LOGGER.warn("Duplicate workstation UUID {}: {}", uuid, workstation);
        } else {
            this.byUuid.put(uuid, workstation);
        }
    }

    public void remove(Workstation workstation) {
        this.byUuid.remove(workstation.uuid);
    }

    public void remove(UUID id) {
        this.byUuid.remove(id);
    }

    public void clear() {
        this.byUuid.clear();
    }

    public boolean isEmpty() {
        return byUuid.isEmpty();
    }

    public Stream<Workstation> stream() {
        return byUuid.values().stream();
    }

    @Nullable
    public Workstation getWorkstation(UUID pUuid) {
        return this.byUuid.get(pUuid);
    }

    public int count() {
        return this.byUuid.size();
    }
}
