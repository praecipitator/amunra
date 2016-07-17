package de.katzenpapst.amunra.client.gui;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.lwjgl.BufferUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ibm.icu.text.ArabicShaping;
import com.ibm.icu.text.ArabicShapingException;
import com.ibm.icu.text.Bidi;

import cpw.mods.fml.client.FMLClientHandler;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.RecipeHelper;
import de.katzenpapst.amunra.mothership.Mothership;
import de.katzenpapst.amunra.mothership.MothershipWorldData;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR;
import de.katzenpapst.amunra.tick.TickHandlerServer;
import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.event.client.CelestialBodyRenderEvent;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.IChildBody;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.Satellite;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.recipe.SpaceStationRecipe;
import micdoodle8.mods.galacticraft.api.world.SpaceStationType;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection;
import micdoodle8.mods.galacticraft.core.client.gui.screen.SmallFontRenderer;
import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection.StationDataGUI;
// import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiShuttleSelection.EnumSelectionState;
//import micdoodle8.mods.galacticraft.core.client.gui.screen.GuiCelestialSelection.EnumSelectionState;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.proxy.ClientProxyCore;
import micdoodle8.mods.galacticraft.core.tick.KeyHandlerClient;
import micdoodle8.mods.galacticraft.core.util.ColorUtil;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.MinecraftForge;

public class GuiShuttleSelection extends GuiCelestialSelection {

    protected int numPlayersMotherships = -1;

    protected CelestialBody lastSelectedBodyMS;

    protected List<CelestialBody> shuttlePossibleBodies;


    public GuiShuttleSelection(boolean mapMode, List<CelestialBody> possibleBodies)
    {
        super(mapMode, possibleBodies);
        shuttlePossibleBodies = possibleBodies;
    }

    protected boolean isSiblingOf(CelestialBody celestialBody, Mothership ship)
    {
        if (celestialBody instanceof Planet)
        {
            SolarSystem solarSystem = ((Planet) celestialBody).getParentSolarSystem();
            return solarSystem.getMainStar().equals(ship.getParent());
        }
        else if (celestialBody instanceof IChildBody)
        {
            Planet planet = ((IChildBody) celestialBody).getParentPlanet();

            return planet.equals(ship.getParent());
        } else if(celestialBody instanceof Mothership) {
            ((Mothership)celestialBody).getParent().equals(ship.getParent());
        }

        return false;
    }

    protected List<Mothership> getMothershipListToRender() {
        LinkedList<Mothership> result = new LinkedList<Mothership>();

        if (this.selectedBody != null)
        {
            MothershipWorldData msData = TickHandlerServer.mothershipData;

            for (Mothership ms:  msData.getMotherships().values())
            {
                if (
                        (ms == this.selectedBody || (ms.getParent() == this.selectedBody && this.selectionCount != 1))
                        &&
                        (
                                this.ticksSinceSelection > 35
                                ||
                                this.selectedBody == ms
                                ||
                                (
                                        this.lastSelectedBody instanceof Mothership
                                        &&
                                        ((Mothership)this.lastSelectedBody).getParent().equals(ms)
                                        //GalaxyRegistry.getMoonsForPlanet(((Moon) this.lastSelectedBody).getParentPlanet()).contains(moon)
                                )
                        )
                        ||
                        isSiblingOf(this.selectedBody, ms)
                )
                {
                    result.add(ms);
                }
            }
       }


        return result;
    }

    protected CelestialBody getParent(CelestialBody body) {
        if(body instanceof IChildBody) {// satellite apparently implements this already?
            return ((IChildBody)body).getParentPlanet();
        }
        if(body instanceof Mothership) {
            return ((Mothership)body).getParent();
        }
        return body;


    }

    protected CelestialBody getBodyToRenderMothershipsAround() {

        if(this.selectedBody instanceof Star) {
            if(selectionCount != 1 && this.ticksSinceSelection > 35) {
                return this.selectedBody;
            }
            return null;
        }
        if(this.selectedBody instanceof Planet) {
            // ship's parent is the body and selectionCount != 1
            // AND
            // this.ticksSinceSelection > 35
            if(selectionCount != 1 && this.ticksSinceSelection > 35) {
                return this.selectedBody;
            }
            return null;
        } else if(this.selectedBody instanceof IChildBody) {
            if(selectionCount != 1 && this.ticksSinceSelection > 35) {
                return this.selectedBody;
            }
            return null;
            //renderShipsAround = ((IChildBody)this.selectedBody).getParentPlanet();
            // I almost think never TODO find out
        } else if(this.selectedBody instanceof Mothership) {
            return ((Mothership)this.selectedBody).getParent();
        }
        return null;
    }

