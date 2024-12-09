package birsy.clinker.common.world.item;

import birsy.clinker.client.sound.OrdnanceSoundInstance;
import birsy.clinker.common.world.entity.projectile.OrdnanceEntity;
import birsy.clinker.common.world.item.components.FuseTimer;
import birsy.clinker.common.world.item.components.OrdnanceEffects;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerDataComponents;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.Fireworks;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrdnanceItem extends Item implements ProjectileItem {
    public static Map<UUID, OrdnanceSoundInstance> sounds = new HashMap<>();

    public OrdnanceItem(Properties pProperties) {
        super(pProperties);
    }

    private static OrdnanceEffects getOrdnanceEffects(ItemStack stack) {
        return stack.getOrDefault(ClinkerDataComponents.ORDNANCE_EFFECTS.get(), OrdnanceEffects.DEFAULT);
    }
    private static FuseTimer getFuseTimer(ItemStack stack) {
        return stack.getOrDefault(ClinkerDataComponents.FUSE_TIMER.get(), FuseTimer.EMPTY);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        OrdnanceEffects effects = getOrdnanceEffects(stack);
        effects.addToTooltip(context, tooltipComponents::add, tooltipFlag);
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        pPlayer.startUsingItem(pHand);

        FuseTimer fuseTimer = getFuseTimer(stack);
        if (fuseTimer.lit()) {
            pLevel.playSound(null,
                    pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                    SoundEvents.TNT_PRIMED, SoundSource.PLAYERS,
                    0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 1F)
            );
        } else {
            stack.set(ClinkerDataComponents.FUSE_TIMER.get(), new FuseTimer(fuseTimer.tickCount(), true));
        }

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return this.getOrdnanceEffects(stack).fuseTime() + 1;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        pLevel.playSound(null,
                pEntityLiving.getX(), pEntityLiving.getY(), pEntityLiving.getZ(),
                SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS,
                0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        FuseTimer fuseTimer = getFuseTimer(stack);
        OrdnanceEffects effects = getOrdnanceEffects(stack);
        if (!pLevel.isClientSide) {
            OrdnanceEntity ordnance = OrdnanceEntity.toss(pLevel, pEntityLiving);
            ordnance.setEffects(effects);
            ordnance.setFuseTime(fuseTimer.tickCount());
            pLevel.addFreshEntity(ordnance);
        }

        use(pEntityLiving, stack);
    }

    private void use(Entity entity, ItemStack stack) {
        if (entity instanceof Player player) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
            player.getCooldowns().addCooldown(this, 40);
        } else {
            stack.shrink(1);
        }
        stack.set(ClinkerDataComponents.FUSE_TIMER.get(), new FuseTimer(0, false));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }


    @Override
    public void inventoryTick(ItemStack stack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(stack, pLevel, pEntity, pSlotId, pIsSelected);
        FuseTimer fuseTimer = getFuseTimer(stack);
        if (!fuseTimer.lit()) return;

        OrdnanceEffects effects = getOrdnanceEffects(stack);

        if (fuseTimer.tickCount() > effects.fuseTime()) {
            OrdnanceEntity.createOrdnanceExplosion(pEntity.position(), pLevel, pEntity, null, effects);
            this.use(pEntity, stack);
        } else {
            stack.set(ClinkerDataComponents.FUSE_TIMER.get(), new FuseTimer(fuseTimer.tickCount() + 1, true));
        }
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        FuseTimer fuseTimer = getFuseTimer(stack);
        OrdnanceEffects effects = getOrdnanceEffects(stack);
        OrdnanceEntity ordnance = OrdnanceEntity.create(level, pos.x(), pos.y(), pos.z());
        ordnance.setEffects(effects);
        ordnance.setFuseTime(fuseTimer.tickCount());
        return ordnance;
    }

    @Override
    public DispenseConfig createDispenseConfig() {
        return ProjectileItem.super.createDispenseConfig();
    }

    @Override
    public void shoot(Projectile projectile, double x, double y, double z, float velocity, float inaccuracy) {
        projectile.shoot(x, y, z, velocity, inaccuracy);
    }
}
