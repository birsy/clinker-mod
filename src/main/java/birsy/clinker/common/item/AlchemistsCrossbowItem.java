package birsy.clinker.common.item;

import birsy.clinker.common.entity.projectile.ShotItemEntity;
import birsy.clinker.common.item.components.CrossbowState;
import birsy.clinker.common.item.components.LoadedItemStack;
import birsy.clinker.core.registry.ClinkerDataComponents;
import birsy.clinker.core.registry.ClinkerTags;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class AlchemistsCrossbowItem extends ProjectileWeaponItem {
    public static final int TICKS_BETWEEN_SHOTS = 3;
    public static final int ITEM_LOAD_TIME = 20;

    public AlchemistsCrossbowItem(Properties properties) {
        super(properties);
    }

    // component helper functions
    public static LoadedItemStack getLoadedItems(ItemStack stack) {
        return stack.getOrDefault(ClinkerDataComponents.LOADED_ITEM_STACK.get(), LoadedItemStack.EMPTY);
    }
    public static boolean hasRepeater(ItemStack stack) {
        return stack.has(ClinkerDataComponents.REPEATER.get());
    }
    public static CrossbowState getCrossbowState(ItemStack stack) {
        return stack.getOrDefault(ClinkerDataComponents.CROSSBOW_STATE.get(), CrossbowState.STANDBY);
    }
    public static int capacity(ItemStack crossbow, ItemStack ammo) {
        return hasRepeater(crossbow) ? Math.max(1, Math.ceilDiv(ammo.getMaxStackSize(), 4)) : 1;
    }
    public static boolean isFull(ItemStack crossbow) {
        ItemStack ammo = getLoadedItems(crossbow).stack();
        return ammo.getCount() >= capacity(crossbow, ammo);
    }
    public static void settle(ItemStack crossbow) {
        crossbow.set(ClinkerDataComponents.CROSSBOW_STATE.get(), getLoadedItems(crossbow).isEmpty() ? CrossbowState.STANDBY : CrossbowState.LOADED);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack repairCandidate) {
        return repairCandidate.is(ClinkerTags.Items.ALCHEMISTS_CROSSBOW_REPAIRABLE);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        ItemStack loadedItems = getLoadedItems(stack).stack();
        if (!loadedItems.isEmpty()) {
            tooltipComponents.add(Component.translatable("item.clinker.alchemists_crossbow.primed").withStyle(ChatFormatting.YELLOW));
            tooltipComponents.add(Component.literal("   ").append(Component.translatable("container.shulkerBox.itemCount", loadedItems.getHoverName(), loadedItems.getCount()).withStyle(ChatFormatting.GRAY)));
        }
        if (hasRepeater(stack))
            tooltipComponents.add(Component.translatable("item.clinker.alchemists_crossbow.repeater").withStyle(ChatFormatting.BLUE, ChatFormatting.ITALIC));

        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return (item) -> true;
    }

    @Override
    public int getDefaultProjectileRange() {
        return 8;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack crossbow = player.getItemInHand(hand);
        CrossbowState crossbowState = getCrossbowState(crossbow);
        switch (crossbowState) {
            case STANDBY -> {
                InteractionHand oppositeHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
                ItemStack oppositeHandStack = player.getItemInHand(oppositeHand);
                if (!oppositeHandStack.isEmpty()) {
                    crossbow.set(ClinkerDataComponents.CROSSBOW_STATE.get(), CrossbowState.LOADING);
                    player.startUsingItem(hand);
                    level.playSound(null,
                            player.getX(), player.getY(), player.getZ(),
                            SoundEvents.CROSSBOW_LOADING_START,
                            SoundSource.PLAYERS,
                            1, 1);
                    return InteractionResultHolder.consume(crossbow);
                } else {
                    player.displayClientMessage(Component.translatable("item.clinker.alchemists_crossbow.no_ammo").withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.GRAY)), true);
                    return InteractionResultHolder.fail(crossbow);
                }
            }
            case LOADED -> {
                if (player.isUsingItem()) return InteractionResultHolder.fail(crossbow);
                this.pullTrigger(level, player, hand, crossbow, null);
                return InteractionResultHolder.consume(crossbow);
            }
            default -> {
                return InteractionResultHolder.fail(crossbow);
            }
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack crossbow, int remainingUseDuration) {
        super.onUseTick(level, livingEntity, crossbow, remainingUseDuration);
        CrossbowState crossbowState = getCrossbowState(crossbow);
        switch (crossbowState) {
            case LOADING -> loadAmmoTick(level, livingEntity, crossbow);
            case FIRING -> tickRepeaterFire(crossbow, livingEntity, level, livingEntity.getUsedItemHand());
        }
    }

    protected void pullTrigger(Level level, LivingEntity shooter, InteractionHand hand, ItemStack crossbow, @Nullable LivingEntity target) {
        if (level instanceof ServerLevel serverlevel) {
            ItemStack ammo = getLoadedItems(crossbow).stack();
            if (!ammo.isEmpty()) {
                if (!hasRepeater(crossbow) || ammo.getCount() == 1) {
                    this.fire(serverlevel, shooter, hand, crossbow, ammo, target);
                    crossbow.set(ClinkerDataComponents.CROSSBOW_STATE.get(), CrossbowState.STANDBY);
                } else {
                    crossbow.set(ClinkerDataComponents.CROSSBOW_STATE.get(), CrossbowState.FIRING);
                    // will fire immediately
                    shooter.startUsingItem(hand);
                }

                if (shooter instanceof ServerPlayer player) {
                    CriteriaTriggers.SHOT_CROSSBOW.trigger(player, crossbow);
                    player.awardStat(Stats.ITEM_USED.get(crossbow.getItem()));
                }
            }
        }
    }

    protected void fire(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack crossbow, ItemStack ammo, @Nullable LivingEntity target) {
        this.shoot(level, shooter, hand, crossbow, List.of(ammo.copyWithCount(1)), 1.5F, ammo.getCount() <= 1 ? 0 : 1.5F, shooter instanceof Player, target);
        int count = ammo.getCount();
        LoadedItemStack currentLoadedStack = getLoadedItems(crossbow);
        crossbow.set(ClinkerDataComponents.LOADED_ITEM_STACK.get(),
                new LoadedItemStack(
                        count <= 1 ? ItemStack.EMPTY : ammo.copyWithCount(count - 1),
                        count <= 1 ? 0 : currentLoadedStack.lastCount()));
        level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_SHOOT, shooter instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE, 1, 1);
    }

    private void tickRepeaterFire(ItemStack crossbow, LivingEntity living, Level level, InteractionHand hand) {
        if (!(level instanceof ServerLevel serverLevel)) return;
        if (living.getTicksUsingItem() % TICKS_BETWEEN_SHOTS != 0) return;
        ItemStack ammo = getLoadedItems(crossbow).stack();
        if (!ammo.isEmpty()) {
            this.fire(serverLevel, living, hand, crossbow, ammo, null);
        } else {
            // play an "out of ammo" click!
            serverLevel.playSound(null, living.getX(), living.getY(), living.getZ(),
                    SoundEvents.DISPENSER_FAIL,
                    living instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE, 1, 1);
        }
    }

    @Override
    protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit) {
        Projectile projectile;
        if (ammo.getItem() instanceof ArrowItem arrowItem) {
            AbstractArrow arrow = arrowItem.createArrow(level, ammo, shooter, weapon);
            arrow.setCritArrow(isCrit);
            projectile = arrow;
        } else if (ammo.getItem() instanceof ProjectileItem projectileItem) {
            Vec3 lookVector = shooter.getLookAngle();
            projectile = projectileItem.asProjectile(level, shooter.getEyePosition(), ammo, Direction.getNearest(lookVector.x(), lookVector.y(), lookVector.z()));
        } else {
            projectile = ShotItemEntity.create(level, shooter, ammo);
            level.addFreshEntity(projectile);
        }
        Vec3 lookVector = shooter.getLookAngle();
        projectile.setPos(shooter.getEyePosition().add(lookVector.scale((shooter.getBbWidth() + projectile.getBbWidth()) * 0.5)));
        projectile.setOwner(shooter);
        return projectile;
    }

    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {
        projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, velocity, inaccuracy);
    }

    // LOADING
    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack crossbow, Level level, LivingEntity living, int timeCharged) {
        CrossbowState crossbowState = getCrossbowState(crossbow);
        switch (crossbowState) {
            case LOADING -> {
                level.playSound(null,
                        living.getX(), living.getY(), living.getZ(),
                        SoundEvents.CROSSBOW_LOADING_END,
                        living instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE,
                        1, 1);
                settle(crossbow);
            }
            case FIRING -> {
                // prevent players from rapid-firing
                if (living instanceof Player player && !getLoadedItems(crossbow).isEmpty())
                    player.getCooldowns().addCooldown(this, TICKS_BETWEEN_SHOTS);
                settle(crossbow);
            }
        }
    }

    protected void loadAmmoTick(Level level, LivingEntity living, ItemStack crossbow) {
        if (isFull(crossbow)) return;
        int used = living.getTicksUsingItem();
        if (used == 0 || used % ITEM_LOAD_TIME != 0) return;

        ItemStack ammo = living.getItemInHand(living.getUsedItemHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
        loadAmmo(level, living, crossbow, ammo);
    }

    protected boolean loadAmmo(Level level, LivingEntity living, ItemStack crossbow, ItemStack ammo) {
        if (ammo == null || ammo.isEmpty())
            return false;
        if (isFull(crossbow))
            return false;

        ItemStack currentlyLoadedItems = getLoadedItems(crossbow).stack();
        if (!currentlyLoadedItems.isEmpty() && !ItemStack.isSameItemSameComponents(ammo, currentlyLoadedItems))
            return false;

        crossbow.set(ClinkerDataComponents.LOADED_ITEM_STACK.get(),
                new LoadedItemStack(
                ammo.copyWithCount(currentlyLoadedItems.getCount() + 1),
                currentlyLoadedItems.getCount() + 1)
        );
        if (!level.isClientSide())
            level.playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.CROSSBOW_LOADING_MIDDLE, living instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE, 1, 1);
        if (living instanceof Player player) {
            level.playSound(player, living.getX(), living.getY(), living.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.1F, 0.5F + level.random.nextFloat() * 0.1F);
        }
        ammo.shrink(1);
        return true;
    }

    @Override
    public boolean useOnRelease(ItemStack stack) {
        return false;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int slot, boolean selected) {
        CrossbowState state = getCrossbowState(stack);
        if (state == CrossbowState.LOADING || state == CrossbowState.FIRING) {
            boolean inUse = entity instanceof LivingEntity living && living.isUsingItem()
                    && ItemStack.isSameItem(living.getUseItem(), stack);
            if (!inUse) settle(stack);
        }
    }
}
