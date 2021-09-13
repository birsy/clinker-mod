package birsy.clinker.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class AlchemicalItem extends Item {
    public AlchemicalItem(Properties properties) {
        super(properties);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        this.cycleQuality(playerIn.getHeldItem(handIn));
        return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (this.getQuality(stack) != AlchemicalQuality.NONE) {
            tooltip.add(new TranslationTextComponent("item.clinker.quality").appendSibling(this.getQuality(stack).getTranslationComponent()));
        }
        tooltip.add(new TranslationTextComponent(this.getTranslationKey(stack) + ".description").mergeStyle(TextFormatting.DARK_GRAY).mergeStyle(TextFormatting.ITALIC));
        super.addInformation(stack, worldIn, tooltip, flagIn);
    }

    @Override
    public ITextComponent getDisplayName(ItemStack stack) {
        boolean isAcceptableQuality = this.getQuality(stack) != AlchemicalQuality.DUBIOUS;
        return isAcceptableQuality ? new TranslationTextComponent(this.getTranslationKey(stack)) : new TranslationTextComponent(this.getTranslationKey(stack)).mergeStyle(TextFormatting.RED);
    }

    public boolean hasQuality(ItemStack stack) {
        CompoundNBT nbt = stack.getChildTag("quality");
        return nbt != null && nbt.contains("index", 99);
    }

    public AlchemicalQuality getQuality(ItemStack stack) {
        if (hasQuality(stack)) {
            CompoundNBT nbt = stack.getChildTag("quality");
            return AlchemicalQuality.fromIndex(nbt.getInt("index") % 5);
        } else {
            return AlchemicalQuality.NONE;
        }
    }

    public void setQuality(ItemStack stack, AlchemicalQuality qualityIn) {
        stack.getOrCreateChildTag("quality").putInt("index", qualityIn.getIndex());
    }

    public void setQuality(ItemStack stack, int qualityIndex) {
        stack.getOrCreateChildTag("quality").putInt("index", qualityIndex % 5);
    }

    public void cycleQuality(ItemStack stack) {
        setQuality(stack, getQuality(stack).getIndex() + 1);
    }

    public enum AlchemicalQuality implements IStringSerializable{
        NONE("none", TextFormatting.RESET, 0),
        DUBIOUS("bad", TextFormatting.DARK_RED, 1),
        ACCEPTABLE("average", TextFormatting.YELLOW, 2),
        PROFICIENT("good", TextFormatting.GREEN, 3),
        EXQUISITE("perfect", TextFormatting.AQUA,4);

        private final int index;
        private final String name;
        private final String translationKey;
        private final TextFormatting colorFormatting;

        AlchemicalQuality(String name, TextFormatting color, int index) {
            this.name = name;
            this.translationKey = "item.clinker.quality." + name;
            this.colorFormatting = color;
            this.index = index;
        }

        public TranslationTextComponent getTranslationComponent() {
            return (TranslationTextComponent) new TranslationTextComponent(this.getString()).mergeStyle(this.getColor());
        }

        public TextFormatting getColor() {
            return this.colorFormatting;
        }

        public String getName() {
            return this.name;
        }

        public int getIndex() {
            return this.index;
        }

        public static AlchemicalQuality fromIndex(int index) {
            if (index < 0) {
                return NONE;
            } else {
                for(AlchemicalQuality alchemicalQuality : values()) {
                    if (alchemicalQuality.getIndex() == index) {
                        return alchemicalQuality;
                    }
                }
                return null;
            }
        }

        @Override
        public String getString() {
            return this.translationKey;
        }
    }
}
