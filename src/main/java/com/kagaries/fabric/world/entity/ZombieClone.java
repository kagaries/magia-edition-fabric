package com.kagaries.fabric.world.entity;

import com.kagaries.fabric.world.entity.ai.goal.ZombieCloneAttackGoal;
import com.kagaries.fabric.world.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.NavigationConditions;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.ai.pathing.MobNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.time.LocalDate;
import java.time.temporal.ChronoField;
import java.util.List;
import java.util.UUID;
import java.util.function.Predicate;

public class ZombieClone extends HostileEntity {
    private static final UUID BABY_SPEED_ID = UUID.fromString("B9766B59-9566-4402-BC1F-2EE2A276D836");
    private static final EntityAttributeModifier BABY_SPEED_BONUS;
    private static final TrackedData<Boolean> BABY;
    private static final TrackedData<Integer> ZOMBIE_TYPE;
    private static final TrackedData<Boolean> CONVERTING_IN_WATER;
    public static final float field_30519 = 0.05F;
    public static final int field_30515 = 50;
    public static final int field_30516 = 40;
    public static final int field_30517 = 7;
    protected static final float field_41028 = 0.81F;
    private static final float field_30518 = 0.1F;
    private static final Predicate<Difficulty> DOOR_BREAK_DIFFICULTY_CHECKER;
    private final BreakDoorGoal breakDoorsGoal;
    private boolean canBreakDoors;
    private int inWaterTime;
    private int ticksUntilWaterConversion;

    public ZombieClone(EntityType<? extends ZombieClone> entityType, World world) {
        super(entityType, world);
        this.breakDoorsGoal = new BreakDoorGoal(this, DOOR_BREAK_DIFFICULTY_CHECKER);
    }

    protected void initGoals() {
        this.goalSelector.add(4, new ZombieClone.DestroyEggGoal(this, 1.0, 3));
        this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
        this.goalSelector.add(8, new LookAroundGoal(this));
        this.initCustomGoals();
    }

