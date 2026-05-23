package birsy.clinker.common.world.item.components;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.level.Level;

public record FuseTimer(long gameTimeAtOpening) {
    public static final Codec<FuseTimer> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.LONG.fieldOf("game_time_at_opening").forGetter(FuseTimer::gameTimeAtOpening)
            ).apply(instance, FuseTimer::new)
    );
    public static final StreamCodec<RegistryFriendlyByteBuf, FuseTimer> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG, FuseTimer::gameTimeAtOpening,
            FuseTimer::new
    );

    public FuseTimer(int ticksSinceOpening, Level level) {
        this(level.getGameTime() - ticksSinceOpening);
    }
    public FuseTimer(Level level) {
        this(level.getGameTime());
    }

    public long ticksSinceOpening(Level level) {
        return level.getGameTime() - this.gameTimeAtOpening();
    }
}
