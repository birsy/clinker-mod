package birsy.clinker.common.alchemy.knowledge;

import birsy.clinker.common.alchemy.knowledge.type.AlchemyKnowledgeData;
import birsy.clinker.common.alchemy.knowledge.type.AlchemyKnowledgeType;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class AlchemyKnowledgeTracker extends SavedData {
    private final Map<UUID, AlchemyKnowledge> knowledgeByPlayer;
    private final Map<UUID, KnowledgeContract> contracts;
    private final Map<UUID, UUID> contractByPlayer;

    public AlchemyKnowledgeTracker() {
        this.knowledgeByPlayer = new HashMap<>();

        this.contracts = new HashMap<>();
        this.contractByPlayer = new HashMap<>();
    }

    public AlchemyKnowledge getOrCreateAlchemyKnowledge(UUID playerUUID) {
        return knowledgeByPlayer.computeIfAbsent(playerUUID, (key) -> new AlchemyKnowledge(this, playerUUID));
    }

    // returns the UUID of the contract
    public UUID createContract(UUID drafterUUID, KnowledgeContract.ContractType type) {
        KnowledgeContract contract = new KnowledgeContract(this, UUID.randomUUID(), drafterUUID, new HashSet<>(), type);
        this.contracts.put(contract.uuid(), contract);
        return contract.uuid();
    }

    public boolean addPlayerToContract(UUID contractUUID, UUID playerUUID) {
        if (!this.contracts.containsKey(contractUUID)) return false;
        KnowledgeContract contract = this.contracts.get(contractUUID);

        if (contract.type() == KnowledgeContract.ContractType.SHARE_ALL && !contract.signatories().isEmpty()) {
            UUID signatoryUUID = contract.signatories().stream().findAny().get();
            AlchemyKnowledge sharedKnowledge = getOrCreateAlchemyKnowledge(signatoryUUID);
            AlchemyKnowledge newPlayerKnowledge = getOrCreateAlchemyKnowledge(playerUUID);

            // merge knowledge for each data type!
            for (AlchemyKnowledgeType dataType : sharedKnowledge.dataMap.keySet()) {
                // get the current contract holders data and the new players data
                AlchemyKnowledgeData sharedData = sharedKnowledge.getData(dataType);
                if (sharedData == null) continue;
                AlchemyKnowledgeData newPlayerData = newPlayerKnowledge.getData(dataType);
                if (newPlayerData == null) continue;

                // merge data
                AlchemyKnowledgeData mergedData = dataType.merge(sharedData, newPlayerData);

                // set data for the type
                for (UUID signatory : contract.signatories())
                    getOrCreateAlchemyKnowledge(signatory).setData(dataType, mergedData);
                newPlayerKnowledge.setData(dataType, mergedData);
            }
        }

        this.contracts.get(contractUUID).signatories().add(playerUUID);
        this.contractByPlayer.put(playerUUID, contractUUID);

        return true;
    }

    public boolean removePlayerFromContract(UUID contractUUID, UUID playerUUID) {
        if (!this.contracts.containsKey(contractUUID)) return false;
        KnowledgeContract contract = this.contracts.get(contractUUID);
        this.contractByPlayer.remove(playerUUID, contractUUID);
        boolean removed = contract.signatories().remove(playerUUID);
        if (contract.signatories().isEmpty()) this.contracts.remove(contractUUID);
        return removed;
    }

    public boolean breakContract(UUID contractUUID) {
        if (!this.contracts.containsKey(contractUUID)) return false;
        KnowledgeContract contract = this.contracts.get(contractUUID);
        for (UUID signatory : contract.signatories())
            this.contractByPlayer.remove(signatory, contractUUID);
        this.contracts.remove(contractUUID);
        return true;
    }

    <D extends AlchemyKnowledgeData> void syncContractData(UUID playerUUID, AlchemyKnowledgeType<D> type, D data) {
        if (!this.contractByPlayer.containsKey(playerUUID)) return;
        UUID contractUUID = this.contractByPlayer.get(playerUUID);
        if (contractUUID == null) return;

        if (!this.contracts.containsKey(contractUUID)) return;
        KnowledgeContract contract = this.contracts.get(contractUUID);
        if (contract == null) return;

        // set the data for all signatories
        for (UUID signatory : contract.signatories()) {
            getOrCreateAlchemyKnowledge(signatory).setData(type, data);
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        // save knowledge
        CompoundTag knowledgeTag = new CompoundTag(knowledgeByPlayer.size());
        int knowledgeEntryIndex = 0;
        for (Map.Entry<UUID, AlchemyKnowledge> entry : knowledgeByPlayer.entrySet()) {
            AlchemyKnowledge knowledge = entry.getValue();
            knowledgeTag.put("Entry" + knowledgeEntryIndex++, knowledge.save(new CompoundTag(), registries));
        }
        tag.put("Knowledge", knowledgeTag);

        // save contracts
        CompoundTag contractsTag = new CompoundTag(2);
        int contractsEntryIndex = 0;
        for (Map.Entry<UUID, KnowledgeContract> entry : contracts.entrySet()) {
            KnowledgeContract contract = entry.getValue();
            contractsTag.put("Entry" + contractsEntryIndex++, contract.save(new CompoundTag(), registries));
        }
        tag.put("Contracts", contractsTag);

        return tag;
    }

    public static AlchemyKnowledgeTracker load(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        AlchemyKnowledgeTracker tracker = new AlchemyKnowledgeTracker();

        // load knowledge
        CompoundTag knowledgeTag = tag.getCompound("Knowledge");
        for (String key : knowledgeTag.getAllKeys()) {
            CompoundTag entryTag = knowledgeTag.getCompound(key);
            AlchemyKnowledge knowledge = AlchemyKnowledge.load(tracker, entryTag, lookupProvider);
            tracker.knowledgeByPlayer.put(knowledge.owner, knowledge);
        }

        // load contracts
        CompoundTag contractsTag = tag.getCompound("Contracts");
        for (String key : contractsTag.getAllKeys()) {
            CompoundTag entryTag = contractsTag.getCompound(key);
            KnowledgeContract contract = KnowledgeContract.load(tracker, entryTag, lookupProvider);
            tracker.contracts.put(contract.uuid(), contract);
            for (UUID signatory : contract.signatories())
                tracker.contractByPlayer.put(signatory, contract.uuid());
        }

        return tracker;
    }

    private static final SavedData.Factory<AlchemyKnowledgeTracker> FACTORY =
            new Factory<>(AlchemyKnowledgeTracker::new, AlchemyKnowledgeTracker::load);
    // retrieves the knowledge tracker from the provided server or server level
    public static AlchemyKnowledgeTracker get(MinecraftServer server) { return server.overworld().getDataStorage().get(FACTORY, "knowledge_tracker"); }
    public static AlchemyKnowledgeTracker get(ServerLevel level) { return get(level.getServer()); }
}
