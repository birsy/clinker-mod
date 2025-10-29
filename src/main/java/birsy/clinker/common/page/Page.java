package birsy.clinker.common.page;

import birsy.clinker.common.page.elements.ImagePageElement;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerDynamicRegistries;
import birsy.clinker.core.registry.ClinkerRegistries;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import foundry.veil.api.client.color.Color;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Page {
    public static final Map<Page, ResourceKey<Page>> KEY_LOOKUP = new HashMap<>();

    public static final PageLayout FALLBACK_LAYOUT = new PageLayout(List.of(
       new ImagePageElement(Clinker.resource("textures/page/test.png"), List.of(0F, 0F, 1F, 1F), Color.WHITE, new PageElementTransform(0, 0, 256, 256, 0, 0))
    ));
    public static final Page BLANK_PAGE = new Page("page.clinker.title.blank", FALLBACK_LAYOUT, List.of());
    public static final int PAGE_WIDTH = 300, PAGE_HEIGHT = 400;

    public static final Codec<Page> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.STRING.fieldOf("title_translation_key")
                    .forGetter(page -> page.titleTranslationKey),
            PageLayout.CODEC.fieldOf("default_layout")
                    .forGetter(page -> page.defaultLayout),
            LayoutLanguageOverrideEntry.CODEC.listOf().fieldOf("layout_language_overrides").orElse(List.of())
                    .forGetter(Page::makeLanguageLayoutEntryList)
    ).apply(instance, Page::new));
    public static final StreamCodec<RegistryFriendlyByteBuf, Page> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, page -> page.titleTranslationKey,
            PageLayout.STREAM_CODEC, page -> page.defaultLayout,
            LayoutLanguageOverrideEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), Page::makeLanguageLayoutEntryList,
            Page::new
    );

    public final String titleTranslationKey;
    public final PageLayout defaultLayout;
    public final ImmutableMap<String, PageLayout> layoutByLanguage;

    private Page(String titleTranslationKey, PageLayout defaultLayout, List<LayoutLanguageOverrideEntry> layoutOverrideEntries) {
        this.titleTranslationKey = titleTranslationKey;
        this.defaultLayout = defaultLayout;
        ImmutableMap.Builder<String, PageLayout> builder = ImmutableMap.builder();
        for (LayoutLanguageOverrideEntry layoutOverride : layoutOverrideEntries) {
            for (String localization : layoutOverride.localizations) builder.put(localization, layoutOverride.layout);
        }
        this.layoutByLanguage = builder.buildKeepingLast();
    }

    public Page(String title, PageLayout defaultLayout, Map<String, PageLayout> layoutByLanguage) {
        this.titleTranslationKey = title;
        this.defaultLayout = defaultLayout;
        this.layoutByLanguage = ImmutableMap.copyOf(layoutByLanguage);
    }

    public PageLayout getLayout(String languageId) {
        return layoutByLanguage.getOrDefault(languageId, defaultLayout);
    }

    public ResourceKey<Page> getResourceKey() {
        if (KEY_LOOKUP.isEmpty()) Clinker.LOGGER.error("Page - ResourceKey lookup has not yet been initialized!");
        if (KEY_LOOKUP.get(this) == null) throw new RuntimeException("No ResourceKey loaded for page " + this);
        return KEY_LOOKUP.get(this);
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

    public record PageLayout(ImmutableList<PageElement> elements) {
        public static final Codec<PageLayout> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                PageElement.CODEC.listOf().fieldOf("elements").forGetter(page -> page.elements)
        ).apply(instance, PageLayout::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, PageLayout> STREAM_CODEC = StreamCodec.composite(
                PageElement.STREAM_CODEC.apply(ByteBufCodecs.list()), PageLayout::elements,
                PageLayout::new);

        public PageLayout(List<PageElement> elements) {
            this(ImmutableList.sortedCopyOf(Comparator.comparingInt(element -> element.transform.renderOrder()), elements));
        }
    }

    private record LayoutLanguageOverrideEntry(List<String> localizations, PageLayout layout) {
        private static final Codec<LayoutLanguageOverrideEntry> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                Codec.STRING.listOf().fieldOf("languages").forGetter(entry -> entry.localizations),
                PageLayout.CODEC.fieldOf("layout").forGetter(page -> page.layout)
        ).apply(instance, LayoutLanguageOverrideEntry::new));
        public static final StreamCodec<RegistryFriendlyByteBuf, LayoutLanguageOverrideEntry> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list()), LayoutLanguageOverrideEntry::localizations,
                PageLayout.STREAM_CODEC, LayoutLanguageOverrideEntry::layout,
                LayoutLanguageOverrideEntry::new);
    }
}
