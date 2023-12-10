package birsy.clinker.common.world.alchemy.chemicals;

import birsy.clinker.common.world.alchemy.MatterState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

public class ItemChemicalStack {
//    private final Item item;
//    private int amount;
//
//    public ItemChemicalStack(Item item, int amount) {
//        this.item = item;
//        this.amount = amount;
//    }
//
//    public ItemChemicalStack(CompoundTag tagIn) {
//        this(ForgeRegistries.ITEMS.getValue(new ResourceLocation(tagIn.getString("id"))), tagIn.getInt("amount"));
//    }
//
//
//    @Override
//    public float getAmount() {
//        return this.amount;
//    }
//
//    @Override
//    public boolean setAmount(float amount) {
//        if (Math.floor(amount) != amount) {
//            return false;
//        } else {
//            this.amount = (int)Math.floor(amount);
//            return true;
//        }
//    }
//
//    //TODO: add the stuff for powders
//    @Override
//    public MatterState getState() {
//        return MatterState.SOLID;
//    }
//
//    @Override
//    public ChemicalHolder create(float amount) {
//        return new ItemChemicalStack(this.item, (int) amount);
//    }
//
//    @Override
//    public boolean is(ChemicalHolder holder) {
//        if (holder instanceof ItemChemicalStack) {
//            return (holder.getState() == this.getState()) && (((ItemChemicalStack) holder).asItem() == this.asItem());
//        }
//        return false;
//    }
//
//    @Override
//    public CompoundTag write(CompoundTag tagIn) {
//        ResourceLocation key = ForgeRegistries.ITEMS.getKey(this.asItem());
//        tagIn.putString("id", key.toString());
//        tagIn.putInt ("amount", amount);
//
//        return tagIn;
//    }
//
//    @Override
//    public ChemicalHolder read(CompoundTag tagIn) {
//        Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(tagIn.getString("id")));
//        int amount = tagIn.getInt("amount");
//
//        return new ItemChemicalStack(item, amount);
//    }
//
//    @Override
//    //TODO: add volumes for different items
//    public float getVolume() {
//        if (this.asItem() instanceof BlockItem) {
//            return 1.0F * amount;
//        } else {
//            return (1.0F / 16.0F) * amount;
//        }
//    }
//
//    @Override
//    public Item asItem() {
//        return item;
//    }
}
