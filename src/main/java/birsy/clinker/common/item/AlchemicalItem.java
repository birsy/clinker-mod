package birsy.clinker.common.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class AlchemicalItem extends Item {
    public AlchemicalItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, InteractionHand handIn) {
        this.cycleQuality(playerIn.getItemInHand(handIn));
        return InteractionResultHolder.success(playerIn.getItemInHand(handIn));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag isAdvanced) {
        if (this.getQuality(stack) != AlchemicalQuality.NONE) {
            tooltip.add(Component.translatable("item.clinker.quality").append(this.getQuality(stack).getTranslationComponent()));
        }
        tooltip.add(Component.translatable(this.getDescriptionId(stack) + ".description").withStyle(ChatFormatting.DARK_GRAY, ChatFormatting.ITALIC));
        super.appendHoverText(stack, worldIn, tooltip, isAdvanced);
    }

    @Override
    public Component getName(ItemStack stack) {
        boolean isAcceptableQuality = this.getQuality(stack) != AlchemicalQuality.DUBIOUS;
        return isAcceptableQuality ? Component.translatable(this.getDescriptionId(stack)) : Component.translatable(this.getDescriptionId(stack)).withStyle(ChatFormatting.RED);
    }

    public boolean hasQuality(ItemStack stack) {
        CompoundTag nbt = stack.getTagElement("quality");
        return nbt != null && nbt.contains("index", 99);
    }

    public AlchemicalQuality getQuality(ItemStack stack) {
        if (hasQuality(stack)) {
            CompoundTag nbt = stack.getTagElement("quality");
            return AlchemicalQuality.fromIndex(nbt.getInt("index") % 5);
        } else {
            return AlchemicalQuality.NONE;
        }
    }

    public void setQuality(ItemStack stack, AlchemicalQuality qualityIn) {
        stack.getOrCreateTagElement("quality").putInt("index", qualityIn.getIndex());
    }

    public void setQuality(ItemStack stack, int qualityIndex) {
        stack.getOrCreateTagElement("quality").putInt("index", qualityIndex % 5);
    }

    public void cycleQuality(ItemStack stack) {
        setQuality(stack, getQuality(stack).getIndex() + 1);
    }

    public enum AlchemicalQuality {
        NONE("none", ChatFormatting.RESET, 0),
        DUBIOUS("bad", ChatFormatting.DARK_RED, 1),
        ACCEPTABLE("average", ChatFormatting.YELLOW, 2),
        PROFICIENT("good", ChatFormatting.GREEN, 3),
        EXQUISITE("perfect", ChatFormatting.AQUA,4);

        private final int index;
        private final String name;
        private final String translationKey;
        private final ChatFormatting colorFormatting;

        AlchemicalQuality(String name, ChatFormatting color, int index) {
            this.name = name;
            this.translationKey = "item.clinker.quality." + name;
            this.colorFormatting = color;
            this.index = index;
        }

        public Component getTranslationComponent() {
            return Component.translatable(this.getString()).withStyle(this.getColor());
        }

        public ChatFormatting getColor() {
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

        public String getString() {
            return this.translationKey;
        }
    }
}
