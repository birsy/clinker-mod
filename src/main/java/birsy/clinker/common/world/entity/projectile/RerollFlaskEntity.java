package birsy.clinker.common.world.entity.projectile;

import birsy.clinker.core.registry.ClinkerDataAttachments;
import birsy.clinker.core.registry.ClinkerItems;
import birsy.clinker.core.registry.entity.ClinkerEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class RerollFlaskEntity extends ThrowableItemProjectile {
    public RerollFlaskEntity(EntityType<? extends RerollFlaskEntity> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    public static RerollFlaskEntity toss(Level pLevel, LivingEntity thrower) {
        RerollFlaskEntity entity = new RerollFlaskEntity(ClinkerEntities.REROLL_FLASK.get(), pLevel);
        entity.setOwner(thrower);
        entity.shootFromRotation(thrower, thrower.getXRot(), thrower.getYRot(), 0.0F, 0.8F, 0.0F);
        entity.setPos(thrower.getEyePosition().add(entity.getDeltaMovement().normalize()));
        return entity;
    }

    @Override
    protected Item getDefaultItem() {
        return ClinkerItems.REROLL_FLASK.get();
    }

    @Override
    protected float getGravity() {
        return 0.07F;
    }

    @Override
    protected void onHit(HitResult pResult) {
        super.onHit(pResult);
        if (this.level() instanceof ServerLevel) {
            this.level().levelEvent(2002, this.blockPosition(), PotionUtils.getColor(Potions.WATER));
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult pResult) {
        super.onHitBlock(pResult);
        if (this.level().isClientSide()) {
            return;
        }


        BlockEntity blockEntity = this.level().getBlockEntity(pResult.getBlockPos());
        if (blockEntity != null && blockEntity instanceof RandomizableContainerBlockEntity container) {

            if (!container.hasData(ClinkerDataAttachments.CAN_REROLL_LOOT) || !container.hasData(ClinkerDataAttachments.REROLL_LOOT_LOCATION)) {
                if (container.getLootTable() != null) {
                    applyRerollVFX(pResult.getBlockPos());
                }
                return;
            }

            if (!container.getData(ClinkerDataAttachments.CAN_REROLL_LOOT)) {
                return;
            }

            rerollLoot(container);

            applyRerollVFX(pResult.getBlockPos());
        }
    }

    protected void rerollLoot(RandomizableContainerBlockEntity container) {
        container.setData(ClinkerDataAttachments.FILLING_LOOT_TABLE, true);

        container.clearContent();
        container.setLootTable(new ResourceLocation(container.getData(ClinkerDataAttachments.REROLL_LOOT_LOCATION)), this.random.nextLong());

        Player player = null;
        if (this.getOwner() instanceof Player owner) player = owner;

        container.unpackLootTable(player);
        this.transmogrifyItems(container);

        container.setData(ClinkerDataAttachments.FILLING_LOOT_TABLE, false);
    }

    protected void applyRerollVFX(BlockPos pos) {

        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.INSTANT_EFFECT, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 24, 0.25, 0.25, 0.25, 0);
        }


        this.level().playSound(null, pos, SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.BLOCKS, 0.3F, 2.0F);
    }

    protected void transmogrifyItems(RandomizableContainerBlockEntity container) {
        // TODO: item transmogrification recipes
    }
}
