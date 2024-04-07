package birsy.clinker.common.world.level.interactable;

import birsy.clinker.common.world.level.interactable.manager.ClientInteractableManager;
import birsy.clinker.core.Clinker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import org.joml.Vector3d;

import java.util.Collection;
import java.util.Optional;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientInteractionHandler extends InteractionHandler {
    public Optional<Interactable> seenInteractable = Optional.empty();

    public ClientInteractionHandler(ClientInteractableManager manager) {
        super(manager);
    }

    @Override
    public void tick() {
        super.tick();
    }

    /**
     * Called every frame.
     */
    public void update() {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;

        double blockReach = player.getBlockReach();
        double entityReach = player.getEntityReach();

        ClientLevel level = (ClientLevel) this.manager.level;
        Vec3 direction = player.getLookAngle();
        Vec3 fromPos = player.getEyePosition(minecraft.getPartialTick());

        Vec3 toPosBlock = direction.scale(blockReach).add(fromPos);
        Vec3 toPosEntity = direction.scale(entityReach).add(fromPos);

        ClipContext blockClipContext = new ClipContext(fromPos, toPosBlock, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
        BlockHitResult bResult = level.clip(blockClipContext);
        double blockDistance = bResult == null ? Float.POSITIVE_INFINITY : bResult.getLocation().distanceTo(fromPos);

        EntityHitResult eResult = ProjectileUtil.getEntityHitResult(player, fromPos, toPosEntity, new AABB(fromPos, toPosEntity), (entity) -> !entity.isSpectator() && entity.isPickable(), entityReach);
        double entityDistance = eResult == null ? Float.POSITIVE_INFINITY : eResult.getLocation().distanceTo(fromPos);

        Collection<Interactable> interactablesInChunks = this.manager.getInteractablesInBounds(new AABB(fromPos, toPosBlock));
        Interactable closestInteractable = null;
        double closestDistance = Float.POSITIVE_INFINITY;

        Vector3d fPos = new Vector3d(fromPos.x, fromPos.y, fromPos.z);
        Vector3d tPos = new Vector3d(toPosBlock.x, toPosBlock.y, toPosBlock.z);
        for (Interactable interactable : interactablesInChunks) {
            // if the interactable has been marked for removal, ignore it.
            if (interactable.removed) continue;
            Optional<Vector3d> cast = interactable.raycast(fPos, tPos);
            if (cast.isEmpty()) continue;
            double distance = cast.get().distance(fPos);
            if (distance > blockDistance) continue;
            if (distance > entityDistance) continue;
            if (distance < closestDistance) {
                closestInteractable = interactable;
                closestDistance = distance;
            }
        }
        seenInteractable = Optional.ofNullable(closestInteractable);
    }

    @SubscribeEvent
    public static void onRenderLevel(RenderLevelStageEvent event) {
//        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_LEVEL) return;
//        if (Minecraft.getInstance().isPaused()) return;
//        ClientInteractableManager manager = (ClientInteractableManager) InteractableLevelAttachment.getInteractableManagerForLevel(Minecraft.getInstance().level);
//        if (manager != null) {
//            ClientInteractionHandler handler = (ClientInteractionHandler) manager.interactionHandler;
//            handler.update();
//        }
    }
}
