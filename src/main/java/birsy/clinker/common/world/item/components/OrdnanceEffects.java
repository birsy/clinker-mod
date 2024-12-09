package birsy.clinker.common.world.item.components;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.codecs.ExtraByteBufCodecs;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.component.TooltipProvider;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.function.IntFunction;

public record OrdnanceEffects(
        DetonationType detonationType,
        TouchType touchType,
        PotionContents potion,
        boolean electrified,
        boolean trail,
        int color,
        int fuseTime) implements TooltipProvider {

    public static final OrdnanceEffects DEFAULT = new OrdnanceEffects (
            DetonationType.NORMAL,
            TouchType.NORMAL,
            PotionContents.EMPTY,
            false,
            false,
            0xFFFF80,
            120
    );

    public static final Codec<OrdnanceEffects> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                    StringRepresentable.fromValues(DetonationType::values).fieldOf("detonation_type").forGetter(OrdnanceEffects::detonationType),
                    StringRepresentable.fromValues(TouchType::values).fieldOf("touch_type").forGetter(OrdnanceEffects::touchType),
                    PotionContents.CODEC.fieldOf("potion").forGetter(OrdnanceEffects::potion),
                    Codec.BOOL.fieldOf("electrified").forGetter(OrdnanceEffects::electrified),
                    Codec.BOOL.fieldOf("trail").forGetter(OrdnanceEffects::trail),
                    Codec.INT.fieldOf("color").forGetter(OrdnanceEffects::color),
                    Codec.INT.fieldOf("fuse_time").forGetter(OrdnanceEffects::fuseTime)
            ).apply(instance, OrdnanceEffects::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, OrdnanceEffects> STREAM_CODEC = ExtraByteBufCodecs.composite(
            ByteBufCodecs.idMapper(DetonationType.BY_ID, DetonationType::ordinal), OrdnanceEffects::detonationType,
            ByteBufCodecs.idMapper(TouchType.BY_ID, TouchType::ordinal), OrdnanceEffects::touchType,
            PotionContents.STREAM_CODEC, OrdnanceEffects::potion,
            ByteBufCodecs.BOOL, OrdnanceEffects::electrified,
            ByteBufCodecs.BOOL, OrdnanceEffects::trail,
            ByteBufCodecs.INT, OrdnanceEffects::color,
            ByteBufCodecs.INT, OrdnanceEffects::fuseTime,
            OrdnanceEffects::new
    );

    public Tag serialize(RegistryAccess registryAccess) {
        return CODEC.encodeStart(registryAccess.createSerializationContext(NbtOps.INSTANCE), this).getOrThrow();
    }
    public static OrdnanceEffects deserialize(Tag tag, RegistryAccess registryAccess) {
        return CODEC.parse(registryAccess.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow();
    }

    @Override
    public void addToTooltip(Item.TooltipContext context, Consumer<Component> tooltipAdder, TooltipFlag tooltipFlag) {
        this.addAdditionalTooltip(tooltipAdder);
    }

    public void addAdditionalTooltip(Consumer<Component> tooltipAdder) {
        Style style = Style.EMPTY.withFont(Clinker.resource("small")).withColor(ChatFormatting.GRAY);
        if (this.detonationType() != DetonationType.DUD)
            tooltipAdder.accept(Component.translatable("item.clinker.ordnance.fuse_duration", this.fuseTime() / 20.0F).withStyle(style));

        if (this.detonationType() != DetonationType.NORMAL)
            tooltipAdder.accept(Component.translatable("item.clinker.ordnance.detonation_" + this.detonationType().getSerializedName()).withStyle(style));

        if (this.touchType() != TouchType.NORMAL)
            tooltipAdder.accept(Component.translatable("item.clinker.ordnance.touch_" + this.touchType().getSerializedName()).withStyle(style));

        if (this.electrified())
            tooltipAdder.accept(Component.translatable("item.clinker.ordnance.electrified").withStyle(style));

        if (this.trail())
            tooltipAdder.accept(Component.translatable("item.clinker.ordnance.trail").withStyle(style));

        if (this.detonationType() != DetonationType.DUD && !this.trail() && !this.electrified())
            tooltipAdder.accept(Component.translatable("item.color", String.format(Locale.ROOT, "#%06X", this.color)).withColor(this.color).withStyle(Style.EMPTY.withFont(Clinker.resource("small"))));

        if (this.potion().hasEffects()) {
            tooltipAdder.accept(Component.translatable("item.clinker.ordnance.potion").withStyle(style));
            List<Component> potionToolTips = new ArrayList<>();
            this.potion.addPotionTooltip(potionToolTips::add, 1.0F, 20);
            for (Component potionToolTip : potionToolTips) {
                tooltipAdder.accept(Component.literal("   ").append(potionToolTip).withStyle(Style.EMPTY.withFont(Clinker.resource("small"))));
            }
        }
    }



    public enum DetonationType implements StringRepresentable {
        NORMAL("normal"), DUD("dud"), FLECHETTE("flechette"), OIL("oil");
        private static final IntFunction<DetonationType> BY_ID = ByIdMap.continuous(DetonationType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        private final String name;
        DetonationType(String name) {
            this.name = name;
        }
        @Override
        public String getSerializedName() { return this.name; }
    }
    public enum TouchType implements StringRepresentable {
        NORMAL("normal"), DETONATE("detonate"), STICK("stick"), BOUNCE("bounce");
        private static final IntFunction<TouchType> BY_ID = ByIdMap.continuous(TouchType::ordinal, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
        private final String name;
        TouchType(String name) {
            this.name = name;
        }
        @Override
        public String getSerializedName() { return this.name; }
    }
}
