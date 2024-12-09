package birsy.clinker.common.world.item.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record LoadedItemStack(ItemStack stack) {
    public static final LoadedItemStack EMPTY = new LoadedItemStack(ItemStack.EMPTY);
    public static final Codec<LoadedItemStack> CODEC = ItemStack.CODEC.xmap(LoadedItemStack::new, LoadedItemStack::stack);
    public static final StreamCodec<RegistryFriendlyByteBuf, LoadedItemStack> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, LoadedItemStack::stack,
            LoadedItemStack::new
    );

    public boolean isEmpty() {
        return this.stack().isEmpty();
    }
}
