package birsy.clinker.common.alchemy.knowledge.type;

import birsy.clinker.common.page.Page;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerDynamicRegistries;
import birsy.clinker.core.registry.ClinkerRegistries;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PageKnowledge {
    public static class PageKnowledgeDataType extends AlchemyKnowledgeType<PageKnowledgeData> {
        @Override
        public void save(PageKnowledgeData data, CompoundTag tag, HolderLookup.Provider registries) {
            tag.putInt("Size", data.knownPages.size());
            CompoundTag entries = new CompoundTag(data.knownPages.size());
            ImmutableSet<Page> knownPages = data.knownPages;
            int i = 0;
            for (Page knownPage : knownPages) {
                entries.putString("Entry" + i++, knownPage.getResourceKey().location().toString());
            }
            tag.put("Entries", entries);
        }

        @Override
        @Nullable
        public PageKnowledgeData load(CompoundTag tag, HolderLookup.Provider registries) {
            ImmutableSet.Builder<Page> builder = ImmutableSet.builder();

            HolderLookup.RegistryLookup<Page> pageRegistry = registries.lookupOrThrow(ClinkerDynamicRegistries.PAGE_REGISTRY_KEY);
            int entryCount = tag.getInt("Size");
            CompoundTag entries = tag.getCompound("Entries");
            for (int i = 0; i < entryCount; i++) {
                String entry = entries.getString("Entry" + i);
                ResourceLocation pageLoc = ResourceLocation.tryParse(entry);
                if (pageLoc == null) {
                    Clinker.LOGGER.warn("Unable to parse page ResourceLocation from string {}", entry);
                    continue;
                }
                ResourceKey<Page> pageKey = ResourceKey.create(ClinkerDynamicRegistries.PAGE_REGISTRY_KEY, pageLoc);
                Optional<Holder.Reference<Page>> pageRef = pageRegistry.get(pageKey);
                if (pageRef.isEmpty()) {
                    Clinker.LOGGER.warn("Page not associated with key {}", pageKey);
                    continue;
                }

                builder.add(pageRef.get().value());
            }

            return new PageKnowledgeData(builder.build());
        }

        @Override
        public PageKnowledgeData merge(PageKnowledgeData... datas) {
            ImmutableSet.Builder<Page> builder = ImmutableSet.builder();
            for (PageKnowledgeData data : datas)
                builder.addAll(data.knownPages);
            return new PageKnowledgeData(builder.build());
        }
    }

    public record PageKnowledgeData(ImmutableSet<Page> knownPages) implements AlchemyKnowledgeData {
        @Override
        public AlchemyKnowledgeType<? extends AlchemyKnowledgeData> type() {
            return null;
        }

        public PageKnowledgeData append(Page... pages) {
            if (pages.length == 0) return this;

            ImmutableSet.Builder<Page> builder = ImmutableSet.builder();
            builder.addAll(knownPages);
            builder.add(pages);

            return new PageKnowledgeData(builder.build());
        }
    }
}
