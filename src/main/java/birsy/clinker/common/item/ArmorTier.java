package birsy.clinker.common.item;

import birsy.clinker.core.Clinker;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class ArmorTier {
    public static ArmorMaterial create(
            String name,
            int bootsDefense, int legsDefense, int chestDefense, int headDefense, int bodyDefense,
            int enchantmentValue, float toughness, float knockbackResistance,
            Supplier<Ingredient> repairIngredient,
            Holder<SoundEvent> equipSound
    ) {
        EnumMap<ArmorItem.Type, Integer> map = new EnumMap<>(ArmorItem.Type.class);
        map.put(ArmorItem.Type.BOOTS, bootsDefense);
        map.put(ArmorItem.Type.LEGGINGS, legsDefense);
        map.put(ArmorItem.Type.CHESTPLATE, chestDefense);
        map.put(ArmorItem.Type.HELMET, headDefense);
        map.put(ArmorItem.Type.BODY, bodyDefense);
        return new ArmorMaterial(
                map,
                enchantmentValue,
                equipSound,
                repairIngredient,
                List.of(new ArmorMaterial.Layer(Clinker.resource(name))),
                toughness,
                knockbackResistance
        );
    }
}
