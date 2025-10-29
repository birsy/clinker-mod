package birsy.clinker.common.world.item;

import birsy.clinker.common.page.Page;
import birsy.clinker.common.world.item.components.PageContents;
import birsy.clinker.core.registry.ClinkerDataComponents;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.*;

import java.util.List;

public class PageItem extends Item {
    public PageItem(Properties properties) {
        super(properties);
    }

    private Holder<Page> getPage(ItemStack stack) {
        return stack.getOrDefault(ClinkerDataComponents.PAGE, PageContents.DEFAULT).page();
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        stack.getOrDefault(ClinkerDataComponents.PAGE, PageContents.DEFAULT).addToTooltip(context, tooltipComponents::add, tooltipFlag);
    }
}
