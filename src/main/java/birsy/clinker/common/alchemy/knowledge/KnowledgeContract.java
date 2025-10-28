package birsy.clinker.common.alchemy.knowledge;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;

import java.util.*;

record KnowledgeContract(AlchemyKnowledgeTracker tracker, UUID uuid, UUID drafter, Set<UUID> signatories, ContractTerms type) {
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        tag.putUUID("UUID", uuid);
        tag.putUUID("Drafter", drafter);
        CompoundTag signatoriesTag = new CompoundTag(signatories.size());
        int index = 0;
        for (UUID signatory : signatories) signatoriesTag.putUUID("" + index++, signatory);
        tag.put("Signatories", signatoriesTag);
        tag.putString("Terms", type == ContractTerms.SHARE_ALL ? "ShareAll" : "ShareNew");
        return tag;
    }

    public static KnowledgeContract load(AlchemyKnowledgeTracker tracker, CompoundTag tag, HolderLookup.Provider registries) {
        UUID uuid = tag.getUUID("UUID");
        UUID drafter = tag.getUUID("Drafter");
        ContractTerms type = tag.getString("Terms").equals("ShareAll") ? ContractTerms.SHARE_ALL : ContractTerms.SHARE_NEW;

        CompoundTag signatoriesTag = tag.getCompound("Signatories");
        Set<UUID> signatories = new HashSet<>(signatoriesTag.size());
        for (String key : signatoriesTag.getAllKeys()) signatories.add(signatoriesTag.getUUID(key));

        return new KnowledgeContract(tracker, uuid, drafter, signatories, type);
    }

    public enum ContractTerms {
        SHARE_ALL, SHARE_NEW;
    }
}
