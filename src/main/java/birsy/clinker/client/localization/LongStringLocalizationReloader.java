package birsy.clinker.client.localization;

import birsy.clinker.core.Clinker;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class LongStringLocalizationReloader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().create();

    public LongStringLocalizationReloader() {
        super(GSON, "lang");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        LongStringLocalizationAuthority.get().clear();
        for (ResourceLocation id : resourceLocationJsonElementMap.keySet()) {
            try {
                String[] strings = id.getPath().split("/");
                JsonObject json = resourceLocationJsonElementMap.get(id).getAsJsonObject();

                if (strings.length > 2) {
                    String languageId = strings[0];
                    String translationKey = id.getPath().replaceFirst(languageId + "/", "");

                    // can override the identifier by specifying a "key"
                    if (json.has("key"))
                        translationKey = json.getAsJsonPrimitive("key").getAsString();

                    ResourceLocation keyLocation = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), translationKey);

                    // "text" can either be a single string, an array, or a map
                    if (json.has("text")) {
                        JsonElement textElement = json.get("text");

                        if (textElement.isJsonPrimitive()) {
                            // single string
                            LongStringLocalizationAuthority.get().putLongString(languageId, keyLocation, textElement.getAsJsonPrimitive().getAsString());
                        } else if (textElement.isJsonObject()) {
                            // map
                            for (Map.Entry<String, JsonElement> elementEntry : textElement.getAsJsonObject().asMap().entrySet()) {
                                LongStringLocalizationAuthority.get().putLongString(languageId, keyLocation.withSuffix("." + elementEntry.getKey()), elementEntry.getValue().getAsString());
                            }
                        } else if (textElement.isJsonArray()) {
                            // array
                            int index = 0;
                            for (JsonElement element : textElement.getAsJsonArray()) {
                                LongStringLocalizationAuthority.get().putLongString(languageId, keyLocation.withSuffix("." + index), element.getAsString());
                                index++;
                            }
                        }
                    } else {
                        throw new IllegalStateException("Long string must include valid 'text' component!");
                    }
                }
            } catch (Exception e) {
                Clinker.LOGGER.error("Failed to load long string {}", id, e);
            }
        }
    }

    @Override
    public @NotNull String getName() {
        return "loc_authority";
    }
}
