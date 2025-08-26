package birsy.clinker.client.loc;

import net.minecraft.client.Minecraft;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Objects;

public class LocalisationAuthority {
    private static final LocalisationAuthority INSTANCE = new LocalisationAuthority();

    public static LocalisationAuthority getLoc() {
        return INSTANCE;
    }

    public HashMap<String, HashMap<ResourceLocation, String>> longStrings = new HashMap<>(128);

    public void putLongString(String loc, ResourceLocation id, String str) {
        if(longStrings.containsKey(loc)) {
            longStrings.get(loc).put(id, str);
        } else {
            longStrings.put(loc, new HashMap<>());
            longStrings.get(loc).put(id, str);
        }
    }

    public String getLongString(ResourceLocation id) {
        String sel = Minecraft.getInstance().getLanguageManager().getSelected();
        String out = longStrings.get(longStrings.containsKey(sel) ? sel : "en_us").getOrDefault(id, "nil");
        if(Objects.equals(out, "nil")) {
            out = longStrings.get("en_us").getOrDefault(id, "?LongStringName?");
        }
        return out;
    }

    public static ResourceLocation validatePath(ResourceLocation rid) {
        if(!rid.getPath().contains(".")) {
            rid = rid.withSuffix(".default");
        }
        return rid;
    }

    public LocalisationAuthority() {

    }
}
