package birsy.clinker.common.world.item.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record FuseTimer(int tickCount, boolean lit) {
    public static final FuseTimer EMPTY = new FuseTimer(0, false);
    public static final Codec<FuseTimer> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.INT.fieldOf("tick_count").forGetter(FuseTimer::tickCount),
                    Codec.BOOL.fieldOf("lit").forGetter(FuseTimer::lit)
            ).apply(instance, FuseTimer::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, FuseTimer> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, FuseTimer::tickCount,
            ByteBufCodecs.BOOL, FuseTimer::lit,
            FuseTimer::new
    );
}
