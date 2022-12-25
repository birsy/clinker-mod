package birsy.clinker.core.util;

import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.FontManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClinkerFontManager {
    private FontManager fontManager;
    public Font alchemicalFont;
    public Font smallFont;

    public ClinkerFontManager(Minecraft mc) {
        fontManager = mc.fontManager;
        this.alchemicalFont =  new Font((resourceLocation) -> fontManager.fontSets.getOrDefault(new ResourceLocation(Clinker.MOD_ID, "font/alchemical.json"), fontManager.missingFontSet), false);
        this.smallFont =  new Font((resourceLocation) -> fontManager.fontSets.getOrDefault(new ResourceLocation(Clinker.MOD_ID, "font/small.json"), fontManager.missingFontSet), false);

    }
}