    protected void _workaroundDrawMoon(Matrix4f worldMatrix0, Moon moon, FloatBuffer fb, HashMap<CelestialBody, Matrix4f> matrixMap) {
        GL11.glPushMatrix();
        Matrix4f worldMatrix1 = new Matrix4f(worldMatrix0);
        Matrix4f.translate(this.getCelestialBodyPosition(moon), worldMatrix1, worldMatrix1);

        Matrix4f worldMatrix2 = new Matrix4f();
        Matrix4f.rotate((float) Math.toRadians(45), new Vector3f(0, 0, 1), worldMatrix2, worldMatrix2);
        Matrix4f.rotate((float) Math.toRadians(-55), new Vector3f(1, 0, 0), worldMatrix2, worldMatrix2);
        Matrix4f.scale(new Vector3f(0.25F, 0.25F, 1.0F), worldMatrix2, worldMatrix2);
        worldMatrix2 = Matrix4f.mul(worldMatrix1, worldMatrix2, worldMatrix2);

        fb.rewind();
        worldMatrix2.store(fb);
        fb.flip();
        GL11.glMultMatrix(fb);

        CelestialBodyRenderEvent.Pre preEvent = new CelestialBodyRenderEvent.Pre(moon, moon.getBodyIcon(), 8);
        MinecraftForge.EVENT_BUS.post(preEvent);

        GL11.glColor4f(1, 1, 1, 1);
        if (preEvent.celestialBodyTexture != null)
        {
            this.mc.renderEngine.bindTexture(preEvent.celestialBodyTexture);
        }

        if (!preEvent.isCanceled())
        {
            int size = this.getWidthForCelestialBodyStatic(moon);
            this.drawTexturedModalRect(-size / 2, -size / 2, size, size, 0, 0, preEvent.textureSize, preEvent.textureSize, false, false, preEvent.textureSize, preEvent.textureSize);
            matrixMap.put(moon, worldMatrix1);
        }

        CelestialBodyRenderEvent.Post postEvent = new CelestialBodyRenderEvent.Post(moon);
        MinecraftForge.EVENT_BUS.post(postEvent);
        fb.clear();
        GL11.glPopMatrix();
    }

    protected void _workaroundDrawMoonCircle(Moon moon, float sin, float cos) {
        float x = this.getScale(moon);
        float y = 0;

        float alpha = 1;

        GL11.glPushMatrix();
        Vector3f planetPos = this.getCelestialBodyPosition(moon.getParentPlanet());
        GL11.glTranslatef(planetPos.x, planetPos.y, 0);

        if (this.selectionCount >= 2)
        {
            alpha = this.selectedBody instanceof IChildBody ? 1.0F : Math.min(Math.max((this.ticksSinceSelection - 30) / 15.0F, 0.0F), 1.0F);

            if (this.lastSelectedBody instanceof Moon)
            {
                if (GalaxyRegistry.getMoonsForPlanet(((Moon) this.lastSelectedBody).getParentPlanet()).contains(moon))
                {
                    alpha = 1.0F;
                }
            }
        }

        if (alpha != 0)
        {
            /*switch (count % 2)
            {
            case 0:
                GL11.glColor4f(0.0F, 0.6F, 1.0F, alpha);
                break;
            case 1:*/
                GL11.glColor4f(0.4F, 0.9F, 1.0F, alpha);
               /* break;
            }*/

            CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre preEvent = new CelestialBodyRenderEvent.CelestialRingRenderEvent.Pre(moon, new Vector3f(0.0F, 0.0F, 0.0F));
            MinecraftForge.EVENT_BUS.post(preEvent);

            if (!preEvent.isCanceled())
            {
                GL11.glBegin(GL11.GL_LINE_LOOP);

                float temp;
                for (int i = 0; i < 90; i++)
                {
                    GL11.glVertex2f(x, y);

                    temp = x;
                    x = cos * x - sin * y;
                    y = sin * temp + cos * y;
                }

                GL11.glEnd();

                //count++;
            }

            CelestialBodyRenderEvent.CelestialRingRenderEvent.Post postEvent = new CelestialBodyRenderEvent.CelestialRingRenderEvent.Post(moon);
            MinecraftForge.EVENT_BUS.post(postEvent);
        }
        GL11.glPopMatrix();
    }


