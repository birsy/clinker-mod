package birsy.clinker.common.page;

import birsy.clinker.core.Clinker;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Page {
    public static final Codec<Page> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PageLayout.CODEC.fieldOf("default_layout")
                    .forGetter(page -> page.defaultLayout),
            LayoutLanguageOverrideEntry.CODEC.listOf().fieldOf("layout_language_overrides").orElse(List.of())
                    .forGetter(Page::makeLanguageLayoutEntryList)
    ).apply(instance, Page::new));
    public static final Codec<Page> NETWORK_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            PageLayout.CODEC.fieldOf("default_layout")
                    .forGetter(page -> page.defaultLayout),
            Codec.unboundedMap(Codec.STRING, PageLayout.CODEC).fieldOf("layout_language_overrides")
                    .forGetter(page -> page.layoutByLanguage)
    ).apply(instance, Page::new));

    public final PageLayout defaultLayout;
    public final ImmutableMap<String, PageLayout> layoutByLanguage;

    private Page(PageLayout defaultLayout, List<LayoutLanguageOverrideEntry> layoutOverrideEntries) {
        this.defaultLayout = defaultLayout;
        ImmutableMap.Builder<String, PageLayout> builder = ImmutableMap.builder();
        for (LayoutLanguageOverrideEntry layoutOverride : layoutOverrideEntries) {
            for (String localization : layoutOverride.localizations)
                builder.put(localization, layoutOverride.layout);
        }
        this.layoutByLanguage = builder.buildKeepingLast();

        Clinker.LOGGER.info("PAGE LAYOUT: {}", this.defaultLayout);
    }

    public Page(PageLayout defaultLayout, Map<String, PageLayout> layoutByLanguage) {
        this.defaultLayout = defaultLayout;
        this.layoutByLanguage = ImmutableMap.copyOf(layoutByLanguage);
    }

    public PageLayout getLayout(String languageId) {
        return layoutByLanguage.getOrDefault(languageId, defaultLayout);
    }

    private List<LayoutLanguageOverrideEntry> makeLanguageLayoutEntryList() {
        List<LayoutLanguageOverrideEntry> list = new ArrayList<>();
        Map<PageLayout, LayoutLanguageOverrideEntry> layouts = new HashMap<>();
        for (Map.Entry<String, PageLayout> entry : this.layoutByLanguage.entrySet()) {
            PageLayout layout = entry.getValue();
            String localizationName = entry.getKey();

            if (!layouts.containsKey(layout)) {
                LayoutLanguageOverrideEntry layoutOverrideEntry =
                        new LayoutLanguageOverrideEntry(new ArrayList<>(), layout);
                layouts.put(layout, layoutOverrideEntry);
                list.add(layoutOverrideEntry);
            }
            layouts.get(layout).localizations().add(localizationName);
        }

        return list;
    }

    public record PageLayout(int width, int height, ImmutableList<PageElement> elements) {
        public static final Codec<PageLayout> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.INT.fieldOf("width").forGetter(page -> page.width),
                Codec.INT.fieldOf("height").forGetter(page -> page.height),
                PageElement.CODEC.listOf().fieldOf("elements").forGetter(page -> page.elements)
        ).apply(instance, PageLayout::new));

        public PageLayout(int width, int height, List<PageElement> elements) {
            this(width, height, ImmutableList.sortedCopyOf(Comparator.comparingInt(element -> element.transform.renderOrder()), elements));
        }
    }

    private record LayoutLanguageOverrideEntry(List<String> localizations, PageLayout layout) {
        private static final Codec<LayoutLanguageOverrideEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.listOf().fieldOf("languages").forGetter(entry -> entry.localizations),
                PageLayout.CODEC.fieldOf("layout").forGetter(page -> page.layout)
        ).apply(instance, LayoutLanguageOverrideEntry::new));
    }
}
