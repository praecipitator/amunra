package de.katzenpapst.amunra.network;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.network.NetworkRegistry;
import de.katzenpapst.amunra.AmunRa;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.ChannelHandler.Sharable;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.network.IPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.INetHandler;
import net.minecraft.network.NetHandlerPlayServer;

@Sharable
public class ARPacketHandler extends SimpleChannelInboundHandler<IPacket> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, IPacket msg) throws Exception
    {
        INetHandler netHandler = ctx.channel().attr(NetworkRegistry.NET_HANDLER).get();
        //this should work
        // EntityPlayer player = getPlayerFromNetHandler(netHandler);
        EntityPlayer player = GalacticraftCore.proxy.getPlayerFromNetHandler(netHandler);

        switch (FMLCommonHandler.instance().getEffectiveSide())
        {
        case CLIENT:
            msg.handleClientSide(player);
            break;
        case SERVER:
            msg.handleServerSide(player);
            break;
        default:
            break;
        }
    }

    protected EntityPlayer getPlayerFromNetHandler(INetHandler handler)
    {
        if (handler instanceof NetHandlerPlayServer)
        {
            return ((NetHandlerPlayServer) handler).playerEntity;
        }
        else
        {
            return null;
        }
    }

}



