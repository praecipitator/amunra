package de.katzenpapst.amunra.mob;

import de.katzenpapst.amunra.entity.EntityBaseLaserArrow;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.StatCollector;

public class DamageSourceAR {
    public static DamageSource dsSuffocate = new DamageSource("wrongAtmoSuffocate").setDamageBypassesArmor();
    public static DamageSource dsFallOffShip = new DamageSource("fallOffMothership").setDamageBypassesArmor().setDamageAllowedInCreativeMode();
	public static DamageSource dsEngine = new DamageSource("death.attack.killedByEngine");

	public static DamageSource getDSCrashIntoPlanet(CelestialBody body) {
	    return new DamageSourceCrash(body.getUnlocalizedName());
	}

	public static class DamageSourceCrash extends DamageSource {

	    protected String bodyName;

        public DamageSourceCrash(String bodyName) {
            super("fallOffMothershipIntoPlanet");
            this.setDamageBypassesArmor();
            this.setDamageAllowedInCreativeMode();
            this.bodyName = bodyName;
        }

        @Override
        public IChatComponent func_151519_b(EntityLivingBase p_151519_1_)
        {
            //EntityLivingBase entitylivingbase1 = p_151519_1_.func_94060_bK();
            String s = "death.attack." + this.damageType;
            return new ChatComponentTranslation(s, p_151519_1_.func_145748_c_(), new ChatComponentTranslation(this.bodyName));
        }

	}

	/**
     * returns EntityDamageSourceIndirect of an arrow
     */
    public static DamageSource causeLaserDamage(EntityBaseLaserArrow p_76353_0_, Entity p_76353_1_)
    {
        return (new EntityDamageSourceIndirect("ar_laser", p_76353_0_, p_76353_1_)).setProjectile();
    }
}
