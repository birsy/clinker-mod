package birsy.clinker.client.loc;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ComponentContents;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LongStringContents implements ComponentContents {

    public static final MapCodec<LongStringContents> CODEC = RecordCodecBuilder.mapCodec((p_337512_) -> p_337512_.group(Codec.STRING.fieldOf("long_string").forGetter((p_304759_) -> p_304759_.id.toString())).apply(p_337512_, LongStringContents::create));
    public static final ComponentContents.Type<LongStringContents> TYPE = new ComponentContents.Type<>(CODEC, "translatable");

    private String cloc;
    private String render = "";
    private final ResourceLocation id;

    public LongStringContents(ResourceLocation id) {
        id = LocalisationAuthority.validatePath(id);
        this.cloc = Minecraft.getInstance().getLanguageManager().getSelected();
        this.id = id;
        this.render = LocalisationAuthority.getLoc().getLongString(id);
    }

    public static MutableComponent create(ResourceLocation id) {
        return MutableComponent.create(new LongStringContents(id));
    }

    private static LongStringContents create(String id) {
        return new LongStringContents(ResourceLocation.parse(id));
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
        if(!Objects.equals(this.cloc, Minecraft.getInstance().getLanguageManager().getSelected())) {
            this.cloc = Minecraft.getInstance().getLanguageManager().getSelected();
            this.render = LocalisationAuthority.getLoc().getLongString(id);
        }
        return this.render;
    }

    @Override
    public Type<?> type() {
        return TYPE;
    }

    public String toString() {
        return "loc_long_string{"+ this.cloc + ", [" + this.id.toString() + "], " + this.render + "}";
    }
}
