package de.katzenpapst.amunra.entity.spaceship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.helper.CoordHelper;
import de.katzenpapst.amunra.helper.ShuttleTeleportHelper;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR.EnumSimplePacket;
import de.katzenpapst.amunra.tile.TileEntityShuttleDock;
import de.katzenpapst.amunra.vec.Vector3int;
import de.katzenpapst.amunra.world.ShuttleDockHandler;
import de.katzenpapst.amunra.world.WorldHelper;
import io.netty.buffer.ByteBuf;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket;
import micdoodle8.mods.galacticraft.api.tile.IFuelDock;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.api.world.IZeroGDimension;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.tile.TileEntityMulti;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;

public class EntityShuttle extends EntityTieredRocket {

    protected boolean doKnowOnWhatImStanding = false;
    protected boolean isOnBareGround = false;

    protected int numTanks = 0;

    //protected Vector3int targetDockPosition = null;

    // so, apparently, there is no real way to figure out when an entity has been dismounted
    protected Entity prevRiddenByEntity = null;

    protected Vector3int dockPosition = null;

    public EntityShuttle(World par1World) {
        super(par1World);

        this.setSize(1.2F, 5.5F);
        this.yOffset = 1.5F;
    }

    public EntityShuttle(World world, double posX, double posY, double posZ, int type) {
        super(world, posX, posY, posZ);
        //this.rocketType = type;
        this.setSize(1.2F, 3.5F);
        this.yOffset = 1.5F;
        decodeItemDamage(type);
        this.cargoItems = new ItemStack[this.getSizeInventory()];
        fuelTank = new FluidTank(getFuelCapacityFromDamage(type));
    }

    public void setTargetDock(Vector3int dockPos) {
        this.targetVec = dockPos.toBlockVec3();
    }

    protected void decodeItemDamage(int dmg) {

        rocketType = getRocketTypeFromDamage(dmg);
        numTanks = getNumTanksFromDamage(dmg);
    }

    protected int encodeItemDamage() {
        /*
        if(this.rocketType == EnumRocketType.PREFUELED) {
            return 15; // 1111 = 12+3
        }*/

        return encodeItemDamage(this.rocketType.ordinal(), this.numTanks);
    }

    public void setLanding() {
        this.landing = true;
        this.launchPhase = EnumLaunchPhase.LAUNCHED.ordinal();
    }

    public static int encodeItemDamage(int numChests, int numTanks) {
        return numChests | (numTanks << 2);
    }

    public static int getFuelCapacityFromDamage(int damage) {
        int numTanks = getNumTanksFromDamage(damage);
        return (1000 + 500 * numTanks) * ConfigManagerCore.rocketFuelFactor;
    }

    public static EnumRocketType getRocketTypeFromDamage(int damage) {
        return EnumRocketType.values()[getNumChestsFromDamage(damage)];
    }

    public static boolean isPreFueled(int damage) {
        return damage == 15;
    }

    public static int getNumChestsFromDamage(int damage) {
        return damage & 3;
    }

    public static int getNumTanksFromDamage(int damage) {
        return (damage >> 2) & 3;
    }

    @Override
    public void decodePacketdata(ByteBuf buffer)
    {
        this.numTanks = buffer.readInt();
        super.decodePacketdata(buffer);
    }

    @Override
    public void getNetworkedData(ArrayList<Object> list)
    {
        list.add(this.numTanks);
        super.getNetworkedData(list);
    }



    public EntityShuttle(World par1World, double par2, double par4, double par6, boolean reversed, int rocketType, ItemStack[] inv)
    {
        this(par1World, par2, par4, par6, rocketType);
        this.cargoItems = inv;
    }

    @Override
    public int getSizeInventory()
    {
        if (this.rocketType == null) return 2;
        /*if(this.rocketType == EnumRocketType.PREFUELED) {
            return 56;
        }*/
        return this.rocketType.getInventorySpace();
    }

    public void setCargoContents(ItemStack[] newCargo) {

        cargoItems = new ItemStack[this.getSizeInventory()];
        int curIndex = 0;

        for(int i=0;i<newCargo.length;i++) {
            if(newCargo[i] == null) {
                continue;
            }
            cargoItems[curIndex] = newCargo[i].copy();
            curIndex++;
        }
        this.markDirty();
    }

