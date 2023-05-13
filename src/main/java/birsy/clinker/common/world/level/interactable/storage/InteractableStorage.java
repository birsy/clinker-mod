package birsy.clinker.common.world.level.interactable.storage;

import birsy.clinker.common.world.level.interactable.Interactable;
import it.unimi.dsi.fastutil.longs.*;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.AABB;

import java.util.*;
import java.util.function.Consumer;

public class InteractableStorage {
    private final InteractableLookup interactables;
    private final Map<UUID, Long> sectionIdByInteractable = new HashMap();
    private final Long2ObjectMap<InteractableSection> sections;
    private final LongSortedSet sectionIds = new LongAVLTreeSet();

    public InteractableStorage() {
        this.interactables = new InteractableLookup();
        this.sections = new Long2ObjectOpenHashMap<>();
    }

    public List<Interactable> getInteractablesInBounds(AABB pBoundingBox) {
        int j = SectionPos.posToSectionCoord(pBoundingBox.minX - 2.0D);
        int k = SectionPos.posToSectionCoord(pBoundingBox.minY - 4.0D);
        int l = SectionPos.posToSectionCoord(pBoundingBox.minZ - 2.0D);
        int i1 = SectionPos.posToSectionCoord(pBoundingBox.maxX + 2.0D);
        int j1 = SectionPos.posToSectionCoord(pBoundingBox.maxY + 0.0D);
        int k1 = SectionPos.posToSectionCoord(pBoundingBox.maxZ + 2.0D);

        List<Interactable> list = new ArrayList<>();

        for(int l1 = j; l1 <= i1; ++l1) {
            long i2 = SectionPos.asLong(l1, 0, 0);
            long j2 = SectionPos.asLong(l1, -1, -1);
            LongIterator longiterator = this.sectionIds.subSet(i2, j2 + 1L).iterator();

            while(longiterator.hasNext()) {
                long k2 = longiterator.nextLong();
                int l2 = SectionPos.y(k2);
                int i3 = SectionPos.z(k2);
                if (l2 >= k && l2 <= j1 && i3 >= l && i3 <= k1) {
                    InteractableSection section = this.sections.get(k2);
                    if (section != null && !section.isEmpty()) {
                        list.addAll(section.getInteractables().toList());
                    }
                }
            }
        }

        return list;
    }

    public void forEachAccessibleNonEmptySection(AABB pBoundingBox, Consumer<InteractableSection> pConsumer) {
        int j = SectionPos.posToSectionCoord(pBoundingBox.minX - 2.0D);
        int k = SectionPos.posToSectionCoord(pBoundingBox.minY - 4.0D);
        int l = SectionPos.posToSectionCoord(pBoundingBox.minZ - 2.0D);
        int i1 = SectionPos.posToSectionCoord(pBoundingBox.maxX + 2.0D);
        int j1 = SectionPos.posToSectionCoord(pBoundingBox.maxY + 0.0D);
        int k1 = SectionPos.posToSectionCoord(pBoundingBox.maxZ + 2.0D);

        for(int l1 = j; l1 <= i1; ++l1) {
            long i2 = SectionPos.asLong(l1, 0, 0);
            long j2 = SectionPos.asLong(l1, -1, -1);
            LongIterator longiterator = this.sectionIds.subSet(i2, j2 + 1L).iterator();

            while(longiterator.hasNext()) {
                long k2 = longiterator.nextLong();
                int l2 = SectionPos.y(k2);
                int i3 = SectionPos.z(k2);
                if (l2 >= k && l2 <= j1 && i3 >= l && i3 <= k1) {
                    InteractableSection section = this.sections.get(k2);
                    if (section != null && !section.isEmpty()) {
                        pConsumer.accept(section);
                    }
                }
            }
        }
    }

    public void forInteractablesInBounds(AABB pBounds, Consumer<Interactable> pConsumer) {
        this.forEachAccessibleNonEmptySection(pBounds, (section) -> section.forInteractablesInBounds(pBounds, pConsumer));
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
        this.sectionIdByInteractable.replace(interactable.uuid, sectionID);
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
        if(!this.sectionIdByInteractable.containsKey(interactable.uuid)) return;
        long sectionId = this.sectionIdByInteractable.get(interactable.uuid);
        long newSectionId = interactable.getSectionPosition().asLong();

        if (sectionId == newSectionId) return;
        removeInteractableFromCurrentSection(interactable);
        addInteractableToSection(interactable, sectionId);
    }
}
