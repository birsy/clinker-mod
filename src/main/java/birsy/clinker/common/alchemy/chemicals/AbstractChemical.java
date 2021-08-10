package birsy.clinker.common.alchemy.chemicals;

import net.minecraft.world.effect.MobEffect;

import MobEffect;

public abstract class AbstractChemical {
    public MobEffect getEffect() {
        return null;
    }

    /**
     * @return The effect that the element gives to potions.
     */
    public MobEffect getDrinkEffect() {
        return this.getEffect();
    }

    /**
     * @return The effect that the element gives when contacted, via walking on or swam in.
     */
    public MobEffect getContactEffect() {
        return this.getEffect();
    }
}
