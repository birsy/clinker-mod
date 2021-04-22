package birsy.clinker.common.alchemy.chemicals;

import net.minecraft.potion.Effect;

public abstract class AbstractChemical {
    public Effect getEffect() {
        return null;
    }

    /**
     * @return The effect that the element gives to potions.
     */
    public Effect getDrinkEffect() {
        return this.getEffect();
    }

    /**
     * @return The effect that the element gives when contacted, via walking on or swam in.
     */
    public Effect getContactEffect() {
        return this.getEffect();
    }
}
