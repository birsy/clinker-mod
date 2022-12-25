package birsy.clinker.common.alchemy.chemicals;

import birsy.clinker.common.alchemy.MatterState;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class SolventStack extends ChemicalStack {
    public SolventMixture mixture;

    public SolventStack(Solvent solvent, MatterState state, float amount) {
        super(solvent, state, amount);
        this.mixture = new SolventMixture(solvent, solvent.capacity * amount);
    }

    public SolventStack(Solvent solvent, MatterState state) {
        this(solvent, state, 0.0F);
    }

    public SolventStack(CompoundTag tagIn) {
        super(ClinkerRegistries.CHEMICAL.get().getValue(new ResourceLocation(tagIn.getString("id"))),
                MatterState.fromName(tagIn.getString("state")),
                tagIn.getFloat("amount"));

        Solvent chemical = (Solvent) ClinkerRegistries.CHEMICAL.get().getValue(new ResourceLocation(tagIn.getString("id")));
        this.mixture = (SolventMixture) new SolventMixture(chemical, 1.0F).read(tagIn.getCompound("dissolved mixture"));
    }

    @Override
    public boolean setAmount(float amount) {
        this.mixture.maxAmount = this.getSolvent().capacity * amount;
        return super.setAmount(amount);
    }

    @Override
    public float getAmount() {
        return super.getAmount() + mixture.getAmount();
    }

    @Override
    public float getVolume() {
        return super.getVolume() + mixture.getVolume();
    }

    @Override
    public void tick(Mixture currentMixture, float deltaTime) {
        super.tick(currentMixture, deltaTime);
        this.mixture.tick(deltaTime);
    }

    @Override
    public CompoundTag write(CompoundTag tagIn) {
        ResourceLocation key = ClinkerRegistries.CHEMICAL.get().getKey(this.asChemical());
        tagIn.putString("id", key.toString());
        tagIn.putString("state", this.getState().name);
        tagIn.putFloat ("amount", this.getAmount());
        CompoundTag mixtureTag = new CompoundTag();
        this.mixture.write(mixtureTag);
        tagIn.put("dissolved mixture", mixtureTag);

        return tagIn;
    }

    @Override
    public ChemicalHolder read(CompoundTag tagIn) {
        Solvent chemical = (Solvent) ClinkerRegistries.CHEMICAL.get().getValue(new ResourceLocation(tagIn.getString("id")));
        MatterState state = MatterState.fromName(tagIn.getString("state"));
        float amount = tagIn.getFloat("amount");

        SolventMixture mixture = (SolventMixture) new SolventMixture(chemical, 1.0F).read(tagIn.getCompound("dissolved mixture"));

        SolventStack stack = new SolventStack(chemical, state, amount);
        stack.mixture = mixture;
        return stack;
    }

    public ChemicalHolder dissolve(ChemicalHolder chemical) {
        return this.mixture.addChemical(chemical);
    }
    public void dissolve(ChemicalHolder chemical, Mixture currentMixture) {
        ChemicalHolder spilledChemical = this.mixture.addChemical(chemical);
        if (spilledChemical != null) {
            currentMixture.addChemical(spilledChemical);
        }
    }

    public Solvent getSolvent() {
        return (Solvent) this.asChemical();
    }

    private class SolventMixture extends Mixture {
        final Solvent solvent;
        float maxAmount; //not volume! amount!
        public SolventMixture(Solvent solvent, float maxAmount) {
            super(maxAmount * solvent.density);
            this.maxAmount = maxAmount;
            this.solvent = solvent;
        }

        @Override
        public ChemicalHolder addChemical(ChemicalHolder chemical) {
            float chemAmount = chemical.getAmount();
            float amountSpilled = maxAmount - (this.getAmount() + chemAmount);
            if (amountSpilled <= 0) {
                addToMakeup(chemical);
                return null;
            } else {
                if (chemical instanceof ItemChemicalStack) {
                    chemical.subtractAmount((float) Math.ceil(amountSpilled));
                    addToMakeup(chemical);
                    return chemical.create((float) Math.ceil(amountSpilled));
                } else {
                    chemical.subtractAmount(amountSpilled);
                    addToMakeup(chemical);
                    return chemical.create(amountSpilled);
                }
            }
        }

        @Override
        public CompoundTag write(CompoundTag tag) {
            ResourceLocation key = ClinkerRegistries.CHEMICAL.get().getKey(this.solvent);
            tag.putString("Solvent Id", key.toString());
            tag.putFloat("Maximum Amount", maxAmount);
            return super.write(tag);
        }

        @Override
        public Mixture read(CompoundTag tag) {
            float heat = tag.getFloat("Heat");
            float maxAmount = tag.getFloat("Maximum Amount");
            Solvent solvent = (Solvent) ClinkerRegistries.CHEMICAL.get().getValue(new ResourceLocation(tag.getString("Solvent Id")));

            CompoundTag chemicalContainer = tag.getCompound("Chemicals");
            ListTag chemicals = chemicalContainer.getList("Chemicals", 10);
            ListTag items = chemicalContainer.getList("Items", 10);
            ListTag solvents = chemicalContainer.getList("Solvents", 10);
            List<ChemicalHolder> chemicalList = new ArrayList<>();
            for (Tag chemical : chemicals) {
                CompoundTag t = (CompoundTag) chemical;
                ChemicalStack stack = new ChemicalStack(t);
                chemicalList.add(stack);
            }
            for (Tag chemical : items) {
                CompoundTag t = (CompoundTag) chemical;
                ItemChemicalStack stack = new ItemChemicalStack(t);
                chemicalList.add(stack);
            }
            for (Tag chemical : solvents) {
                CompoundTag t = (CompoundTag) chemical;
                SolventStack stack = new SolventStack(t);
                chemicalList.add(stack);
            }

            Mixture mix = new SolventMixture(solvent, maxAmount);
            mix.setHeat(heat);

            return mix;
        }
    }
}
