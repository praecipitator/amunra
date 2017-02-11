package de.katzenpapst.amunra.mothership.fueldisplay;

import de.katzenpapst.amunra.helper.GuiHelper;
import net.minecraft.util.IIcon;

/**
 * Not really an item, just a pseudo thingy
 * @author katzenpapst
 *
 */
abstract public class MothershipFuelDisplay {

    // protected final ResourceLocation icon;

    // protected final String unlocalizedName;
    // protected final ItemDamagePair item;



    // protected final int



    abstract public IIcon getIcon();

    // abstract public String getUnlocalizedName();

    abstract public String getDisplayName();

    abstract public int getSpriteNumber();

    abstract public String getUnit();

    abstract public float getFactor();

    public String formatValue(float value) {
        return GuiHelper.formatMetric(value * getFactor(), getUnit(), true);
        //return String.valueOf(value)+" "+getUnit();
    }
/*
    @Override
    public boolean equals(Object other) {
        if(!(other instanceof MothershipFuelDisplay)) {
            return false;
        }
        return ((MothershipFuelDisplay)other).factor == factor &&
                ((MothershipFuelDisplay)other).icon.equals(icon) &&
                ((MothershipFuelDisplay)other).unit.equals(unit) &&
                ((MothershipFuelDisplay)other).spriteNumber == spriteNumber;
        // return ((MothershipFuel)other).item.equals(item);
    }

    @Override
    public int hashCode() {
        return icon.hashCode() ^ unit.hashCode() ^ (spriteNumber << 8) ^ (int)(factor*10000);
    }
*/
}
