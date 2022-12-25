package birsy.clinker.common.entity.gnomad;

import net.minecraft.Util;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public enum GnomadArmor {
    HELMET_EMPTY(0, "helmet_empty", GnomadArmorLocation.HELMET, 0, 0, 1.0F),
    HELMET_HAT(1, "helmet_hat", GnomadArmorLocation.HELMET, 0, 0, 1.0F),
    HELMET_BRIM(2, "helmet_brim", GnomadArmorLocation.HELMET, 0, 0, 1.0F),
    HELMET_SOLDIER(3, "helmet_soldier", GnomadArmorLocation.HELMET, 0, 0, 1.0F),
    HELMET_SOLDIER_VISOR(4, "helmet_solider_visor", GnomadArmorLocation.HELMET, 0, 0, 1.0F),
    HELMET_PLATE(5, "helmet_plate", GnomadArmorLocation.HELMET, 0, 0, 1.0F),
    HELMET_CRUSADER(6, "helmet_crusader", GnomadArmorLocation.HELMET, 0, 0, 1.0F),
    HELMET_SKULL(7, "helmet_skull", GnomadArmorLocation.HELMET, 0, 0, 1.0F),

    CHESTPLATE_EMPTY(0, "chestplate_empty", GnomadArmorLocation.CHEST, 0, 0, 1.0F),
    CHESTPLATE_GLADIATOR(1, "chestplate_gladiator", GnomadArmorLocation.CHEST, 0, 0, 1.0F),

    LEFT_SHOULDER_EMPTY(0, "left_shoulder_empty", GnomadArmorLocation.LEFT_SHOULDER, 0, 0, 1.0F),
    LEFT_SHOULDER_PAULDRON(1, "left_shoulder_pauldron", GnomadArmorLocation.LEFT_SHOULDER, 0, 0, 1.0F),

    LEFT_GLOVE_EMPTY(0, "left_glove_empty", GnomadArmorLocation.LEFT_GLOVE, 0, 0, 1.0F),
    LEFT_GLOVE_SHORT(1, "left_glove_short", GnomadArmorLocation.LEFT_GLOVE, 0, 0, 1.0F),
    LEFT_GLOVE_LONG(2, "left_glove_long", GnomadArmorLocation.LEFT_GLOVE, 0, 0, 1.0F),

    LEFT_FOOT_EMPTY(0, "left_foot_empty", GnomadArmorLocation.LEFT_FOOT, 0, 0, 1.0F),
    LEFT_FOOT_BOOT(1, "left_foot_boot", GnomadArmorLocation.LEFT_FOOT, 0, 0, 1.0F),
    LEFT_FOOT_KNEEPADS(2, "left_foot_kneepads", GnomadArmorLocation.LEFT_FOOT, 0, 0, 1.0F),

    RIGHT_SHOULDER_EMPTY(0, "right_shoulder_empty", GnomadArmorLocation.RIGHT_SHOULDER, 0, 0, 1.0F),
    RIGHT_SHOULDER_PAULDRON(1, "right_shoulder_pauldron", GnomadArmorLocation.RIGHT_SHOULDER, 0, 0, 1.0F),

    RIGHT_GLOVE_EMPTY(0, "right_glove_empty", GnomadArmorLocation.RIGHT_GLOVE, 0, 0, 1.0F),
    RIGHT_GLOVE_SHORT(1, "right_glove_short", GnomadArmorLocation.RIGHT_GLOVE, 0, 0, 1.0F),
    RIGHT_GLOVE_LONG(2, "right_glove_long", GnomadArmorLocation.RIGHT_GLOVE, 0, 0, 1.0F),

    RIGHT_FOOT_EMPTY(0, "right_foot_empty", GnomadArmorLocation.RIGHT_FOOT, 0, 0, 1.0F),
    RIGHT_FOOT_BOOT(1, "right_foot_boot", GnomadArmorLocation.RIGHT_FOOT, 0, 0, 1.0F),
    RIGHT_FOOT_KNEEPADS(2, "right_foot_kneepads", GnomadArmorLocation.RIGHT_FOOT, 0, 0, 1.0F);

    private static final ArrayList<GnomadArmor>[] valuesByLocation = Util.make(new ArrayList[GnomadArmorLocation.values().length], (vbl -> {
        for (int i = 0; i < vbl.length; i++) {
            vbl[i] = new ArrayList<GnomadArmor>();
        }
        for (GnomadArmor armor : GnomadArmor.values()) {
            vbl[armor.armorLocation.index].add(armor);
        }
    }));
    private static final HashMap<Integer, GnomadArmor>[] idHashByLocation = Util.make(new HashMap[GnomadArmorLocation.values().length], (ihbl -> {
        for (int i = 0; i < ihbl.length; i++) {
            ihbl[i] = new HashMap<Integer, GnomadArmor>();
        }
        for (GnomadArmor armor : GnomadArmor.values()) {
            ihbl[armor.armorLocation.index].put(armor.id, armor);
        }
    }));
    final int id;
    final String resourceName;
    final GnomadArmorLocation armorLocation;

    final AttributeModifier armorModifier;
    final AttributeModifier speedModifier;
    final AttributeModifier knockbackModifier;

    GnomadArmor(int id, String resourceName, GnomadArmorLocation armorLocation, float armor, float speed, float knockback) {
        this.id = id;
        this.resourceName = resourceName;
        this.armorLocation = armorLocation;

        this.armorModifier = new AttributeModifier(UUID.randomUUID(), armorLocation.name + " Armor Bonus", armor,  AttributeModifier.Operation.ADDITION);
        this.speedModifier = new AttributeModifier(UUID.randomUUID(), armorLocation.name + " Speed Negation", speed, AttributeModifier.Operation.ADDITION);
        this.knockbackModifier = new AttributeModifier(UUID.randomUUID(), armorLocation.name + " Knockback Negation", knockback, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    public String getResourceName() {
        return this.resourceName;
    }

    public static GnomadArmor getRandomArmorPiece(GnomadArmorLocation armorLocation, RandomSource random) {
        int index = random.nextInt(valuesByLocation[armorLocation.index].size());
        return valuesByLocation[armorLocation.index].get(index);
    }

    public static GnomadArmor getArmorFromID(GnomadArmorLocation armorLocation, int id) {
        return idHashByLocation[armorLocation.index].get(id);
    }

    public static GnomadArmor getArmorFromID(int locationIndex, int id) {
        return idHashByLocation[locationIndex].get(id);
    }

    public enum GnomadArmorLocation {
        HELMET(0, "Helmet"),
        CHEST(1, "Chest"),
        LEFT_SHOULDER(2, "Left Shoulder"),
        LEFT_GLOVE(3, "Left Glove"),
        LEFT_FOOT(4, "Left Foot"),
        RIGHT_SHOULDER(5, "Right Shoulder"),
        RIGHT_GLOVE(6, "Right Glove"),
        RIGHT_FOOT(7, "Right Foot");

        private static final HashMap<Integer, GnomadArmorLocation> locationFromID = Util.make(new HashMap(), (lfi -> {
            for (GnomadArmorLocation location : GnomadArmorLocation.values()) {
                lfi.put(location.index, location);
            }
        }));

        final int index;
        final String name;

        GnomadArmorLocation(int index, String name) {
            this.index = index;
            this.name = name;
        }

        public static GnomadArmorLocation locationFromIndex(int index) {
            return locationFromID.get(index);
        }
    }
}