    protected void initCustomGoals() {
        this.goalSelector.add(2, new ZombieCloneAttackGoal(this, 1.0, false));
        this.goalSelector.add(6, new MoveThroughVillageGoal(this, 1.0, true, 4, this::canBreakDoors));
        this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0));
        this.targetSelector.add(1, (new RevengeGoal(this, new Class[0])).setGroupRevenge(new Class[]{ZombifiedPiglinEntity.class}));
        this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
        this.targetSelector.add(3, new ActiveTargetGoal(this, MerchantEntity.class, false));
        this.targetSelector.add(3, new ActiveTargetGoal(this, IronGolemEntity.class, true));
        this.targetSelector.add(5, new ActiveTargetGoal(this, TurtleEntity.class, 10, true, false, TurtleEntity.BABY_TURTLE_ON_LAND_FILTER));
    }

    public static DefaultAttributeContainer.Builder createZombieAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 35.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23000000417232513).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 3.0).add(EntityAttributes.GENERIC_ARMOR, 2.0).add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.getDataTracker().startTracking(BABY, false);
        this.getDataTracker().startTracking(ZOMBIE_TYPE, 0);
        this.getDataTracker().startTracking(CONVERTING_IN_WATER, false);
    }

    public boolean isConvertingInWater() {
        return (Boolean)this.getDataTracker().get(CONVERTING_IN_WATER);
    }

    public boolean canBreakDoors() {
        return this.canBreakDoors;
    }

    public void setCanBreakDoors(boolean canBreakDoors) {
        if (this.shouldBreakDoors() && NavigationConditions.hasMobNavigation(this)) {
            if (this.canBreakDoors != canBreakDoors) {
                this.canBreakDoors = canBreakDoors;
                ((MobNavigation)this.getNavigation()).setCanPathThroughDoors(canBreakDoors);
                if (canBreakDoors) {
                    this.goalSelector.add(1, this.breakDoorsGoal);
                } else {
                    this.goalSelector.remove(this.breakDoorsGoal);
                }
            }
        } else if (this.canBreakDoors) {
            this.goalSelector.remove(this.breakDoorsGoal);
            this.canBreakDoors = false;
        }

    }

    protected boolean shouldBreakDoors() {
        return true;
    }

    public boolean isBaby() {
        return (Boolean)this.getDataTracker().get(BABY);
    }

    public int getXpToDrop() {
        if (this.isBaby()) {
            this.experiencePoints = (int)((double)this.experiencePoints * 2.5);
        }

        return super.getXpToDrop();
    }

    public void setBaby(boolean baby) {
        this.getDataTracker().set(BABY, baby);
        if (this.getWorld() != null && !this.getWorld().isClient) {
            EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
            entityAttributeInstance.removeModifier(BABY_SPEED_BONUS.getId());
            if (baby) {
                entityAttributeInstance.addTemporaryModifier(BABY_SPEED_BONUS);
            }
        }

    }

    public void onTrackedDataSet(TrackedData<?> data) {
        if (BABY.equals(data)) {
            this.calculateDimensions();
        }

        super.onTrackedDataSet(data);
    }

    protected boolean canConvertInWater() {
        return true;
    }

    public void tick() {
        super.tick();
    }

    public void tickMovement() {
        if (this.isAlive()) {
            boolean bl = this.burnsInDaylight() && this.isAffectedByDaylight();
            if (bl) {
                ItemStack itemStack = this.getEquippedStack(EquipmentSlot.HEAD);
                if (!itemStack.isEmpty()) {
                    if (itemStack.isDamageable()) {
                        itemStack.setDamage(itemStack.getDamage() + this.random.nextInt(2));
                        if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
                            this.sendEquipmentBreakStatus(EquipmentSlot.HEAD);
                            this.equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }

                    bl = false;
                }

                if (bl) {
                    this.setOnFireFor(8);
                }
            }
        }

        super.tickMovement();
    }

    private void setTicksUntilWaterConversion(int ticksUntilWaterConversion) {
        this.ticksUntilWaterConversion = ticksUntilWaterConversion;
        this.getDataTracker().set(CONVERTING_IN_WATER, true);
    }

    protected void convertInWater() {
        this.convertTo(EntityType.DROWNED);
        if (!this.isSilent()) {
            this.getWorld().syncWorldEvent((PlayerEntity)null, 1040, this.getBlockPos(), 0);
        }

    }

    protected void convertTo(EntityType<? extends HostileEntity> entityType) {
        ZombieClone zombieEntity = (ZombieClone)this.convertTo(entityType, true);
        if (zombieEntity != null) {
            zombieEntity.applyAttributeModifiers(zombieEntity.getWorld().getLocalDifficulty(zombieEntity.getBlockPos()).getClampedLocalDifficulty());
            zombieEntity.setCanBreakDoors(zombieEntity.shouldBreakDoors() && this.canBreakDoors());
        }

    }

    protected boolean burnsInDaylight() {
        return true;
    }

    public boolean damage(DamageSource source, float amount) {
        if (!super.damage(source, amount)) {
            return false;
        } else if (!(this.getWorld() instanceof ServerWorld)) {
            return false;
        } else {
            ServerWorld serverWorld = (ServerWorld)this.getWorld();
            LivingEntity livingEntity = this.getTarget();
            if (livingEntity == null && source.getAttacker() instanceof LivingEntity) {
                livingEntity = (LivingEntity)source.getAttacker();
            }

            if (livingEntity != null && this.getWorld().getDifficulty() == Difficulty.HARD && (double)this.random.nextFloat() < this.getAttributeValue(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS) && this.getWorld().getGameRules().getBoolean(GameRules.DO_MOB_SPAWNING)) {
                int i = MathHelper.floor(this.getX());
                int j = MathHelper.floor(this.getY());
                int k = MathHelper.floor(this.getZ());
                ZombieEntity zombieEntity = new ZombieEntity(this.getWorld());

                for(int l = 0; l < 50; ++l) {
                    int m = i + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    int n = j + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    int o = k + MathHelper.nextInt(this.random, 7, 40) * MathHelper.nextInt(this.random, -1, 1);
                    BlockPos blockPos = new BlockPos(m, n, o);
                    EntityType<?> entityType = zombieEntity.getType();
                    SpawnRestriction.Location location = SpawnRestriction.getLocation(entityType);
                    if (SpawnHelper.canSpawn(location, this.getWorld(), blockPos, entityType) && SpawnRestriction.canSpawn(entityType, serverWorld, SpawnReason.REINFORCEMENT, blockPos, this.getWorld().random)) {
                        zombieEntity.setPosition((double)m, (double)n, (double)o);
                        if (!this.getWorld().isPlayerInRange((double)m, (double)n, (double)o, 7.0) && this.getWorld().doesNotIntersectEntities(zombieEntity) && this.getWorld().isSpaceEmpty(zombieEntity) && !this.getWorld().containsFluid(zombieEntity.getBoundingBox())) {
                            zombieEntity.setTarget(livingEntity);
                            zombieEntity.initialize(serverWorld, this.getWorld().getLocalDifficulty(zombieEntity.getBlockPos()), SpawnReason.REINFORCEMENT, (EntityData)null, (NbtCompound)null);
                            serverWorld.spawnEntityAndPassengers(zombieEntity);
                            this.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS).addPersistentModifier(new EntityAttributeModifier("Zombie reinforcement caller charge", -0.05000000074505806, EntityAttributeModifier.Operation.ADDITION));
                            zombieEntity.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS).addPersistentModifier(new EntityAttributeModifier("Zombie reinforcement callee charge", -0.05000000074505806, EntityAttributeModifier.Operation.ADDITION));
                            break;
                        }
                    }
                }
            }

            return true;
        }
    }

    public boolean tryAttack(Entity target) {
        boolean bl = super.tryAttack(target);
        if (bl) {
            float f = this.getWorld().getLocalDifficulty(this.getBlockPos()).getLocalDifficulty();
            if (this.getMainHandStack().isEmpty() && this.isOnFire() && this.random.nextFloat() < f * 0.3F) {
                target.setOnFireFor(2 * (int)f);
            }
        }

        return bl;
    }

    protected SoundEvent getAmbientSound() {
        return SoundEvents.ENTITY_ZOMBIE_AMBIENT;
    }

    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_ZOMBIE_HURT;
    }

    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_ZOMBIE_DEATH;
    }

    protected SoundEvent getStepSound() {
        return SoundEvents.ENTITY_ZOMBIE_STEP;
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        this.playSound(this.getStepSound(), 0.15F, 1.0F);
    }

    public EntityGroup getGroup() {
        return EntityGroup.UNDEAD;
    }

    protected void initEquipment(Random random, LocalDifficulty localDifficulty) {
        super.initEquipment(random, localDifficulty);
        if (random.nextFloat() < (this.getWorld().getDifficulty() == Difficulty.HARD ? 1F : 0.99F)) {
            int i = random.nextInt(20);
            if (i == 0) {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
                this.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
                this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(1.5f);
            } else if (i == 1) {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_AXE));
                this.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
                this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(0.5f);
            } else if (i == 2) {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_HOE));
                this.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
                this.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
            } else if (i == 3) {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.GOLDEN_SWORD));
                this.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
                this.equipStack(EquipmentSlot.FEET, new ItemStack(Items.CHAINMAIL_BOOTS));
                this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(2.5f);
            } else if (i == 4 || i == 5) {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SHOVEL));
                this.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
                this.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
                this.equipStack(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
                this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(4.5f);
            } else if (i == 6 || i == 7) {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_SWORD));
                this.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
                this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(0.5f);
            } else if (i == 8 || i == 9) {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(ModItems.EMERALD_AXE));
                this.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
                this.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
                this.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
                this.equipStack(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
                this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(0.15f);
            } else if (i == 10 || i == 11) {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(ModItems.EMERALD_AXE));
                this.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.CHAINMAIL_HELMET));
                this.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.CHAINMAIL_LEGGINGS));
                this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(-2f);
            } else if (i == 12 || i == 13) {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.STONE_SHOVEL));
                this.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.GOLDEN_HELMET));
                this.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.GOLDEN_CHESTPLATE));
                this.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.GOLDEN_LEGGINGS));
                this.equipStack(EquipmentSlot.FEET, new ItemStack(Items.GOLDEN_BOOTS));
                this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(2.5f);
            } else if (i == 14 || i == 15) {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.DIAMOND_HOE));
                this.equipStack(EquipmentSlot.HEAD, new ItemStack(Items.IRON_HELMET));
                this.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.IRON_CHESTPLATE));
                this.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.IRON_LEGGINGS));
                this.equipStack(EquipmentSlot.FEET, new ItemStack(Items.IRON_BOOTS));
                this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(0.5f);
            } else {
                this.equipStack(EquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SHOVEL));
                this.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.LEATHER_CHESTPLATE));
                this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(2.5f);
            }
        }

    }

    public void writeCustomDataToNbt(NbtCompound nbt) {
        super.writeCustomDataToNbt(nbt);
        nbt.putBoolean("IsBaby", this.isBaby());
        nbt.putBoolean("CanBreakDoors", this.canBreakDoors());
        nbt.putInt("InWaterTime", this.isTouchingWater() ? this.inWaterTime : -1);
        nbt.putInt("DrownedConversionTime", this.isConvertingInWater() ? this.ticksUntilWaterConversion : -1);
    }

    public void readCustomDataFromNbt(NbtCompound nbt) {
        super.readCustomDataFromNbt(nbt);
        this.setBaby(nbt.getBoolean("IsBaby"));
        this.setCanBreakDoors(nbt.getBoolean("CanBreakDoors"));
        this.inWaterTime = nbt.getInt("InWaterTime");
        if (nbt.contains("DrownedConversionTime", 99) && nbt.getInt("DrownedConversionTime") > -1) {
            this.setTicksUntilWaterConversion(nbt.getInt("DrownedConversionTime"));
        }

    }

    public boolean onKilledOther(ServerWorld world, LivingEntity other) {
        boolean bl = super.onKilledOther(world, other);
        if ((world.getDifficulty() == Difficulty.NORMAL || world.getDifficulty() == Difficulty.HARD) && other instanceof VillagerEntity villagerEntity) {
            if (world.getDifficulty() != Difficulty.HARD && this.random.nextBoolean()) {
                return bl;
            }

            ZombieVillagerEntity zombieVillagerEntity = (ZombieVillagerEntity)villagerEntity.convertTo(EntityType.ZOMBIE_VILLAGER, false);
            if (zombieVillagerEntity != null) {
                zombieVillagerEntity.initialize(world, world.getLocalDifficulty(zombieVillagerEntity.getBlockPos()), SpawnReason.CONVERSION, new ZombieEntity.ZombieData(false, true), (NbtCompound)null);
                zombieVillagerEntity.setVillagerData(villagerEntity.getVillagerData());
                zombieVillagerEntity.setGossipData((NbtElement)villagerEntity.getGossip().serialize(NbtOps.INSTANCE));
                zombieVillagerEntity.setOfferData(villagerEntity.getOffers().toNbt());
                zombieVillagerEntity.setXp(villagerEntity.getExperience());
                if (!this.isSilent()) {
                    world.syncWorldEvent((PlayerEntity)null, 1026, this.getBlockPos(), 0);
                }

                bl = false;
            }
        }

        return bl;
    }

    protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return this.isBaby() ? 0.93F : 1.74F;
    }

    public boolean canPickupItem(ItemStack stack) {
        return stack.isOf(Items.EGG) && this.isBaby() && this.hasVehicle() ? false : super.canPickupItem(stack);
    }

    public boolean canGather(ItemStack stack) {
        return stack.isOf(Items.GLOW_INK_SAC) ? false : super.canGather(stack);
    }

    @Nullable
    public EntityData initialize(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt) {
        Random random = world.getRandom();
        EntityData entityData1 = super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
        float f = difficulty.getClampedLocalDifficulty();
        this.setCanPickUpLoot(random.nextFloat() < 0.55F * f);
        if (entityData1 == null) {
            entityData1 = new ZombieEntity.ZombieData(shouldBeBaby(random), true);
        }

        if (entityData1 instanceof ZombieEntity.ZombieData zombieData) {
            if (zombieData.baby) {
                this.setBaby(true);
                if (zombieData.tryChickenJockey) {
                    if ((double)random.nextFloat() < 0.05) {
                        List<ChickenEntity> list = world.getEntitiesByClass(ChickenEntity.class, this.getBoundingBox().expand(5.0, 3.0, 5.0), EntityPredicates.NOT_MOUNTED);
                        if (!list.isEmpty()) {
                            ChickenEntity chickenEntity = (ChickenEntity)list.get(0);
                            chickenEntity.setHasJockey(true);
                            this.startRiding(chickenEntity);
                        }
                    } else if ((double)random.nextFloat() < 0.05) {
                        ChickenEntity chickenEntity2 = (ChickenEntity)EntityType.CHICKEN.create(this.getWorld());
                        if (chickenEntity2 != null) {
                            chickenEntity2.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0F);
                            chickenEntity2.initialize(world, difficulty, SpawnReason.JOCKEY, (EntityData)null, (NbtCompound)null);
                            chickenEntity2.setHasJockey(true);
                            this.startRiding(chickenEntity2);
                            world.spawnEntity(chickenEntity2);
                        }
                    }
                }
            }

            this.setCanBreakDoors(this.shouldBreakDoors() && random.nextFloat() < f * 0.1F);
            this.initEquipment(random, difficulty);
            this.updateEnchantments(random, difficulty);
        }

        if (this.getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
            LocalDate localDate = LocalDate.now();
            int i = localDate.get(ChronoField.DAY_OF_MONTH);
            int j = localDate.get(ChronoField.MONTH_OF_YEAR);
            if (j == 10 && i == 31 && random.nextFloat() < 0.25F) {
                this.equipStack(EquipmentSlot.HEAD, new ItemStack(random.nextFloat() < 0.1F ? Blocks.JACK_O_LANTERN : Blocks.CARVED_PUMPKIN));
                this.armorDropChances[EquipmentSlot.HEAD.getEntitySlotId()] = 0.0F;
            }
        }

        this.applyAttributeModifiers(f);
        return (EntityData)entityData1;
    }

    public static boolean shouldBeBaby(Random random) {
        return random.nextFloat() < 0.05F;
    }

    protected void applyAttributeModifiers(float chanceMultiplier) {
        this.initAttributes();
        this.getAttributeInstance(EntityAttributes.GENERIC_KNOCKBACK_RESISTANCE).addPersistentModifier(new EntityAttributeModifier("Random spawn bonus", this.random.nextDouble() * 0.05000000074505806, EntityAttributeModifier.Operation.ADDITION));
        double d = this.random.nextDouble() * 1.5 * (double)chanceMultiplier;
        if (d > 1.0) {
            this.getAttributeInstance(EntityAttributes.GENERIC_FOLLOW_RANGE).addPersistentModifier(new EntityAttributeModifier("Random zombie-spawn bonus", d, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
        }

        if (this.random.nextFloat() < chanceMultiplier * 0.05F) {
            this.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS).addPersistentModifier(new EntityAttributeModifier("Leader zombie bonus", this.random.nextDouble() * 0.25 + 0.5, EntityAttributeModifier.Operation.ADDITION));
            this.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).addPersistentModifier(new EntityAttributeModifier("Leader zombie bonus", this.random.nextDouble() * 3.0 + 1.0, EntityAttributeModifier.Operation.MULTIPLY_TOTAL));
            this.setCanBreakDoors(this.shouldBreakDoors());
        }

    }

    protected void initAttributes() {
        this.getAttributeInstance(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS).setBaseValue(this.random.nextDouble() * 0.10000000149011612);
    }

    protected Vector3f getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
        return new Vector3f(0.0F, dimensions.height + 0.0625F * scaleFactor, 0.0F);
    }

    protected float getUnscaledRidingOffset(Entity vehicle) {
        return -0.7F;
    }

    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        super.dropEquipment(source, lootingMultiplier, allowDrops);
        Entity entity = source.getAttacker();
        if (entity instanceof CreeperEntity creeperEntity) {
            if (creeperEntity.shouldDropHead()) {
                ItemStack itemStack = this.getSkull();
                if (!itemStack.isEmpty()) {
                    creeperEntity.onHeadDropped();
                    this.dropStack(itemStack);
                }
            }
        }

    }

    protected ItemStack getSkull() {
        return new ItemStack(Items.ZOMBIE_HEAD);
    }

    static {
        BABY_SPEED_BONUS = new EntityAttributeModifier(BABY_SPEED_ID, "Baby speed boost", 0.5, EntityAttributeModifier.Operation.MULTIPLY_BASE);
        BABY = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        ZOMBIE_TYPE = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.INTEGER);
        CONVERTING_IN_WATER = DataTracker.registerData(ZombieEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        DOOR_BREAK_DIFFICULTY_CHECKER = (difficulty) -> {
            return difficulty == Difficulty.HARD;
        };
    }

    class DestroyEggGoal extends StepAndDestroyBlockGoal {
        DestroyEggGoal(PathAwareEntity mob, double speed, int maxYDifference) {
            super(Blocks.TURTLE_EGG, mob, speed, maxYDifference);
        }

        public void tickStepping(WorldAccess world, BlockPos pos) {
            world.playSound((PlayerEntity)null, pos, SoundEvents.ENTITY_ZOMBIE_DESTROY_EGG, SoundCategory.HOSTILE, 0.5F, 0.9F + ZombieClone.this.random.nextFloat() * 0.2F);
        }

        public void onDestroyBlock(World world, BlockPos pos) {
            world.playSound((PlayerEntity)null, pos, SoundEvents.ENTITY_TURTLE_EGG_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + world.random.nextFloat() * 0.2F);
        }

        public double getDesiredDistanceToTarget() {
            return 1.14;
        }
    }

    public static class ZombieData implements EntityData {
        public final boolean baby;
        public final boolean tryChickenJockey;

        public ZombieData(boolean baby, boolean tryChickenJockey) {
            this.baby = baby;
            this.tryChickenJockey = tryChickenJockey;
        }
    }
}