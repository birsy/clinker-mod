package birsy.clinker.common.alchemy.chemicals;

import birsy.clinker.core.Clinker;

import java.util.ArrayList;
import java.util.List;

public class Element extends net.minecraftforge.registries.ForgeRegistryEntry<Element> {
    protected List<ChemicalBond> chemicalBonds;

    //Below or equal to 0, the element is considered "FIXED." Above zero, the element is considered "VOLATILE."
    //An element can only form *stable* bonds with an element whose nature is one away from itself.
    public final int elementNature;

    public Element(int elementNature) {
        this.elementNature = elementNature;
        chemicalBonds = new ArrayList<>(getBondStrength() + 1);
    }

    //Determines the amount of bonds an element can have. The closer the element's nature is to zero, the higher the bond strength.
    //If a bond is forced, and no unstable bonds can be created, than a HIGHLY UNSTABLE bond is formed with the MOST STABLE POSSIBLE bond in a system. This is how explosions happen!
    public int getBondStrength() {
        return Math.max(1, (-1 * Math.abs(2 * elementNature)) + 6);
    }

    /**
     * TODO: FIGURE OUT HOW IN GOD'S NAME THIS WILL WORK.
     * TODO CONT'D: NEED TO CONSIDER SHAPE OF CHEMICAL?
     * TODO CONT'D: BONDS BETWEEN CHEMICALS? UHHH...
     * TODO CONT'D: I'LL NEED PENCIL AND PAPER FOR THIS. LEAVE IT AS "IN PROGRESS" FOR NOW.
     */
    public class ChemicalBond {
        public final Element elementOne;
        public final Element elementTwo;

        public float bondStrength = 1.0F;
        public float vitriol = 0.0F;

        private ChemicalBond(Element elementOne, Element elementTwo) {
            this(elementOne, elementTwo, 1.0F);
        }

        private ChemicalBond(Element elementOne, Element elementTwo, float bondStrength) {
            this.elementOne = elementOne;
            this.elementTwo = elementTwo;

            this.initializeBond(bondStrength);

            this.elementOne.chemicalBonds.add(this);
            this.elementTwo.chemicalBonds.add(this);
        }

        private void initializeBond(float baseBondStrength) {

        }

        public void create(Element elementOne, Element elementTwo, float bondStrength) {
            int elementOneBonds = 0;
            for (ChemicalBond chemicalBond : elementOne.chemicalBonds) {
                elementOneBonds += chemicalBond != null ? 1 : 0;
            }

            int elementTwoBonds = 0;
            for (ChemicalBond chemicalBond : elementTwo.chemicalBonds) {
                elementTwoBonds += chemicalBond != null ? 1 : 0;
            }

            if (elementOneBonds > elementOne.chemicalBonds.size() && elementTwoBonds > elementTwo.chemicalBonds.size()) {
                boolean isBondStable = elementOneBonds > elementOne.chemicalBonds.size() - 1 && elementTwoBonds > elementTwo.chemicalBonds.size() - 1;
                float vitriol = (Math.abs(elementOne.elementNature - elementTwo.elementNature) - 1) * 0.75F * (isBondStable ? 1 : 2);

            } else {
                Clinker.LOGGER.info("Failed to make bond!");
            }
        }

        public void destroy() {
            this.elementOne.chemicalBonds.remove(this);
            this.elementTwo.chemicalBonds.remove(this);
        }

        public float getBondStrength(float temperature) {
            return bondStrength;
        }
    }
}
