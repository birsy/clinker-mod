package birsy.clinker.common.world.physics.particle;

public abstract class Constraint {
    public final ParticleParent parent;

    public Constraint(ParticleParent parent) {
        this.parent = parent;
        this.parent.addConstraint(this);
    }

    protected abstract void apply();

    public void tick() {}
}
