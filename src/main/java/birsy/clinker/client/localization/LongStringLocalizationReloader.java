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

import java.util.Iterator;
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

                        if (textElement.isJsonObject()) {
                            // for map objects
                            for (Map.Entry<String, JsonElement> elementEntry : textElement.getAsJsonObject().asMap().entrySet()) {
                                String key = elementEntry.getKey();
                                JsonElement element = elementEntry.getValue();
                                String parsedText = parseJsonElementToText(element);
                                LongStringLocalizationAuthority.get().putLongString(languageId, keyLocation.withSuffix("." + key), parsedText);
                            }
                        } else {
                            // for primitive strings or arrays
                            String parsedText = parseJsonElementToText(textElement);
                            LongStringLocalizationAuthority.get().putLongString(languageId, keyLocation, parsedText);
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

    private String parseJsonElementToText(JsonElement textElement) {
        if (textElement.isJsonPrimitive()) {
            return textElement.getAsJsonPrimitive().getAsString();
        } else if (textElement.isJsonArray()) {
            StringBuilder string = new StringBuilder();
            for (Iterator<JsonElement> iterator = textElement.getAsJsonArray().iterator(); iterator.hasNext(); ) {
                JsonElement element = iterator.next();
                string.append(element.getAsString());
                if (iterator.hasNext()) string.append("\n");
            }

            return string.toString();
        }

        throw new IllegalStateException("Unable to parse text from component " + textElement);
    }

    @Override
    public @NotNull String getName() {
        return "loc_authority";
    }
}
