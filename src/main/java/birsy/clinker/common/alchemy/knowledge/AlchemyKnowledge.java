package birsy.clinker.common.alchemy.knowledge;

import birsy.clinker.common.alchemy.knowledge.type.AlchemyKnowledgeData;
import birsy.clinker.common.alchemy.knowledge.type.AlchemyKnowledgeType;
import birsy.clinker.core.registry.ClinkerRegistries;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.*;

// basically a per-player, per-world save file for clinker.
// books contents are constructed based off the current Alchemical Knowledge
public class AlchemyKnowledge {
    private final AlchemyKnowledgeTracker tracker;
    final UUID owner;
    final Map<AlchemyKnowledgeType<?>, AlchemyKnowledgeData> dataMap;

    public AlchemyKnowledge(AlchemyKnowledgeTracker tracker, UUID owner) {
        this.tracker = tracker;
        this.owner = owner;
        this.dataMap = new HashMap<>();
    }

    // returns an immutable data container
    @Nullable
    public <D extends AlchemyKnowledgeData> D getData(AlchemyKnowledgeType<D> type) {
        return (D) dataMap.getOrDefault(type, null);
    }

    // use this for updating data!
    public <D extends AlchemyKnowledgeData> void setData(AlchemyKnowledgeType<D> type, D data) {
        dataMap.put(type, data);
        this.tracker.syncContractData(this.owner, type, data);
    }

    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putUUID("OwnerUUID", this.owner);
        for (Map.Entry<AlchemyKnowledgeType<?>, AlchemyKnowledgeData> entry : dataMap.entrySet()) {
            AlchemyKnowledgeType type = entry.getKey();
            ResourceLocation dataTypeKey = ClinkerRegistries.ALCHEMY_KNOWLEDGE_TYPE_REGISTRY.getKey(type);
            if (dataTypeKey == null) continue;

            AlchemyKnowledgeData typeData = entry.getValue();
            if (typeData == null) continue;

            CompoundTag dataTag = new CompoundTag();
            type.save(typeData, dataTag, registries);
            tag.put(dataTypeKey.toString(), dataTag);
        }

        return tag;
    }

    public static AlchemyKnowledge load(AlchemyKnowledgeTracker tracker, CompoundTag tag, HolderLookup.Provider registries) {
        AlchemyKnowledge knowledge = new AlchemyKnowledge(tracker, tag.getUUID("OwnerUUID"));

        // load data types
        for (String key : tag.getAllKeys()) {
            ResourceLocation dataTypeKey = ResourceLocation.tryParse(key);
            if (dataTypeKey == null) continue;

            Optional<AlchemyKnowledgeType<?>> dataTypeReference =
                    ClinkerRegistries.ALCHEMY_KNOWLEDGE_TYPE_REGISTRY.getOptional(dataTypeKey);
            if (dataTypeReference.isEmpty()) continue;

            AlchemyKnowledgeType dataType = dataTypeReference.get();
            AlchemyKnowledgeData data = dataType.load(tag, registries);
            if (data == null) continue;

            knowledge.setData(dataType, data);
        }

        return knowledge;
    }
}
