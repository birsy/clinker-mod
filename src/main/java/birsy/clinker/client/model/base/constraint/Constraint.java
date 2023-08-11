package birsy.clinker.client.model.base.constraint;

public interface Constraint {
    void initialize();

    void apply();

    boolean isSatisfied();

    boolean isIterative();
}
