package birsy.clinker.common.alchemy.chemicals;

import birsy.clinker.common.alchemy.MatterState;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

public class ChemicalStack implements ChemicalHolder, ChemicalLike {
    protected float amount;
    protected final Chemical chemical;
    protected final MatterState state;

    public ChemicalStack(Chemical chemical, MatterState state) {
        this(chemical, state, 0.0F);
    }

    public ChemicalStack(Chemical chemical, MatterState state, float amount) {
        this.chemical = chemical;
        this.amount = amount;
        this.state = state;
    }

    public ChemicalStack(CompoundTag tagIn) {
        Chemical chemical = ClinkerRegistries.CHEMICAL.get().getValue(new ResourceLocation(tagIn.getString("id")));
        MatterState state = MatterState.fromName(tagIn.getString("state"));
        float amount = tagIn.getFloat("amount");

        this.chemical = chemical;
        this.amount = amount;
        this.state = state;
    }

    @Override
    public float getAmount() {
        return amount;
    }

    @Override
    public float getVolume() {
        return amount * chemical.density;
    }

    @Override
    public boolean setAmount(float amount) {
        this.amount = amount;
        return true;
    }

    @Override
    public MatterState getState() {
        return state;
    }

    @Override
    public void tick(Mixture currentMixture, float deltaTime) {
        this.asChemical().tick(currentMixture, deltaTime);
    }

    @Override
    public ChemicalHolder create(float amount) {
        return new ChemicalStack(this.chemical, this.state, amount);
    }

    @Override
    public boolean is(ChemicalHolder holder) {
        if (holder instanceof ChemicalStack) {
            return (holder.getState() == this.getState()) && (((ChemicalStack) holder).asChemical() == this.asChemical());
        }
        return false;
    }

    @Override
    public CompoundTag write(CompoundTag tagIn) {
        ResourceLocation key = ClinkerRegistries.CHEMICAL.get().getKey(this.asChemical());
        tagIn.putString("id", key.toString());
        tagIn.putString("state", this.getState().name);
        tagIn.putFloat ("amount", this.getAmount());

        return tagIn;
    }

    @Override
    public ChemicalHolder read(CompoundTag tagIn) {
        Chemical chemical = ClinkerRegistries.CHEMICAL.get().getValue(new ResourceLocation(tagIn.getString("id")));
        MatterState state = MatterState.fromName(tagIn.getString("state"));
        float amount = tagIn.getFloat("amount");

        return new ChemicalStack(chemical, state, amount);
    }

    public Chemical asChemical() {
        return chemical;
    }
}
