package birsy.clinker.common.world.level.interactable;


import birsy.clinker.core.util.CollisionUtils;
import net.minecraft.core.SectionPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.util.INBTSerializable;
import org.joml.Quaterniond;
import org.joml.Vector3d;
import org.joml.Vector3f;

import java.util.Optional;
import java.util.UUID;

public class Interactable {
    private Vector3d position, previousPosition;
    private Quaterniond orientation, previousOrientation;
    public final UUID id;
    public boolean markedDirty = false;
    public boolean removed = false;
    public boolean shouldSync = true;
    public Vector3f size;
    private AABB bounds;
    private boolean calculatedBounds = false;

    public Interactable(Vector3f size, Vector3d position, Quaterniond orientation) {
        this(UUID.randomUUID());
        this.size = size;
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

    public Vector3d getPosition(float partialTicks) {
        return this.previousPosition.lerp(this.position, partialTicks, new Vector3d());
    }

    public void setPosition(Vector3d newPosition) {
        if (calculatedBounds && this.bounds != null) this.bounds = this.bounds.move(this.position.x - newPosition.x, this.position.y - newPosition.y, this.position.z - newPosition.z());
        this.position.set(newPosition);
        this.markedDirty = true;
    }

    /**
     * @return An immutable copy of the orientation.
     */
    public Quaterniond getOrientation() {
        return new Quaterniond(orientation);
    }

    public Quaterniond getOrientation(float partialTicks) {
        return this.previousOrientation.slerp(this.orientation, partialTicks, new Quaterniond());
    }

    public void setOrientation(Quaterniond orientation) {
        this.orientation.set(orientation);
        this.markedDirty = true;
        this.calculatedBounds = false;
    }

    public void move(Vector3d offset) {
        this.setPosition(this.getPosition().add(offset));
    }

    public SectionPos getSectionPos() {
        return SectionPos.of(new Vec3(position.x, position.y, position.z));
    }

    private static float[] multipliers = {-0.5f, 0.5f};
    public AABB getBounds() {
        if (calculatedBounds && this.bounds != null) return bounds;
        float maxX = Float.MIN_VALUE, minX = Float.MAX_VALUE, maxY = Float.MIN_VALUE, minY = Float.MAX_VALUE, maxZ = Float.MIN_VALUE, minZ = Float.MAX_VALUE;
        Vector3f vertexPos = new Vector3f();
        for (float xMult : multipliers) {
            for (float yMult : multipliers) {
                for (float zMult : multipliers) {
                    vertexPos = vertexPos.set(this.size).mul(xMult, yMult, zMult);
                    vertexPos = this.orientation.transform(vertexPos);
                    if (vertexPos.x() > maxX) maxX = vertexPos.x();
                    if (vertexPos.y() > maxY) maxY = vertexPos.y();
                    if (vertexPos.z() > maxZ) maxZ = vertexPos.z();
                    if (vertexPos.x() < minX) minX = vertexPos.x();
                    if (vertexPos.y() < minY) minY = vertexPos.y();
                    if (vertexPos.z() < minZ) minZ = vertexPos.z();
                }
            }
        }
        this.bounds = new AABB(minX, minY, minZ, maxX, maxY, maxZ).move(this.position.x, this.position.y, this.position.z);
        this.calculatedBounds = true;
        return bounds;
    }

    public Optional<Vector3d> raycast(Vector3d from, Vector3d to) {
        return CollisionUtils.raycast(new CollisionUtils.OrientableBoundingBox(new Vector3d(this.size), this.position, this.orientation), from, to);
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

        tag.putFloat("xs", size.x);
        tag.putFloat("ys", size.y);
        tag.putFloat("zs", size.z);

        tag.putUUID("id", id);
        return tag;
    }

    public static Interactable deserializeNBT(CompoundTag nbt) {
        UUID id = nbt.getUUID("id");
        Vector3d pos = new Vector3d(nbt.getDouble("xp"), nbt.getDouble("yp"), nbt.getDouble("zp"));
        Quaterniond rot = new Quaterniond(nbt.getDouble("xo"), nbt.getDouble("yo"), nbt.getDouble("zo"), nbt.getDouble("wo"));
        Vector3f size = new Vector3f(nbt.getFloat("xs"), nbt.getFloat("ys"), nbt.getFloat("zs"));
        Interactable interactable = new Interactable(id);
        interactable.position = pos;
        interactable.previousPosition = new Vector3d(pos);
        interactable.orientation = rot;
        interactable.previousOrientation = new Quaterniond(rot);
        interactable.size = size;
        return interactable;
    }
}
