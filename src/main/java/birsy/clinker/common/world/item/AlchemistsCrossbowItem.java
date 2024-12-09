package birsy.clinker.common.world.item;

import birsy.clinker.common.world.item.components.LoadedItemStack;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerDataComponents;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.Snowball;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

// kind of jank right now...
// todo: less jank.
public class AlchemistsCrossbowItem extends ProjectileWeaponItem {
    static int REPEATER_STACK_SIZE = 16;
    static int TICKS_BETWEEN_SHOTS = 8;
    static int ITEM_LOAD_TIME = 20;

    public AlchemistsCrossbowItem(Properties properties) {
        super(properties);
    }

    private static LoadedItemStack getLoadedItems(ItemStack stack) {
        return stack.getOrDefault(ClinkerDataComponents.LOADED_ITEM_STACK.get(), LoadedItemStack.EMPTY);
    }
    private static boolean hasRepeater(ItemStack stack) {
        return true;
    }
    private static boolean isFiring(ItemStack stack) {
        return stack.getOrDefault(ClinkerDataComponents.FIRING.get(), false);
    }
    private static int tickDelay(ItemStack stack) {
        return stack.getOrDefault(ClinkerDataComponents.TICK_DELAY.get(), 0);
    }
    private static boolean isFull(ItemStack stack) {
        ItemStack ammo = getLoadedItems(stack).stack();
        if (hasRepeater(stack)) {
            return ammo.getCount() >= REPEATER_STACK_SIZE || ammo.getCount() >= ammo.getMaxStackSize();
        } else {
            return !ammo.isEmpty();
        }
    }
    private static boolean isRepeatFiring(ItemStack stack) {
        return hasRepeater(stack) && isFiring(stack);
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
        ItemStack stack = player.getItemInHand(hand);
        LoadedItemStack loadedItems = getLoadedItems(stack);
        if (!loadedItems.isEmpty()) {
            this.pullTrigger(level, player, hand, stack, null);
            return InteractionResultHolder.consume(stack);
        } else {
            stack.set(ClinkerDataComponents.FIRING.get(), false);

            InteractionHand oppositeHand = hand == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            ItemStack oppositeHandStack = player.getItemInHand(oppositeHand);
            if (!oppositeHandStack.isEmpty()) {
                stack.set(ClinkerDataComponents.TICK_DELAY.get(), 0);
                player.startUsingItem(hand);
                return InteractionResultHolder.consume(stack);
            } else {
                player.displayClientMessage(Component.translatable("item.clinker.alchemists_crossbow.no_ammo").withStyle(Style.EMPTY.withItalic(true).withColor(ChatFormatting.GRAY)), true);
                return InteractionResultHolder.fail(stack);
            }
        }
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack crossbow, int remainingUseDuration) {
        super.onUseTick(level, livingEntity, crossbow, remainingUseDuration);

        if (isRepeatFiring(crossbow)) {
            tickRepeaterFire(crossbow, livingEntity, level, livingEntity.getUsedItemHand());
        } else {
            loadItemsTick(level, livingEntity, crossbow);
        }
    }

