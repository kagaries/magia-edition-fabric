package com.kagaries.fabric.mixin.entity;

import com.kagaries.fabric.world.entity.ModEntities;
import com.kagaries.fabric.world.entity.ZombieClone;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public abstract class ZombieMixin extends HostileEntity {

    protected ZombieMixin(EntityType<? extends HostileEntity> entityType, World level) {
        super(entityType, level);
    }

    @Redirect(method = "createZombieAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/HostileEntity;createHostileAttributes()Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;"))
    private static DefaultAttributeContainer.Builder createAttributes() {
        return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_FOLLOW_RANGE, 100.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, (double)0.2F).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 5.5D).add(EntityAttributes.GENERIC_ARMOR, 6.0D).add(EntityAttributes.ZOMBIE_SPAWN_REINFORCEMENTS).add(EntityAttributes.GENERIC_MAX_HEALTH, 22.5D);
    }

    @Redirect(method = "tickMovement", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/mob/HostileEntity;tickMovement()V"))
    private void aiStep(HostileEntity instance) {
        if (((ZombieEntity)(Object)this).isAlive()) {
            boolean bl = true;
            if (bl) {
                ItemStack itemStack = ((ZombieEntity)(Object)this).getEquippedStack(EquipmentSlot.HEAD);
                if (!itemStack.isEmpty() && !itemStack.isOf(Items.GREEN_BANNER)) {
                    if (itemStack.isDamageable()) {
                        itemStack.setDamage(itemStack.getDamage() + random.nextInt(2));
                        if (itemStack.getDamage() >= itemStack.getMaxDamage()) {
                            ((ZombieEntity)(Object)this).sendEquipmentBreakStatus(EquipmentSlot.HEAD);
                            ((ZombieEntity)(Object)this).equipStack(EquipmentSlot.HEAD, ItemStack.EMPTY);
                        }
                    }

                    bl = false;
                }

                if (bl) {
                    ((ZombieEntity)(Object)this).setFireTicks(8);
                }
            }
        }


        super.tickMovement();
    }

    @Inject(method = "initialize", at = @At(value = "TAIL"))
    private void finalizeSpawn(ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, EntityData entityData, NbtCompound entityNbt, CallbackInfoReturnable<EntityData> cir) {
        if (((ZombieEntity)(Object)this).getEquippedStack(EquipmentSlot.HEAD).isEmpty()) {
            ((ZombieEntity)(Object)this).equipStack(EquipmentSlot.HEAD, new ItemStack(Blocks.GREEN_BANNER));
        }

        if (this.getEquippedStack(EquipmentSlot.CHEST).isEmpty()) {
            this.equipStack(EquipmentSlot.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
        }

        if (this.getEquippedStack(EquipmentSlot.LEGS).isEmpty()) {
            this.equipStack(EquipmentSlot.LEGS, new ItemStack(Items.CHAINMAIL_LEGGINGS));
        }

        if (this.getEquippedStack(EquipmentSlot.FEET).isEmpty()) {
            this.equipStack(EquipmentSlot.FEET, new ItemStack(Items.CHAINMAIL_BOOTS));
        }

        int randomInt = random.nextInt(5);

        for (int i = 0; i < randomInt; i++) {
            ZombieClone zombieClone = ModEntities.ZOMBIE_CLONE.create(world.toServerWorld());
            zombieClone.setPos(this.getX(), this.getY(), this.getZ());
            zombieClone.initialize(world, difficulty, SpawnReason.REINFORCEMENT, (EntityData) null, (NbtCompound) null);
            world.spawnEntity(zombieClone);
        }

    }

}
