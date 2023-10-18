package birsy.clinker.common.world.alchemy.workstation;

import birsy.clinker.client.gui.AlchemicalWorkstationScreen;
import birsy.clinker.common.world.alchemy.workstation.camera.CameraPath;
import birsy.clinker.common.world.alchemy.workstation.camera.WorkstationCamera;
import birsy.clinker.common.world.alchemy.workstation.storage.SectionBitSet;
import birsy.clinker.common.world.alchemy.workstation.storage.WorkstationBlocks;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Workstation {
    public WorkstationBlocks containedBlocks;
    public final UUID uuid;
    public final Level level;
    public boolean isValidPath = false;
    public final CameraPath path;

    public WorkstationEnvironment environment;

    @OnlyIn(Dist.CLIENT)
    public final WorkstationCamera camera;

    public Workstation(Level level) {
        this(level, UUID.randomUUID());
    }

    public Workstation(Level level, UUID uuid) {
        this.level = level;
        this.uuid = uuid;
        this.path = new CameraPath(this);
        this.camera = new WorkstationCamera(this);
        this.containedBlocks = new WorkstationBlocks();
        this.environment = new WorkstationEnvironment(this);
    }

    public void tick() {
        if (!this.isValidPath) {
            this.regeneratePath();
        }

        this.environment.tick();
    }

    @OnlyIn(Dist.CLIENT)
    public void initializeCameraPosition(Vec3 position) {
        CameraPath.CameraPathNode closestNode = path.cameraPathNodes.get(0);
        double distance = Double.MAX_VALUE;
        for (CameraPath.CameraPathNode node : path.cameraPathNodes) {
            double dist = node.position.distanceToSqr(position);
            if (dist < distance) {
                closestNode = node;
                distance = dist;
            }
        }

        camera.node = closestNode;
        camera.lineProgress = 0.5F;
        camera.update();
    }

    public void addBlock(BlockPos pos) {
        this.containedBlocks.insertBlock(pos);
        isValidPath = false;
    }

    public void removeBlock(BlockPos pos) {
        this.containedBlocks.removeBlock(pos);
        isValidPath = false;
    }

    public void regeneratePath() {
        this.bootClientsFromGui();
        this.path.generateCameraPath();
        this.isValidPath = true;
    }

    public Workstation merge(Workstation other) {
        this.isValidPath = false;

        this.containedBlocks.blocks.addAll(other.containedBlocks.blocks);
        for (Long2ObjectMap.Entry<SectionBitSet> entry : other.containedBlocks.bitsetBySection.long2ObjectEntrySet()) {
            if (this.containedBlocks.bitsetBySection.containsKey(entry.getLongKey())) {
                this.containedBlocks.bitsetBySection.get(entry.getLongKey()).or(entry.getValue());
            } else {
                this.containedBlocks.bitsetBySection.put(entry.getLongKey(), entry.getValue());
            }
        }

        return this;
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("uuid", this.uuid);

        CompoundTag containedBlocksTag = new CompoundTag();
        int i = 0;
        for (BlockPos containedBlock : containedBlocks) {
            containedBlocksTag.putIntArray("" + (i++), new int[]{containedBlock.getX(), containedBlock.getY(), containedBlock.getZ()});
        }
        tag.put("containedBlocks", containedBlocksTag);

        return tag;
    }

    public static Workstation deserialize(CompoundTag tag, Level level) {
        Workstation station = new Workstation(level, tag.getUUID("uuid"));

        CompoundTag containedBlocksTag = tag.getCompound("containedBlocks");
        for (int i = 0; i < containedBlocksTag.size(); i++) {
            int[] pos = containedBlocksTag.getIntArray("" + i);
            station.addBlock(new BlockPos(pos[0], pos[1], pos[2]));
        }

        return station;
    }

    @OnlyIn(Dist.CLIENT)
    public void bootClientsFromGui() {
        if (Minecraft.getInstance().screen instanceof AlchemicalWorkstationScreen screen) {
            if (screen.workstation == this) screen.beginClosing();
        }
    }
}