    /**
     * Return the full item representation of the entity, including type, fuel, and whatever else
     * @return
     */
    public ItemStack getItemRepresentation()
    {
        ItemStack rocket = new ItemStack(ARItems.shuttleItem, 1, this.encodeItemDamage());
        rocket.setTagCompound(new NBTTagCompound());
        rocket.getTagCompound().setInteger("RocketFuel", this.fuelTank.getFluidAmount());

        return rocket;
        //return new ItemStack(ARItems.shuttleItem, 1, this.encodeItemDamage());
    }

    @Override
    public ItemStack getPickedResult(MovingObjectPosition target)
    {
        return new ItemStack(ARItems.shuttleItem, 1, this.encodeItemDamage());
    }

    @Override
    public int getRocketTier() {
        // Keep it at 0, the shuttle can't reach most stuff
        return 0;
    }

    @Override
    public float getCameraZoom() {
        return 15.0F;
    }

    @Override
    public boolean defaultThirdPerson() {
        return true;
    }

    @Override
    public int getFuelTankCapacity() {
        return 1000 + 500 * this.numTanks;
    }

    @Override
    public int getPreLaunchWait() {
        return 400;
    }

    @Override
    public double getOnPadYOffset()
    {
        return 1.6D;
        //return 2.4D;
    }

    /**
     * This gets added onto getOnPadYOffset
     * @return
     */
    public double getOnGroundYOffset() {
        return 1.0D;
    }

    public double getDistanceFromGround() {
        return 2.8D;
    }

    @Override
    public double getMountedYOffset()
    {
        return 0.0D;
    }

