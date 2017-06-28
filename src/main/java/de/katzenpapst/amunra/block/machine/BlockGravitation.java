package de.katzenpapst.amunra.block.machine;

import java.util.Random;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.GuiIds;
import de.katzenpapst.amunra.block.BlockMachineMeta;
import de.katzenpapst.amunra.block.SubBlockMachine;
import de.katzenpapst.amunra.helper.CoordHelper;
import de.katzenpapst.amunra.proxy.ARSidedProxy.ParticleType;
import de.katzenpapst.amunra.tile.TileEntityGravitation;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockGravitation extends SubBlockMachine {

    private String backTexture;
    private String sideTexture;
    private String activeTexture;

    private IIcon backIcon = null;
    private IIcon sideIcon = null;
    private IIcon activeIcon = null;


    public BlockGravitation(String name, String frontInactiveTexture, String activeTexture, String sideTexture, String backTexture) {
        super(name, frontInactiveTexture);

        this.backTexture = backTexture;
        this.sideTexture = sideTexture;
        this.activeTexture = activeTexture;
    }

    @Override
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
        super.registerBlockIcons(par1IconRegister);
        backIcon = par1IconRegister.registerIcon(backTexture);
        sideIcon = par1IconRegister.registerIcon(sideTexture);
        activeIcon = par1IconRegister.registerIcon(activeTexture);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta)
    {
        int realMeta = ((BlockMachineMeta)this.parent).getRotationMeta(meta);

        ForgeDirection front = CoordHelper.rotateForgeDirection(ForgeDirection.SOUTH, realMeta);
        ForgeDirection back = CoordHelper.rotateForgeDirection(ForgeDirection.NORTH, realMeta);

        if(side == front.ordinal()) {
            return this.blockIcon;
        }
        if(side == back.ordinal()) {
            return this.backIcon;
        }
        return this.sideIcon;
    }


    @Override
    public TileEntity createTileEntity(World world, int metadata)
    {
        return new TileEntityGravitation();
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public boolean onMachineActivated(World world, int x, int y, int z, EntityPlayer entityPlayer, int side, float hitX, float hitY, float hitZ)
    {
        entityPlayer.openGui(AmunRa.instance, GuiIds.GUI_GRAVITY, world, x, y, z);
        return true;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World par1World, int x, int y, int z, Random rand)
    {
        boolean test = true;
        if (par1World.getTileEntity(x, y, z) instanceof TileEntityGravitation)
        {
            TileEntityGravitation tile = (TileEntityGravitation)par1World.getTileEntity(x, y, z);
            if(tile.isRunning()) {
                for (int particleCount = 0; particleCount < 10; particleCount++)
                {
                    double x2 = x + rand.nextFloat();
                    double y2 = y + rand.nextFloat();
                    double z2 = z + rand.nextFloat();
                    double mX = 0.0D;
                    double mY = 0.0D;
                    double mZ = 0.0D;
                    int dir = rand.nextInt(2) * 2 - 1;
                    mX = 0;//(rand.nextFloat() - 0.5D) * 0.5D;
                    mY = (rand.nextFloat() - 0.5D) * 0.5D;
                    mZ = 0;//(rand.nextFloat() - 0.5D) * 0.5D;

                    final int var2 = par1World.getBlockMetadata(x, y, z);

                    if (var2 == 3 || var2 == 2)
                    {
                        x2 = x + 0.5D + 0.25D * dir;
                        mX = rand.nextFloat() * 2.0F * dir;
                    }
                    else
                    {
                        z2 = z + 0.5D + 0.25D * dir;
                        mZ = rand.nextFloat() * 2.0F * dir;
                    }

                    if(test) {
                        AmunRa.proxy.spawnParticles(ParticleType.PT_GRAVITY_DUST, par1World, new Vector3(x+0.5, y+0.5, z+0.5), new Vector3(mX, tile.getGravityForce(), mZ));
                    } else {
                        GalacticraftCore.proxy.spawnParticle("oxygen", new Vector3(x2, y2, z2), new Vector3(mX, mY, mZ), new Object[] { new Vector3(0.7D, 0.7D, 1.0D) });
                    }
                }
            }
        }
    }
}
