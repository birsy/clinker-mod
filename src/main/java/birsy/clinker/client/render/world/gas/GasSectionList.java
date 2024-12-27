package birsy.clinker.client.render.world.gas;

import birsy.clinker.core.Clinker;

import java.util.Arrays;

public class GasSectionList {
    final GasSection[] array;
    //final BitSet bits;

    public GasSectionList(int size) {
        this.array = new GasSection[size + 1];
        Arrays.fill(array, null);
    }

    public int add(GasSection section) {
        for (int i = 1; i < array.length; i++) {
            if (array[i] == null) {
                array[i] = section;
                return i;
            }
        }
        Clinker.LOGGER.error("GasSection buffer out of memory!");
        return -1;
    }

    public void remove(int i) {
        array[i] = null;
    }

    public GasSection retrieve(int i) {
        return array[i];
    }

    public void clear() {
        Arrays.fill(array, null);
    }
}
