package birsy.clinker.common.world.ordnance.modifiers;

import birsy.clinker.common.world.ordnance.OrdnanceModifierSet;
import birsy.clinker.common.world.ordnance.OrdnanceModifierType;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.util.FastColor;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class ColoredOrdnanceModifier<T extends ColoredOrdnanceModifier<T>> extends SimpleOrdnanceModifier<T> {
    protected final int color;
    public ColoredOrdnanceModifier(OrdnanceModifierType<T> type, int color, Style textStyle) {
        super(type, textStyle);
        this.color = color;
    }
    public ColoredOrdnanceModifier(OrdnanceModifierType<T> type, int color, String translationKey, Style textStyle) {
        super(type, "ordnance_modifier." + ClinkerRegistries.ORDNANCE_MODIFIER_TYPE_REGISTRY.getKeyOrNull(type).toLanguageKey(), textStyle);
        this.color = color;
    }

    public int color() { return color; }

    @Override
    public void tooltip(@Nullable OrdnanceModifierSet set, Consumer<Component> tooltipAdder) {
        tooltipAdder.accept(
                Component.translatable(this.translationKey)
                         .withStyle(
                                 textStyle.withColor(textStyle.getColor() != null ? FastColor.ARGB32.average(color, textStyle.getColor().getValue()) : color)
                         )
        );
    }
}