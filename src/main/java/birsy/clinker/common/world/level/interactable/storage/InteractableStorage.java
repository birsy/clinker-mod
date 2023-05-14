package birsy.clinker.common.world.level.interactable.storage;

import birsy.clinker.common.world.level.interactable.Interactable;
import birsy.clinker.core.Clinker;
import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;

import java.util.*;
import java.util.function.Consumer;

public class InteractableStorage {
    public final InteractableLookup interactables;
    public final Map<UUID, Long> sectionIdByInteractable = new HashMap();
    public final Long2ObjectMap<InteractableSection> sections;
    public final LongSortedSet sectionIds = new LongAVLTreeSet();

    public InteractableStorage() {
        this.interactables = new InteractableLookup();
        this.sections = new Long2ObjectOpenHashMap<>();
    }

    public List<Interactable> getInteractablesInBounds(AABB pBoundingBox) {
        int minX = SectionPos.posToSectionCoord(pBoundingBox.minX - 16.0D);
        int minY = SectionPos.posToSectionCoord(pBoundingBox.minY - 16.0D);
        int minZ = SectionPos.posToSectionCoord(pBoundingBox.minZ - 16.0D);
        int maxX = SectionPos.posToSectionCoord(pBoundingBox.maxX + 16.0D);
        int maxY = SectionPos.posToSectionCoord(pBoundingBox.maxY + 16.0D);
        int maxZ = SectionPos.posToSectionCoord(pBoundingBox.maxZ + 16.0D);

        List<Interactable> list = new ArrayList<>();

        for(int x = minX; x <= maxX; x++) {
            for(int y = minY; y <= maxY; y++) {
                for(int z = minZ; z <= maxZ; z++) {
                    long sectionId = SectionPos.asLong(x, y, z);
                    if (sectionIds.contains(sectionId)) {
                        InteractableSection section = this.sections.get(sectionId);
                        list.addAll(section.getInteractables().filter(interactable -> pBoundingBox.intersects(interactable.shape.getBounds())).toList());
                    }
                }
            }
        }

        return list;
    }

    public void forInteractablesInBounds(AABB pBounds, Consumer<Interactable> pConsumer) {
        this.getInteractablesInBounds(pBounds).forEach(pConsumer);
    }

    public Iterable<Interactable> getAllInteractables() {
        return this.interactables.getAllInteractables();
    }

    public void removeChunk(ChunkPos pos) {
        LongSortedSet set = getChunkSections(pos.x, pos.z);
        if (set.isEmpty()) return;
        for (Long id : set) {
            this.removeSection(id);
        }
    }

    public Iterable<Interactable> getInteractablesInChunk(ChunkPos pos) {
        List<Interactable> list = new ArrayList<>();
        LongSortedSet set = getChunkSections(pos.x, pos.z);
        for (long id : set) {
            if (this.sections.containsKey(id)) list.addAll(this.sections.get(id).getInteractables().toList());
        }

        return list;
    }

    private LongSortedSet getChunkSections(int pX, int pZ) {
        long x = SectionPos.asLong(pX, 0, pZ);
        long y = SectionPos.asLong(pX, -1, pZ);
        return this.sectionIds.subSet(x, y + 1L);
    }

    private InteractableSection createSection(SectionPos pos) {
        return this.createSection(pos.asLong());
    }

    private InteractableSection createSection(long sectionID) {
        InteractableSection section = new InteractableSection();
        this.sectionIds.add(sectionID);
        this.sections.put(sectionID, section);
        return section;
    }

    public void removeSection(SectionPos pos) {
        this.removeSection(pos.asLong());
    }

    public void removeSection(long pSectionId) {
        InteractableSection s = this.sections.get(pSectionId);
        s.getInteractables().forEach(interactable -> this.sectionIdByInteractable.remove(interactable.uuid));
        s.clear();
        this.sections.remove(pSectionId);
        this.sectionIds.remove(pSectionId);
    }

    private void removeSectionIfEmpty(SectionPos pos) {
        this.removeSectionIfEmpty(pos.asLong());
    }

    private void removeSectionIfEmpty(long id) {
        if (this.sectionIds.contains(id)) {
            if (this.sections.get(id).isEmpty()) removeSection(id);
        }
    }

    public void clear() {
        interactables.clear();
        sectionIdByInteractable.clear();
        sections.clear();
        sectionIds.clear();
    }

    public void addInteractable(Interactable interactable) {
        this.interactables.add(interactable);
        long sectionID = interactable.getSectionPosition().asLong();
        if (!this.sectionIds.contains(sectionID)) createSection(sectionID);
        this.sections.get(sectionID).add(interactable);
        this.sectionIdByInteractable.put(interactable.uuid, sectionID);
    }

    public Interactable getInteractable(UUID id) {
        return this.interactables.getInteractable(id);
    }

    public void removeInteractable(Interactable interactable) {
        this.interactables.remove(interactable);
        if(!this.sectionIdByInteractable.containsKey(interactable.uuid)) return;
        long sectionID = this.sectionIdByInteractable.get(interactable.uuid);
        if (this.sectionIds.contains(sectionID)) {
            this.sections.get(sectionID).remove(interactable);
            this.sectionIdByInteractable.remove(interactable.uuid);
            removeSectionIfEmpty(sectionID);
        }
    }

    public void removeInteractable(UUID id) {
        this.interactables.remove(id);
        if(!this.sectionIdByInteractable.containsKey(id)) return;
        long sectionID = this.sectionIdByInteractable.get(id);
        if (this.sectionIds.contains(sectionID)) {
            this.sections.get(sectionID).remove(id);
            this.sectionIdByInteractable.remove(id);
            removeSectionIfEmpty(sectionID);
        }
    }

    private void addInteractableToSection(Interactable interactable, long sectionID) {
        if (!this.sectionIds.contains(sectionID)) createSection(sectionID);
        this.sections.get(sectionID).add(interactable);
        if (sectionIdByInteractable.containsKey(interactable.uuid)) {
            this.sectionIdByInteractable.replace(interactable.uuid, sectionID);
        } else {
            this.sectionIdByInteractable.put(interactable.uuid, sectionID);
        }
    }

    private void removeInteractableFromCurrentSection(Interactable interactable) {
        if(!this.sectionIdByInteractable.containsKey(interactable.uuid)) return;
        long sectionID = this.sectionIdByInteractable.get(interactable.uuid);
        if (this.sectionIds.contains(sectionID)) {
            this.sections.get(sectionID).remove(interactable);
            removeSectionIfEmpty(sectionID);
        }
    }

    public void updateInteractableLocation(Interactable interactable) {
        boolean hasInteractableID = this.sectionIdByInteractable.containsKey(interactable.uuid);
        long newSectionId = interactable.getSectionPosition().asLong();
        long sectionId = hasInteractableID ? this.sectionIdByInteractable.get(interactable.uuid) : 0;

        if (sectionId != newSectionId || !hasInteractableID) {
            removeInteractableFromCurrentSection(interactable);
            addInteractableToSection(interactable, newSectionId);
        }
    }
}
