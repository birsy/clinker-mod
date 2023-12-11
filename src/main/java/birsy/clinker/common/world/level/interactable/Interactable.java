package birsy.clinker.common.world.level.interactable;


import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.joml.Quaterniond;
import org.joml.Vector3d;

import java.util.UUID;

public class Interactable {
    private Vector3d position, previousPosition;
    private Quaterniond orientation, previousOrientation;
    public final UUID id;
    public boolean markedDirty = false;
    public boolean removed = false;
    public boolean shouldSync = true;

    public Interactable(Vector3d position, Quaterniond orientation) {
        this(UUID.randomUUID());
        this.position = position;
        this.previousPosition = new Vector3d(position);
        this.orientation = orientation;
        this.previousOrientation = new Quaterniond(orientation);
    }

    public Interactable(UUID id) {
        this.id = id;
    }

    public void tick() {
        this.previousPosition.set(this.position);
        this.previousOrientation.set(this.orientation);
    }

    /**
     * @return An immutable copy of the position.
     */
    public Vector3d getPosition() {
        return new Vector3d(position);
    }

    public void setPosition(Vector3d position) {
        this.position.set(position);
        this.markedDirty = true;
    }

    /**
     * @return An immutable copy of the orientation.
     */
    public Quaterniond getOrientation() {
        return new Quaterniond(orientation);
    }

    public void setOrientation(Quaterniond orientation) {
        this.orientation.set(orientation);
        this.markedDirty = true;
    }

    public void move(Vector3d offset) {
        this.setPosition(this.getPosition().add(offset));
    }

    public SectionPos getSectionPos() {
        return SectionPos.of(new Vec3(position.x, position.y, position.z));
    }

    public Tag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putDouble("xp", position.x);
        tag.putDouble("yp", position.y);
        tag.putDouble("zp", position.z);
        tag.putDouble("xo", orientation.x);
        tag.putDouble("yo", orientation.y);
        tag.putDouble("zo", orientation.z);
        tag.putDouble("wo", orientation.w);
        tag.putUUID("id", id);
        return tag;
    }

    public static Interactable deserializeNBT(CompoundTag nbt) {
        UUID id = nbt.getUUID("id");
        Vector3d pos = new Vector3d(nbt.getDouble("xp"), nbt.getDouble("yp"), nbt.getDouble("zp"));
        Quaterniond rot = new Quaterniond(nbt.getDouble("xo"), nbt.getDouble("yo"), nbt.getDouble("zo"), nbt.getDouble("wo"));
        Interactable interactable = new Interactable(id);
        interactable.position = pos;
        interactable.previousPosition = new Vector3d(pos);
        interactable.orientation = rot;
        interactable.previousOrientation = new Quaterniond(rot);
        return interactable;
    }
}
