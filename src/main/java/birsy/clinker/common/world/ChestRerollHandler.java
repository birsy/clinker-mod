package birsy.clinker.common.world;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerDataAttachments;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import org.jetbrains.annotations.Nullable;

public class ChestRerollHandler {
    public static void unpackLootTable(RandomizableContainerBlockEntity container, @Nullable Player player) {
        if (container.getLootTable() != null && container.getLevel() != null && container.getLevel().getServer() != null) {
            // if there is a loot table, save it.
            container.setData(ClinkerDataAttachments.REROLL_LOOT_LOCATION, container.getLootTable().toString());
            // then mark us as filling in the loot table.
            container.setData(ClinkerDataAttachments.FILLING_LOOT_TABLE, true);
            // and we can reroll!
            container.setData(ClinkerDataAttachments.CAN_REROLL_LOOT, true);

            // run the original function.
            container.unpackLootTable(player);

            container.setData(ClinkerDataAttachments.FILLING_LOOT_TABLE, false);
        }
    }

    // run whenever something touches a loot container.
    public static void disableRerolls(RandomizableContainerBlockEntity container) {
        // if we're currently filling in the loot table, don't disable rerolls!
        if (container.hasData(ClinkerDataAttachments.FILLING_LOOT_TABLE)) {
            if (container.getData(ClinkerDataAttachments.FILLING_LOOT_TABLE)) {
                return;
            } else {
                Clinker.LOGGER.info(container.getData(ClinkerDataAttachments.FILLING_LOOT_TABLE));
                // otherwise, disable them.
                container.setData(ClinkerDataAttachments.CAN_REROLL_LOOT, false);
            }
        }

    }
}
