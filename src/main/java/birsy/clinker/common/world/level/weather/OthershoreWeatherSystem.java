package birsy.clinker.common.world.level.weather;

import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerRegistries;
import birsy.clinker.core.registry.worldgen.ClinkerWorld;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;

public class OthershoreWeatherSystem {
    public final Level level;
    public final boolean isClientSide;
    public final RandomSource random;
    OthershoreWeather currentWeather;

    int weatherTickCount = 0;
    boolean changedWeatherThisTick = false;

    public OthershoreWeatherSystem(Level level) {
        this.level = level;
        this.isClientSide = level.isClientSide();
        this.random = level.random.fork();
    }

    public void tick() {
        changedWeatherThisTick = false;
        if (currentWeather != null) currentWeather.tick(this);
        weatherTickCount++;
    }

    public void setWeather(OthershoreWeather.Type weatherType) {
        setWeather(weatherType.factory().apply(this));
    }

    public void setWeather(OthershoreWeather weather) {
        if (currentWeather != null) currentWeather.end(this);
        currentWeather = weather;
        currentWeather.begin(this);
        changedWeatherThisTick = true;
        weatherTickCount = 0;

        Clinker.LOGGER.info("othershore weather changed to {}", ClinkerRegistries.OTHERSHORE_WEATHER_TYPE_REGISTRY.getKey(currentWeather.type()));
    }

    public OthershoreWeather getWeather() {
        return currentWeather;
    }

    public int getWeatherTicks() {
        return weatherTickCount;
    }

    // serialization stuff
    public void serialize(CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("CurrentWeather", OthershoreWeather.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), currentWeather).getOrThrow());
        tag.putInt("WeatherTickCount", weatherTickCount);
    }
    public void deserialize(CompoundTag tag, HolderLookup.Provider registries) {
        currentWeather = OthershoreWeather.CODEC.decode(registries.createSerializationContext(NbtOps.INSTANCE), tag.get("CurrentWeather")).getOrThrow().getFirst();
        weatherTickCount = tag.getInt("WeatherTickCount");
    }

    // misc
    public static boolean hasOthershoreWeather(Level level) {
        if (level == null) return false;
        return level.dimension().equals(ClinkerWorld.OTHERSHORE);
    }
}
