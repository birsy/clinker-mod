package birsy.clinker.common.world.ordnance.modifiers;

import birsy.clinker.common.world.ordnance.OrdnanceModifier;
import birsy.clinker.common.world.ordnance.OrdnanceModifierSet;
import birsy.clinker.common.world.ordnance.OrdnanceModifierType;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public abstract class SimpleOrdnanceModifier<T extends SimpleOrdnanceModifier<T>> implements OrdnanceModifier<T> {
    protected final OrdnanceModifierType<T> type;
    protected final String translationKey;
    protected final Style textStyle;

    public SimpleOrdnanceModifier(OrdnanceModifierType<T> type, Style textStyle) {
        this(type, "ordnance_modifier." + ClinkerRegistries.ORDNANCE_MODIFIER_TYPE_REGISTRY.getKeyOrNull(type).toLanguageKey(), textStyle);
    }

    public SimpleOrdnanceModifier(OrdnanceModifierType<T> type, String translationKey, Style textStyle) {
        this.type = type;
        this.translationKey = translationKey;
        this.textStyle = textStyle;
    }

    @Override
    public void tooltip(@Nullable OrdnanceModifierSet set, Consumer<Component> tooltipAdder) {
        tooltipAdder.accept(
                Component.translatable(translationKey)
                         .withStyle(textStyle)
        );
    }

    @Override public boolean canMerge(OrdnanceModifier modifier) { return false; }
    @Override public T merge(OrdnanceModifier modifier) { return null; }
    @Override public OrdnanceModifierType<?> type() { return type; }
}
