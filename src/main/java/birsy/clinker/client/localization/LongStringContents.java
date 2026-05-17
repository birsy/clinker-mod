package birsy.clinker.client.localization;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.Optional;

public class LongStringContents implements ComponentContents {
    public static final MapCodec<LongStringContents> CODEC = RecordCodecBuilder.mapCodec(
            (instance) -> instance.group(
                        ResourceLocation.CODEC.fieldOf("long_string").forGetter((longString) -> longString.id)
                    ).apply(instance, LongStringContents::new)
    );
    public static final ComponentContents.Type<LongStringContents> TYPE = new ComponentContents.Type<>(CODEC, "translatable");

    private String currentLocalization;
    private String resolvedContents = "";
    private final ResourceLocation id;

    public LongStringContents(ResourceLocation id) {
        id = LongStringLocalizationAuthority.validatePath(id);
        this.currentLocalization = Minecraft.getInstance().getLanguageManager().getSelected();
        this.id = id;
        this.resolvedContents = LongStringLocalizationAuthority.get().getLongString(id);
    }

    public static MutableComponent create(ResourceLocation id) {
        return MutableComponent.create(new LongStringContents(id));
    }

    @Override
    public <T> Optional<T> visit(FormattedText.StyledContentConsumer<T> styledContentConsumer, Style style) {
        String render = render();
        return styledContentConsumer.accept(style, render);
    }

    @Override
    public <T> Optional<T> visit(FormattedText.ContentConsumer<T> contentConsumer) {
        String render = render();
        return contentConsumer.accept(render);
    }

    private String render() {
        if(!Objects.equals(this.currentLocalization, Minecraft.getInstance().getLanguageManager().getSelected())) {
            this.currentLocalization = Minecraft.getInstance().getLanguageManager().getSelected();
            this.resolvedContents = LongStringLocalizationAuthority.get().getLongString(id);
        }
        return this.resolvedContents;
    }

    @Override
    public Type<?> type() {
        return TYPE;
    }

    public String toString() {
        return "loc_long_string{"+ this.currentLocalization + ", [" + this.id.toString() + "], " + this.resolvedContents + "}";
    }
}
