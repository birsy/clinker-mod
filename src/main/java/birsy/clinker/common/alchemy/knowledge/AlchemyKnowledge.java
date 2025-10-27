package birsy.clinker.common.alchemy.knowledge;

import birsy.clinker.common.page.Page;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerDynamicRegistries;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// basically a per-player, per-world save file for clinker.
// books contents are constructed based off the current Alchemical Knowledge
public class AlchemyKnowledge {
    private final AlchemyKnowledgeTracker owner;
    private final List<ResourceKey<Page>> unlockedPageKeys = new ArrayList<>();
    private final List<Page> unlockedPages = new ArrayList<>();

    public AlchemyKnowledge(AlchemyKnowledgeTracker owner) {
        this.owner = owner;
    }

    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        CompoundTag unlockedPagesTag = new CompoundTag(unlockedPages.size());
        for (int i = 0; i < unlockedPages.size(); i++) {
            ResourceKey<Page> unlockedPage = unlockedPageKeys.get(i);
            unlockedPagesTag.putString("" + i, unlockedPage.location().toString());
        }
        tag.put("UnlockedPages", unlockedPagesTag);

        return tag;
    }

    public static AlchemyKnowledge load(AlchemyKnowledgeTracker owner, CompoundTag tag, HolderLookup.Provider lookupProvider) {
        AlchemyKnowledge knowledge = new AlchemyKnowledge(owner);

        // load pages
        HolderLookup.RegistryLookup<Page> pageRegistryLookup = lookupProvider.lookupOrThrow(ClinkerDynamicRegistries.PAGE_REGISTRY_KEY);
        CompoundTag unlockedPagesTag = tag.getCompound("UnlockedPages");
        for (int i = 0; i < unlockedPagesTag.size(); i++) {
            ResourceKey<Page> unlockedPageKey = ResourceKey.create(ClinkerDynamicRegistries.PAGE_REGISTRY_KEY,
                    ResourceLocation.parse(unlockedPagesTag.getString("" + i)));
            Optional<Holder.Reference<Page>> unlockedPage = pageRegistryLookup.get(unlockedPageKey);
            if (unlockedPage.isPresent()) {
                knowledge.unlockedPages.add(unlockedPage.get().value());
                knowledge.unlockedPageKeys.add(unlockedPageKey);
            } else {
                Clinker.LOGGER.warn("Invalid key {} found in AlchemyKnowledge!", unlockedPageKey);
            }
        }


        return new AlchemyKnowledge(owner);
    }

    void unlockPage(Page page, HolderLookup.Provider lookupProvider) {
        this.unlockedPages.add(page);
        for (Holder.Reference<Page> pageReference : lookupProvider.lookupOrThrow(ClinkerDynamicRegistries.PAGE_REGISTRY_KEY).listElements().toList()) {
            if (pageReference.value() == page) {
                this.unlockedPageKeys.add(pageReference.key());
                return;
            }
        }
    }
}
