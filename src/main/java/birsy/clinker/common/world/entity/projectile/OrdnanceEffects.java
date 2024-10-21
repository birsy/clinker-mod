package birsy.clinker.common.world.entity.projectile;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;

public record OrdnanceEffects(DetonationType detonationType, TouchType touchType, Potion potion, boolean electrified, boolean phosphorTrail, int maxFuseTime) {
    enum DetonationType {
        NORMAL, DUD, FLECHETTE, OIL
    }
    enum TouchType {
        NORMAL, DETONATE, STICK, BOUNCE
    }

    private static final DetonationType[] detonationTypeByOrdinal = DetonationType.values();
    private static final TouchType[] touchTypeByOrdinal = TouchType.values();
    public void serialize(CompoundTag tag) {
        tag.putInt("DetonationType", this.detonationType().ordinal());
        tag.putInt("TouchType", this.touchType().ordinal());
        tag.putString("Potion", BuiltInRegistries.POTION.getKey(this.potion).toString());
        tag.putBoolean("Electrified", this.electrified());
        tag.putInt("MaxFuseTime", this.maxFuseTime());
    }
    public static OrdnanceEffects deserialize(CompoundTag tag) {
        return new OrdnanceEffects(
                detonationTypeByOrdinal[tag.getInt("DetonationType")],
                touchTypeByOrdinal[tag.getInt("TouchType")],
                PotionUtils.getPotion(tag),
                tag.getBoolean("Electrified"),
                tag.getInt("MaxFuseTime")
        );
    }
}
