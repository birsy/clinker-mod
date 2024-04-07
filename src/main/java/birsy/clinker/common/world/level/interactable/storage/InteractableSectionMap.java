package birsy.clinker.common.world.level.interactable.storage;

import birsy.clinker.common.world.level.interactable.Interactable;
import birsy.clinker.core.Clinker;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import net.minecraft.core.SectionPos;

import java.util.*;

public class InteractableSectionMap {
    private final Long2ObjectMap<InteractableLookup> lookupBySection = new Long2ObjectOpenHashMap<>();
    private final HashMap<UUID, Long> sectionByInteractable = new HashMap<>();

    public InteractableLookup getLookupInSection(SectionPos pos) {
        return lookupBySection.get(pos.asLong());
    }

    public Set<SectionPos> getSectionsWithInteractables() {
        Set<SectionPos> set = new HashSet<>();
        for (long key : lookupBySection.keySet()) set.add(SectionPos.of(key));
        return set;
    }

    public Collection<Interactable> getInteractablesInSection(SectionPos pos) {
        if (!lookupBySection.containsKey(pos.asLong())) return Collections.emptyList();
        return this.getLookupInSection(pos).getInteractables();
    }

    public boolean sectionContainsInteractables(SectionPos pos) {
        if (!lookupBySection.containsKey(pos.asLong())) return false;
        return getInteractablesInSection(pos).size() > 0;
    }

    public void putInteractable(Interactable interactable) {
        SectionPos pos = interactable.getSectionPos();
        long key = pos.asLong();

        sectionByInteractable.put(interactable.id, key);
        addInteractableToSection(interactable, pos);
    }

    public void removeInteractable(Interactable interactable) {
        long key = sectionByInteractable.get(interactable.id);
        sectionByInteractable.remove(interactable.id);
        removeInteractableFromSection(interactable, SectionPos.of(key));
    }

    public void clear() {
        lookupBySection.clear();
        sectionByInteractable.clear();
    }

    protected boolean updateInteractablePosition(Interactable interactable) {
        if (!sectionByInteractable.containsKey(interactable.id)) {
            Clinker.LOGGER.info("No interactable with ID " + interactable.id + " found in sectionByInteractable! This means something has gone terribly, terribly wrong.");
            return false;
        }

        SectionPos oldSectionPos = SectionPos.of(sectionByInteractable.get(interactable.id));
        SectionPos newSectionPos = interactable.getSectionPos();

        if (oldSectionPos.asLong() != newSectionPos.asLong()) {
            removeInteractableFromSection(interactable, oldSectionPos);
            addInteractableToSection(interactable, newSectionPos);

            return true;
        }

        return false;
    }

    protected void removeSectionWithInteractables(SectionPos pos) {
        long key = pos.asLong();
        if (!this.lookupBySection.containsKey(key)) return;
        if (getInteractablesInSection(pos).isEmpty()) {
            removeEmptySection(pos);
            return;
        }

        for (Interactable interactable : this.getInteractablesInSection(pos)) {
            this.sectionByInteractable.remove(interactable.id);
        }
        this.getLookupInSection(pos).byUuid.clear();
        this.lookupBySection.remove(pos.asLong());
    }

    private void removeEmptySection(SectionPos pos) {
        this.lookupBySection.remove(pos.asLong());
    }

    private void addInteractableToSection(Interactable interactable, SectionPos pos) {
        // add the interactable to the section storage. if there's nothing there yet, create it.
        if (!lookupBySection.containsKey(pos.asLong())) this.lookupBySection.put(pos.asLong(), new InteractableLookup());
        this.getLookupInSection(pos).putInteractable(interactable);
    }

    private void removeInteractableFromSection(Interactable interactable, SectionPos pos) {
        // remove the interactable from the section storage. if there's nothing there anymore, yeet it.
        this.getLookupInSection(pos).removeInteractable(interactable);
        if (lookupBySection.get(pos.asLong()).getInteractables().isEmpty()) removeEmptySection(pos);
    }
}
