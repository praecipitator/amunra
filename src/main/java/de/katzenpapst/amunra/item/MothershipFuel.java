package de.katzenpapst.amunra.item;

import de.katzenpapst.amunra.client.gui.GuiHelper;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Not really an item, just a pseudo thingy
 * @author katzenpapst
 *
 */
public class MothershipFuel {

    // protected final ResourceLocation icon;

    // protected final String unlocalizedName;
    protected final ItemDamagePair item;


    protected final String unit;

    public MothershipFuel(ItemDamagePair item) {
        this(item, "");
    }

    public MothershipFuel(ItemDamagePair item, String unit) {
        this.item = item;
        // this.unlocalizedName = unlocalizedName;
        this.unit = unit;
    }

    public ItemDamagePair getItem() {
        return item;
    }

    public String getUnit() {
        return unit;
    }

    public String formatValue(int value) {
        return GuiHelper.formatMetric(value, getUnit());
        //return String.valueOf(value)+" "+getUnit();
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof MothershipFuel)) {
            return false;
        }
        return ((MothershipFuel)other).item.equals(item);
    }

    @Override
    public int hashCode() {
        return item.hashCode();
    }

}
