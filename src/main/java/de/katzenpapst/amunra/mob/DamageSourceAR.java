package de.katzenpapst.amunra.mob;

import de.katzenpapst.amunra.entity.EntityBaseLaserArrow;
import net.minecraft.entity.Entity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;

public class DamageSourceAR {
	public static DamageSource dsSuffocate = new DamageSource("wrongAtmoSuffocate").setDamageBypassesArmor();

	/**
     * returns EntityDamageSourceIndirect of an arrow
     */
    public static DamageSource causeLaserDamage(EntityBaseLaserArrow p_76353_0_, Entity p_76353_1_)
    {
        return (new EntityDamageSourceIndirect("ar_laser", p_76353_0_, p_76353_1_)).setProjectile();
    }
}
