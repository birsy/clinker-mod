package birsy.clinker.common.world.block.blockentity.fairyfruit;

import birsy.clinker.common.networking.ClinkerPacketHandler;
import birsy.clinker.common.networking.packet.ClientboundFairyFruitBreakPacket;
import birsy.clinker.common.networking.packet.ServerboundInteractableInteractionPacket;
import birsy.clinker.common.world.level.interactable.*;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.util.MathUtils;
import birsy.clinker.core.util.rigidbody.colliders.OBBCollisionShape;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class FairyFruitInteractable extends CollidableInteractable {
    final BlockPos pos;
    final int index;
    public FairyFruitBlockEntity parent;
    public FairyFruitSegment segmentParent;

    public FairyFruitInteractable(FairyFruitBlockEntity parent, FairyFruitSegment segmentParent, OBBCollisionShape shape) {
        super(shape, true, 0.0, 0.01, true);
        parent.addChild(this);
        this.parent = parent;
        this.pos = parent.getBlockPos();
        segmentParent.interactable = this;
        this.segmentParent = segmentParent;
        this.index = this.segmentParent.index;
    }

    public FairyFruitInteractable(BlockPos pos, int index, OBBCollisionShape shape, UUID uuid) {
        super(shape, true, 0.0, 0.01, uuid, true);
        this.pos = pos;
        this.index = index;

        Level level = InteractableManager.clientInteractableManager.level;
        FairyFruitBlockEntity entity = (FairyFruitBlockEntity) level.getBlockEntity(pos);
        if (entity != null) {
            this.parent = entity;
            this.parent.addChild(this);
            this.segmentParent = entity.segments.get(index);
            this.segmentParent.interactable = this;
        }
    }

    public FairyFruitInteractable() {
        super();
        this.parent = null;
        this.segmentParent = null;
        this.pos = null;
        this.index = 0;
    }

    @Override
    protected void tick(boolean clientside) {
        super.tick(clientside);
        if (this.parent == null && clientside) {
            Level level = InteractableManager.clientInteractableManager.level;
            FairyFruitBlockEntity entity = (FairyFruitBlockEntity) level.getBlockEntity(pos);
            if (entity != null) {
                this.parent = entity;
                this.parent.addChild(this);
                this.segmentParent = entity.segments.get(index);
                this.segmentParent.interactable = this;
            }
        }
    }

    @Override
    public boolean onHit(InteractionContext interactionContext, @Nullable Entity entity, boolean clientSide) {
        if (clientSide) ClinkerPacketHandler.NETWORK.sendToServer(new ServerboundInteractableInteractionPacket(new InteractionInfo(this.uuid, InteractionInfo.Interaction.HIT, interactionContext)));
        if (this.parent == null || this.segmentParent == null) return super.onHit(interactionContext, entity, clientSide);
        if (entity instanceof Player player) {
            if (player.getAbilities().mayBuild &&
                    player.getItemInHand(interactionContext.hand()).getItem().canAttackBlock(this.parent.getBlockState(), this.parent.getLevel(), MathUtils.blockPosFromVec3(this.getPosition()), player)) {
                this.parent.breakAt(entity, segmentParent.index);
                if (parent.hasLevel() && !clientSide) ClinkerPacketHandler.sendToClientsInChunk(parent.getLevel().getChunkAt(parent.getBlockPos()), new ClientboundFairyFruitBreakPacket(parent, segmentParent.index, entity));
            }
        }
        return true;
    }


    @Override
    public boolean onTouch(InteractionContext context, Entity touchingEntity, boolean clientSide) {
        return true;
    }

    @Override
    public void push(Vec3 movement) {
        segmentParent.push(movement);
        super.push(movement);
    }

    @Override
    public CompoundTag serialize(@Nullable CompoundTag tag) {
        if (tag == null) tag = new CompoundTag();
        tag.putUUID("uuid", this.uuid);
        BlockPos pos = this.parent.getBlockPos();
        tag.putInt("x", pos.getX());
        tag.putInt("y", pos.getY());
        tag.putInt("z", pos.getZ());
        tag.putInt("index", this.segmentParent.index);
        tag.putString("name", this.getClass().getName());
        this.shape.serialize(tag);

        return tag;
    }

    @Override
    public <I extends Interactable> I reconstructOnClient(CompoundTag tag) {
        return (I) new FairyFruitInteractable(new BlockPos(tag.getInt("x"), tag.getInt("y"), tag.getInt("z")), tag.getInt("index"), (OBBCollisionShape) new OBBCollisionShape(0, 0, 0).deserialize(tag), tag.getUUID("uuid"));
    }
}
