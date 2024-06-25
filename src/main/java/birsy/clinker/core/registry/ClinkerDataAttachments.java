package birsy.clinker.core.registry;

import birsy.clinker.core.Clinker;
import com.mojang.serialization.Codec;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ClinkerDataAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, Clinker.MOD_ID);

    // CHAIN LIGHTNING
    public static final Supplier<AttachmentType<Integer>> CHAIN_LIGHTNING_SPREAD_DELAY = ATTACHMENT_TYPES.register(
            "chain_lightning_spread_delay", () -> AttachmentType.builder(() -> (0)).serialize(Codec.INT).build());
    public static final Supplier<AttachmentType<Integer>> CHAIN_LIGHTNING_RECEIVE_COOLDOWN = ATTACHMENT_TYPES.register(
            "chain_lightning_recieve_cooldown", () -> AttachmentType.builder(() -> (0)).serialize(Codec.INT).build());

    // LOOT REROLLING
    public static final Supplier<AttachmentType<Boolean>> FILLING_LOOT_TABLE = ATTACHMENT_TYPES.register(
            "initialized_rerolls", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build());
    public static final Supplier<AttachmentType<Boolean>> CAN_REROLL_LOOT = ATTACHMENT_TYPES.register(
            "can_reroll_loot", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).build());
    public static final Supplier<AttachmentType<String>> REROLL_LOOT_LOCATION = ATTACHMENT_TYPES.register(
            "reroll_loot_location", () -> AttachmentType.builder(() -> "").serialize(Codec.STRING).build());
}
