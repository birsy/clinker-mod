package birsy.clinker.common.world.item.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

public record LoadedItemStack(ItemStack stack, int lastCount) {
    public static final LoadedItemStack EMPTY = new LoadedItemStack(ItemStack.EMPTY, 0);
    public static final Codec<LoadedItemStack> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ItemStack.CODEC.fieldOf("Stack").forGetter(LoadedItemStack::stack),
                    Codec.INT.fieldOf("LastCount").forGetter(LoadedItemStack::lastCount)
            ).apply(instance, LoadedItemStack::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, LoadedItemStack> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, LoadedItemStack::stack,
            ByteBufCodecs.INT, LoadedItemStack::lastCount,
            LoadedItemStack::new
    );

    public boolean isEmpty() {
        return this.stack().isEmpty();
    }
}
