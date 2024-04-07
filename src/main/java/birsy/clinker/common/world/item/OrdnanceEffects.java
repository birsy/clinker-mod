package birsy.clinker.common.world.item;

import birsy.clinker.common.world.entity.OldOrdnanceEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class OrdnanceEffects {
    private float radius;
    private float damage;
    private float knockback;
    private float stun;
    private List<OrdnanceModifier> modifiers;

    public float getRadius(OldOrdnanceEntity ordnance) {
        float initialRadius = this.radius;
        for (OrdnanceModifier modifier : this.modifiers) {
            initialRadius = modifier.getAdjustedRadius(initialRadius, ordnance);
        }
        return initialRadius;
    }

    public float getDamage(@Nullable OldOrdnanceEntity ordnance, @Nullable Entity entity, float distance) {
        float radius = this.getRadius(ordnance);
        float initialDamage = getRadiusAdjustedFactor(radius, distance, this.damage);
        for (OrdnanceModifier modifier : this.modifiers) {
            initialDamage = modifier.getAdjustedDamage(initialDamage, distance, radius, entity, ordnance);
        }
        return initialDamage;
    }

    public float getKnockback(@Nullable OldOrdnanceEntity ordnance, @Nullable Entity entity, float distance) {
        float radius = this.getRadius(ordnance);
        float initialKnockback = getRadiusAdjustedFactor(radius, distance, this.knockback);
        for (OrdnanceModifier modifier : this.modifiers) {
            initialKnockback = modifier.getAdjustedKnockback(initialKnockback, distance, radius, entity, ordnance);
        }
        return initialKnockback;
    }

    public float getStun(@Nullable OldOrdnanceEntity ordnance, @Nullable Entity entity, float distance) {
        float radius = this.getRadius(ordnance);
        float initialStun = getRadiusAdjustedFactor(radius, distance, this.knockback);
        for (OrdnanceModifier modifier : this.modifiers) {
            initialStun = modifier.getAdjustedStun(initialStun, distance, radius, entity, ordnance);
        }
        return initialStun;
    }

    // lower radius means the value is more highly concentrated.
    private static float getRadiusAdjustedFactor(float radius, float distance, float value) {
        if (distance > radius) return 0;
        return ((1.0F - (distance / radius)) / getTotalOutput(radius)) * value;
    }

    // the total amount of stuff within the ordnance, accounting for falloff.
    // thanks to wolfram alpha:
    // https://www.wolframalpha.com/input?i2d=true&i=Integrate%5B%5C%2840%29%5C%2840%291+-+Divide%5Br%2CR%5D%5C%2841%29+*+%5C%2840%29Divide%5B4%2C3%5D%CF%80*Power%5Br%2C3%5D%5C%2841%29%5C%2841%29%2C%7Br%2C0%2CR%7D%5D
    private static float getTotalOutput(float radius) {
        return (Mth.PI * radius * radius * radius * radius) / 15.0F;
    }

    public static abstract class OrdnanceModifier {
        final float amount;

        public OrdnanceModifier(float amount) {
            this.amount = amount;
        }

        protected void onOrdnanceCreation(OldOrdnanceEntity ordnance) {}

        protected void onFuseEnd(OldOrdnanceEntity ordnance) {}
        protected void onImpact(OldOrdnanceEntity ordnance, Vec3 velocity) {}
        protected void onImpactTerrain(OldOrdnanceEntity ordnance, Vec3 velocity, BlockState state, BlockPos blockPos, Vec3 impactPosition) { this.onImpact(ordnance, velocity); }
        protected void onImpactEntity(OldOrdnanceEntity ordnance, Vec3 velocity, Entity entity, Vec3 position) { this.onImpact(ordnance, velocity); }

        public float getAdjustedRadius(float radiusIn, @Nullable OldOrdnanceEntity ordnance) {
            return radiusIn;
        }
        public float getAdjustedDamage(float damageIn, float distance, float radius, @Nullable Entity entity, @Nullable OldOrdnanceEntity ordnance) {
            return damageIn;
        }
        public float getAdjustedKnockback(float knockbackIn, float distance, float radius, @Nullable Entity entity, @Nullable OldOrdnanceEntity ordnance) {
            return knockbackIn;
        }
        public float getAdjustedStun(float stunIn, float distance, float radius, @Nullable Entity entity, @Nullable OldOrdnanceEntity ordnance) {
            return stunIn;
        }
    }

    // causes the ordnance to explode
    public static class ExplosiveModifier extends OrdnanceModifier {
        public ExplosiveModifier(float amount) {
            super(amount);
        }

//        @Override
//        protected void onFuseEnd(OrdnanceEntity ordnance) {
//            super.onFuseEnd(ordnance, entitiesInRadius);
//        }
    }

    // causes the ordnance to explode on impact.
    public static class ContactExplosiveModifier extends OrdnanceModifier {
        public ContactExplosiveModifier() {
            super(1.0F);
        }

        @Override
        protected void onImpact(OldOrdnanceEntity ordnance, Vec3 velocity) {
            super.onImpact(ordnance, velocity);
            ordnance.detonate();
        }
    }

    // causes the ordnance to stick to walls
    public static class StickyModifier extends OrdnanceModifier {
        public StickyModifier() {
            super(1.0F);
        }

        @Override
        protected void onImpactEntity(OldOrdnanceEntity ordnance, Vec3 velocity, Entity entity, Vec3 position) {
            super.onImpactEntity(ordnance, velocity, entity, position);
            ordnance.stickToEntity(entity);
        }

        @Override
        protected void onImpactTerrain(OldOrdnanceEntity ordnance, Vec3 velocity, BlockState state, BlockPos blockPos, Vec3 impactPosition) {
            super.onImpactTerrain(ordnance, velocity, state, blockPos, impactPosition);
            ordnance.setStuck(true);
        }
    }

    // causes the ordnance to bounce off walls
    public static class BouncyModifier extends OrdnanceModifier {
        public BouncyModifier(float amount) {
            super(amount);
        }

        @Override
        protected void onOrdnanceCreation(OldOrdnanceEntity ordnance) {
            super.onOrdnanceCreation(ordnance);
            float elasticity = (-0.5F / (this.amount + 1.0F)) + 1.0F;
            ordnance.setElasticity(elasticity);
        }
    }
}
