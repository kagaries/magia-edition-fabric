package com.kagaries.fabric.mixin.accessor;

import net.minecraft.entity.mob.ZombieEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ZombieEntity.class)
public interface ZombieAccessor {
    @Invoker("burnsInDaylight")
    public boolean invokeburnsInDaylight();
}
