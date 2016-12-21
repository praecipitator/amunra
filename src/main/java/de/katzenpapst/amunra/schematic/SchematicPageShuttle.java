package de.katzenpapst.amunra.schematic;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.GuiIds;
import de.katzenpapst.amunra.client.gui.schematic.GuiSchematicShuttle;
import de.katzenpapst.amunra.inventory.schematic.ContainerSchematicShuttle;
import micdoodle8.mods.galacticraft.api.recipe.ISchematicPage;
import micdoodle8.mods.galacticraft.core.Constants;
import micdoodle8.mods.galacticraft.planets.GuiIdsPlanets;
import micdoodle8.mods.galacticraft.planets.asteroids.client.gui.GuiSchematicTier3Rocket;
import micdoodle8.mods.galacticraft.planets.asteroids.inventory.ContainerSchematicTier3Rocket;
import micdoodle8.mods.galacticraft.planets.asteroids.schematic.SchematicTier3Rocket;
import micdoodle8.mods.galacticraft.planets.mars.items.MarsItems;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;

public class SchematicPageShuttle implements ISchematicPage {

    public SchematicPageShuttle() {
        // TODO Auto-generated constructor stub
    }

    @Override
    public int getPageID() {
        return AmunRa.instance.confSchematicIdShuttle;
    }

    @Override
    public int getGuiID()
    {
        // but, how does it know which gui handler to use?
        return GuiIds.GUI_SHUTTLE_SCHEMATIC;
        // TODO!! return GuiIdsPlanets.NASA_WORKBENCH_TIER_3_ROCKET + Constants.MOD_ID_PLANETS.hashCode();
    }

    @Override
    public ItemStack getRequiredItem()
    {
        /*TODO!!*/ return new ItemStack(MarsItems.schematic, 1, 0);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public GuiScreen getResultScreen(EntityPlayer player, int x, int y, int z)
    {
        return new GuiSchematicShuttle(player.inventory, x, y, z);
    }

    @Override
    public Container getResultContainer(EntityPlayer player, int x, int y, int z)
    {
        return new ContainerSchematicShuttle(player.inventory, x, y, z);
    }

    @Override
    public int compareTo(ISchematicPage o)
    {
        if (this.getPageID() > o.getPageID())
        {
            return 1;
        }
        else
        {
            return -1;
        }
    }

}
