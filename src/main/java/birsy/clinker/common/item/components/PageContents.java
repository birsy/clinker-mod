package birsy.clinker.common.item.components;

import birsy.clinker.client.render.ClinkerFonts;
import birsy.clinker.common.page.Page;
import birsy.clinker.core.registry.ClinkerDataComponents;
import birsy.clinker.core.registry.ClinkerDynamicRegistries;
import birsy.clinker.core.registry.ClinkerItems;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.function.Consumer;

public record PageContents(Holder<Page> page) implements TooltipProvider {
    public static final Codec<PageContents> CODEC =
            RegistryFileCodec.create(ClinkerDynamicRegistries.PAGE_REGISTRY_KEY, Page.CODEC)
                             .xmap(PageContents::new, PageContents::page);
    public static final StreamCodec<RegistryFriendlyByteBuf, PageContents> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holder(ClinkerDynamicRegistries.PAGE_REGISTRY_KEY, Page.STREAM_CODEC), PageContents::page,
            PageContents::new
    );
    public static final PageContents DEFAULT = new PageContents(Holder.direct(Page.BLANK_PAGE));

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        tooltipAdder.accept(Component.translatable(this.page.value().titleTranslationKey)
                .withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.DARK_GRAY).withFont(ClinkerFonts.SERIF_SOFT)));
    }

    public static ItemStack createItemStack(Holder<Page> page) {
        ItemStack itemstack = new ItemStack(ClinkerItems.PAGE.get());
        itemstack.set(ClinkerDataComponents.PAGE, new PageContents(page));
        return itemstack;
    }
}
