package birsy.clinker.client.localization;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class LongStringLocalizationAuthority {
    private static final LongStringLocalizationAuthority INSTANCE = new LongStringLocalizationAuthority();

    public static LongStringLocalizationAuthority get() {
        return INSTANCE;
    }

    private final HashMap<String, HashMap<ResourceLocation, LabeledString>> longStrings = new HashMap<>(128);

    void clear() {
        this.longStrings.clear();
    }

    public void putLongString(String loc, ResourceLocation id, String str) {
        LabeledString parsedString = LabeledString.parse(str);
        if(longStrings.containsKey(loc)) {
            longStrings.get(loc).put(id, parsedString);
        } else {
            longStrings.put(loc, new HashMap<>());
            longStrings.get(loc).put(id, parsedString);
        }
    }

    public LabeledString getLabelledLongString(ResourceLocation id) {
        String currentLanguage = Minecraft.getInstance().getLanguageManager().getSelected();
        LabeledString outputString;
        // default to english if the language isn't supported at all.
        HashMap<ResourceLocation, LabeledString> localizedLongStrings = longStrings.get(longStrings.containsKey(currentLanguage) ? currentLanguage : "en_us");
        if (localizedLongStrings.containsKey(id)) {
            // if the current language has the id, return that
            outputString = localizedLongStrings.get(id);
        } else {
            // otherwise, default to english again. if no such string exists in english, just use the resource as a placeholder.
            outputString = longStrings.get("en_us").computeIfAbsent(id, (key) -> LabeledString.parse(id.toString()));
        }

        return outputString;
    }

    public String getLongString(ResourceLocation id) {
        return this.getLabelledLongString(id).text();
    }

    public static ResourceLocation validatePath(ResourceLocation rid) {
        if(!rid.getPath().contains(".")) {
            rid = rid.withSuffix(".default");
        }
        return rid;
    }

    public LongStringLocalizationAuthority() {

    }
}
