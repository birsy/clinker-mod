package birsy.clinker.common;

import birsy.clinker.common.page.Page;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerDataMaps;
import birsy.clinker.core.registry.ClinkerDynamicRegistries;
import birsy.clinker.core.registry.entity.ClinkerAttributes;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;

@EventBusSubscriber(modid = Clinker.MOD_ID)
public class ClinkerCommonEventHandler {
    @SubscribeEvent
    public static void onDataLoad(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();
        // set up the page key lookup.
        Page.KEY_LOOKUP.clear();
        for (Holder.Reference<Page> pageReference : server.registryAccess().lookupOrThrow(ClinkerDynamicRegistries.PAGE_REGISTRY_KEY).listElements().toList()) {
            Page.KEY_LOOKUP.put(pageReference.value(), pageReference.getKey());
        }
    }

    private static final ResourceLocation CONDUCTIVITY_MODIFIER_ID = Clinker.resource("conductivity");
    @SubscribeEvent
    public static void modifyItemAttributes(ItemAttributeModifierEvent event) {
        Float conductivity = event.getItemStack().getItemHolder().getData(ClinkerDataMaps.ITEM_CONDUCTIVITY);
        if (conductivity != null) {
            EquipmentSlotGroup equipmentSlotGroup = EquipmentSlotGroup.HAND;
            if (event.getItemStack().has(DataComponents.TOOL)) {
                equipmentSlotGroup = EquipmentSlotGroup.MAINHAND;
            }
            if  (event.getItemStack().getItem() instanceof ArmorItem armor) {
                equipmentSlotGroup = switch (armor.getType()) {
                    case HELMET -> EquipmentSlotGroup.HEAD;
                    case CHESTPLATE -> EquipmentSlotGroup.CHEST;
                    case LEGGINGS -> EquipmentSlotGroup.LEGS;
                    case BOOTS -> EquipmentSlotGroup.FEET;
                    case BODY -> EquipmentSlotGroup.BODY;
                };
            }

            event.addModifier(
                    ClinkerAttributes.CONDUCTIVITY,
                    new AttributeModifier(CONDUCTIVITY_MODIFIER_ID, conductivity, AttributeModifier.Operation.ADD_VALUE),
                    equipmentSlotGroup
            );
        }
    }

    @SubscribeEvent
    public static void modifyEntityAttributes(EntityAttributeModificationEvent event) {
        for (EntityType<? extends LivingEntity> type : event.getTypes()) {
            Float conductivity = type.builtInRegistryHolder().getData(ClinkerDataMaps.ENTITY_CONDUCTIVITY);
            if (conductivity != null) {
                event.add(type, ClinkerAttributes.CONDUCTIVITY, conductivity);
            } else {
                event.add(type, ClinkerAttributes.CONDUCTIVITY);
            }
        }
    }
}
