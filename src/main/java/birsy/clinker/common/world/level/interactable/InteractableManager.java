package birsy.clinker.common.world.level.interactable;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ClientboundInteractableAddPacket;
import birsy.clinker.common.networking.packet.ClientboundInteractableRemovePacket;
import birsy.clinker.common.networking.packet.ClientboundInteractableSyncPacket;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.Quaterniond;
import birsy.clinker.core.util.rigidbody.colliders.OBBCollisionShape;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.PacketDistributor;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.Nullable;

import java.util.*;

// TODO : add back in... actually interacting with the stuff lmao.
//        networking is hard AAAAA

@Mod.EventBusSubscriber(modid = Clinker.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class InteractableManager {
    public static Map<ServerLevel, InteractableManager> serverInteractableManagers = new HashMap<>();
    @OnlyIn(Dist.CLIENT)
    public static InteractableManager clientInteractableManager;

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
        ClinkerPacketHandler.sendToClientsInChunk((chunk), new ClientboundInteractableAddPacket(new ClientDummyInteractable(interactable.shape, interactable.uuid)));
        ClinkerPacketHandler.sendToClientsInChunk((chunk), new ClientboundInteractableSyncPacket(interactable));

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
            Clinker.LOGGER.info("added clientside interactable!");
        } catch (NullPointerException e) {
            Clinker.LOGGER.warn("clientInteractableManager not yet initialized!");
        }
    }

    public Map<UUID, Interactable> interactableMap;
    public Map<ChunkPos, List<Interactable>> chunkMap;
    private Level level;
    private int ticks;
    public InteractableManager(Level level) {
        this.interactableMap = new HashMap<>();
        this.chunkMap = new HashMap<>();
        this.level = level;
        this.ticks = 0;
    }

    private void addInteractable(Interactable interactable) {
        BlockPos blockPos = MathUtils.blockPosFromVec3(interactable.getTransform().getPosition());
        if (level.isLoaded(blockPos)) {
            this.interactableMap.put(interactable.uuid, interactable);
            ChunkPos pos = this.level.getChunkAt(blockPos).getPos();
            this.addInteractablesToChunk(pos, interactable);
        } else {
            Clinker.LOGGER.warn("Attempting to add interactable to unloaded chunk!");
        }
    }

    public void tick() {
        List<Interactable> iValues = interactableMap.values().stream().toList();

        for (Interactable interactable : iValues) {
            //if (interactable.previousTransform != interactable.getTransform()) interactable.incomingRays.clear();
            interactable.tick();
            BlockPos pBlockPos = MathUtils.blockPosFromVec3(interactable.previousTransform.getPosition());
            BlockPos blockPos = MathUtils.blockPosFromVec3(interactable.getTransform().getPosition());
            LevelChunk currentChunk = level.getChunkAt(pBlockPos);
            LevelChunk nextChunk = level.getChunkAt(blockPos);


            // remove any interactables that are marked for removal.
            if (interactable.shouldBeRemoved) {
                Clinker.LOGGER.info("removed interactable " + interactable.uuid.toString());
                interactableMap.remove(interactable.uuid);
                removeInteractableFromChunk(nextChunk.getPos(), interactable);
                if (!level.isClientSide()) {
                    ClinkerPacketHandler.sendToClientsInChunk((nextChunk), new ClientboundInteractableRemovePacket(interactable.uuid));
                }
            }

            // if any interactables have moved beyond their current chunk, update the chunkmap to reflect that.
            if (currentChunk != nextChunk) {
                removeInteractableFromChunk(currentChunk.getPos(), interactable);
                addInteractablesToChunk(nextChunk.getPos(), interactable);
            }
        }



        if (this.ticks++ % 120 == 0) {
            if (!level.isClientSide()) {
                StringBuilder builder = new StringBuilder();
                builder.append("Serverside stuff for world ");
                builder.append(this.level.dimension().location());
                builder.append(" : [");
                for (Interactable value : this.interactableMap.values()) {
                    builder.append(value.getTransform().getPosition());
                    builder.append(", \n");
                }
                builder.append("]");
                Clinker.LOGGER.info(builder.toString());
            } else {
                StringBuilder builder = new StringBuilder();
                builder.append("Clientside stuff: [");
                for (Interactable value : this.interactableMap.values()) {
                    builder.append(value.getTransform().getPosition());
                    builder.append(", \n");
                }
                builder.append("]");
                Clinker.LOGGER.info(builder.toString());
            }
        }
        if (!level.isClientSide()) {
            serverTick();
        }

        // if any chunks don't contain interactables or aren't loaded, we can remove them from the chunkmap.
        List<Map.Entry<ChunkPos, List<Interactable>>> cValues = chunkMap.entrySet().stream().toList();
        for (Map.Entry<ChunkPos, List<Interactable>> entry : cValues) {
            if (entry.getValue().isEmpty() || !this.level.getChunkSource().hasChunk(entry.getKey().x, entry.getKey().z)) {
                unloadChunk(entry.getKey());
                chunkMap.remove(entry.getKey());
            }
        }
    }

    private void serverTick() {
        // if an interactable has been moved, send that information to the client.
        int id = 0;
        for (Interactable interactable : interactableMap.values()) {
            if (interactable.getTransform() != interactable.previousTransform) {
                BlockPos blockPos = MathUtils.blockPosFromVec3(interactable.getTransform().getPosition());
                LevelChunk chunk = level.getChunkAt(blockPos);
                ClinkerPacketHandler.sendToClientsInChunk((chunk), new ClientboundInteractableSyncPacket(interactable));
            }

            float rot = 45.0F;
            interactable.getTransform().setOrientation(new Quaterniond());
            //interactable.getTransform().rotate(Vector3f.YP.rotationDegrees(rot));
            //interactable.getTransform().rotate(Vector3f.ZP.rotationDegrees(rot));

            double s = 0.01;
            //interactable.setPosition(interactable.getTransform().getPosition().add(0, Mth.sin(id++ + ticks * 0.01F) * s, 0));
        }

        // test shit to see if it works
        if (this.ticks == 200) {
            if (this.level.players().isEmpty()) return;
            Vec3 pos = this.level.players().get(0).position();
            OBBCollisionShape shape = new OBBCollisionShape(0.5, 2.0, 0.5);
            shape.transform.setPosition(Vec3.ZERO);
            //shape.transform.setOrientation(Vector3f.XP.rotationDegrees(level.random.nextFloat() * 360));
            //shape.transform.rotate(Vector3f.YP.rotationDegrees(level.random.nextFloat() * 360));
            //shape.transform.rotate(Vector3f.ZP.rotationDegrees(level.random.nextFloat() * 360));

            addServerInteractable(new Interactable(shape) {
                @Override
                public boolean onInteract(InteractionContext interactionContext, @Nullable Entity entity) {
                    Clinker.LOGGER.info("fart");
                    return true;
                }

                @Override
                public boolean onHit(InteractionContext interactionContext, @Nullable Entity entity) {
                    Clinker.LOGGER.info("poo");
                    return true;
                }

                @Override
                public boolean onPick(InteractionContext interactionContext, @Nullable Entity entity) {
                    Clinker.LOGGER.info("shit");
                    return true;
                }

                @Override
                public boolean onTouch(Entity touchingEntity) {
                    Clinker.LOGGER.info("ass");
                    return true;
                }
            }, (ServerLevel) this.level);
        }
    }

    public List<Interactable> getInteractablesInChunk(ChunkPos pos) {
        if (!chunkMap.containsKey(pos)) return new ArrayList<>();
        return chunkMap.get(pos);
    }

    private void addInteractablesToChunk(ChunkPos pos, Interactable interactable) {
        if (!chunkMap.containsKey(pos)) chunkMap.put(pos, new ArrayList<>());
        chunkMap.get(pos).add(interactable);
    }

    public void removeInteractableFromChunk(ChunkPos pos, Interactable interactable) {
        Clinker.LOGGER.info("removing interactable!");
        if (!chunkMap.containsKey(pos)) return;
        chunkMap.get(pos).remove(interactable);
    }

    public void unloadChunk(ChunkPos chunkPos) {
        if (!chunkMap.containsKey(chunkPos)) return;
        for (Interactable interactable : getInteractablesInChunk(chunkPos)) {
            interactable.markForRemoval();
        }
    }

    public void loadChunkToPlayer(ChunkPos chunkPos, ServerPlayer player) {
        List<Interactable> list = this.getInteractablesInChunk(chunkPos);
        for (int i = 0; i < list.size(); i++) {
            ClinkerPacketHandler.sendToClient(player, new ClientboundInteractableAddPacket(list.get(i)));
        }
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (clientInteractableManager != null) {
            clientInteractableManager.interactableMap.clear();
            clientInteractableManager.chunkMap.clear();
        }
    }

    @SubscribeEvent
    public static void onLevelLoad(LevelEvent.Load event) {
        if (clientInteractableManager == null) {
            clientInteractableManager = new InteractableManager((Level) event.getLevel());
        } else {
            clientInteractableManager.level = (Level) event.getLevel();
        }
    }

    @SubscribeEvent
    public static void onInteract(InputEvent.InteractionKeyMappingTriggered event) {
        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;
        double reach = player.getReachDistance();
        Level level = InteractableManager.clientInteractableManager.level;

        Vec3 direction = player.getLookAngle();
        Vec3 fromPos = player.getEyePosition(mc.getPartialTick());
        Vec3 toPos = direction.scale(reach);

        ClipContext cContext = new ClipContext(fromPos, toPos, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player);
        BlockHitResult bResult = level.clip(cContext);
        EntityHitResult eResult = ProjectileUtil.getEntityHitResult(player, fromPos, toPos, new AABB(fromPos, toPos), (entity) -> !entity.isSpectator() && entity.isPickable(), reach);

        // get all chunks that your reach ray may intersect.
        // way over-engineered system for raycasting, but it'll work with any reach.
        Vec3 rayPos = fromPos;
        double distanceCanMove = 16;
        List<ChunkPos> chunksToCheck = new ArrayList<>();
        for (double d = 0; d < reach; d += distanceCanMove) {
            rayPos = rayPos.add(direction.scale(distanceCanMove));
            BlockPos p = MathUtils.blockPosFromVec3(rayPos);
            chunksToCheck.add(new ChunkPos((int) Math.floor(p.getX() / 16.0D), (int) Math.floor(p.getZ() / 16.0D)));
            chunksToCheck.add(new ChunkPos((int) Math.ceil(p.getX() / 16.0D),  (int) Math.floor(p.getZ() / 16.0D)));
            chunksToCheck.add(new ChunkPos((int) Math.floor(p.getX() / 16.0D), (int) Math.ceil(p.getZ() / 16.0D)));
            chunksToCheck.add(new ChunkPos((int) Math.ceil(p.getX() / 16.0D),  (int) Math.ceil(p.getZ() / 16.0D)));
        }

        // get all the interactables in the chunks your reach ray may intersect.
        List<Interactable> interactablesInChunks = new ArrayList<>();
        //Clinker.LOGGER.info(fromPos);
        for (ChunkPos chunkPos : chunksToCheck) {
            //Clinker.LOGGER.info(chunkPos.toString());
            interactablesInChunks.addAll(InteractableManager.clientInteractableManager.getInteractablesInChunk(chunkPos));
        }

        // sort the list by distance...
        // it should already be mostly sorted, so this isn't too slow.
        interactablesInChunks.sort((a, b) -> a.getTransform().getPosition().distanceToSqr(fromPos) > b.getTransform().getPosition().distanceToSqr(fromPos) ? 0 : 1);

        // finally, do the actual checks.
        InteractionContext iContext = new InteractionContext(fromPos, toPos, event.getHand());
        for (Interactable interactable : interactablesInChunks) {
            // if the interactable has been marked for removal, ignore it.
            if (interactable.shouldBeRemoved) { Clinker.LOGGER.info("a"); continue; }

            double distance = interactable.getTransform().getPosition().distanceTo(fromPos);
            /*if (distance > reach) { Clinker.LOGGER.info("b"); return; }
            // if there's a block in front of the interactable, don't check it. the rest are further away so don't check those either.
            if (bResult != null) if (distance < bResult.getLocation().distanceTo(fromPos)) { Clinker.LOGGER.info("c"); return; }
            // if there's an entity in front of the interactable, don't check it. the rest are further away so don't check those either.
            if (eResult != null) if (distance < eResult.getLocation().distanceTo(fromPos)) { Clinker.LOGGER.info("d"); return; }*/

            Optional<Vec3> cast = interactable.shape.raycast(fromPos, toPos);
            if (cast.isEmpty()) { Clinker.LOGGER.info("e"); continue; }
            //Clinker.LOGGER.info(cast.get());

            Options options = mc.options;
            if (interactable.run(cast.get(), new InteractionInfo(interactable.uuid,
                    event.getKeyMapping() == options.keyPickItem ? InteractionInfo.Interaction.PICK :
                    event.getKeyMapping() == options.keyAttack ? InteractionInfo.Interaction.HIT :
                            InteractionInfo.Interaction.INTERACT, iContext), player)) {
                Clinker.LOGGER.info("interaction successful: " + cast.get());
                event.setCanceled(true);
                event.setSwingHand(true);
                return;
            }
        }
    }
}
