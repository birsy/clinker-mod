package birsy.clinker.core.registry;

import birsy.clinker.common.alchemy.knowledge.type.AlchemyKnowledgeType;
import birsy.clinker.common.alchemy.knowledge.type.PageKnowledge;
import birsy.clinker.core.Clinker;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ClinkerAlchemyKnowledgeTypes {
    public static final DeferredRegister<AlchemyKnowledgeType<?>> ALCHEMY_DATA_TYPES =
            DeferredRegister.create(ClinkerRegistries.ALCHEMY_KNOWLEDGE_TYPE_REGISTRY, Clinker.MOD_ID);

    public static final Supplier<PageKnowledge.PageKnowledgeDataType> PAGE_KNOWLEDGE =
            ALCHEMY_DATA_TYPES.register("page_knowledge", PageKnowledge.PageKnowledgeDataType::new);
}