    @Override
    public void drawCircles() {


        // do the motherships first, because I have no idea where the matrix will be after the super call
        GL11.glPushMatrix();


        GL11.glLineWidth(3);


        final float theta = (float) (2 * Math.PI / 90);
        final float cos = (float) Math.cos(theta);
        final float sin = (float) Math.sin(theta);

        CelestialBody body = getBodyToRenderMothershipsAround();
        if(body instanceof Moon && this.selectionCount >= 1) { // TODO add condition to figure out if stuff
            this._workaroundDrawMoonCircle((Moon) body, sin, cos);
        }
        GL11.glColor4f(0.6F, 0.2F, 0.2F, 0.8F);
        if(body != null) {
            if(TickHandlerServer.mothershipData.hasMothershipsInOrbit(body)) {


                float dist = TickHandlerServer.mothershipData.getMothershipOrbitDistanceFor(body);
                float scale = 3.0F * dist * (1.0F / 5.0F);

                if(body instanceof Star) {
                    scale *= 3;
                }

                Vector3f planetPos = this.getCelestialBodyPosition(body);
                GL11.glTranslatef(planetPos.x, planetPos.y, 0);

                float x = scale;
                float y = 0;

                GL11.glBegin(GL11.GL_LINE_LOOP);

                float temp;
                for (int i = 0; i < 90; i++)
                {
                    GL11.glVertex2f(x, y);

                    temp = x;
                    x = cos * x - sin * y;
                    y = sin * temp + cos * y;
                }

                GL11.glEnd();
            }
        }
        // List<Mothership> msList = TickHandlerServer.mothershipData.getMothershipsForParent(renderShipsAround);
        GL11.glLineWidth(1);
        GL11.glPopMatrix();


        super.drawCircles();

    }

    @Override
    public HashMap<CelestialBody, Matrix4f> drawCelestialBodies(Matrix4f worldMatrix)
    {
        HashMap<CelestialBody, Matrix4f> result = super.drawCelestialBodies(worldMatrix);
        FloatBuffer fb = BufferUtils.createFloatBuffer(16 * Float.SIZE);

        /*protected CelestialBody lastSelectedBodyMS;
    protected float mothershipsOrbit;
    protected int numMotherships;
    protected float mothershipPhase;*/


        if (this.selectedBody != null)
        {
            Matrix4f worldMatrix0 = new Matrix4f(worldMatrix);

            CelestialBody renderShipsAround = null;



            /*
             *  render them if:
             *  - renderShipsAround == lastSelectedBody
             *  - renderShipsAround's parent == lastSelectedBody
             *  - renderShipsAround == lastSelectedBody's parent
             */

            renderShipsAround = getBodyToRenderMothershipsAround();
            if(renderShipsAround != null) {


                //MothershipWorldData msData = TickHandlerServer.mothershipData;
                List<Mothership> msList = TickHandlerServer.mothershipData.getMothershipsForParent(renderShipsAround);
                int numShips = msList.size();

                // if selectionCount > 0 && this.selectedBody instanceof mothership, also render the moon
                // use it on matrix0?
                if(this.selectionCount > 0 && renderShipsAround instanceof Moon && this.selectedBody instanceof Mothership) {
                    _workaroundDrawMoon(worldMatrix0, (Moon) renderShipsAround, fb, result);
                }

                for (Mothership ms: msList)
                {
                    GL11.glPushMatrix();
                    Matrix4f worldMatrix1 = new Matrix4f(worldMatrix0);
                    Matrix4f.translate(this.getCelestialBodyPosition(ms), worldMatrix1, worldMatrix1);

                    Matrix4f worldMatrix2 = new Matrix4f();
                    Matrix4f.rotate((float) Math.toRadians(45), new Vector3f(0, 0, 1), worldMatrix2, worldMatrix2);
                    Matrix4f.rotate((float) Math.toRadians(-55), new Vector3f(1, 0, 0), worldMatrix2, worldMatrix2);
                    Matrix4f.scale(new Vector3f(0.25F, 0.25F, 1.0F), worldMatrix2, worldMatrix2);
                    worldMatrix2 = Matrix4f.mul(worldMatrix1, worldMatrix2, worldMatrix2);

                    fb.rewind();
                    worldMatrix2.store(fb);
                    fb.flip();
                    GL11.glMultMatrix(fb);

                    CelestialBodyRenderEvent.Pre preEvent = new CelestialBodyRenderEvent.Pre(ms, ms.getBodyIcon(), 8);
                    MinecraftForge.EVENT_BUS.post(preEvent);

                    GL11.glColor4f(1, 1, 1, 1);
                    if (preEvent.celestialBodyTexture != null)
                    {
                        this.mc.renderEngine.bindTexture(preEvent.celestialBodyTexture);
                    }

                    if (!preEvent.isCanceled())
                    {
                        int size = this.getWidthForMothershipStatic(ms);
                        this.drawTexturedModalRect(-size / 2, -size / 2, size, size, 0, 0, preEvent.textureSize, preEvent.textureSize, false, false, preEvent.textureSize, preEvent.textureSize);
                        result.put(ms, worldMatrix1);
                    }

                    CelestialBodyRenderEvent.Post postEvent = new CelestialBodyRenderEvent.Post(ms);
                    MinecraftForge.EVENT_BUS.post(postEvent);
                    fb.clear();
                    GL11.glPopMatrix();

                }
            }
        }

        return result;
    }

