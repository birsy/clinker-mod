package birsy.clinker.common.world.entity.gnomad;

import birsy.clinker.common.world.entity.MudScarabEntity;
import birsy.clinker.common.world.entity.gnomad.GnomadAi.GnomadAxemanAi;
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
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.SmoothSwimmingMoveControl;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;

import javax.annotation.Nullable;
import java.util.HashMap;

@Deprecated
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

    private static final EntityDataAccessor<Integer>[] DATA_ARMOR_IDS = Util.make(new EntityDataAccessor[GnomadArmor.GnomadArmorLocation.values().length], (array) -> {
        for (int i = 0; i < array.length; i++) {
            array[i] = SynchedEntityData.defineId(GnomadAxemanEntity.class, EntityDataSerializers.INT);
        }
    });
    public HashMap<GnomadArmor.GnomadArmorLocation, GnomadArmor> WORN_ARMOR = Util.make(new HashMap<>(), (map) -> {
        for (GnomadArmor.GnomadArmorLocation armorLocation : GnomadArmor.GnomadArmorLocation.values()) {
            map.put(armorLocation, GnomadArmor.getArmorFromID(armorLocation, 0));
        }
    });

    public GnomadAxemanEntity(EntityType<? extends GnomadAxemanEntity> entityType, Level level) {
        super(entityType, level);
        this.setCanPickUpLoot(true);
        this.moveControl = new SmoothSwimmingMoveControl(this, 365, 25, 1000.0F, 1.0F, false);

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

    protected void registerGoals() {
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 0.2D, 0.001F));
        this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers());
        //this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_VARIANT_ID, 0);
        for (EntityDataAccessor<Integer> DATA_ID : DATA_ARMOR_IDS) {
            this.entityData.define(DATA_ID, 0);
        }
    }

    public void addAdditionalSaveData(CompoundTag pCompound) {
        super.addAdditionalSaveData(pCompound);
        pCompound.put("Inventory", this.inventory.createTag());
        pCompound.putInt("Variant", this.getVariant());

        CompoundTag armorTag = new CompoundTag();
        for (GnomadArmor.GnomadArmorLocation location : GnomadArmor.GnomadArmorLocation.values()) {
            armorTag.putInt(location.name, this.getEntityData().get(DATA_ARMOR_IDS[location.index]));
        }
        pCompound.put("Armor", armorTag);
    }
    public void readAdditionalSaveData(CompoundTag pCompound) {
        super.readAdditionalSaveData(pCompound);
        this.inventory.fromTag(pCompound.getList("Inventory", 10));
        this.setVariant(pCompound.getInt("Variant"));

        CompoundTag armorTag = pCompound.getCompound("Armor");
        for (GnomadArmor.GnomadArmorLocation location : GnomadArmor.GnomadArmorLocation.values()) {
            int armorID = armorTag.getInt(location.name);
            this.setArmor(location, GnomadArmor.getArmorFromID(location, armorID));
        }
    }

    @Override
    public SimpleContainer getInventory() {
        return this.inventory;
    }

    public void setVariant(int variant) {
        this.getEntityData().set(DATA_VARIANT_ID, variant);
    }
    public int getVariant() {
        return this.getEntityData().get(DATA_VARIANT_ID);
    }

    public void setArmor(GnomadArmor.GnomadArmorLocation location, GnomadArmor armor) {
        this.getEntityData().set(DATA_ARMOR_IDS[location.index], armor.id);

        AttributeInstance armorAttribute = this.getAttribute(Attributes.ARMOR);
        AttributeInstance speedAttribute = this.getAttribute(Attributes.MOVEMENT_SPEED);
        AttributeInstance knockbackAttribute = this.getAttribute(Attributes.KNOCKBACK_RESISTANCE);

        if (armor.id != 0) {
            this.playSound(SoundEvents.ARMOR_EQUIP_IRON, 0.5F, 0.5F);
        }

        armorAttribute.removeModifier(armor.armorModifier.getId());
        speedAttribute.removeModifier(armor.speedModifier.getId());
        knockbackAttribute.removeModifier(armor.knockbackModifier.getId());

        armorAttribute.addTransientModifier(armor.armorModifier);
        speedAttribute.addTransientModifier(armor.speedModifier);
        knockbackAttribute.addTransientModifier(armor.knockbackModifier);
    }

    public GnomadArmor getArmor(GnomadArmor.GnomadArmorLocation location) {
        return GnomadArmor.getArmorFromID(location, this.getEntityData().get(DATA_ARMOR_IDS[location.index]));

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
        this.level().getProfiler().push("gnomadBrain");
        this.getBrain().tick((ServerLevel)this.level(), this);
        this.level().getProfiler().pop();
        GnomadAxemanAi.updateActivity(this);
        super.customServerAiStep();
    }
    public static AttributeSupplier.Builder createAttributes() {
        return createMonsterAttributes()
                .add(Attributes.MAX_HEALTH, 20.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.02D)
                .add(Attributes.ATTACK_DAMAGE, 2.0D);
    }

    public void playSound(SoundEvent event) {
        this.playSound(event, this.getSoundVolume(), this.getVoicePitch());
    }

    @Override
    public void tick() {
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

        this.setArmor(GnomadArmor.GnomadArmorLocation.HELMET, pLevel.getRandom().nextFloat() < 0.1F ? GnomadArmor.HELMET_HAT : GnomadArmor.HELMET_EMPTY);
        for (GnomadArmor.GnomadArmorLocation armorLocation : GnomadArmor.GnomadArmorLocation.values()) {
            this.setArmor(armorLocation, GnomadArmor.getRandomArmorPiece(armorLocation, pLevel.getRandom()));
        }

        this.setVariant(pLevel.getRandom().nextInt(GnomadAxemanEntity.VARIANT_AMOUNT));

        return super.finalizeSpawn(pLevel, pDifficulty, pReason, pSpawnData, pDataTag);
    }
}
