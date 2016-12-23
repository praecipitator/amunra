package de.katzenpapst.amunra.item;

import de.katzenpapst.amunra.entity.EntityBaseLaserArrow;
import de.katzenpapst.amunra.entity.EntityLaserArrow;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

public class ItemRaygun extends ItemAbstractRaygun {

    protected IIcon itemEmptyIcon;

    public ItemRaygun(String assetName) {
        super(assetName);
    }

    @Override
    protected void spawnProjectile(ItemStack itemStack, EntityPlayer entityPlayer, World world) {
        EntityBaseLaserArrow ent = new EntityLaserArrow(world, entityPlayer);
        world.spawnEntityInWorld(ent);
    }


}
