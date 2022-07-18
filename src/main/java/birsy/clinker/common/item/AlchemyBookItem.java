package birsy.clinker.common.item;

import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
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
    public static void onClick(InputEvent.MouseButton.Pre event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null){
            ItemStack mainHandItem = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack offHandItem = mc.player.getItemInHand(InteractionHand.OFF_HAND);
            ItemStack book = null;

            boolean isRightClick = event.getButton() == 0;
            boolean isLeftClick = event.getButton() == 1;

            if (mainHandItem.getItem() instanceof AlchemyBookItem) {
                book = mainHandItem;
            } else if (offHandItem.getItem() instanceof AlchemyBookItem) {
                book = offHandItem;
            }

            if (book != null) {
                if (book.getItem() instanceof AlchemyBookItem && (isRightClick || isLeftClick) && mc.cameraEntity.getViewXRot(1.0F) > 20.25) {
                    boolean direction = !isRightClick;
                    boolean didTurnPage = turnPage(book, direction, 1);
                    if (didTurnPage) {
                        event.setCanceled(true);
                        mc.level.playSound(mc.player, mc.cameraEntity, SoundEvents.BOOK_PAGE_TURN, SoundSource.BLOCKS, 1.0f, 1.0f);
                        Clinker.LOGGER.info(mc.cameraEntity.getViewXRot(1.0F));
                    }
                }
            }
        }

    }
}
