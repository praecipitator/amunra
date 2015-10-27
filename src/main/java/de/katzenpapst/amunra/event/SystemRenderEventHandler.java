package de.katzenpapst.amunra.event;

import org.lwjgl.Sys;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Vector3f;

import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.IChildBody;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore.EventSpecialRender;
import micdoodle8.mods.galacticraft.planets.asteroids.client.render.NetworkRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;

public class SystemRenderEventHandler {
	/*@SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onClientTick(ClientTickEvent event)
    {
    }*/
	
	

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onRingRender(CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre renderEvent)
    {
    	// sky: D:\Code\Galacticraft\src\main\java\micdoodle8\mods\galacticraft\planets\mars\client\SkyProviderMars.java
        if (renderEvent.celestialBody.equals(AmunRa.instance.asteroidBeltMehen) || renderEvent.celestialBody.equals(AmunRa.instance.moonBaalRings))
        {
        	drawAsteroidRings(renderEvent, renderEvent.celestialBody);
        } 
    }
    
    
    
    protected void drawAsteroidRings(CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre renderEvent, CelestialBody aroundBody) {
    	
    	Vector3f mapPos = renderEvent.parentOffset;
    	
    	float xOffset = (float) mapPos.x;
    	float yOffset = (float) mapPos.y;
    	
    	if (FMLClientHandler.instance().getClient().currentScreen instanceof GuiCelestialSelection)
    		GL11.glColor4f(0.7F, 0.0F, 0.0F, 0.5F);
    	else
    		GL11.glColor4f(0.3F, 0.1F, 0.1F, 1.0F);
        renderEvent.setCanceled(true);
        GL11.glBegin(GL11.GL_LINE_LOOP);

        final float theta = (float) (2 * Math.PI / 90);
        final float cos = (float) Math.cos(theta);
        final float sin = (float) Math.sin(theta);

        float min = 0;
        float max = 0;
        
        if(aroundBody instanceof Planet) {
        	min = 72.F;
        	max = 78.F;
        } else if(aroundBody instanceof Moon) {
        	max = 1 / 1.5F;
        	min = 1 / 1.9F;      	
        }

        float x = max * renderEvent.celestialBody.getRelativeDistanceFromCenter().unScaledDistance;
        float y = 0;

        // outer ring
        float temp;
        for (int i = 0; i < 90; i++)
        {
            GL11.glVertex2f(x+xOffset, y+yOffset);

            temp = x;
            x = cos * x - sin * y;
            y = sin * temp + cos * y;
        }

        GL11.glEnd();
        
        // inner ring
        GL11.glBegin(GL11.GL_LINE_LOOP);

        x = min * renderEvent.celestialBody.getRelativeDistanceFromCenter().unScaledDistance;
        y = 0;

        for (int i = 0; i < 90; i++)
        {
            GL11.glVertex2f(x+xOffset, y+yOffset);

            temp = x;
            x = cos * x - sin * y;
            y = sin * temp + cos * y;
        }

        GL11.glEnd();
        
        // inner red area
        GL11.glColor4f(0.7F, 0.0F, 0.0F, 0.1F);
        GL11.glBegin(GL11.GL_QUADS);

        x = min * renderEvent.celestialBody.getRelativeDistanceFromCenter().unScaledDistance;
        y = 0;
        float x2 = max * renderEvent.celestialBody.getRelativeDistanceFromCenter().unScaledDistance;
        float y2 = 0;

        for (int i = 0; i < 90; i++)
        {
            GL11.glVertex2f(x2+xOffset, y2+yOffset);
            GL11.glVertex2f(x+xOffset, y+yOffset);

            temp = x;
            x = cos * x - sin * y;
            y = sin * temp + cos * y;
            temp = x2;
            x2 = cos * x2 - sin * y2;
            y2 = sin * temp + cos * y2;

            GL11.glVertex2f(x+xOffset, y+yOffset);
            GL11.glVertex2f(x2+xOffset, y2+yOffset);
        }

        GL11.glEnd();
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onBodyRender(CelestialBodyRenderEvent.Pre renderEvent)
    {
        if (renderEvent.celestialBody.equals(AmunRa.instance.asteroidBeltMehen) || renderEvent.celestialBody.equals(AmunRa.instance.moonBaalRings))
        {
            GL11.glRotatef(Sys.getTime() / 10.0F % 360, 0, 0, 1);
        }
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onSpecialRender(EventSpecialRender event)
    {
        NetworkRenderer.renderNetworks(FMLClientHandler.instance().getClient().theWorld, event.partialTicks);
    }
}