    public static int getWidthForMothershipStatic(Mothership celestialBody)
    {
        //CelestialBody parent = celestialBody.getParent();
        //boolean isScreenWtf = false;
        if (Minecraft.getMinecraft().currentScreen instanceof GuiShuttleSelection &&
                (celestialBody != ((GuiShuttleSelection) Minecraft.getMinecraft().currentScreen).selectedBody ||
                ((GuiShuttleSelection) Minecraft.getMinecraft().currentScreen).selectionCount != 1))
        {
            //isScreenWtf = true;
            return 4;
            //return 6;
        }
        return 6;
        /*

        if(parent instanceof Star) {
            // like planet
            return isScreenWtf ? 4 : 6;
        }
        if(parent instanceof Planet) {
            // like moon
            return isScreenWtf ? 4 : 6;
        }
        if()

        return celestialBody instanceof Star ? 8 : (celestialBody instanceof Planet ? 4 : (celestialBody instanceof IChildBody ? 4 : (celestialBody instanceof Satellite ? 4 : 2)));

        return celestialBody instanceof Star ? 12 : (celestialBody instanceof Planet ? 6 : (celestialBody instanceof IChildBody ? 6 : (celestialBody instanceof Satellite ? 6 : 2)));
        */
        // return 2;
    }

    public void mothershipListUpdated() {
        // remove all the ships from the tick list
        for(Iterator<Entry<CelestialBody, Integer>> it = this.celestialBodyTicks.entrySet().iterator(); it.hasNext(); ) {
            Entry<CelestialBody, Integer> entry = it.next();
            if(entry.getKey() instanceof Mothership) {
                it.remove();
            }
        }
        // fill the new data in
        MothershipWorldData msData = TickHandlerServer.mothershipData;
        for (Mothership ms:  msData.getMotherships().values())
        {
            this.celestialBodyTicks.put(ms, 0);
        }

        updateNumPlayerMotherships();
    }

    public void newMothershipCreated(Mothership ship) {
        this.celestialBodyTicks.put(ship, 0);
        updateNumPlayerMotherships();
    }


    @Override
    protected Vector3f getCelestialBodyPosition(CelestialBody cBody)
    {
        if (cBody instanceof Mothership)
        {
            // failsafe against a possible race condition
            int cBodyTicks = 0;
            if(this.celestialBodyTicks.get(cBody) != null) {

                cBodyTicks = this.celestialBodyTicks.get(cBody);
            }
            float timeScale = 2.0F;
            float distanceFromCenter = this.getScale(cBody);
            float orbitTime = 1 / 0.01F;// 5.0F;

            CelestialBody msParent = ((Mothership) cBody).getParent();
            if(msParent instanceof Star) {
                distanceFromCenter *= 3;
            }

            Vector3f cBodyPos = new Vector3f((float) Math.sin(cBodyTicks / (timeScale * orbitTime) + cBody.getPhaseShift()) * distanceFromCenter, (float) Math.cos(cBodyTicks / (timeScale * orbitTime) + cBody.getPhaseShift()) * distanceFromCenter, 0);

            Vector3f parentVec = this.getCelestialBodyPosition(((Mothership) cBody).getParent());
            return Vector3f.add(cBodyPos, parentVec, null);
        }


        return super.getCelestialBodyPosition(cBody);
    }

