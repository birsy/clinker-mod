package birsy.clinker.client.render.world.gas;

import java.util.BitSet;

public class GasSectionList {
    final GasSection[] array;
    final BitSet bits;

    public GasSectionList(int size) {
        this.array = new GasSection[size];
        this.bits = new BitSet(size);
        // reserve the first index
        this.bits.set(0);
    }

    public int add(GasSection section) {
        int i = bits.nextClearBit(0);
        array[i] = section;
        bits.set(i);
        return i;
    }

    public void remove(int index) {
        array[index] = null;
        bits.set(index, false);
    }

    public GasSection retrieve(int index) {
        return array[index];
    }
}
