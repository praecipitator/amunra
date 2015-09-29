package de.katzenpapst.amunra.proxy;

import net.minecraftforge.common.MinecraftForge;
import micdoodle8.mods.galacticraft.planets.PlanetsProxy;
import micdoodle8.mods.galacticraft.planets.asteroids.AsteroidsModuleClient;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import de.katzenpapst.amunra.event.SystemRenderEventHandler;

public class ClientProxy extends ARSidedProxy {
	@Override
    public void preInit(FMLPreInitializationEvent event)
    {
	
        /*GalacticraftPlanets.clientModules.put(GalacticraftPlanets.MODULE_KEY_MARS, new MarsModuleClient());
        GalacticraftPlanets.clientModules.put(GalacticraftPlanets.MODULE_KEY_ASTEROIDS, new AsteroidsModuleClient());

        super.preInit(event);

        for (IPlanetsModuleClient module : GalacticraftPlanets.clientModules.values())
        {
            module.preInit(event);
        }*/
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
    	SystemRenderEventHandler clientEventHandler = new SystemRenderEventHandler();
        FMLCommonHandler.instance().bus().register(clientEventHandler);
        MinecraftForge.EVENT_BUS.register(clientEventHandler);
    }
/*
    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);

        for (IPlanetsModuleClient module : GalacticraftPlanets.clientModules.values())
        {
            module.postInit(event);
        }
    }*/
}
