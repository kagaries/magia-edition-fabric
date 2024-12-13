package com.kagaries.fabric.client.renderer;

import com.kagaries.fabric.world.entity.ZombieClone;
import com.kagaries.fabric.client.renderer.model.ZombieCloneModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.model.EntityModelLayer;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class ZombieCloneRenderer extends ZombieCloneBaseRenderer<ZombieClone, ZombieCloneModel<ZombieClone>> {

    public ZombieCloneRenderer(EntityRendererFactory.Context context) {
        this(context, EntityModelLayers.ZOMBIE, EntityModelLayers.ZOMBIE_INNER_ARMOR, EntityModelLayers.ZOMBIE_OUTER_ARMOR);
    }

    public ZombieCloneRenderer(EntityRendererFactory.Context ctx, EntityModelLayer layer, EntityModelLayer legsArmorLayer, EntityModelLayer bodyArmorLayer) {
        super(ctx, new ZombieCloneModel<>(ctx.getPart(layer)), new ZombieCloneModel<>(ctx.getPart(legsArmorLayer)), new ZombieCloneModel<>(ctx.getPart(bodyArmorLayer)));
    }

    @Override
    public Identifier getTexture(ZombieClone entity) {
        return new Identifier("textures/entity/zombie/zombie.png");
    }
}
