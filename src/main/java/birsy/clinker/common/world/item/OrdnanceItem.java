package birsy.clinker.common.world.item;

import birsy.clinker.client.sound.OrdnanceFuseSoundInstance;
import birsy.clinker.common.world.components.FuseTimer;
import birsy.clinker.common.world.entity.projectile.OrdnanceEntity;
import birsy.clinker.common.world.ordnance.OrdnanceHelper;
import birsy.clinker.common.world.ordnance.OrdnanceModifierSet;
import birsy.clinker.common.world.ordnance.modifiers.FuseTimeModifier;
import birsy.clinker.core.registry.ClinkerDataComponents;
import birsy.clinker.core.registry.ClinkerOrdnanceModifierTypes;
import birsy.clinker.core.registry.ClinkerTags;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.tslat.smartbrainlib.util.RandomUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class OrdnanceItem extends Item implements ProjectileItem {
    public static Map<UUID, OrdnanceFuseSoundInstance> sounds = new HashMap<>();

    public OrdnanceItem(Properties pProperties) {
        super(pProperties);
    }

    private static OrdnanceModifierSet getOrdnanceModifiers(ItemStack stack) {
        return stack.getOrDefault(ClinkerDataComponents.ORDNANCE_MODIFIERS, OrdnanceModifierSet.NONE);
    }

    private static long ticksSinceOpening(ItemStack stack, Level level) {
        if (stack.has(ClinkerDataComponents.FUSE_TIMER))
            return stack.get(ClinkerDataComponents.FUSE_TIMER).ticksSinceOpening(level);
        return 0;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        OrdnanceModifierSet set = getOrdnanceModifiers(stack);

        if (ticksSinceOpening(stack, context.level()) > 1 && set.hasModifier(ClinkerTags.OrdnanceModifiers.DETONATES)) {
            tooltipComponents.add(
                    Component.translatable("ordnance_modifier.clinker.about_to_explode")
                             .withStyle(Style.EMPTY.withItalic(true).withColor(0xAF7A00))
            );
            tooltipComponents.add(
                    Component.translatable("ordnance_modifier.clinker.about_to_explode_description")
                             .withStyle(Style.EMPTY.withItalic(true).withColor(0xAF7A00))
            );
            tooltipComponents.add(Component.empty());
        }

        set.addToTooltip(context, tooltipComponents::add, tooltipFlag);
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack stack = pPlayer.getItemInHand(pHand);
        pPlayer.startUsingItem(pHand);

        OrdnanceModifierSet set = getOrdnanceModifiers(stack);
        FuseTimeModifier fuseTimeModifier = set.getModifier(ClinkerOrdnanceModifierTypes.FUSE_TIME.get());

        // if we have a fuse, but not a fuse timer, then light a new fuse timer!
        if (fuseTimeModifier != null && !stack.has(ClinkerDataComponents.FUSE_TIMER)) {
            pLevel.playSound(null,
                    pPlayer.getX(), pPlayer.getY(), pPlayer.getZ(),
                    SoundEvents.TNT_PRIMED, SoundSource.PLAYERS,
                    0.5F, (float) RandomUtil.randomValueBetween(1.3, 1.5)
            );
            stack.set(ClinkerDataComponents.FUSE_TIMER.get(), new FuseTimer(pLevel));
        }

        return InteractionResultHolder.consume(stack);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        OrdnanceModifierSet set = getOrdnanceModifiers(stack);
        FuseTimeModifier fuseTimeModifier = set.getModifier(ClinkerOrdnanceModifierTypes.FUSE_TIME.get());
        // if we have a fuse and our bomb detonates, then we can only hold it until it detonates in our hand.
        if (fuseTimeModifier != null && set.hasModifier(ClinkerTags.OrdnanceModifiers.DETONATES))
            return fuseTimeModifier.getFuseTicks() + 1;
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level pLevel, LivingEntity pEntityLiving, int pTimeLeft) {
        OrdnanceModifierSet set = getOrdnanceModifiers(stack);

        // don't release something if it should actually be detonating right now!
        FuseTimeModifier fuseTimeModifier = set.getModifier(ClinkerOrdnanceModifierTypes.FUSE_TIME.get());
        if (fuseTimeModifier != null && ticksSinceOpening(stack, pLevel) >= fuseTimeModifier.getFuseTicks() && set.hasModifier(ClinkerTags.OrdnanceModifiers.DETONATES)) {
            return;
        }

        pLevel.playSound(null,
                pEntityLiving.getX(), pEntityLiving.getY(), pEntityLiving.getZ(),
                SoundEvents.TRIDENT_THROW, SoundSource.PLAYERS,
                0.5F, 0.4F / (pLevel.getRandom().nextFloat() * 0.4F + 0.8F)
        );

        if (!pLevel.isClientSide) {
            OrdnanceEntity ordnance = OrdnanceEntity.toss(pLevel, pEntityLiving);
            ordnance.setModifiers(set);
            ordnance.setFuseTime((int) ticksSinceOpening(stack, pLevel));
            pLevel.addFreshEntity(ordnance);
        }

        completeUsage(pEntityLiving, stack);
    }

    private void completeUsage(Entity entity, ItemStack stack) {
        if (entity instanceof Player player) {
            player.awardStat(Stats.ITEM_USED.get(this));
            if (!player.getAbilities().instabuild) stack.shrink(1);
            player.getCooldowns().addCooldown(this, 40);
        } else {
            stack.shrink(1);
        }
        // this particular stack has been "used," prevent it from detonating again.
        stack.remove(ClinkerDataComponents.FUSE_TIMER.get());
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.BOW;
    }

    @Override
    public void inventoryTick(ItemStack stack, Level pLevel, Entity pEntity, int pSlotId, boolean pIsSelected) {
        super.inventoryTick(stack, pLevel, pEntity, pSlotId, pIsSelected);

        if (!stack.has(ClinkerDataComponents.FUSE_TIMER.get())) return;

        OrdnanceModifierSet set = getOrdnanceModifiers(stack);
        FuseTimeModifier fuseTimeModifier = set.getModifier(ClinkerOrdnanceModifierTypes.FUSE_TIME.get());
        if (fuseTimeModifier != null && ticksSinceOpening(stack, pLevel) == fuseTimeModifier.getFuseTicks()) {
            // kaboom!
            if (set.hasModifier(ClinkerTags.OrdnanceModifiers.DETONATES) && !pLevel.isClientSide()) {
                Vec3 lookVec = pEntity.calculateViewVector(0.0F, pEntity.getYHeadRot());
                OrdnanceHelper.detonate(set,
                        pEntity.getX() + lookVec.x() * 0.25F,
                        pEntity.getY() + pEntity.getBbHeight() * 0.5,
                        pEntity.getZ() + lookVec.z() * 0.25, pLevel,
                        null, pEntity
                );

                // stop holding it
                if (pEntity instanceof Player player && player.isUsingItem() && pIsSelected)
                    player.releaseUsingItem();
                // and decrement it etc
                this.completeUsage(pEntity, stack);
            } else {
                // it's a dud...
                pLevel.playSound(null,
                        pEntity.getX(), pEntity.getY(), pEntity.getZ(),
                        SoundEvents.SQUID_SQUIRT, SoundSource.PLAYERS,
                        0.5F, 1.5F
                );
            }
        } else if (fuseTimeModifier == null && !pLevel.isClientSide()) {
            // invalid state! checked on server, so there's no weird desync...
            stack.remove(ClinkerDataComponents.FUSE_TIMER.get());
        }
    }

    // dispenser stuff
    @Override
    public void shoot(Projectile projectile, double x, double y, double z, float velocity, float inaccuracy) {
        projectile.shoot(x, y, z, velocity, inaccuracy);
    }

    @Override
    public Projectile asProjectile(Level level, Position pos, ItemStack stack, Direction direction) {
        OrdnanceModifierSet modifiers = getOrdnanceModifiers(stack);
        OrdnanceEntity ordnance = OrdnanceEntity.create(level, pos.x(), pos.y(), pos.z());
        ordnance.setModifiers(modifiers);
        ordnance.setFuseTime((int) ticksSinceOpening(stack, level));
        return ordnance;
    }

    @Override
    public DispenseConfig createDispenseConfig() {
        return ProjectileItem.super.createDispenseConfig();
    }
}
