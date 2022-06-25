package birsy.clinker.common.item;

import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AlchemyBookItem extends Item {
    public static final String TAG_PAGE_NUMBER = "PageNumber";
    public static final String TAG_NOTES = "Notes";

    public static final int PAGES = 16;

    public AlchemyBookItem(Properties itemProperties) {
        super(itemProperties.stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack pStack, @Nullable Level pLevel, List<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        pTooltipComponents.add(Component.literal("Page " + getPageNumber(pStack)).withStyle(Style.EMPTY.withFont( new ResourceLocation(Clinker.MOD_ID, "alchemical"))));
        super.appendHoverText(pStack, pLevel, pTooltipComponents, pIsAdvanced);
    }

    public static boolean turnPage(ItemStack itemstack, boolean direction, int pages) {
        CompoundTag compoundtag = itemstack.hasTag() ? itemstack.getTag().copy() : new CompoundTag();
        int pageNumber = compoundtag.getInt(TAG_PAGE_NUMBER);
        int difference = pages * (direction ? 1 : -1);
        int newPage = Mth.clamp(pageNumber + difference, 0, PAGES);

        if (newPage != pageNumber) {
            compoundtag.putInt(TAG_PAGE_NUMBER, newPage);
            itemstack.setTag(compoundtag);
        }

        return newPage != pageNumber;
    }

    public static int getPageNumber(ItemStack itemstack) {
        CompoundTag compoundtag = itemstack.hasTag() ? itemstack.getTag().copy() : new CompoundTag();
        return compoundtag.getInt(TAG_PAGE_NUMBER);
    }

    @SubscribeEvent
    public static void onClick(InputEvent.ClickInputEvent event) {
        Minecraft mc = Minecraft.getInstance();
        ItemStack book = mc.player.getItemInHand(event.getHand());
        if (book.getItem() instanceof AlchemyBookItem && (event.isAttack() || event.isUseItem())) {
            boolean direction = !event.isAttack();
            boolean didTurnPage = turnPage(book, direction, 1);
            if (didTurnPage) {
                event.setSwingHand(false);
                event.setCanceled(true);
            }
        }
    }
}
