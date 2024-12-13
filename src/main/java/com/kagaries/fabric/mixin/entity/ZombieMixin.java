package com.kagaries.fabric.mixin.entity;

import com.kagaries.fabric.mixin.accessor.ZombieAccessor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Blocks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.time.LocalDate;
import java.time.temporal.ChronoField;

@Mixin(Zombie.class)
public abstract class ZombieMixin extends Monster {

    protected ZombieMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(method = "createAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Monster;createMonsterAttributes()Lnet/minecraft/world/entity/ai/attributes/AttributeSupplier$Builder;"))
    private static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes().add(Attributes.FOLLOW_RANGE, 100.0D).add(Attributes.MOVEMENT_SPEED, (double)0.2F).add(Attributes.ATTACK_DAMAGE, 5.5D).add(Attributes.ARMOR, 6.0D).add(Attributes.SPAWN_REINFORCEMENTS_CHANCE).add(Attributes.MAX_HEALTH, 22.5D);
    }

    @Redirect(method = "aiStep", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Monster;aiStep()V"))
    private void aiStep(Monster instance) {
        if (((Zombie)(Object)this).isAlive()) {
            boolean bl = true;
            if (bl) {
                ItemStack itemStack = ((Zombie)(Object)this).getItemBySlot(EquipmentSlot.HEAD);
                if (!itemStack.isEmpty() && !itemStack.is(Items.GREEN_BANNER)) {
                    if (itemStack.isDamageableItem()) {
                        RandomSource random = RandomSource.create();
                        itemStack.setDamageValue(itemStack.getDamageValue() + random.nextInt(2));
                        if (itemStack.getDamageValue() >= itemStack.getMaxDamage()) {
                            ((Zombie)(Object)this).broadcastBreakEvent(EquipmentSlot.HEAD);
                            ((Zombie)(Object)this).setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }

                    bl = false;
                }

                if (bl) {
                    ((Zombie)(Object)this).setSecondsOnFire(8);
                }
            }
        }


        super.aiStep();
    }

    @Inject(method = "finalizeSpawn", at = @At(value = "TAIL"))
    private void finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, SpawnGroupData spawnGroupData, CompoundTag compoundTag, CallbackInfoReturnable<SpawnGroupData> cir) {
        if (((Zombie)(Object)this).getItemBySlot(EquipmentSlot.HEAD).isEmpty()) {
            RandomSource randomSource = RandomSource.create();
            ((Zombie)(Object)this).setItemSlot(EquipmentSlot.HEAD, new ItemStack(Blocks.GREEN_BANNER));
        }

        if (this.getItemBySlot(EquipmentSlot.CHEST).isEmpty()) {
            this.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
        }

        if (this.getItemBySlot(EquipmentSlot.LEGS).isEmpty()) {
            this.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.CHAINMAIL_LEGGINGS));
        }

        if (this.getItemBySlot(EquipmentSlot.FEET).isEmpty()) {
            this.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.CHAINMAIL_BOOTS));
        }
    }

}
