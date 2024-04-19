package birsy.clinker.core.registry;

import birsy.clinker.core.Clinker;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ClinkerDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Clinker.MOD_ID);

    public static final Supplier<AttachmentType<Byte>> CHAIN_LIGHTNING = ATTACHMENT_TYPES.register(
            "chain_lightning", () -> AttachmentType.builder(() -> 0).serialize(Codec.BYTE).build());
}
