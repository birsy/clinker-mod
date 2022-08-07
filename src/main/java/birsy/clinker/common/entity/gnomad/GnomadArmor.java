package birsy.clinker.common.entity.gnomad;

import net.minecraft.Util;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public enum GnomadArmor {
    EMPTY_HEAD(0, "Empty Head", GnomadArmorLocation.HELMET, 0, 0, 1.0F),
    EMPTY_CHEST(0, "Empty Chest", GnomadArmorLocation.CHEST, 0, 0, 1.0F),
    EMPTY_LEFT_SHOULDER(0, "Empty Left Shoulder", GnomadArmorLocation.LEFT_SHOULDER, 0, 0, 1.0F),
    EMPTY_LEFT_GLOVE(0, "Empty Left Glove", GnomadArmorLocation.LEFT_GLOVE, 0, 0, 1.0F),
    EMPTY_LEFT_FOOT(0, "Empty Left Foot", GnomadArmorLocation.LEFT_FOOT, 0, 0, 1.0F),
    EMPTY_RIGHT_SHOULDER(0, "Empty Right Shoulder", GnomadArmorLocation.RIGHT_SHOULDER, 0, 0, 1.0F),
    EMPTY_RIGHT_GLOVE(0, "Empty Right Glove", GnomadArmorLocation.RIGHT_GLOVE, 0, 0, 1.0F),
    EMPTY_RIGHT_FOOT(0, "Empty Right Foot", GnomadArmorLocation.RIGHT_FOOT, 0, 0, 1.0F);

    private static final ArrayList<GnomadArmor>[] valuesByLocation = Util.make(new ArrayList[GnomadArmorLocation.values().length], (vbl -> {for (GnomadArmor armor : GnomadArmor.values()) {vbl[armor.armorLocation.index].add(armor);}}));
    private static final HashMap<Integer, GnomadArmor>[] idHashByLocation = Util.make(new HashMap[GnomadArmorLocation.values().length], (ihbl -> {
        for (GnomadArmor armor : GnomadArmor.values()) {
            ihbl[armor.armorLocation.index].put(armor.id, armor);
        }
    }));

    final int id;
    String name;
    GnomadArmorLocation armorLocation;

    AttributeModifier armorModifier;
    AttributeModifier speedModifier;
    AttributeModifier knockbackModifier;

    GnomadArmor(int id, String name, GnomadArmorLocation armorLocation, float armor, float speed, float knockback) {
        this.id = id;
        this.name = name;
        this.armorLocation = armorLocation;

        this.armorModifier = new AttributeModifier(UUID.randomUUID(), name + " Armor Bonus", armor,  AttributeModifier.Operation.ADDITION);
        this.speedModifier = new AttributeModifier(UUID.randomUUID(), name + " Speed Negation", speed, AttributeModifier.Operation.ADDITION);
        this.knockbackModifier = new AttributeModifier(UUID.randomUUID(), name + " Knockback Negation", knockback, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    public static GnomadArmor getRandomArmorPiece(GnomadArmorLocation armorLocation, RandomSource random) {
        int index = random.nextInt(valuesByLocation[armorLocation.index].size());
        return valuesByLocation[armorLocation.index].get(index);
    }

    public static GnomadArmor getArmorFromID(GnomadArmorLocation armorLocation, int id) {
        return idHashByLocation[armorLocation.index].get(id);
    }

    public enum GnomadArmorLocation {
        HELMET(0), CHEST(1), LEFT_SHOULDER(2), LEFT_GLOVE(3), LEFT_FOOT(4), RIGHT_SHOULDER(5), RIGHT_GLOVE(6), RIGHT_FOOT(7);
        int index;
        GnomadArmorLocation(int index) {
            this.index = index;
        }
    }
}
