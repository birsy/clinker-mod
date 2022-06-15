package birsy.clinker.common.alchemy.chemicals;

import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mixture {
    public List<Pair<Chemical, Float>> makeup;

    public Mixture(Chemical chemical, float amountInMB) {
        this(new Pair<>(chemical, amountInMB));
    }

    public Mixture(Pair<Chemical, Float> chemical) {
        this.makeup = Arrays.asList(chemical);
    }

    public Mixture(Pair<Chemical, Float>... chemicals) {
        this.makeup = Arrays.asList(chemicals);
    }
}
