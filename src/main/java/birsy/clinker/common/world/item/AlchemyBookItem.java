package birsy.clinker.common.world.item;

import net.minecraft.world.item.Item;

// todo: redo to use components
public class AlchemyBookItem extends Item {
    public static final String TAG_PAGE_NUMBER = "PageNumber";
    public static final String TAG_NOTES = "Notes";

    public static final int PAGES = 16;

    public AlchemyBookItem(Properties itemProperties) {
        super(itemProperties.stacksTo(1));
    }
}
