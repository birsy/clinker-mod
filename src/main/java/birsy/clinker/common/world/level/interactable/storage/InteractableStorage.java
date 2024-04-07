package birsy.clinker.common.world.level.interactable.storage;

import birsy.clinker.common.world.level.interactable.Interactable;
import net.minecraft.core.SectionPos;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/**
 * Stores interactable info within a single level.
 */
public class InteractableStorage {
    private final InteractableLookup levelLookup = new InteractableLookup();
    private final InteractableSectionMap lookupBySection = new InteractableSectionMap();

    public void reorganize() {
        for (Interactable interactable : levelLookup.getInteractables()) {
            if (!interactable.markedDirty) continue;
            lookupBySection.updateInteractablePosition(interactable);
        }
    }

    public Collection<Interactable> getInteractables() {
        return levelLookup.getInteractables();
    }

    public Collection<Interactable> getInteractablesInSection(SectionPos pos) {
        return lookupBySection.getInteractablesInSection(pos);
    }

    public boolean sectionContainsInteractables(SectionPos pos) {
        return lookupBySection.sectionContainsInteractables(pos);
    }

    public void unloadSection(SectionPos pos) {
        for (Interactable interactable : this.getInteractablesInSection(pos)) {
            levelLookup.removeInteractable(interactable);
            interactable.removed = true;
        }
        lookupBySection.removeSectionWithInteractables(pos);
    }

    public Interactable getInteractableFromUUID(UUID id) {
        return levelLookup.getInteractableFromUUID(id);
    }

    public void addInteractable(Interactable interactable) {
        levelLookup.putInteractable(interactable);
        lookupBySection.putInteractable(interactable);
    }

    public void removeInteractable(Interactable interactable) {
        levelLookup.removeInteractable(interactable);
        lookupBySection.removeInteractable(interactable);
    }

    public Set<SectionPos> getSectionsWithInteractables() {
        return lookupBySection.getSectionsWithInteractables();
    }

    public void clear() {
        levelLookup.clear();
        lookupBySection.clear();
    }
}