    // FIRING
    protected void pullTrigger(Level level, LivingEntity shooter, InteractionHand hand, ItemStack crossbow, @Nullable LivingEntity target) {
        if (level instanceof ServerLevel serverlevel) {
            ItemStack ammo = getLoadedItems(crossbow).stack();
            if (!ammo.isEmpty()) {
                boolean repeater = hasRepeater(crossbow);
                if (!repeater || ammo.getCount() == 1) {
                    this.fire(serverlevel, shooter, hand, crossbow, ammo, target);
                } else {
                    crossbow.set(ClinkerDataComponents.FIRING.get(), true);
                    // will fire immediately
                    crossbow.set(ClinkerDataComponents.TICK_DELAY.get(), TICKS_BETWEEN_SHOTS);
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
        this.shoot(level, shooter, hand, crossbow, List.of(ammo), 1.5F, ammo.getCount() <= 1 ? 0 : 1.5F, shooter instanceof Player, target);
        int count = ammo.getCount();
        crossbow.set(ClinkerDataComponents.LOADED_ITEM_STACK.get(), new LoadedItemStack(count <= 1 ? ItemStack.EMPTY : ammo.copyWithCount(count - 1)));
        crossbow.set(ClinkerDataComponents.TICK_DELAY.get(), 0);
        level.playSound(null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.CROSSBOW_SHOOT, shooter instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE, 1, 1);
    }

    private void tickRepeaterFire(ItemStack crossbow, LivingEntity living, Level level, InteractionHand hand) {
        TICKS_BETWEEN_SHOTS = 2;
        int ticksSinceLastShot = tickDelay(crossbow);
        if (ticksSinceLastShot >= TICKS_BETWEEN_SHOTS) {
            if (level instanceof ServerLevel serverLevel) {
                ItemStack ammo = getLoadedItems(crossbow).stack();
                if (ammo.isEmpty()) {
                    // play an "out of ammo" click!
                    serverLevel.playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.DISPENSER_FAIL, living instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE, 1, 1);
                    crossbow.set(ClinkerDataComponents.TICK_DELAY.get(), 0);
                } else {
                    this.fire(serverLevel, living, hand, crossbow, ammo, null);
                }
            }
        } else {
            crossbow.set(ClinkerDataComponents.TICK_DELAY.get(), ticksSinceLastShot + 1);
        }
    }


    @Override
    protected Projectile createProjectile(Level level, LivingEntity shooter, ItemStack weapon, ItemStack ammo, boolean isCrit) {
        if (ammo.getItem() instanceof ArrowItem arrowItem) {
            return arrowItem.createArrow(level, ammo, shooter, weapon);
        }
        if (ammo.getItem() instanceof ProjectileItem projectileItem) {
            return projectileItem.asProjectile(level, shooter.getEyePosition(), ammo, Direction.UP);
        }
        return new Snowball(level, shooter);
    }

    @Override
    protected void shootProjectile(LivingEntity shooter, Projectile projectile, int index, float velocity, float inaccuracy, float angle, @Nullable LivingEntity target) {
        projectile.shootFromRotation(shooter, shooter.getXRot(), shooter.getYRot(), 0.0F, velocity, inaccuracy);
    }

    // LOADING
    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        if (isRepeatFiring(stack)) return 72000;
        return 16 * ITEM_LOAD_TIME + 3;
    }

    @Override
    public void releaseUsing(ItemStack crossbow, Level level, LivingEntity livingEntity, int timeCharged) {
        if (isRepeatFiring(crossbow)) {
            crossbow.set(ClinkerDataComponents.TICK_DELAY.get(), 0);
            crossbow.set(ClinkerDataComponents.FIRING.get(), false);
        }
        if (livingEntity instanceof Player player) player.getCooldowns().addCooldown(this, 20);
        super.releaseUsing(crossbow, level, livingEntity, timeCharged);
    }

    protected void loadItemsTick(Level level, LivingEntity livingEntity, ItemStack crossbow) {
        if (isFull(crossbow)) {
            livingEntity.releaseUsingItem();
        } else {
            ItemStack ammo = livingEntity.getItemInHand(livingEntity.getUsedItemHand() == InteractionHand.MAIN_HAND ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND);
            int ticksSinceLastLoad = tickDelay(crossbow);
            if (ticksSinceLastLoad >= ITEM_LOAD_TIME) {
                if (!loadAmmo(level, livingEntity, crossbow, ammo)) {
                    livingEntity.releaseUsingItem();
                }
            } else {
                crossbow.set(ClinkerDataComponents.TICK_DELAY.get(), ticksSinceLastLoad + 1);
            }
        }
    }

    protected boolean loadAmmo(Level level, LivingEntity living, ItemStack crossbow, ItemStack ammo) {
        if (ammo == null || ammo.isEmpty())
            return false;
        if (isFull(crossbow))
            return false;
        ItemStack currentlyLoadedItems = getLoadedItems(crossbow).stack();
        if (!currentlyLoadedItems.isEmpty() && !ammo.is(currentlyLoadedItems.getItem()))
            return false;

        crossbow.set(ClinkerDataComponents.LOADED_ITEM_STACK.get(), new LoadedItemStack(ammo.copyWithCount(currentlyLoadedItems.getCount() + 1)));
        crossbow.set(ClinkerDataComponents.TICK_DELAY.get(), 0);
        if (!level.isClientSide())
            level.playSound(null, living.getX(), living.getY(), living.getZ(), SoundEvents.CROSSBOW_LOADING_MIDDLE, living instanceof Player ? SoundSource.PLAYERS : SoundSource.HOSTILE, 1, 1);
//        if (living instanceof Player player && player.getAbilities().instabuild)
//            return true;
        ammo.shrink(1);
        return true;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.CROSSBOW;
    }
}