    protected void updateNumPlayerMotherships() {


        numPlayersMotherships = TickHandlerServer.mothershipData.getNumMothershipsForPlayer(this.mc.thePlayer.getUniqueID().toString());
        // numPlayersMotherships
    }

    @Override
    protected Vector2f getTranslationAdvanced(float partialTicks)
    {
        /*if (this.selectedBody instanceof Planet && this.lastSelectedBody instanceof IChildBody && ((IChildBody) this.lastSelectedBody).getParentPlanet() == this.selectedBody)
        {
            Vector3f posVec = this.getCelestialBodyPosition(this.selectedBody);
            return new Vector2f(posVec.x, posVec.y);
        }*/
        if(this.selectedBody != null) {
            if(this.selectedBody instanceof Mothership) {
                CelestialBody parent = ((Mothership)this.selectedBody).getParent();
                Vector3f result;
                if(parent instanceof IChildBody) {
                    result = this.getCelestialBodyPosition(((IChildBody)parent).getParentPlanet());
                    return new Vector2f(result.x, result.y);
                }
                result = this.getCelestialBodyPosition(parent);
                return new Vector2f(result.x, result.y);
            }
            if(this.selectedBody instanceof Planet && this.lastSelectedBody instanceof Mothership && ((Mothership)this.lastSelectedBody).getParent() == this.selectedBody) {
                Vector3f posVec = this.getCelestialBodyPosition(this.selectedBody);
                return new Vector2f(posVec.x, posVec.y);
            }
        }
        return super.getTranslationAdvanced(partialTicks);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        // do stuff
        MothershipWorldData msData = TickHandlerServer.mothershipData;
        for (Mothership ms:  msData.getMotherships().values())
        {
            this.celestialBodyTicks.put(ms, 0);
        }

        updateNumPlayerMotherships();

        /*
        for (Planet planet : GalaxyRegistry.getRegisteredPlanets().values())
        {
            this.celestialBodyTicks.put(planet, 0);
        }

        for (Moon moon : GalaxyRegistry.getRegisteredMoons().values())
        {
            this.celestialBodyTicks.put(moon, 0);
        }

        for (Satellite satellite : GalaxyRegistry.getRegisteredSatellites().values())
        {
            this.celestialBodyTicks.put(satellite, 0);
        }*/

        //GuiShuttleSelection.BORDER_WIDTH = this.width / 65;
        //GuiShuttleSelection.BORDER_EDGE_WIDTH = GuiShuttleSelection.BORDER_WIDTH / 4;
    }

    @Override
    public void drawButtons(int mousePosX, int mousePosY)
    {
        this.possibleBodies = this.shuttlePossibleBodies;
        super.drawButtons(mousePosX, mousePosY);
        if (this.selectionState != EnumSelectionState.PROFILE && this.selectedBody != null && canCreateMothership(this.selectedBody))
        {
            drawMothershipButton(mousePosX, mousePosY);
        }
    }

    @Override
    protected void keyTyped(char keyChar, int keyID)
    {
        super.keyTyped(keyChar, keyID);

    }

    @Override
    protected boolean canCreateSpaceStation(CelestialBody atBody) {
        // no stations can be built from the shuttle, because there's not enough space on the screen
        return false;
    }

    protected boolean canCreateMothership(CelestialBody atBody) {
        if(numPlayersMotherships < 0) {
            return false;
        }
        return (
                AmunRa.instance.confMaxMotherships == -1 ||
                numPlayersMotherships < AmunRa.instance.confMaxMotherships
                ) && Mothership.canBeOrbited(atBody);
    }

