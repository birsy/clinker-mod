package birsy.clinker.common.entity;

import birsy.clinker.common.entity.ai.GnomadAi.GnomadAxemanAi;
import birsy.clinker.core.Clinker;
import birsy.clinker.core.registry.ClinkerItems;
import birsy.clinker.core.util.MathUtils;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Dynamic;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GnomadAxemanEntity extends AbstractGnomadEntity implements InventoryCarrier {
    protected static final ImmutableList<SensorType<? extends Sensor<? super GnomadAxemanEntity>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.NEAREST_ITEMS,
            SensorType.HURT_BY);
    protected static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.DOORS_TO_CLOSE,
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.INTERACTION_TARGET,
            MemoryModuleType.PATH,
            MemoryModuleType.HURT_BY,
            MemoryModuleType.HURT_BY_ENTITY,
            MemoryModuleType.ANGRY_AT,
            MemoryModuleType.UNIVERSAL_ANGER,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.ATTACK_COOLING_DOWN,
            MemoryModuleType.AVOID_TARGET,
            MemoryModuleType.CELEBRATE_LOCATION,
            MemoryModuleType.DANCING,
            MemoryModuleType.HUNTED_RECENTLY,
            MemoryModuleType.NEAREST_VISIBLE_NEMESIS,
            MemoryModuleType.RIDE_TARGET,
            MemoryModuleType.ATE_RECENTLY);

    public static int VARIANT_AMOUNT = 2;
    private static final EntityDataAccessor<Integer> DATA_VARIANT_ID = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.INT);
    /*
    slot 0 : right hand tool sheathe
    slot 1 : left hand tool sheathe
    slots 2 - 5 : potions
    slots 6 - 7 : misc.
     */
    private final SimpleContainer inventory = new SimpleContainer(8);

    private static final EntityDataAccessor<Boolean> DATA_HELMET_ID = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_HELMET_VISOR_ID = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_LEFT_PAULDRON_ID = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> DATA_RIGHT_PAULDRON_ID = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.BOOLEAN);

    private static final AttributeModifier ARMOR_MODIFIER_HELMET =     new AttributeModifier(UUID.randomUUID(), "Helmet Armor Bonus",       3.0F,  AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier SPEED_MODIFIER_HELMET =     new AttributeModifier(UUID.randomUUID(), "Helmet Speed Negation",    -0.0025F, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier KNOCKBACK_MODIFIER_HELMET = new AttributeModifier(UUID.randomUUID(), "Helmet Knockback Negation",0.0125F, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final AttributeModifier ARMOR_MODIFIER_VISOR =     new AttributeModifier(UUID.randomUUID(), "Helmet Visor Armor Bonus",       1.0,  AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier SPEED_MODIFIER_VISOR =     new AttributeModifier(UUID.randomUUID(), "Helmet Visor Speed Negation",    -0.004F, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier KNOCKBACK_MODIFIER_VISOR = new AttributeModifier(UUID.randomUUID(), "Helmet Visor Knockback Negation",0.2F, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final AttributeModifier ARMOR_MODIFIER_LEFT_PAULDRON =     new AttributeModifier(UUID.randomUUID(), "Left Pauldron Armor Bonus",       1.0, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier SPEED_MODIFIER_LEFT_PAULDRON =     new AttributeModifier(UUID.randomUUID(), "Left Pauldron Speed Negation",    -0.0025F, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier KNOCKBACK_MODIFIER_LEFT_PAULDRON = new AttributeModifier(UUID.randomUUID(), "Left Pauldron Knockback Negation",0.0125F, AttributeModifier.Operation.MULTIPLY_BASE);
    private static final AttributeModifier ARMOR_MODIFIER_RIGHT_PAULDRON =     new AttributeModifier(UUID.randomUUID(), "Right Pauldron Armor Bonus",       1.0, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier SPEED_MODIFIER_RIGHT_PAULDRON =     new AttributeModifier(UUID.randomUUID(), "Right Pauldron Speed Negation",    -0.0025F, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier KNOCKBACK_MODIFIER_RIGHT_PAULDRON = new AttributeModifier(UUID.randomUUID(), "Right Pauldron Knockback Negation",0.0125F, AttributeModifier.Operation.MULTIPLY_BASE);

    public GnomadAxemanEntity(EntityType<? extends GnomadAxemanEntity> entityType, Level level) {
        super(entityType, level);
        this.setCanPickUpLoot(true);

        //Placeholder, for now.
        //TODO: Make them spawn with an actual inventory :P
        this.inventory.setItem(0, ClinkerItems.LEAD_AXE.get().getDefaultInstance());
        this.inventory.setItem(1, ClinkerItems.LEAD_SWORD.get().getDefaultInstance());
        this.inventory.setItem(2, Items.GLASS_BOTTLE.getDefaultInstance());
        this.inventory.setItem(3, Items.HONEY_BOTTLE.getDefaultInstance());
        this.inventory.setItem(4, Items.LINGERING_POTION.getDefaultInstance());
        this.inventory.setItem(5, Items.SPLASH_POTION.getDefaultInstance());
        this.inventory.setItem(6, ClinkerItems.GNOMEAT_JERKY.get().getDefaultInstance());
        this.inventory.setItem(7, Items.COOKED_BEEF.getDefaultInstance());
    }

    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT_ID, 0);
        this.entityData.define(DATA_HELMET_ID, false);
        this.entityData.define(DATA_HELMET_VISOR_ID,   false);
        this.entityData.define(DATA_LEFT_PAULDRON_ID,  false);
        this.entityData.define(DATA_RIGHT_PAULDRON_ID, false);
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.put("Inventory", this.inventory.createTag());
        pCompound.putInt("Variant", this.getVariant());

        pCompound.putBoolean("Helmet", this.getArmor()[0]);
        pCompound.putBoolean("Helmet Visor", this.getArmor()[1]);
        pCompound.putBoolean("Left Pauldron", this.getArmor()[2]);
        pCompound.putBoolean("Right Pauldron", this.getArmor()[3]);

    }
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.inventory.fromTag(pCompound.getList("Inventory", 10));
        this.setVariant(pCompound.getInt("Variant"));
        this.setArmor(pCompound.getBoolean("Helmet"), pCompound.getBoolean("Helmet Visor"), pCompound.getBoolean("Left Pauldron"), pCompound.getBoolean("Right Pauldron"));
    }

    @Override
    public Container getInventory() {
        return this.inventory;
    }

    public void setVariant(int variant) {
        this.getEntityData().set(DATA_VARIANT_ID, variant);
    }
    public int getVariant() {
        return this.getEntityData().get(DATA_VARIANT_ID);
    }

    public void setArmor(boolean helmet, boolean visor, boolean leftPauldron, boolean rightPauldron) {
        this.getEntityData().set(DATA_HELMET_ID, helmet);
        this.getEntityData().set(DATA_HELMET_VISOR_ID,  visor);
        this.getEntityData().set(DATA_LEFT_PAULDRON_ID,  leftPauldron);
        this.getEntityData().set(DATA_RIGHT_PAULDRON_ID, rightPauldron);

        AttributeInstance armor = this.getAttribute(Attributes.ARMOR);
        AttributeInstance speed = this.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance knockback = this.getAttribute(Attributes.KNOCKBACK_RESISTANCE);

        if (helmet || visor || leftPauldron || rightPauldron) {
            this.playSound(SoundEvents.ARMOR_EQUIP_IRON, 0.5F, 0.5F);
        }

        armor.removeModifier(ARMOR_MODIFIER_HELMET);
        speed.removeModifier(SPEED_MODIFIER_HELMET);
        knockback.removeModifier(KNOCKBACK_MODIFIER_HELMET);
        if (helmet) {
            armor.addTransientModifier(ARMOR_MODIFIER_HELMET);
            speed.addTransientModifier(SPEED_MODIFIER_HELMET);
            knockback.addTransientModifier(KNOCKBACK_MODIFIER_HELMET);
        }
        armor.removeModifier(ARMOR_MODIFIER_VISOR);
        speed.removeModifier(SPEED_MODIFIER_VISOR);
        knockback.removeModifier(KNOCKBACK_MODIFIER_VISOR);
        if (helmet && visor) {
            armor.addTransientModifier(ARMOR_MODIFIER_VISOR);
            speed.addTransientModifier(SPEED_MODIFIER_VISOR);
            knockback.addTransientModifier(KNOCKBACK_MODIFIER_VISOR);
        }
        armor.removeModifier(ARMOR_MODIFIER_LEFT_PAULDRON);
        speed.removeModifier(SPEED_MODIFIER_LEFT_PAULDRON);
        knockback.removeModifier(KNOCKBACK_MODIFIER_LEFT_PAULDRON);
        if (leftPauldron) {
            armor.addTransientModifier(ARMOR_MODIFIER_LEFT_PAULDRON);
            speed.addTransientModifier(SPEED_MODIFIER_LEFT_PAULDRON);
            knockback.addTransientModifier(KNOCKBACK_MODIFIER_LEFT_PAULDRON);
        }
        armor.removeModifier(ARMOR_MODIFIER_RIGHT_PAULDRON);
        speed.removeModifier(SPEED_MODIFIER_RIGHT_PAULDRON);
        knockback.removeModifier(KNOCKBACK_MODIFIER_RIGHT_PAULDRON);
        if (rightPauldron) {
            armor.addTransientModifier(ARMOR_MODIFIER_RIGHT_PAULDRON);
            speed.addTransientModifier(SPEED_MODIFIER_RIGHT_PAULDRON);
            knockback.addTransientModifier(KNOCKBACK_MODIFIER_RIGHT_PAULDRON);
        }
    }
    public boolean[] getArmor() {
        return new boolean[]{this.entityData.get(DATA_HELMET_ID), this.entityData.get(DATA_HELMET_VISOR_ID), this.entityData.get(DATA_LEFT_PAULDRON_ID), this.entityData.get(DATA_RIGHT_PAULDRON_ID)};
    }

    @Override
    public LivingEntity getTarget() {
        return this.brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
    }
    public Brain.Provider<GnomadAxemanEntity> brainProvider() {
        return Brain.provider(MEMORY_TYPES, SENSOR_TYPES);
    }
    public Brain<?> makeBrain(Dynamic<?> pDynamic) { return GnomadAxemanAi.makeBrain(this, this.brainProvider().makeBrain(pDynamic)); }
    public Brain<GnomadAxemanEntity> getBrain() {
        return (Brain<GnomadAxemanEntity>)super.getBrain();
    }
    protected void customServerAiStep() {
        this.level.getProfiler().push("gnomadBrain");
        this.getBrain().tick((ServerLevel)this.level, this);
        this.level.getProfiler().pop();
        GnomadAxemanAi.updateActivity(this);
        super.customServerAiStep();
    }
    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.02D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    public void playSound(SoundEvent event) {
        this.playSound(event, this.getSoundVolume(), this.getVoicePitch());
    }

    @Override
    public void tick() {
        //this.setSitting(true);
        //this.setDeltaMovement(0.0, this.getDeltaMovement().y, 0.0);
        super.tick();
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource pSource, int pLooting, boolean pRecentlyHit) {
        super.dropCustomDeathLoot(pSource, pLooting, pRecentlyHit);
        /*
        for (ItemStack itemstack : inventory.removeAllItems()) {
            if (!EnchantmentHelper.hasVanishingCurse(itemstack) && (pRecentlyHit)) {
                if (itemstack.isDamageableItem()) {
                    itemstack.setDamageValue(itemstack.getMaxDamage() - this.random.nextInt(1 + this.random.nextInt(Math.max(itemstack.getMaxDamage() - 3, 1))));
                }

                this.spawnAtLocation(itemstack);
            }
        }*/
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor pLevel, DifficultyInstance pDifficulty, MobSpawnType pReason, @Nullable SpawnGroupData pSpawnData, @Nullable CompoundTag pDataTag) {
        float gnomePower = MathUtils.bias(pLevel.getRandom().nextFloat(), 0.1F);
        float difficultyValue = (pDifficulty.getEffectiveDifficulty() / 3.0F) * gnomePower;
        boolean hasHelmet = pLevel.getRandom().nextFloat() < difficultyValue;
        this.setArmor(hasHelmet, hasHelmet && pLevel.getRandom().nextFloat() < difficultyValue, pLevel.getRandom().nextFloat() < difficultyValue, pLevel.getRandom().nextFloat() < difficultyValue);
        this.setVariant(pLevel.getRandom().nextInt(GnomadAxemanEntity.VARIANT_AMOUNT));

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }


}
