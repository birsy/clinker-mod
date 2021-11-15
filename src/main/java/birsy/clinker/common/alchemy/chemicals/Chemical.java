package birsy.clinker.common.alchemy.chemicals;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds elements, and the bonds between them.
 * Bonds can also form between distinct chemicals.
 */
public class Chemical {
    public ElementNode[] elementNodes;
    public ElementBond[] elementBonds;

    public Chemical(ElementNode... nodes) {
        this();
        this.addNodes(nodes);
    }
    
    public Chemical() {}

    public Chemical addNodes(ElementNode... nodes) {
        for (ElementNode node : nodes) {
            node.add(elementNodes);
        }
        return this;
    }


    public ElementBond bondNodes(int nodeIndex1, int nodeIndex2) {
        return new ElementBond(this, nodeIndex1, nodeIndex2);
    }
    
    public List<ElementBond> getBondsFromNode(int nodeIndex) {
        List<ElementBond> elementBondsList = new ArrayList<>();

        for (ElementBond elementBond : elementBonds) {
            if (elementBond.nodeIndex1 == nodeIndex || elementBond.nodeIndex2 == nodeIndex) {
                elementBondsList.add(elementBond);
            }
        }

        return elementBondsList;
    }


    public class ElementNode {
        public final Element element;
        private int index;
        protected List<Integer> connectedNodes;

        public ElementNode(Element element, ElementNode[] chemicalNodeList) {
            this(element);

            this.index = chemicalNodeList.length + 1;
            chemicalNodeList[this.index] = this;
        }

        public ElementNode(Element element) {
            this.element = element;
            connectedNodes = new ArrayList<>(element.getBondStrength());
        }

        public void add(ElementNode[] chemicalElementNodeList) {
            chemicalElementNodeList[this.index] = this;
            this.index = chemicalElementNodeList.length;
        }
    }
    
    public class ElementBond {
        public final Chemical chemical;
        public final int nodeIndex1;
        public final int nodeIndex2;

        public float bondStrength = 1.0F;
        public float vitriol = 0.0F;

        private ElementBond(Chemical chemical, int nodeIndex1, int nodeIndex2) {
            this(chemical, nodeIndex1, nodeIndex2, 1.0F);
        }

        private ElementBond(Chemical chemical, int nodeIndex1, int nodeIndex2, float bondStrength) {
            this.chemical = chemical;
            this.nodeIndex1 = nodeIndex1;
            this.nodeIndex2 = nodeIndex2;

            this.initializeBond(bondStrength);

            this.getNodeOne().connectedNodes.add(nodeIndex2);
            this.getNodeTwo().connectedNodes.add(nodeIndex1);
        }

        private void initializeBond(float baseBondStrength) {
            float natureDifference = Math.abs(this.getNodeOne().element.elementNature - getNodeTwo().element.elementNature);

            this.bondStrength = baseBondStrength;
            if (natureDifference - 1.0F > 0.0F) {
                this.vitriol = natureDifference - 1.0F;
            }
        }

        public void destroy() {
            this.getNodeOne().connectedNodes.remove(nodeIndex2);
            this.getNodeTwo().connectedNodes.remove(nodeIndex1);
        }

        public float getBondStrength(float temperature) {
            return bondStrength;
        }

        public ElementNode getNodeOne() {
            return chemical.elementNodes[nodeIndex1];
        }

        public ElementNode getNodeTwo() {
            return chemical.elementNodes[nodeIndex2];
        }
    }
}
