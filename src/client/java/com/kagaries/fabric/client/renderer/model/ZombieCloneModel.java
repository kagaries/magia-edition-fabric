package com.kagaries.fabric.client.renderer.model;

import com.kagaries.fabric.world.entity.ZombieClone;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.entity.model.AbstractZombieModel;

@Environment(EnvType.CLIENT)
public class ZombieCloneModel<T extends ZombieClone> extends AbstractZombieModel<T> {
    public ZombieCloneModel(ModelPart modelPart) {
        super(modelPart);
    }

    @Override
    public boolean isAttacking(T entity) {
        return entity.isAttacking();
    }
}
