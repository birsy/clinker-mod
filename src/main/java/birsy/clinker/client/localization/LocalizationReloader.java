package birsy.clinker.client.localization;

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

import static birsy.clinker.client.localization.LocalizationAuthority.get;

public class LocalizationReloader extends SimpleJsonResourceReloadListener {
    private static final Gson GSPOT = new GsonBuilder().create();

    public LocalizationReloader() {
        super(GSPOT, "lang");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceLocationJsonElementMap, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        for(ResourceLocation id : resourceLocationJsonElementMap.keySet()) {
            String[] strings = id.getPath().split("/");
            JsonObject jobj = resourceLocationJsonElementMap.get(id).getAsJsonObject();
            if(strings.length > 2) {
                String name = id.getPath().replaceFirst(strings[0] + "/", "").split("\\.")[0];
                if(jobj.has("fated")) {
                    name = name.replace(strings[strings.length-1].split("\\.")[0], jobj.getAsJsonPrimitive("fated").getAsString());
                }
                ResourceLocation rope_id = ResourceLocation.fromNamespaceAndPath(id.getNamespace(), name);

                if(jobj.has("text_single")) {
                    get().putLongString(strings[0], rope_id.withSuffix(".default"), jobj.getAsJsonPrimitive("text_single").getAsString());
                } else {
                    if(jobj.has("text")) {
                        for(Map.Entry<String, JsonElement> elem : jobj.getAsJsonObject("text").asMap().entrySet()) {
                            get().putLongString(strings[0], rope_id.withSuffix("." + elem.getKey()), elem.getValue().getAsString());
                        }
                    }
                }
                // debug get().longStrings.forEach((s, resourceLocationStringHashMap) -> System.out.println(s + resourceLocationStringHashMap.toString()));
            }
        }
    }

    @Override
    public @NotNull String getName() {
        return "loc_authority";
    }
}
