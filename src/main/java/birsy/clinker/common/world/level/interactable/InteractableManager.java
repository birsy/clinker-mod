package birsy.clinker.common.world.level.interactable;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ClientboundInteractableAddPacket;
import birsy.clinker.common.networking.packet.ClientboundInteractableRemovePacket;
import birsy.clinker.common.networking.packet.ClientboundInteractableShapeSyncPacket;
import birsy.clinker.common.networking.packet.ClientboundInteractableTranslationSyncPacket;
import birsy.clinker.common.world.level.interactable.storage.InteractableStorage;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.common.world.physics.rigidbody.colliders.OBBCollisionShape;
import birsy.clinker.common.world.physics.rigidbody.gjkepa.GJKEPA;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class InteractableManager {
    public static Map<ServerLevel, InteractableManager> serverInteractableManagers = new HashMap<>();
    @OnlyIn(Dist.CLIENT)
    public static InteractableManager clientInteractableManager;
    @OnlyIn(Dist.CLIENT)
    public static Optional<Interactable> seenInteractable = Optional.empty();

    /**
     * Adds a serverside interactable.
     * The interactable's functionality exists only on the server, and a dummy is reproduced on the client that sends a packet to the server when interacted with.
     * Collision interactions are handled entirely serverside.
     * @param interactable The interactable to add.
     */
    public static void addServerInteractable(Interactable interactable, ServerLevel level) {
        Vec3 position = interactable.getTransform().getPosition();
        BlockPos blockPos = new BlockPos(position.x(), position.y(), position.z());

        LevelChunk chunk = level.getChunkAt(blockPos);
        //ClinkerPacketHandler.sendToClientsInChunk((chunk), new ClientboundInteractableAddPacket(interactable));
        ClinkerPacketHandler.sendToAllClients(new ClientboundInteractableAddPacket(interactable));

        try {
            serverInteractableManagers.get(level).addInteractable(interactable);
        } catch (NullPointerException e) {
            Clinker.LOGGER.warn("Specified ServerLevel does not exist!");
        }
    }

    /**
     * Adds a clientside interactable.
     * The interactable only exists on the client, and has no effect on the server.
     * @param interactable The interactable to add.
     */
    @OnlyIn(Dist.CLIENT)
    public static void addClientsideInteractable(Interactable interactable) {
        try {
            clientInteractableManager.addInteractable(interactable);
        } catch (NullPointerException e) {
            Clinker.LOGGER.warn("clientInteractableManager not yet initialized!");
        }
    }


    public final InteractableStorage storage;
    public Level level;
    private int ticks;

    public List<ChunkPos> chunksIntersectedWithRay = new ArrayList<>();
    public InteractableManager(Level level) {
        this.storage = new InteractableStorage();
        this.level = level;
        this.ticks = 0;
    }

    private void addInteractable(Interactable interactable) {
        this.storage.addInteractable(interactable);
    }

    public void tick() {
        ticks++;
        if (!level.isClientSide()) {
            serverTick();
        }

        for(Interactable interactable : storage.getAllInteractables()) {
            interactable.tick(level.isClientSide);
            if (interactable.shouldBeRemoved) {
                storage.removeInteractable(interactable.uuid);
                if (!level.isClientSide) {
                    LevelChunk chunk = level.getChunk(interactable.getSectionPosition().chunk().x, interactable.getSectionPosition().chunk().z);
                    ClinkerPacketHandler.sendToClientsInChunk(chunk, new ClientboundInteractableRemovePacket(interactable.uuid));
                }
            } else {
                storage.updateInteractableLocation(interactable);
                List<Entity> entitiesInRange = this.level.getEntities(null, interactable.shape.getBounds());
                for (Entity entity : entitiesInRange) {
                    AABB entityAABB = entity.getBoundingBox();
                    Vec3 center = entityAABB.getCenter();
                    OBBCollisionShape entityBB = new OBBCollisionShape(entityAABB.maxX - center.x(), entityAABB.maxY - center.y(), entityAABB.maxZ - center.z());
                    entityBB.transform.setPosition(center);

                    GJKEPA.Manifold m = GJKEPA.collisionTest(interactable.shape, entityBB, 5);
                    if (m != null) {
                        interactable.run(new InteractionInfo(interactable.uuid, InteractionInfo.Interaction.TOUCH, new InteractionContext(m.contactPointA(), m.contactPointB(), null)), entity, this.level.isClientSide());
                    }
                }
            }
        }
    }

    private void serverTick() {
        // if an interactable has been moved, send that information to the client.
        for (Interactable interactable : storage.getAllInteractables()) {
            BlockPos blockPos = MathUtils.blockPosFromVec3(interactable.getTransform().getPosition());
            LevelChunk chunk = level.getChunkAt(blockPos);

            if (interactable.getTransform() != interactable.previousTransform) {
                ClinkerPacketHandler.sendToClientsInChunk((chunk), new ClientboundInteractableTranslationSyncPacket(interactable));
            }
            if (interactable.shouldUpdateShape) {
                ClinkerPacketHandler.sendToClientsInChunk(chunk, new ClientboundInteractableShapeSyncPacket(interactable));
                interactable.shouldUpdateShape = false;
            }
        }
    }

    public void unloadChunk(ChunkPos chunkPos) {
        storage.removeChunk(chunkPos);
    }

    public void loadChunkToPlayer(ChunkPos chunkPos, ServerPlayer player) {
        for(Interactable interactable : this.storage.getInteractablesInChunk(chunkPos)) {
            ClinkerPacketHandler.sendToClient(player, new ClientboundInteractableAddPacket(interactable));
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (clientInteractableManager != null) {
            clientInteractableManager.storage.clear();
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        clientInteractableManager = new InteractableManager((Level) event.getLevel());
    }

    @SubscribeEvent
    public static void clientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null) return;
        if (mc.level == null) return;
        if (clientInteractableManager == null) return;

        LocalPlayer player = mc.player;
        double reach = player.getReachDistance();
        Level level = InteractableManager.clientInteractableManager.level;

        Vec3 direction = player.getLookAngle();
        Vec3 fromPos = player.getEyePosition(mc.getPartialTick());
        Vec3 toPos = direction.scale(reach).add(fromPos);

        ClipContext cContext = new ClipContext(fromPos, toPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
        BlockHitResult bResult = level.clip(cContext);
        double blockDistance = bResult == null ? Float.POSITIVE_INFINITY : bResult.getLocation().distanceTo(fromPos);
        EntityHitResult eResult = ProjectileUtil.getEntityHitResult(player, fromPos, toPos, new AABB(fromPos, toPos), (entity) -> !entity.isSpectator() && entity.isPickable(), reach);
        double entityDistance = eResult == null ? Float.POSITIVE_INFINITY : eResult.getLocation().distanceTo(fromPos);

        List<Interactable> interactablesInChunks = InteractableManager.clientInteractableManager.storage.getInteractablesInBounds(new AABB(fromPos, toPos));

        Interactable closestInteractable = null;
        double closestDistance = Float.POSITIVE_INFINITY;
        for (Interactable interactable : interactablesInChunks) {
            // if the interactable has been marked for removal, ignore it.
            if (interactable.shouldBeRemoved) continue;

            Optional<Vec3> cast = interactable.shape.raycast(fromPos, toPos);
            if (cast.isEmpty()) continue;

            double distance = cast.get().distanceTo(fromPos);
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
    public static void onInteract(InputEvent.InteractionKeyMappingTriggered event) {
        if (seenInteractable.isPresent()) {
            Interactable interactable = seenInteractable.get();
            Minecraft mc = Minecraft.getInstance();

            LocalPlayer player = mc.player;
            Vec3 fromPos = player.getEyePosition(mc.getPartialTick());
            Vec3 direction = player.getLookAngle();
            Vec3 toPos = direction.scale(player.getReachDistance()).add(fromPos);
            InteractionContext iContext = new InteractionContext(fromPos, toPos, event.getHand());

            Options options = mc.options;
            if (interactable.run(new InteractionInfo(interactable.uuid,
                    event.getKeyMapping() == options.keyPickItem ? InteractionInfo.Interaction.PICK :
                            event.getKeyMapping() == options.keyAttack ? InteractionInfo.Interaction.HIT :
                                    InteractionInfo.Interaction.INTERACT, iContext), player, true)) {
                event.setCanceled(true);
                event.setSwingHand(true);
            }
        }
    }
}