    protected void drawItemForRecipe(ItemStack item, int amount, int requiredAmount, int xPos, int yPos, int mousePosX, int mousePosY)
    {
        RenderHelper.enableGUIStandardItemLighting();
        GuiCelestialSelection.itemRender.renderItemAndEffectIntoGUI(
                this.fontRendererObj,
                this.mc.renderEngine,
                item, xPos, yPos);
        RenderHelper.disableStandardItemLighting();
        GL11.glEnable(GL11.GL_BLEND);

        if (mousePosX >= xPos && mousePosX <= xPos + 16 && mousePosY >= yPos && mousePosY <= yPos + 16)
        {
            GL11.glDepthMask(true);
            GL11.glEnable(GL11.GL_DEPTH_TEST);
            GL11.glPushMatrix();
            GL11.glTranslatef(0, 0, 300);
            int k = this.smallFontRenderer.getStringWidth(item.getDisplayName());
            int j2 = mousePosX - k / 2;
            int k2 = mousePosY - 12;
            int i1 = 8;

            if (j2 + k > this.width)
            {
                j2 -= (j2 - this.width + k);
            }

            if (k2 + i1 + 6 > this.height)
            {
                k2 = this.height - i1 - 6;
            }

            int j1 = ColorUtil.to32BitColor(190, 0, 153, 255);
            this.drawGradientRect(j2 - 3, k2 - 4, j2 + k + 3, k2 - 3, j1, j1);
            this.drawGradientRect(j2 - 3, k2 + i1 + 3, j2 + k + 3, k2 + i1 + 4, j1, j1);
            this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 + i1 + 3, j1, j1);
            this.drawGradientRect(j2 - 4, k2 - 3, j2 - 3, k2 + i1 + 3, j1, j1);
            this.drawGradientRect(j2 + k + 3, k2 - 3, j2 + k + 4, k2 + i1 + 3, j1, j1);
            int k1 = ColorUtil.to32BitColor(170, 0, 153, 255);
            int l1 = (k1 & 16711422) >> 1 | k1 & -16777216;
            this.drawGradientRect(j2 - 3, k2 - 3 + 1, j2 - 3 + 1, k2 + i1 + 3 - 1, k1, l1);
            this.drawGradientRect(j2 + k + 2, k2 - 3 + 1, j2 + k + 3, k2 + i1 + 3 - 1, k1, l1);
            this.drawGradientRect(j2 - 3, k2 - 3, j2 + k + 3, k2 - 3 + 1, k1, k1);
            this.drawGradientRect(j2 - 3, k2 + i1 + 2, j2 + k + 3, k2 + i1 + 3, l1, l1);

            this.smallFontRenderer.drawString(item.getDisplayName(), j2, k2, ColorUtil.to32BitColor(255, 255, 255, 255));

            GL11.glPopMatrix();
        }

        String str = "" + amount + "/" + requiredAmount;
        boolean valid = amount >= requiredAmount;

        int color = valid | this.mc.thePlayer.capabilities.isCreativeMode ? ColorUtil.to32BitColor(255, 0, 255, 0) : ColorUtil.to32BitColor(255, 255, 0, 0);
        this.smallFontRenderer.drawString(
                str,
                xPos + 8 - this.smallFontRenderer.getStringWidth(str) / 2,
                //offset + GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 154 + canCreateOffset
                yPos+16, color);


