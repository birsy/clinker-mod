package birsy.clinker.common.world.physics.particle;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.List;

public interface ParticleParent<P extends CollidingParticle, C extends Constraint> {
    @Nullable
    Level getLevel();
    @Nullable
    Vec3 getPosition();

    List<P> getParticles();
    default void addParticle(P particle) {
        this.getParticles().add(particle);
    }
    default void removeParticle(P particle) {
        this.getParticles().remove(particle);
    }

    List<C> getConstraints();
    default void addConstraint(C constraint) {
        this.getConstraints().add(constraint);
    }
    default void removeConstraint(C constraint) {
        this.getConstraints().remove(constraint);
    }

    default void tickParticles() {
        for (C constraint : this.getConstraints()) {
            constraint.tick();
        }
        for (P particle : this.getParticles()) {
            particle.tick();
        }
    }

    default void updatePhysics(Vec3 gravity, float deltaTime, int constraintIterations) {
        for (P particle : this.getParticles()) {
            particle.tick();
            particle.accelerate(gravity);
            applyBuoyancyAndDragForce(particle, gravity);

            particle.beginTick(deltaTime);
        }

        for (int i = 0; i < constraintIterations; i++) {
            for (C constraint : this.getConstraints()) {
                constraint.apply();
            }
            for (P particle1 : this.getParticles()) {
                particle1.applyCollisionConstraints();
                for (P particle2 : this.getParticles()) {
                    collideJoints(particle1, particle2);
                }
            }
        }

        for (P particles : this.getParticles()) {
            particles.finalizeTick();
        }
    }

    default void applyBuoyancyAndDragForce(P particle, Vec3 gravity) {
        double mass = 0.9;
        double inverseMass = 1.0 / mass;
        double airDensity = 0.1;
        Level level = this.getLevel();
        if (level == null) return;

        // calculates the bouyant force in a really jank way
        // takes into account partial submersion... kinda
        Vec3 downPos = particle.position.subtract(0, particle.radius, 0);
        BlockPos jointPosDown = new BlockPos(particle.position);
        BlockPos jointPosUp = new BlockPos(particle.position.add(0, particle.radius, 0));

        double volumeInDown = downPos.y - Math.ceil(downPos.y);
        FluidState stateDown = level.getFluidState(jointPosDown);
        double densityDown = stateDown.isEmpty() ? airDensity : stateDown.getFluidType().getDensity() * 0.001D;

        double volumeInUp = 1 - volumeInDown;
        FluidState stateUp = level.getFluidState(jointPosUp);
        double densityUp = stateUp.isEmpty() ? airDensity : stateUp.getFluidType().getDensity() * 0.001D;

        double averageDensity = (densityDown * volumeInDown + densityUp * volumeInUp);
        particle.accelerate(gravity.scale(-1 * averageDensity).scale(inverseMass));

        Vec3 velocity = particle.getDeltaMovement();
        double dragCoefficient = 0.05;
        Vec3 drag = velocity.scale(Math.exp(-averageDensity * dragCoefficient));
        particle.physicsPrevPosition = particle.position.subtract(drag);
    }

    default void collideJoints(P particle1, P particle2) {
        if (particle1 == particle2) return;
        double minimumDistance = particle1.radius + particle2.radius;
        double distanceSqr = particle1.physicsNextPosition.distanceToSqr(particle2.physicsNextPosition);
        if (distanceSqr < minimumDistance * minimumDistance) {
            double correctionDistance = minimumDistance - Math.sqrt(distanceSqr);
            //point of collision will always be directly in the middle, in the case of spheres.
            Vec3 collisionVector = particle1.physicsNextPosition.subtract(particle2.physicsNextPosition).normalize().scale(correctionDistance * 0.5);

            particle1.physicsNextPosition = particle1.physicsNextPosition.add(collisionVector);
            particle2.physicsNextPosition = particle2.physicsNextPosition.subtract(collisionVector);
        }
    }
}
