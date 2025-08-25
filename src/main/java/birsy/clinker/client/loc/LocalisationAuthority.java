package birsy.clinker.client.loc;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class LocalisationAuthority {
    private static final LocalisationAuthority INSTANCE = new LocalisationAuthority();

    public static LocalisationAuthority get() {
        return INSTANCE;
    }

    public HashMap<ResourceLocation, HashMap<String, String>> longStrings = new HashMap<>();

    public LocalisationAuthority() {

    }
}
