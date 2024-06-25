package birsy.clinker.common.world.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.function.Predicate;

public class AlchemistsCrossbowItem extends ProjectileWeaponItem implements Vanishable {
    private static final String TAG_CHARGED = "Charged";
    private static final String TAG_CHARGED_ITEMSTACK = "ChargedItem";
    private static final String TAG_FIRING = "Firing";
    private static final String TAG_FIRE_DELAY = "FireDelay";
    private boolean startSoundPlayed = false;
    private boolean midLoadSoundPlayed = false;

    public AlchemistsCrossbowItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return (itemStack -> true);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 16;
    }

    public static void pullTrigger(Level pLevel, LivingEntity pShooter, InteractionHand pUsedHand, ItemStack crossbow, float pVelocity, float pInaccuracy) {
        CompoundTag crossbowTag = crossbow.getOrCreateTag();
        crossbowTag.putBoolean(TAG_FIRING, true);
        crossbowTag.putInt(TAG_FIRE_DELAY, 0);
    }

    public static boolean fireProjectile(Level level, LivingEntity shooter, InteractionHand hand, ItemStack crossbow, ItemStack ammo, boolean pIsCreativeMode, float pVelocity, float pInaccuracy) {
        if (ammo.isEmpty()) {
            if (!level.isClientSide()) level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0F, 0.5F);
            return false;
        }

        if (!level.isClientSide) {
            Projectile projectile = new Snowball(level, shooter);

            Vec3 projectileDirection = shooter.getViewVector(1.0F);
            projectile.shoot(projectileDirection.x(), projectileDirection.y(), projectileDirection.z(), pVelocity, pInaccuracy);

            crossbow.hurtAndBreak(1, shooter, entity -> entity.broadcastBreakEvent(hand));
            level.addFreshEntity(projectile);
            level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_SHOOT, SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        ammo.shrink(1);
        if (ammo.getCount() >= 0) ammo = ItemStack.EMPTY;
        CompoundTag crossbowTag = crossbow.getOrCreateTag();
        crossbowTag.put(TAG_CHARGED_ITEMSTACK, ammo.save(new CompoundTag()));
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack crossbow = pPlayer.getItemInHand(pHand);

        if (isCharged(crossbow)) {
            pullTrigger(pLevel, pPlayer, pHand, crossbow, 3.15F, 1.0F);
            discharge(crossbow);
            return InteractionResultHolder.consume(crossbow);
        } else if (!pPlayer.getItemInHand(pHand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND).isEmpty()) { // if you have an item in the opposite hand...
            this.startSoundPlayed = false;
            this.midLoadSoundPlayed = false;
            pPlayer.startUsingItem(pHand);

            return InteractionResultHolder.consume(crossbow);
        } else {
            return InteractionResultHolder.fail(crossbow);
        }
    }

    @Override
    public void onUseTick(Level pLevel, LivingEntity pLivingEntity, ItemStack pStack, int pCount) {
        if (!pLevel.isClientSide) {
            SoundEvent loadStartSound = SoundEvents.CROSSBOW_LOADING_START;
            SoundEvent loadMiddleSound = SoundEvents.CROSSBOW_LOADING_MIDDLE;
            float useFactor = (float)(pStack.getUseDuration() - pCount) / (float)getChargeDuration(pStack);
            if (useFactor < 0.2F) {
                this.startSoundPlayed = false;
                this.midLoadSoundPlayed = false;
            }
            if (useFactor >= 0.2F && !this.startSoundPlayed) {
                this.startSoundPlayed = true;
                pLevel.playSound(null, pLivingEntity.getX(), pLivingEntity.getY(), pLivingEntity.getZ(), loadStartSound, SoundSource.PLAYERS, 0.5F, 1.0F);
            }
            if (useFactor >= 0.5F && !this.midLoadSoundPlayed) {
                this.midLoadSoundPlayed = true;
                pLevel.playSound(null, pLivingEntity.getX(), pLivingEntity.getY(), pLivingEntity.getZ(), loadMiddleSound, SoundSource.PLAYERS, 0.5F, 1.0F);
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack crossbow, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        if (isCharged(crossbow)) return;

        int i = this.getUseDuration(crossbow) - pTimeLeft;
        float power = getPowerForTime(i, crossbow);

        if (power >= 1.0F && tryLoad(pEntityLiving, crossbow)) {
            charge(crossbow);
            pLevel.playSound(null, pEntityLiving.getX(), pEntityLiving.getY(), pEntityLiving.getZ(), SoundEvents.CROSSBOW_LOADING_END, pEntityLiving instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE, 1.0F, 1.0F / (pLevel.getRandom().nextFloat() * 0.2F + 1.5F));
        }
    }

    private static boolean tryLoad(LivingEntity shooter, ItemStack crossbow) {
        boolean hasInfiniteItems = shooter instanceof Player && ((Player)shooter).getAbilities().instabuild;
        InteractionHand hand = shooter.getUsedItemHand();
        ItemStack ammo = shooter.getItemInHand(hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);

        if (ammo.isEmpty()) return false;

        ItemStack ammo2;
        if (!hasInfiniteItems) {
            ammo2 = ammo.split(1);
            if (ammo.isEmpty() && shooter instanceof Player player) {
                player.getInventory().removeItem(ammo);
            }
        } else {
            ammo2 = ammo.copy();
            ammo2.setCount(1);
        }

        insertAmmunition(crossbow, ammo2);
        return true;
    }

    @Override
    public int getUseDuration(ItemStack pStack) {
        return getChargeDuration(pStack) + 3;
    }

    public static boolean isCharged(ItemStack crossbow) {
        CompoundTag compoundtag = crossbow.getTag();
        return compoundtag != null && compoundtag.getBoolean("Charged");
    }

    public static void charge(ItemStack crossbow) {
        CompoundTag compoundtag = crossbow.getOrCreateTag();
        compoundtag.putBoolean(TAG_CHARGED, true);
    }

    public static void insertAmmunition(ItemStack crossbow, ItemStack ammunition) {
        CompoundTag crossbowTag = crossbow.getOrCreateTag();
        crossbowTag.put(TAG_CHARGED_ITEMSTACK, ammunition.save(new CompoundTag()));
    }

    public static void discharge(ItemStack crossbow) {
        CompoundTag crossbowTag = crossbow.getOrCreateTag();
        crossbowTag.putBoolean(TAG_CHARGED, false);
    }

    public static int getChargeDuration(ItemStack crossbow) {
        return 25;
    }

    private static float getPowerForTime(float pUseTime, ItemStack crossbow) {
        return Math.min(1.0F, pUseTime / getChargeDuration(crossbow));
    }
}
