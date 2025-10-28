package birsy.clinker.common.alchemy.knowledge.type;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public abstract class AlchemyKnowledgeType<T extends AlchemyKnowledgeData> {
    protected AlchemyKnowledgeType() {}

    public abstract void save(T data, CompoundTag tag, HolderLookup.Provider registries);
    @Nullable
    public abstract T load(CompoundTag tag, HolderLookup.Provider registries);

    public abstract T merge(T... datas);
}
