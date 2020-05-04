package de.katzenpapst.amunra.proxy;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import de.katzenpapst.amunra.AmunRa;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import net.minecraft.server.MinecraftServer;

public class ServerProxy extends ARSidedProxy {

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        try {
            MinecraftServer s = MinecraftServer.getServer();
            if(s.isDedicatedServer() && !s.isServerInOnlineMode() && AmunRa.config.mothershipUserMatchUUID) {
                GCLog.info("Server running in offline mode. Setting \"matchUsersByUUID\" to false");
                AmunRa.config.mothershipUserMatchUUID = false;
            }
        } catch (NullPointerException e) {
            GCLog.info("Could not detect whenever server is in online mode. If it's not, please manually set \"matchUsersByUUID\" to false");
        }
    }

}
