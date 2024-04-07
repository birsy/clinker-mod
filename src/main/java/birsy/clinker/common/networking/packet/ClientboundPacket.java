package birsy.clinker.common.networking.packet;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.core.Clinker;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.network.FriendlyByteBuf;

import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.PlayPayloadContext;

public abstract class ClientboundPacket implements CustomPacketPayload {
    public ClientboundPacket() {}
    public ClientboundPacket(FriendlyByteBuf buffer) {}

    public boolean handle(PlayPayloadContext context) {
        context.workHandler().submitAsync(() -> this.run(context)).exceptionally(e -> {
            context.packetHandler().disconnect(Component.translatable("my_mod.networking.failed", e.getMessage()));
            return null;
        });
        return true;
    }

    public abstract void run(PlayPayloadContext context);

    public ResourceLocation id() {
        return ClinkerPacketHandler.createId(this.getClass());
    }
}
