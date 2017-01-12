package de.katzenpapst.amunra.mothership.fueldisplay;

import de.katzenpapst.amunra.client.gui.GuiHelper;
import de.katzenpapst.amunra.item.ItemDamagePair;
import micdoodle8.mods.galacticraft.core.energy.EnergyDisplayHelper;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.StatCollector;
import net.minecraftforge.fluids.Fluid;

public class MothershipFuelDisplayEnergy extends MothershipFuelDisplay {

    protected ItemStack stack;

    protected static MothershipFuelDisplayEnergy instance = null;

    protected MothershipFuelDisplayEnergy() {
        stack = new ItemStack(GCItems.battery, 1, 0);
    }

    public static MothershipFuelDisplayEnergy getInstance() {
        if(instance == null) {
            instance = new MothershipFuelDisplayEnergy();
        }
        return instance;
    }

    @Override
    public IIcon getIcon() {

        return stack.getItem().getIconFromDamage(stack.getItemDamage());
    }

    @Override
    public String getDisplayName() {
        return StatCollector.translateToLocal("gui.message.energy");
        // return stack.getDisplayName();
    }

    @Override
    public int getSpriteNumber() {
        return stack.getItemSpriteNumber();
    }

    @Override
    public String getUnit() {
        return "gJ";
    }

    @Override
    public float getFactor() {
        return 1;
    }


    @Override
    public String formatValue(float value) {
        // EnergyDisplayHelper
        return EnergyDisplayHelper.getEnergyDisplayS(value);
    }

    @Override
    public boolean equals(Object other) {
        if(!(other instanceof MothershipFuelDisplayEnergy)) {
            return false;
        }
        return other == this;
    }

    @Override
    public int hashCode() {
        return stack.hashCode() + 135842;
    }

}
