package birsy.clinker.mixin.client;

import birsy.clinker.client.gui.debug.BiomeLayerDebugViewScreen;
import birsy.clinker.client.gui.debug.PageEditorScreen;
import birsy.clinker.core.Clinker;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.neoforged.fml.loading.FMLLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    protected TitleScreenMixin(Component title) { super(title); }

    @Inject(method = "init", at = @At("RETURN"))
    private void clinker$addTitleScreenButtons(CallbackInfo ci) {
        // debug!!
        if (SharedConstants.IS_RUNNING_IN_IDE) {
            Clinker.LOGGER.info("added debug widgets to title screen");
            this.addRenderableWidget(
                    Button.builder(Component.literal("clinker's epic biome layer debug view"), button -> this.minecraft.setScreen(new BiomeLayerDebugViewScreen()))
                            .bounds(this.width - 202, this.height - 42, 200, 20)
                            .build()
            );

            this.addRenderableWidget(
                    Button.builder(Component.literal("clinker's epic page editor"), button -> this.minecraft.setScreen(new PageEditorScreen()))
                            .bounds(this.width - 202, this.height - 84, 200, 20)
                            .build()
            );
        }
    }
}
