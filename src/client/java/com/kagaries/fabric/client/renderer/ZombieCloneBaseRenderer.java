package com.kagaries.fabric.client.renderer;

import com.kagaries.fabric.world.entity.ZombieClone;
import com.kagaries.fabric.client.renderer.model.ZombieCloneModel;
import net.minecraft.client.render.entity.BipedEntityRenderer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.util.Identifier;

public abstract class ZombieCloneBaseRenderer<T extends ZombieClone, M extends ZombieCloneModel<T>> extends BipedEntityRenderer<T, M> {
    private static final Identifier TEXTURE = new Identifier("textures/entity/zombie/zombie.png");

    protected ZombieCloneBaseRenderer(EntityRendererFactory.Context ctx, M bodyModel, M legsArmorModel, M bodyArmorModel) {
        super(ctx, bodyModel, 0.5F);
        this.addFeature(new ArmorFeatureRenderer(this, legsArmorModel, bodyArmorModel, ctx.getModelManager()));
    }

    public ZombieCloneBaseRenderer(EntityRendererFactory.Context ctx, M model, float shadowRadius) {
        super(ctx, model, shadowRadius);
    }

    public Identifier getTexture(ZombieClone zombieEntity) {
        return TEXTURE;
    }

    protected boolean isShaking(T zombieEntity) {
        return super.isShaking(zombieEntity) || zombieEntity.isConvertingInWater();
    }
}
