package birsy.clinker.common.world.othershore;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.world.ClinkerDimensions;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class OthershoreEffectsEventHandler {
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        //If the player is in the Othershore and can take damage, damage their armor once every four ticks.
        //This has a minor bug where you can circumvent this by rapidly leaving and rejoining, but I don't really care.
        if (event.player.getEntityWorld().getDimensionKey() == ClinkerDimensions.OTHERSHORE && event.player.abilities.disableDamage) {
            if (event.player.ticksExisted % 4 == 0) {
                for (ItemStack itemStack : event.player.getArmorInventoryList()) {
                    if (itemStack.getEquipmentSlot() != null) {
                        itemStack.damageItem(1, event.player, (consumer) -> consumer.sendBreakAnimation(itemStack.getEquipmentSlot()));
                    }
                }
            }

            for (ItemStack itemStack : event.player.getArmorInventoryList()) {
                if (itemStack.getEquipmentSlot() != null) {
                    itemStack.damageItem(1, event.player, (consumer) -> consumer.sendBreakAnimation(itemStack.getEquipmentSlot()));
                }
            }
        }
    }
}
