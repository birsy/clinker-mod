package birsy.clinker.common.world.entity.projectile;

import birsy.clinker.core.Clinker;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.item.alchemy.PotionContents;

import java.util.Optional;


public record OrdnanceEffects(
        DetonationType detonationType,
        TouchType touchType,
        PotionContents potion,
        boolean electrified,
        boolean trail,
        int color,
        int maxFuseTime) {

    public static final OrdnanceEffects DEFAULT_EFFECT_PARAMS = new OrdnanceEffects (
            DetonationType.NORMAL,
            TouchType.NORMAL,
            PotionContents.EMPTY,
            false,
            false,
            0xFFFF80,
            120
    );


    private static final DetonationType[] detonationTypeByOrdinal = DetonationType.values();
    private static final TouchType[] touchTypeByOrdinal = TouchType.values();
    public CompoundTag serialize(CompoundTag tag, RegistryAccess registryAccess) {
        tag.putInt("DetonationType", this.detonationType().ordinal());
        tag.putInt("TouchType", this.touchType().ordinal());
        if (!this.potion.equals(PotionContents.EMPTY)) {
            Tag potionTag = PotionContents.CODEC.encodeStart(registryAccess.createSerializationContext(NbtOps.INSTANCE), this.potion).getOrThrow();
            tag.put("Potion", potionTag);
        }
        tag.putBoolean("Electrified", this.electrified());
        tag.putBoolean("Trail", this.trail());
        tag.putInt("Color", this.color());
        tag.putInt("MaxFuseTime", this.maxFuseTime());
        return tag;
    }
    public static OrdnanceEffects deserialize(CompoundTag tag, RegistryAccess registryAccess) {
        PotionContents contents = PotionContents.EMPTY;
        if (tag.contains("potion_contents")) {
            Optional<PotionContents> potionContents = PotionContents.CODEC
                    .parse(registryAccess.createSerializationContext(NbtOps.INSTANCE), tag.get("Potion"))
                    .resultOrPartial(malformed -> Clinker.LOGGER.warn("Failed to parse ordnance potions: '{}'", malformed));
            contents = potionContents.orElse(contents);
        }
        return new OrdnanceEffects(
                detonationTypeByOrdinal[tag.getInt("DetonationType")],
                touchTypeByOrdinal[tag.getInt("TouchType")],
                contents,
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
