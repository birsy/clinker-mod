package birsy.clinker.common.alchemy.chemicals;

import birsy.clinker.core.Clinker;

import java.util.ArrayList;
import java.util.List;

public class Element {

    //Below or equal to 0, the element is considered "FIXED." Above zero, the element is considered "VOLATILE."
    //An element can only form *stable* bonds with an element whose nature is one away from itself.
    public final int elementNature;

    public Element(int elementNature) {
        this.elementNature = elementNature;
    }

    //Determines the amount of bonds an element can have. The closer the element's nature is to zero, the higher the bond strength.
    //If a bond is forced, and no unstable bonds can be created, than a HIGHLY UNSTABLE bond is formed with the MOST STABLE POSSIBLE bond in a system. This is how explosions happen!
    public int getBondStrength() {
        return Math.max(1, (-1 * Math.abs(2 * elementNature)) + 6);
    }
}
