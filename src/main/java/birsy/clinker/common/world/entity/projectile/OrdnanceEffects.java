package birsy.clinker.common.world.entity.projectile;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;

public record OrdnanceEffects(
        DetonationType detonationType,
        TouchType touchType,
        Potion potion,
        boolean electrified,
        boolean trail,
        int color,
        int maxFuseTime) {

    public static final OrdnanceEffects DEFAULT_EFFECT_PARAMS = new OrdnanceEffects (
            OrdnanceEffects.DetonationType.NORMAL,
            OrdnanceEffects.TouchType.NORMAL,
            Potions.EMPTY,
            false,
            false,
            0xFFFF80,
            120
    );


    private static final DetonationType[] detonationTypeByOrdinal = DetonationType.values();
    private static final TouchType[] touchTypeByOrdinal = TouchType.values();
    public CompoundTag serialize(CompoundTag tag) {
        tag.putInt("DetonationType", this.detonationType().ordinal());
        tag.putInt("TouchType", this.touchType().ordinal());
        tag.putString("Potion", BuiltInRegistries.POTION.getKey(this.potion).toString());
        tag.putBoolean("Electrified", this.electrified());
        tag.putBoolean("Trail", this.trail());
        tag.putInt("Color", this.color());
        tag.putInt("MaxFuseTime", this.maxFuseTime());
        return tag;
    }
    public static OrdnanceEffects deserialize(CompoundTag tag) {
        return new OrdnanceEffects(
                detonationTypeByOrdinal[tag.getInt("DetonationType")],
                touchTypeByOrdinal[tag.getInt("TouchType")],
                PotionUtils.getPotion(tag),
                tag.getBoolean("Electrified"),
                tag.getBoolean("Trail"),
                tag.getInt("Color"),
                tag.getInt("MaxFuseTime")
        );
    }

    enum DetonationType {
        NORMAL, DUD, FLECHETTE, OIL
    }
    enum TouchType {
        NORMAL, DETONATE, STICK, BOUNCE
    }
}
