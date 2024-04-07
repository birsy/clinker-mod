package birsy.clinker.common.world.level.interactable.storage;

import birsy.clinker.common.world.level.interactable.Interactable;
import com.google.common.collect.Maps;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

public class InteractableLookup {
    protected final Map<UUID, Interactable> byUuid = Maps.newHashMap();

    public Interactable getInteractableFromUUID(UUID id) {
        return byUuid.get(id);
    }

    public void putInteractable(Interactable interactable) {
        byUuid.put(interactable.id, interactable);
    }

    public void removeInteractable(Interactable interactable) {
        this.removeInteractable(interactable.id);
    }

    public void removeInteractable(UUID id) {
        byUuid.remove(id);
    }

    public Collection<Interactable> getInteractables() {
        return byUuid.values();
    }

    public void clear() {
        this.byUuid.clear();
    }
}
