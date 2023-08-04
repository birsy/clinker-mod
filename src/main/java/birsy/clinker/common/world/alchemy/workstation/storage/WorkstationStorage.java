package birsy.clinker.common.world.alchemy.workstation.storage;

import birsy.clinker.common.world.alchemy.workstation.Workstation;
import birsy.clinker.common.world.alchemy.workstation.WorkstationManager;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongAVLTreeSet;
import it.unimi.dsi.fastutil.longs.LongSortedSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class WorkstationStorage implements List<Workstation> {
    private final List<Workstation> storage = new ArrayList<>();
    private final Map<UUID, Workstation> byUUID = new HashMap<>();
    private final Map<UUID, LongSortedSet> sectionByUUID = new HashMap<>();
    private final Map<UUID, LongSortedSet> loadedSectionByUUID = new HashMap<>();
    private final Long2ObjectMap<WorkstationLookup> bySection = new Long2ObjectOpenHashMap<>();

    private final WorkstationManager manager;

    public WorkstationStorage(WorkstationManager manager) {
        this.manager = manager;
    }

    @Override
    public boolean add(Workstation workstation) {
        byUUID.put(workstation.uuid, workstation);
        sectionByUUID.put(workstation.uuid, new LongAVLTreeSet());
        loadedSectionByUUID.put(workstation.uuid, new LongAVLTreeSet());
        return storage.add(workstation);
    }

    @Override
    public boolean remove(Object o) {
        if (o instanceof Workstation w) {
            this.remove(w.uuid);
            return true;
        }

        return false;
    }

    public void remove(UUID id) {
        this.storage.remove(byUUID.get(id));
        byUUID.remove(id);
        for (long sectionPos : sectionByUUID.get(id)) {
            bySection.get(sectionPos).remove(id);
        }
        sectionByUUID.remove(id);
        loadedSectionByUUID.remove(id);
    }

    public Workstation get(UUID id) {
        return this.byUUID.get(id);
    }

    public boolean contains(UUID id) {
        return this.byUUID.containsKey(id);
    }

    public void addBlockToWorkstation(UUID id, BlockPos pos) {
        Workstation station = this.get(id);
        station.addBlock(pos);

        long sPos = SectionPos.of(pos).asLong();
        if (!bySection.containsKey(sPos)) {
            bySection.put(sPos, new WorkstationLookup());
        }
        bySection.get(sPos).add(station);
        sectionByUUID.get(id).add(sPos);
        loadedSectionByUUID.get(id).add(sPos);
    }

    public void removeBlockFromWorkstation(UUID id, BlockPos pos) {
        Workstation station = this.get(id);
        station.removeBlock(pos);

        long sPos = SectionPos.of(pos).asLong();
        for (BlockPos containedBlock : station.containedBlocks) {
            if (SectionPos.of(containedBlock).asLong() == sPos) return;
        }
        removeWorkstationFromSection(sPos, id);
    }

    private void removeWorkstationFromSection(long sectionPos, UUID id) {
        WorkstationLookup stations = bySection.get(sectionPos);
        stations.remove(id);
        sectionByUUID.get(id).remove(sectionPos);
        loadedSectionByUUID.get(id).remove(sectionPos);
    }

    public List<Workstation> getPotentialAdjacentWorkstations(BlockPos pos) {
        long sPos = SectionPos.of(pos).asLong();
        List<Workstation> list = getWorkstationsInSection(sPos);

        for (Vec3i directionalOffset : WorkstationManager.DIRECTIONAL_OFFSETS) {
            long sPos2 = SectionPos.of(pos.offset(directionalOffset)).asLong();
            if (sPos2 != sPos) {
                list.addAll(getWorkstationsInSection(sPos2));
            }
        }
        return list;
    }

    public List<Workstation> getWorkstationsInSection(SectionPos pos) {
        return this.getWorkstationsInSection(pos.asLong());
    }

    public List<Workstation> getWorkstationsInSection(long sectionPosId) {
        WorkstationLookup workstations = bySection.get(sectionPosId);
        if (workstations == null) return new ArrayList<>();
        return workstations.stream().toList();
    }

    public void unloadChunk(ChunkAccess chunk) {
        List<Workstation> workstationsToCheck = new ArrayList<>();
        SectionPos pos = SectionPos.bottomOf(chunk);

        for (int y = chunk.getMinSection(); y < chunk.getMaxSection(); y++) {
            Collection<Workstation> workstations = bySection.get(pos.asLong()).getAllWorkstations();

            for (Workstation workstation : workstations) {
                loadedSectionByUUID.get(workstation.uuid).remove(pos.asLong());
            }

            workstationsToCheck.addAll(workstations);
            bySection.get(pos.asLong()).clear();

            pos = (SectionPos) pos.above();
        }

        for (Workstation workstation : workstationsToCheck) {
            //workstation no longer exists in any loaded sections, we can unload it
            if (loadedSectionByUUID.get(workstation.uuid).isEmpty()) {
                this.saveWorkstationToLevel(workstation.uuid);
                this.remove(workstation.uuid);
            }
        }
    }


    private void saveWorkstationToLevel(UUID id) {

    }


    @Override
    public int size() {
        return storage.size();
    }

    @Override
    public boolean isEmpty() {
        return storage.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof Workstation w) {
            return this.contains(w.uuid);
        }
        return false;
    }

    @NotNull
    @Override
    public Iterator<Workstation> iterator() {
        return storage.iterator();
    }

    @NotNull
    @Override
    public Object[] toArray() {
        return storage.toArray();
    }

    @NotNull
    @Override
    public <T> T[] toArray(@NotNull T[] a) {
        return storage.toArray(a);
    }

    @Override
    public boolean containsAll(@NotNull Collection<?> c) {
        return storage.containsAll(c);
    }

    @Override
    public boolean addAll(@NotNull Collection<? extends Workstation> c) {
        for (Workstation workstation : c) {
            this.add(workstation);
        }
        return true;
    }

    @Override
    public boolean removeAll(@NotNull Collection<?> c) {
        for (Object o : c) {
            this.remove(o);
        }
        return true;
    }

    @Override
    public boolean retainAll(@NotNull Collection<?> c) {
        return storage.retainAll(c);
    }

    public ListIterator<Workstation> listIterator() {
        return storage.listIterator();
    }

    @Override
    public void clear() {
        this.storage.clear();
        this.byUUID.clear();
        this.loadedSectionByUUID.clear();
        this.bySection.clear();
    }

    public boolean addAll(int index, @NotNull Collection<? extends Workstation> c) { throw new UnsupportedOperationException(); }
    public Workstation get(int index) { throw new UnsupportedOperationException(); }
    public Workstation set(int index, Workstation element) { throw new UnsupportedOperationException(); }
    public void add(int index, Workstation element) { throw new UnsupportedOperationException(); }
    public Workstation remove(int index) { throw new UnsupportedOperationException(); }
    public int indexOf(Object o) { throw new UnsupportedOperationException(); }
    public int lastIndexOf(Object o) { throw new UnsupportedOperationException(); }
    public ListIterator<Workstation> listIterator(int index) { throw new UnsupportedOperationException(); }
    public List<Workstation> subList(int fromIndex, int toIndex) { throw new UnsupportedOperationException(); }
}
