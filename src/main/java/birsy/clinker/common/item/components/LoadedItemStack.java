package birsy.clinker.common.item.components;

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
                    ItemStack.OPTIONAL_CODEC.fieldOf("Stack").forGetter(LoadedItemStack::stack),
                    Codec.INT.fieldOf("LastCount").forGetter(LoadedItemStack::lastCount)
            ).apply(instance, LoadedItemStack::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, LoadedItemStack> STREAM_CODEC = StreamCodec.composite(
            ItemStack.OPTIONAL_STREAM_CODEC, LoadedItemStack::stack,
            ByteBufCodecs.INT, LoadedItemStack::lastCount,
            LoadedItemStack::new
    );

    public boolean isEmpty() {
        return this.stack().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof LoadedItemStack other && lastCount == other.lastCount && ItemStack.matches(stack, other.stack);
    }
    @Override
    public int hashCode() {
        return 31 * ItemStack.hashItemAndComponents(stack) + lastCount;
    }
}
