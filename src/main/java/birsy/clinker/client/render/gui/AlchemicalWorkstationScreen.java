package birsy.clinker.client.render.gui;

import birsy.clinker.common.world.alchemy.workstation.AlchemicalWorkstation;
import birsy.clinker.core.Clinker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class AlchemicalWorkstationScreen {
    public static AlchemicalWorkstationScreen CURRENT_SCREEN;
    public static boolean ACTIVE = false;

    public BlockPos targetedBlock;         // block the camera is aiming at
    public BlockPos[] nextTargetedBlock;   // block the camera will be aiming at when a direction is pressed.
    public Vec3 pCurrentCamPos;  // position the camera was in last tick
    public Vec3 currentCamPos;   // position the camera is in
    public Vec3 targetCamPos;    // camera lerps to this position every tick
    public final AlchemicalWorkstation workstation;

    public AlchemicalWorkstationScreen(AlchemicalWorkstation workstation) {
        this.workstation = workstation;
        this.nextTargetedBlock = new BlockPos[6];
        this.currentCamPos = new Vec3(0, 0, 0);
        this.pCurrentCamPos = new Vec3(0, 0, 0);
        this.targetCamPos = new Vec3(0, 0, 0);
    }

    public void tick() {
        this.pCurrentCamPos = this.currentCamPos;
        this.currentCamPos = this.currentCamPos.lerp(this.targetCamPos, 0.05);
    }

    public Vec3 getCamPos(float partialTick) {
        return pCurrentCamPos.lerp(currentCamPos, partialTick);
    }

    @SubscribeEvent
    public static void onRenderView(ViewportEvent event) {
        if (ACTIVE && CURRENT_SCREEN != null) {
            event.getCamera().setPosition(CURRENT_SCREEN.getCamPos((float) event.getPartialTick()));
        }
    }

    @SubscribeEvent
    public static void onTick(TickEvent.ClientTickEvent event) {
        if (ACTIVE && CURRENT_SCREEN != null) {
            CURRENT_SCREEN.tick();
        }
    }

}