    private void makeFlame(double x2, double y2, double z2, Vector3 motionVec, boolean getLaunched)
    {
        if (getLaunched)
        {
            GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2 + 0.4 - this.rand.nextDouble() / 10, y2, z2 + 0.4 - this.rand.nextDouble() / 10), motionVec, new Object[] { riddenByEntity });
            GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2 - 0.4 + this.rand.nextDouble() / 10, y2, z2 + 0.4 - this.rand.nextDouble() / 10), motionVec, new Object[] { riddenByEntity });
            GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2 - 0.4 + this.rand.nextDouble() / 10, y2, z2 - 0.4 + this.rand.nextDouble() / 10), motionVec, new Object[] { riddenByEntity });
            GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2 + 0.4 - this.rand.nextDouble() / 10, y2, z2 - 0.4 + this.rand.nextDouble() / 10), motionVec, new Object[] { riddenByEntity });
            GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2, y2, z2), motionVec, new Object[] { riddenByEntity });
            GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2 + 0.4, y2, z2), motionVec, new Object[] { riddenByEntity });
            GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2 - 0.4, y2, z2), motionVec, new Object[] { riddenByEntity });
            GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2, y2, z2 + 0.4D), motionVec, new Object[] { riddenByEntity });
            GalacticraftCore.proxy.spawnParticle("launchFlameLaunched", new Vector3(x2, y2, z2 - 0.4D), motionVec, new Object[] { riddenByEntity });
            return;
        }

        double x1 = motionVec.x;
        double y1 = motionVec.y;
        double z1 = motionVec.z;
        GalacticraftCore.proxy.spawnParticle("launchFlameIdle", new Vector3(x2 + 0.4 - this.rand.nextDouble() / 10, y2, z2 + 0.4 - this.rand.nextDouble() / 10), new Vector3(x1 + 0.5D, y1 - 0.3D, z1 + 0.5D), new Object[] { riddenByEntity });
        GalacticraftCore.proxy.spawnParticle("launchFlameIdle", new Vector3(x2 - 0.4 + this.rand.nextDouble() / 10, y2, z2 + 0.4 - this.rand.nextDouble() / 10), new Vector3(x1 - 0.5D, y1 - 0.3D, z1 + 0.5D), new Object[] { riddenByEntity });
        GalacticraftCore.proxy.spawnParticle("launchFlameIdle", new Vector3(x2 - 0.4 + this.rand.nextDouble() / 10, y2, z2 - 0.4 + this.rand.nextDouble() / 10), new Vector3(x1 - 0.5D, y1 - 0.3D, z1 - 0.5D), new Object[] { riddenByEntity });
        GalacticraftCore.proxy.spawnParticle("launchFlameIdle", new Vector3(x2 + 0.4 - this.rand.nextDouble() / 10, y2, z2 - 0.4 + this.rand.nextDouble() / 10), new Vector3(x1 + 0.5D, y1 - 0.3D, z1 - 0.5D), new Object[] { riddenByEntity });
        GalacticraftCore.proxy.spawnParticle("launchFlameIdle", new Vector3(x2 + 0.4, y2, z2), new Vector3(x1 + 0.8D, y1 - 0.3D, z1), new Object[] { riddenByEntity });
        GalacticraftCore.proxy.spawnParticle("launchFlameIdle", new Vector3(x2 - 0.4, y2, z2), new Vector3(x1 - 0.8D, y1 - 0.3D, z1), new Object[] { riddenByEntity });
        GalacticraftCore.proxy.spawnParticle("launchFlameIdle", new Vector3(x2, y2, z2 + 0.4D), new Vector3(x1, y1 - 0.3D, z1 + 0.8D), new Object[] { riddenByEntity });
        GalacticraftCore.proxy.spawnParticle("launchFlameIdle", new Vector3(x2, y2, z2 - 0.4D), new Vector3(x1, y1 - 0.3D, z1 - 0.8D), new Object[] { riddenByEntity });
    }

    protected void spawnParticles(boolean launched)
    {
        if (!this.isDead)
        {
            double x1 = 3.2 * Math.cos(this.rotationYaw / 57.2957795D) * Math.sin(this.rotationPitch / 57.2957795D);
            double z1 = 3.2 * Math.sin(this.rotationYaw / 57.2957795D) * Math.sin(this.rotationPitch / 57.2957795D);
            double y1 = 3.2 * Math.cos((this.rotationPitch - 180) / 57.2957795D);
            if (this.landing && this.targetVec != null)
            {
                double modifier = this.posY - this.targetVec.y;
                modifier = Math.max(modifier, 1.0);
                x1 *= modifier / 60.0D;
                y1 *= modifier / 60.0D;
                z1 *= modifier / 60.0D;
            }

            final double y2 = this.prevPosY + (this.posY - this.prevPosY) + y1;

            final double x2 = this.posX + x1;
            final double z2 = this.posZ + z1;
            Vector3 motionVec = new Vector3(x1, y1, z1);
            Vector3 d1 = new Vector3(y1 * 0.1D, -x1 * 0.1D, z1 * 0.1D).rotate(315 - this.rotationYaw, motionVec);
            Vector3 d2 = new Vector3(x1 * 0.1D, -z1 * 0.1D, y1 * 0.1D).rotate(315 - this.rotationYaw, motionVec);
            Vector3 d3 = new Vector3(-y1 * 0.1D, x1 * 0.1D, z1 * 0.1D).rotate(315 - this.rotationYaw, motionVec);
            Vector3 d4 = new Vector3(x1 * 0.1D, z1 * 0.1D, -y1 * 0.1D).rotate(315 - this.rotationYaw, motionVec);
            Vector3 mv1 = motionVec.clone().translate(d1);
            Vector3 mv2 = motionVec.clone().translate(d2);
            Vector3 mv3 = motionVec.clone().translate(d3);
            Vector3 mv4 = motionVec.clone().translate(d4);
            //T3 - Four flameballs which spread
            makeFlame(x2 + d1.x, y2 + d1.y, z2 + d1.z, mv1, this.getLaunched());
            makeFlame(x2 + d2.x, y2 + d2.y, z2 + d2.z, mv2, this.getLaunched());
            makeFlame(x2 + d3.x, y2 + d3.y, z2 + d3.z, mv3, this.getLaunched());
            makeFlame(x2 + d4.x, y2 + d4.y, z2 + d4.z, mv4, this.getLaunched());
        }
    }

    @Override
    protected void failRocket()
    {
        if(shouldCancelExplosion() && this.landing && this.launchPhase == EnumLaunchPhase.LAUNCHED.ordinal()) {
            // seems like I just landed
            this.launchPhase = EnumLaunchPhase.UNIGNITED.ordinal();
            this.landing = false;

            return;
        }

        super.failRocket();
    }

    protected void repositionMountedPlayer(Entity player) {
        if(!(player instanceof EntityPlayer)) {
            return;
        }
        if(this.getLandingPad() != null && this.getLandingPad() instanceof TileEntityShuttleDock) {
            // just rotate the player away from the dock
            TileEntityShuttleDock dock = ((TileEntityShuttleDock)this.getLandingPad());
            ((EntityPlayer)player).rotationYaw = dock.getExitRotation();
            ((EntityPlayer)player).setPositionAndUpdate(player.posX, player.posY, player.posZ);
        }
    }

    protected void repositionDismountedPlayer(Entity player) {
        if(!(player instanceof EntityPlayer)) {
            return;
        }
        if(this.getLandingPad() != null && this.getLandingPad() instanceof TileEntityShuttleDock) {
            TileEntityShuttleDock dock = ((TileEntityShuttleDock)this.getLandingPad());
            Vector3 pos = dock.getExitPosition();
            ((EntityPlayer)player).rotationYaw = dock.getExitRotation();
            ((EntityPlayer)player).setPositionAndUpdate(pos.x, pos.y, pos.z);
            //player.setPositionAndRotation(pos.x, pos.y, pos.z, 0, 0);
            //player.setPosition(pos.x, pos.y, pos.z);
        } else {

            ((EntityPlayer)player).setPositionAndUpdate(this.posX, this.posY-this.getYOffset(), this.posZ - 2.5D);
        }
        // return new Vector3(this.posX, this.posY, this.posZ);
    }


    protected boolean isSafeForPlayer(double x, double y, double z)
    {
        int y1 = (int)y;


        return WorldHelper.isNonSolid(worldObj, (int)x, y1, (int)z) && WorldHelper.isNonSolid(worldObj, (int)x, y1+1, (int)z) && WorldHelper.isSolid(worldObj, (int)x, y1-1, (int)z, true);

        //return true;
        //this.worldObj.isAirBlock(x, y, y);
    }


    protected void tryFindAnotherDock() {
        Vector3int dock = ShuttleDockHandler.findAvailableDock(this.worldObj.provider.dimensionId);
        if(dock != null) {

            // reposition myself a little to be above it
            double yBak = this.posY;
            this.setPosition(dock.x, yBak, dock.z);
            targetVec = dock.toBlockVec3();
        } else {
            targetVec = null;
        }
    }

    protected void tryToDock() {
        int chunkx = CoordHelper.blockToChunk(targetVec.x);
        int chunkz = CoordHelper.blockToChunk(targetVec.z);
        if (worldObj.getChunkProvider().chunkExists(chunkx, chunkz)) {

            TileEntity te = targetVec.getTileEntity(worldObj);
            if(te != null && te instanceof IFuelDock) {

                if(te instanceof TileEntityShuttleDock) {
                    if(((TileEntityShuttleDock)te).isAvailable()) {

                        // finally
                        ((TileEntityShuttleDock)te).dockEntity(this);
                    } else {
                        tryFindAnotherDock();
                    }
                } else {
                    // just a regular dock. oh well
                    return;
                }

            } else {
                // attempt to find another one?
                tryFindAnotherDock();
            }
        } // otherwise wait
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void onUpdate()
    {
        super.onUpdate();

        // handle player dismounting
        if(!worldObj.isRemote) {

            if(prevRiddenByEntity != riddenByEntity) {
                if(riddenByEntity == null && prevRiddenByEntity != null) {
                    // seems like someone just dismounted
                    //playerRepositionTicks = 20;
                    repositionDismountedPlayer(prevRiddenByEntity);
                    //repositioningEntity = prevRiddenByEntity;
                } else if(prevRiddenByEntity == null && riddenByEntity != null) {
                    repositionMountedPlayer(riddenByEntity);
                }
                prevRiddenByEntity = riddenByEntity;
            }


            // try this
            if(landing && targetVec != null) {
                tryToDock();
            }
        }

        int i;

        if (this.timeUntilLaunch >= 100)
        {
            i = Math.abs(this.timeUntilLaunch / 100);
        }
        else
        {
            i = 1;
        }

        if ((this.getLaunched() || this.launchPhase == EnumLaunchPhase.IGNITED.ordinal() && this.rand.nextInt(i) == 0) && !ConfigManagerCore.disableSpaceshipParticles && this.hasValidFuel())
        {
            if (this.worldObj.isRemote)
            {
                this.spawnParticles(this.getLaunched());
            }
        }

        if(this.getLaunched()) {
            // failsafe
            if(this.riddenByEntity == null) {
                this.landing = true; // go back
            }

            if (this.hasValidFuel())
            {
                if (!this.landing)
                {
                    double d = this.timeSinceLaunch / 150;

                    d = Math.min(d, 1);

                    if (d != 0.0)
                    {
                        this.motionY = -d * 2.0D * Math.cos((this.rotationPitch - 180) * Math.PI / 180.0D);
                    }
                }
                else
                {
                    this.motionY -= 0.008D;
                }

                double multiplier = 1.0D;

                if (this.worldObj.provider instanceof IGalacticraftWorldProvider)
                {
                    multiplier = ((IGalacticraftWorldProvider) this.worldObj.provider).getFuelUsageMultiplier();

                    if (multiplier <= 0)
                    {
                        multiplier = 1;
                    }
                }

                if (this.timeSinceLaunch % MathHelper.floor_double(2 * (1 / multiplier)) == 0)
                {
                    this.removeFuel(1);
                    if (!this.hasValidFuel())
                        this.stopRocketSound();
                }
            }
            else
            {
                // no valid fuel
                // enter landing mode
                this.landing = true;


                if (!this.worldObj.isRemote)
                {
                    if (Math.abs(Math.sin(this.timeSinceLaunch / 1000)) / 10 != 0.0)
                    {
                        this.motionY -= Math.abs(Math.sin(this.timeSinceLaunch / 1000)) / 20;
                    }
                }
            }
        }

        if(this.launchPhase == EnumLaunchPhase.UNIGNITED.ordinal()) {

            checkStandingPosition();

        }
    }

    @Override
    public void stopRocketSound()
    {
        super.stopRocketSound();
        this.rocketSoundUpdater = null; // I hope this works
    }

    protected void checkStandingPosition()
    {
        // hm
        if(this.worldObj.isRemote) {
            return;
        }

        // recheck this from time to time anyway
        if(doKnowOnWhatImStanding && this.ticksExisted % 40 != 0) {
            return;
        }

        if(this.getLandingPad() != null) {
            doKnowOnWhatImStanding = true;
        } else {
            if(dockPosition != null) {
                TileEntity tile = worldObj.getTileEntity(dockPosition.x, dockPosition.y, dockPosition.z);
                if(tile != null) {
                    if(tile instanceof IFuelDock) {
                        this.setPad((IFuelDock) tile);
                        this.landEntity(tile);
                        dockPosition = null;
                        return;
                    } else {
                        // something went wrong
                        dockPosition = null;
                    }
                }
            }

            boolean isInZeroG = false;
            if(this.worldObj.provider instanceof IZeroGDimension) {
                isInZeroG = true;
            }

            // let's look downward
            //this.posY is about 3 blocks above the baseline
            int bX = (int)(this.posX-0.5D);
            int bY = (int)(this.posY-0.5D-1);
            int bZ = (int)(this.posZ-0.5D);

            Vector3int highest = WorldHelper.getHighestNonEmptyBlock(worldObj, bX-1, bX+1, bY-5, bY, bZ-1, bZ+1);

            if(highest != null) {
                TileEntity tileBelow = worldObj.getTileEntity(highest.x, highest.y, highest.z);
                IFuelDock dockTile = null;
                if(tileBelow != null) {
                    if (tileBelow instanceof TileEntityMulti) {
                        tileBelow = ((TileEntityMulti)tileBelow).getMainBlockTile();
                    }
                    if(tileBelow instanceof IFuelDock) {
                        dockTile = (IFuelDock)tileBelow;
                    }
                }
                if(dockTile != null) {
                    isOnBareGround = false;
                    doKnowOnWhatImStanding = true;
                    if(this.getLandingPad() != dockTile) {
                        //((IFuelDock) dockTile).dockEntity(this);
                        this.landEntity((TileEntity)dockTile);
                        //this.setPad(dockTile);
                    }
                } else {
                    isOnBareGround = true;
                    doKnowOnWhatImStanding = true;
                    if(!isInZeroG) {
                        adjustGroundPosition(highest.y);
                    }
                }
            } else {
                if(!isInZeroG) {
                    // make the rocket land
                    this.setLanding();
                }
            }
        }
    }

    @Override
    public void landEntity(int x, int y, int z)
    {
        TileEntity tile = this.worldObj.getTileEntity(x, y, z);

        landEntity(tile);
    }

    public void landEntity(TileEntity tile)
    {

        if (tile instanceof IFuelDock)
        {
            IFuelDock dock = (IFuelDock) tile;

            if (this.isDockValid(dock))
            {
                if (!this.worldObj.isRemote)
                {
                    //Drop any existing rocket on the landing pad
                    if (dock.getDockedEntity() instanceof EntitySpaceshipBase && dock.getDockedEntity() != this)
                    {
                        ((EntitySpaceshipBase)dock.getDockedEntity()).dropShipAsItem();
                        ((EntitySpaceshipBase)dock.getDockedEntity()).setDead();
                    }

                    this.setPad(dock);
                }

                this.onRocketLand(tile.xCoord, tile.yCoord, tile.zCoord);
            }
        }
    }

    @Override
    public void setPad(IFuelDock pad) {
        isOnBareGround = false;
        doKnowOnWhatImStanding = true;
        super.setPad(pad);
    }



    protected void adjustGroundPosition(int blockYPos) {
        // posY = distance-blockYPos
        this.setPosition(this.posX, getDistanceFromGround()+blockYPos, this.posZ);
        //double distance = this.posY-blockYPos;
    }

    @Override
    public void onReachAtmosphere()
    {
        //Not launch controlled
        if (this.riddenByEntity != null && !this.worldObj.isRemote)
        {
            if (this.riddenByEntity instanceof EntityPlayerMP)
            {
                EntityPlayerMP player = (EntityPlayerMP) this.riddenByEntity;

                this.onTeleport(player);
                GCPlayerStats stats = this.setGCPlayerStats(player);

                // this is the part which activates the celestial gui
                toCelestialSelection(player, stats, this.getRocketTier());

            }
        }

        //Destroy any rocket which reached the top of the atmosphere and is not controlled by a Launch Controller
        this.setDead();
    }

    public GCPlayerStats setGCPlayerStats(EntityPlayerMP player) {
        GCPlayerStats stats = GCPlayerStats.get(player);

        if (this.cargoItems == null || this.cargoItems.length == 0)
        {
            stats.rocketStacks = new ItemStack[2];
        }
        else
        {
            stats.rocketStacks = this.cargoItems;
        }

        stats.rocketType = this.encodeItemDamage();
        stats.rocketItem = ARItems.shuttleItem;
        stats.fuelLevel = this.fuelTank.getFluidAmount();
        return stats;
    }

    public static void toCelestialSelection(EntityPlayerMP player, GCPlayerStats stats, int tier)
    {
        toCelestialSelection(player, stats, tier, true);
    }

    public static void toCelestialSelection(EntityPlayerMP player, GCPlayerStats stats, int tier, boolean useFakeEntity)
    {
        player.mountEntity(null);
        stats.spaceshipTier = tier;
        // replace this with my own stuff. this must only contain the nearby stuff
        HashMap<String, Integer> map = ShuttleTeleportHelper.getArrayOfPossibleDimensions(player);
        String dimensionList = "";
        int count = 0;
        for (Entry<String, Integer> entry : map.entrySet())
        {
            dimensionList = dimensionList.concat(entry.getKey() + (count < map.entrySet().size() - 1 ? "?" : ""));
            count++;
        }

        AmunRa.packetPipeline.sendTo(new PacketSimpleAR(EnumSimplePacket.C_OPEN_SHUTTLE_GUI, new Object[] { player.getGameProfile().getName(), dimensionList }), player);
        // do not use this for the shuttle
        stats.usingPlanetSelectionGui = false;
        stats.savedPlanetList = new String(dimensionList);

        if(useFakeEntity) {
            Entity fakeEntity = new EntityShuttleFake(player.worldObj, player.posX, player.posY, player.posZ, 0.0F);
            player.worldObj.spawnEntityInWorld(fakeEntity);
            player.mountEntity(fakeEntity);
        }
    }

    @Override
    public List<ItemStack> getItemsDropped(List<ItemStack> droppedItems)
    {
        super.getItemsDropped(droppedItems);
        ItemStack rocket = getItemRepresentation();
        droppedItems.add(rocket);
        return droppedItems;
    }

    public List<ItemStack> getCargoContents()
    {
        ArrayList<ItemStack> droppedItemList = new ArrayList<ItemStack> ();
        if (this.cargoItems != null)
        {
            for (final ItemStack item : this.cargoItems)
            {
                if (item != null)
                {
                    droppedItemList.add(item);
                }
            }
        }

        return droppedItemList;
    }


    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("NumTanks", numTanks);

        if(this.getLandingPad() != null) {
            Vector3int pos = new Vector3int((TileEntity) this.getLandingPad());
            nbt.setTag("dockPosition", pos.toNBT());
            //pos.toNBT()
        }

        super.writeEntityToNBT(nbt);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        //EnumShuttleMode.
        //this.shuttleMode = EnumShuttleMode.values()[nbt.getInteger("ShuttleMode")];
        //this.setShuttleMode(shuttleMode);
        this.numTanks = nbt.getInteger("NumTanks");
        if(nbt.hasKey("dockPosition")) {
            NBTTagCompound dockPosNbt = nbt.getCompoundTag("dockPosition");
            if(dockPosNbt != null) {
                dockPosition = new Vector3int(dockPosNbt);
            }
        }

        super.readEntityFromNBT(nbt);
    }

}
