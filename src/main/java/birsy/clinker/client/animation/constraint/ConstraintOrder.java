package birsy.clinker.client.animation.constraint;

public enum ConstraintOrder {
    PRE_PHYSICS(1), DURING_PHYSICS(2), POST_PHYSICS(3);

    public int index;
    ConstraintOrder(int index) {
        this.index = index;
    }


}