        /* posY = c+154;
         * drawStr=c+170
         * c = posY-154
         * drawStr = posY-154+170
         * drawStr = posY+16
         */

    }

    protected void drawMothershipButton(int mousePosX, int mousePosY)
    {
        int offset=0;
        String str;

        GL11.glColor4f(0.0F, 0.6F, 1.0F, 1);
        this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain1);
        int canCreateLength = Math.max(0, this.drawSplitString(GCCoreUtil.translate("gui.message.canCreateMothership.name"), 0, 0, 91, 0, true, true) - 2);
        int canCreateOffset = canCreateLength * this.smallFontRenderer.FONT_HEIGHT;

        this.drawTexturedModalRect(
                width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 95, // x
                offset+GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 134,        // y
                93, //w
                4,  //h
                159, // u
                102, //v
                93, //uWidth
                4,  //uHeight
                false, false);
        for (int barY = 0; barY < canCreateLength; ++barY)
        {
            this.drawTexturedModalRect(
                    width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 95,
                    offset+GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 138 + barY * this.smallFontRenderer.FONT_HEIGHT,
                    93,
                    this.smallFontRenderer.FONT_HEIGHT, 159, 106, 93, this.smallFontRenderer.FONT_HEIGHT, false, false);
        }
        this.drawTexturedModalRect(width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 95, offset + GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 138 + canCreateOffset, 93, 43, 159, 106, 93, 43, false, false);
        this.drawTexturedModalRect(width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 79, offset + GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 129, 61, 4, 0, 170, 61, 4, false, false);

        int xPos = 0;
        int yPos = offset + GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 154 + canCreateOffset;
        //
        SpaceStationRecipe recipe = RecipeHelper.mothershipRecipe;
        if (recipe != null)
        {
            GL11.glColor4f(0.0F, 1.0F, 0.1F, 1);
            boolean validInputMaterials = true;

            int i = 0;
            for (Map.Entry<Object, Integer> e : recipe.getInput().entrySet())
            {
                Object next = e.getKey();
                xPos = (int)(width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 95 + i * 93 / (double)recipe.getInput().size() + 5);
                // int yPos = GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 154 + canCreateOffset;
                int requiredAmount = e.getValue();

                if (next instanceof ItemStack)
                {
                    int amount = getAmountInInventory((ItemStack) next);
                    drawItemForRecipe(((ItemStack) next).copy(), amount, requiredAmount, xPos, yPos, mousePosX, mousePosY);
                    validInputMaterials = (amount >= requiredAmount && validInputMaterials);

                } // if itemstack
                else if (next instanceof ArrayList)
                {
                    ArrayList<ItemStack> items = (ArrayList<ItemStack>) next;

                    int amount = 0;

                    for (ItemStack stack : items)
                    {
                        amount += getAmountInInventory(stack);
                    }
                    ItemStack stack = items.get((this.ticksSinceMenuOpen / 20) % items.size()).copy();
                    drawItemForRecipe(stack, amount, requiredAmount, xPos, yPos, mousePosX, mousePosY);
                    validInputMaterials = (amount >= requiredAmount && validInputMaterials);
                }

                i++;
            }

            if (validInputMaterials || this.mc.thePlayer.capabilities.isCreativeMode)
            {
                GL11.glColor4f(0.0F, 1.0F, 0.1F, 1);
            }
            else
            {
                GL11.glColor4f(1.0F, 0.0F, 0.0F, 1);
            }

            this.mc.renderEngine.bindTexture(GuiCelestialSelection.guiMain1);

            if (!this.mapMode)
            {
                if (mousePosX >= width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 95 && mousePosX <= width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH && mousePosY >= GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 182 + canCreateOffset && mousePosY <= GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 182 + 12 + canCreateOffset)
                {
                    this.drawTexturedModalRect(
                            width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 95,
                            offset + GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 182 + canCreateOffset,
                            93, 12, 0, 174, 93, 12, false, false);
                }
            }

            this.drawTexturedModalRect(
                    width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 95,
                    offset + GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 182 + canCreateOffset, 93, 12, 0, 174, 93, 12, false, false);

            int color = (int)((Math.sin(this.ticksSinceMenuOpen / 5.0) * 0.5 + 0.5) * 255);
            this.drawSplitString(
                    GCCoreUtil.translate("gui.message.canCreateMothership.name"),
                    width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 48,
                    offset + GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 137, 91, ColorUtil.to32BitColor(255, color, 255, color), true, false);

            if (!mapMode)
            {
                this.drawSplitString(
                        GCCoreUtil.translate("gui.message.createSS.name").toUpperCase(),
                        width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 48, offset + GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 185 + canCreateOffset, 91, ColorUtil.to32BitColor(255, 255, 255, 255), false, false);
            }
        } // if (recipe != null)
        else
        {
            this.drawSplitString(
                    GCCoreUtil.translate("gui.message.cannotCreateSpaceStation.name"),
                    width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 48,
                    offset + GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 138, 91, ColorUtil.to32BitColor(255, 255, 255, 255), true, false);
        }

    }


    @Override
    protected boolean teleportToSelectedBody()
    {
        // now this is important to override
        // TODO only do motherships here
        this.possibleBodies = this.shuttlePossibleBodies;
        if (this.selectedBody != null)
        {
            if (this.selectedBody.getReachable() && this.possibleBodies != null && this.possibleBodies.contains(this.selectedBody))
            {
                try
                {
                    String dimension;

                    if (this.selectedBody instanceof Satellite)
                    {
                        if (this.spaceStationMap == null)
                        {
                            GCLog.severe("Please report as a BUG: spaceStationIDs was null.");
                            return false;
                        }
                        Satellite selectedSatellite = (Satellite) this.selectedBody;
                        Integer mapping = this.spaceStationMap.get(getSatelliteParentID(selectedSatellite)).get(this.selectedStationOwner).getStationDimensionID();
                        //No need to check lowercase as selectedStationOwner is taken from keys.
                        if (mapping == null)
                        {
                            GCLog.severe("Problem matching player name in space station check: " + this.selectedStationOwner);
                            return false;
                        }
                        int spacestationID = mapping;
                        WorldProvider spacestation = WorldUtil.getProviderForDimensionClient(spacestationID);
                        if (spacestation != null)
                        {
                            dimension = spacestation.getDimensionName();
                        }
                        else
                        {
                            GCLog.severe("Failed to find a spacestation with dimension " + spacestationID);
                            return false;
                        }
                    }
                    else
                    {
                        dimension = WorldUtil.getProviderForDimensionClient(this.selectedBody.getDimensionID()).getDimensionName();
                    }

                    if (dimension.contains("$"))
                    {
                        this.mc.gameSettings.thirdPersonView = 0;
                    }

                    AmunRa.packetPipeline.sendToServer(new PacketSimpleAR(PacketSimpleAR.EnumSimplePacket.S_TELEPORT_SHUTTLE, new Object[] { dimension }));
                    //TODO   Some type of clientside "in Space" holding screen here while waiting for the server to do the teleport
                    //(Otherwise the client will be returned to the destination he was in until now, which looks weird)
                    mc.displayGuiScreen(null);
                    return true;
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }


    @Override
    protected void mouseClicked(int x, int y, int button)
    {

        boolean clickHandled = false;
        CelestialBody curSelection = this.selectedBody;

        if (!this.mapMode)
        {
            if (
                    x > width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH - 96 &&
                    x < width - GuiCelestialSelection.BORDER_WIDTH - GuiCelestialSelection.BORDER_EDGE_WIDTH &&
                    y > GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 182 &&
                    y < GuiCelestialSelection.BORDER_WIDTH + GuiCelestialSelection.BORDER_EDGE_WIDTH + 182 + 12
                    ) {
                if (this.selectedBody != null)
                {
                    SpaceStationRecipe recipe = RecipeHelper.mothershipRecipe;
                    if (recipe != null && this.canCreateMothership(this.selectedBody))
                    {
                        if (recipe.matches(this.mc.thePlayer, false) || this.mc.thePlayer.capabilities.isCreativeMode)
                        {
                            AmunRa.packetPipeline.sendToServer(new PacketSimpleAR(PacketSimpleAR.EnumSimplePacket.S_CREATE_MOTHERSHIP, new Object[] {
                                    Mothership.getOrbitableBodyName(this.selectedBody)
                            }));
                            // GalacticraftCore.packetPipeline.sendToServer(new PacketSimple(EnumSimplePacket.S_BIND_SPACE_STATION_ID, new Object[] { this.selectedBody.getDimensionID() }));
                            /*
                            //Zoom in on Overworld to show the new SpaceStation if not already zoomed
                            if (this.selectionCount < 2)
                            {
                                this.selectionCount = 2;
                                this.preSelectZoom = this.zoom;
                                this.preSelectPosition = this.position;
                                this.ticksSinceSelection = 0;
                                this.doneZooming = false;
                            }*/
                        }

                        clickHandled = true;
                    }
                }
            }
        }

        if(!clickHandled) {
            // hackfix for mothership parent selection
            CelestialBody prevSelection = this.selectedBody;
            int prevTicksSelection = this.ticksSinceSelection;
            int prevTicksUnSelection = this.ticksSinceUnselection;
            super.mouseClicked(x, y, button);
            if(prevSelection instanceof Mothership && this.selectedBody != prevSelection) {
                // not sure why, but...
                if(prevSelection instanceof IChildBody) {
                    this.selectionCount = 1;
                } else {
                    this.selectionCount = 2;
                }
                this.lastSelectedBody = prevSelection;
                this.preSelectZoom = this.zoom;
                this.preSelectPosition = this.position;
                this.ticksSinceSelection = 0;
                //this.ticksSinceUnselection = prevTicksUnSelection;
                //this.ticksSinceSelection = prevTicksSelection;
                this.doneZooming = false;
            }
        }

    }


    /*
    @Override
    protected void mouseClicked(int x, int y, int button) {
        super.mouseClicked(x, y, button);
    }
     */

}
