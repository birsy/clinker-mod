package birsy.clinker.common.alchemy.chemicals;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mixture implements INBTSerializable<CompoundTag> {
    public List<ChemicalHolder> makeup;
    public float heat = 0.0F;
    public float maxVolume;

    public Mixture(float maxVolume, ChemicalHolder... chemicals) {
        this(maxVolume);
        this.makeup = Arrays.asList(chemicals);
    }

    public Mixture(float maxVolume) {
        this.maxVolume = maxVolume;
    }

    public CompoundTag write(CompoundTag tag) {
        tag.putFloat("Heat", this.getHeat());
        tag.putFloat("Max Volume", this.maxVolume);
        CompoundTag chemicalsTag = new CompoundTag();
        ListTag chemicals = new ListTag();
        ListTag items = new ListTag();
        ListTag solvents = new ListTag();
        for (ChemicalHolder chemicalHolder : this.makeup) {
            CompoundTag holder = new CompoundTag();
            chemicalHolder.write(holder);
            if (chemicalHolder instanceof ChemicalStack) {
                chemicals.add(holder);
            }
            if (chemicalHolder instanceof ItemChemicalStack) {
                items.add(holder);
            }
            if (chemicalHolder instanceof SolventStack) {
                solvents.add(holder);
            }
        }
        chemicalsTag.put("Chemicals", chemicals);
        chemicalsTag.put("Items", items);
        chemicalsTag.put("Solvents", solvents);

        tag.put("Chemicals", chemicalsTag);

        return tag;
    }

    public Mixture read(CompoundTag tag) {
        float heat = tag.getFloat("Heat");
        float maxVolume = tag.getFloat("Max Volume");
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

        Mixture mix = new Mixture(maxVolume);
        mix.setHeat(heat);

        return mix;
    }

    public void tick(float deltaTime) {
        for (ChemicalHolder chemical : makeup) {
            if (chemical instanceof ChemicalStack) {
                ((ChemicalStack) chemical).asChemical().tick(this, deltaTime);
            }
        }
    }

    //Returns the chemical that was spilled amount spilled, if any.
    public ChemicalHolder addChemical(ChemicalHolder chemical) {
        float chemVolume = chemical.getVolume();
        float volumeSpilled = maxVolume - (this.getVolume() + chemVolume);
        float amountSpilled = volumeSpilled * chemical.getDensity();
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

    //Returns how much was removed.
    public float removeChemical(ChemicalHolder chemical) {
        for (int i = 0; i < this.makeup.size(); i++) {
            ChemicalHolder chemicalHolder = this.makeup.get(i);
            if (chemicalHolder.is(chemical)) {
                float originalAmount = chemicalHolder.getAmount() + 0.0F;
                float clampedAmount = Math.max(originalAmount - chemical.getAmount(), 0.0F);
                chemicalHolder.setAmount(clampedAmount);
                float currentAmount = chemicalHolder.getAmount();

                if (chemicalHolder.getAmount() >= 0) {
                    this.makeup.remove(i);
                }
                return Math.abs(originalAmount - currentAmount);
            }
        }

       return 0;
    }

    public void addToMakeup(ChemicalHolder chemical) {
        for (ChemicalHolder chemicalHolder : this.makeup) {
            if (chemicalHolder.is(chemical)) {
                chemicalHolder.addAmount(chemical.getAmount());
                return;
            }
        }

        makeup.add(chemical);
    }

    public float getVolume() {
        float totalVolume = 0;

        for (ChemicalHolder chemical : makeup) {
            totalVolume += chemical.getVolume();
        }

        return totalVolume;
    }

    public float getAmount() {
        float totalAmount = 0;

        for (ChemicalHolder chemical : makeup) {
            totalAmount += chemical.getAmount();
        }

        return totalAmount;
    }

    public float getHeat() {
        return heat;
    }

    public void setHeat(float heat) {
        this.heat = heat;
    }

    @Override
    public CompoundTag serializeNBT() {
        return this.write(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.read(nbt);
    }
}
