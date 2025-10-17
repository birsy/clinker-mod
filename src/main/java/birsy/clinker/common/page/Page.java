package birsy.clinker.common.page;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.Comparator;
import java.util.List;

public final class Page {
    public static final Codec<Page> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.INT.fieldOf("width").forGetter(page -> page.width),
            Codec.INT.fieldOf("height").forGetter(page -> page.height),
            PageElement.CODEC.listOf().fieldOf("elements").forGetter(page -> page.elements)
    ).apply(instance, Page::new));

    private final int width, height;
    private final List<PageElement> elements;

    public Page(int width, int height, List<PageElement> elements) {
        this.width = width;
        this.height = height;
        this.elements = elements;
        this.elements.sort(Comparator.comparingInt(element -> element.renderOrder));
    }
}
