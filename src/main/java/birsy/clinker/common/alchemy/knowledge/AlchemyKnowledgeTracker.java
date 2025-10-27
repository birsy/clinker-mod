package birsy.clinker.common.alchemy.knowledge;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// basically a per-player, per-world save file for clinker.
// books contents are constructed based off the current Alchemical Knowledge
public class AlchemyKnowledgeTracker extends SavedData {
    public final Map<UUID, AlchemyKnowledge> knowledgeByPlayer;

    public AlchemyKnowledgeTracker() {
        this.knowledgeByPlayer = new HashMap<>();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        int entryIndex = 0;
        CompoundTag entries = new CompoundTag(knowledgeByPlayer.size());
        for (Map.Entry<UUID, AlchemyKnowledge> entry : knowledgeByPlayer.entrySet()) {
            UUID playerUUID = entry.getKey();
            AlchemyKnowledge knowledge = entry.getValue();

            CompoundTag entryTag = new CompoundTag(2);
            entryTag.putUUID("UUID", playerUUID);
            entryTag.put("Knowledge", knowledge.save(new CompoundTag(), registries));

            entries.put("Entry" + entryIndex++, entryTag);
        }

        tag.put("Entries", entries);
        tag.putInt("EntryCount", entryIndex);

        return tag;
    }

    public static AlchemyKnowledgeTracker load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        AlchemyKnowledgeTracker tracker = new AlchemyKnowledgeTracker();
        int entryCount = tag.getInt("EntryCount");
        CompoundTag entries = tag.getCompound("Entries");

        for (int i = 0; i < entryCount; i++) {
            CompoundTag entryTag = entries.getCompound("Entry" + i);

            UUID playerUUID = entryTag.getUUID("UUID");
            AlchemyKnowledge knowledge = AlchemyKnowledge
                    .load(tracker, entryTag.getCompound("Knowledge"), lookupProvider);

            tracker.knowledgeByPlayer.put(playerUUID, knowledge);
        }
        return tracker;
    }
}
