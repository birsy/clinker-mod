package birsy.clinker.client.render.world.gas;

import birsy.clinker.core.Clinker;
import net.minecraft.core.SectionPos;

import java.util.*;

public class GasSectionHeap {
    protected final GasSection[] array;
    private final Set<Integer> freeSectionIndices;
    protected final Map<SectionPos, Integer> containedSections;

    public GasSectionHeap(int size) {
        array = new GasSection[size + 1];
        Arrays.fill(array, null);
        containedSections = new HashMap<>(size, 1.0F);
        freeSectionIndices = new HashSet<>(size, 1.0F);
        for (int i = 0; i < size; i++) freeSectionIndices.add(i + 1);
    }

    public int getIndex(SectionPos pos) {
        return this.containedSections.getOrDefault(pos, -1);
    }

    public int add(GasSection section) {
        if (freeSectionIndices.isEmpty()) {
            Clinker.LOGGER.error("GasSection buffer out of memory!");
            return -1;
        }

        // get the first available index
        int index = freeSectionIndices.iterator().next();
        array[index] = section;
        containedSections.put(section.sectionPos, index);
        return index;
    }

    public boolean remove(int i) {
        if (i < 0 || i >= array.length || freeSectionIndices.contains(i)) return false;
        freeSectionIndices.add(i);
        if (array[i] != null) {
            containedSections.remove(array[i].sectionPos);
            array[i] = null;
            return true;
        }
        return false;
    }

    public GasSection retrieve(int i) {
        return array[i];
    }

    public void clear() {
        freeSectionIndices.clear();
        containedSections.clear();
        for (int i = 0; i < array.length; i++) freeSectionIndices.add(i + 1);
        Arrays.fill(array, null);
    }

    public boolean full() {
        return freeSectionIndices.isEmpty();
    }
}
