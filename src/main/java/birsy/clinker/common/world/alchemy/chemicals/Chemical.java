package birsy.clinker.common.world.alchemy.chemicals;

import birsy.clinker.common.world.alchemy.anatomy.EntityBody;
import net.minecraft.world.item.Rarity;

import java.util.Map;

public abstract class Chemical {
    public final Rarity rarity = Rarity.COMMON;
    public float density;
    public float hardness;
    public float malleability;
    public float conductivity;
    public float volatility;

    public Map<Solvent, Float> solubility;
    public boolean solvent;

    public float meltingPoint;
    public float boilingPoint;
    public float burningPoint;
    public int color;

    public Chemical(Chemical.Properties properties) {
        this.density = properties.density;
        this.hardness = properties.hardness;
        this.malleability = properties.malleability;
        this.conductivity = properties.conductivity;
        this.volatility = properties.volatility;

        this.solubility = properties.solubility;
        this.solvent = properties.solvent;

        this.meltingPoint = properties.meltingPoint;
        this.boilingPoint = properties.boilingPoint;
        this.burningPoint = properties.burningPoint;

        this.color = properties.color;
    }

    public void tick(Mixture currentMixture, float deltaTime) {}
    public abstract void affectOrgan(EntityBody.EntityOrgan organ, float dillution);

    public static class Properties {
        Rarity rarity = Rarity.COMMON;
        float density = 1.0F;
        float hardness = 1.0F;
        float malleability = 0.0F;
        float conductivity = 0.0F;
        float volatility = 0.0F;

        Map<Solvent, Float> solubility;
        boolean solvent;

        //degrees measured in celsius
        float meltingPoint = 0.0F;
        float boilingPoint = 100.0F;
        float burningPoint = 120.0F;
        int color;

        public Properties rarity(Rarity rarity) {
            this.rarity = rarity;
            return this;
        }

        public Properties density(float density) {
            this.density = density;
            return this;
        }
        public Properties hardness(float hardness) {
            this.hardness = hardness;
            return this;
        }
        public Properties malleability(float malleability) {
            this.malleability = malleability;
            return this;
        }
        public Properties conductivity(float conductivity) {
            this.conductivity = conductivity;
            return this;
        }
        public Properties volatility(float volatility) {
            this.volatility = volatility;
            return this;
        }

        public Properties solvent(boolean solvent) {
            this.solvent = solvent;
            return this;
        }
        public Properties solubility(Solvent solvent, float solubility) {
            this.solubility.put(solvent, solubility);
            return this;
        }

        public Properties meltingPoint(float meltingPoint) {
            this.meltingPoint = meltingPoint;
            return this;
        }
        public Properties boilingPoint(float boilingPoint) {
            this.boilingPoint = boilingPoint;
            return this;
        }
        public Properties burningPoint(float burningPoint) {
            this.burningPoint = burningPoint;
            return this;
        }

        public Properties color(float r, float g, float b) {
            return this.color((int) r * 255, (int) g * 255, (int) b * 255);
        }
        public Properties color(int r, int g, int b) {
            return  this.color((r << 16) & (g << 8) & b);
        }
        public Properties color(int color) {
            this.color = color;
            return this;
        }
    }
}
