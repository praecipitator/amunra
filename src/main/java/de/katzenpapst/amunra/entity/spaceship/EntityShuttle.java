package de.katzenpapst.amunra.entity.spaceship;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.ShuttleTeleportHelper;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR;
import de.katzenpapst.amunra.network.packet.PacketSimpleAR.EnumSimplePacket;
import io.netty.buffer.ByteBuf;
import micdoodle8.mods.galacticraft.api.entity.IRocketType.EnumRocketType;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntityTieredRocket;
import micdoodle8.mods.galacticraft.api.tile.IFuelDock;
import micdoodle8.mods.galacticraft.api.prefab.entity.EntitySpaceshipBase.EnumLaunchPhase;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IGalacticraftWorldProvider;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.blocks.GCBlocks;
import micdoodle8.mods.galacticraft.core.entities.EntityCelestialFake;
import micdoodle8.mods.galacticraft.core.entities.player.GCPlayerStats;
import micdoodle8.mods.galacticraft.core.network.PacketDynamic;
import micdoodle8.mods.galacticraft.core.network.PacketEntityUpdate;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCLog;
import micdoodle8.mods.galacticraft.core.util.WorldUtil;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidTank;

public class EntityShuttle extends EntityTieredRocket {

    protected boolean doKnowOnWhatImStanding = false;
    protected boolean isOnBareGround = false;

    protected int numTanks = 0;

    // NOW TRY THIS
    // first two bits: num chetsts
    // next two bits: num tanks
    // 3 chests and 3 tanks won't be possible, maybe reserve it for the creative rocket
    // I'll simply won't use prefueled


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
        return 1.4D;
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


    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public void onUpdate()
    {
        super.onUpdate();

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
            if(!doKnowOnWhatImStanding) {
                checkStandingPosition();
            }
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
        if(this.getLandingPad() != null) {
            doKnowOnWhatImStanding = true;
        } else {
            // let's look downward
            for(int iy=0;iy<5;iy++) {

                int bX = (int)(this.posX-0.5D);
                int bY = (int)(this.posY-0.5D-iy);
                int bZ = (int)(this.posZ-0.5D);

                Block b = worldObj.getBlock(bX, bY, bZ);
                //int meta = worldObj.getBlockMetadata((int)(this.posX-0.5D), (int)(this.posY-0.5D-iy), (int)(this.posZ-0.5D));

                if(b.isAir(worldObj, bX, bY, bZ)) {
                    continue;
                } else {
                    // whatever it is, it determines where we are
                    TileEntity tileBelow = worldObj.getTileEntity(bX, bY, bZ);
                    if(tileBelow == null || !(tileBelow instanceof IFuelDock)) {
                        isOnBareGround = true;
                        doKnowOnWhatImStanding = true;
                        adjustGroundPosition(bY);
                    } else {
                        isOnBareGround = false;
                        doKnowOnWhatImStanding = true;
                    }
                    break;
                }
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
        /*
        if(this.shuttleMode != EnumShuttleMode.ROCKET) {
            return;
        }*/
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
        player.mountEntity(null);
        stats.spaceshipTier = tier;
        // replace this with my own stuff. this must only contain the nearby stuff
        HashMap<String, Integer> map = ShuttleTeleportHelper.getArrayOfPossibleDimensions(player); // WorldUtil.getArrayOfPossibleDimensions(tier, player);
        String dimensionList = "";
        int count = 0;
        for (Entry<String, Integer> entry : map.entrySet())
        {
            dimensionList = dimensionList.concat(entry.getKey() + (count < map.entrySet().size() - 1 ? "?" : ""));
            count++;
        }

        // now also add all the current spaceships


        AmunRa.packetPipeline.sendTo(new PacketSimpleAR(EnumSimplePacket.C_OPEN_SHUTTLE_GUI, new Object[] { player.getGameProfile().getName(), dimensionList }), player);
        // TODO TEMP!!!
        stats.usingPlanetSelectionGui = false;
        stats.savedPlanetList = new String(dimensionList);


        Entity fakeEntity = new EntityCelestialFake(player.worldObj, player.posX, player.posY, player.posZ, 0.0F);
        player.worldObj.spawnEntityInWorld(fakeEntity);
        player.mountEntity(fakeEntity);
    }

    @Override
    public List<ItemStack> getItemsDropped(List<ItemStack> droppedItems)
    {
        super.getItemsDropped(droppedItems);
        ItemStack rocket = new ItemStack(ARItems.shuttleItem, 1, this.encodeItemDamage());
        rocket.setTagCompound(new NBTTagCompound());
        rocket.getTagCompound().setInteger("RocketFuel", this.fuelTank.getFluidAmount());
        droppedItems.add(rocket);
        return droppedItems;
    }


    @Override
    protected void writeEntityToNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("NumTanks", numTanks);
        super.writeEntityToNBT(nbt);
    }

    @Override
    protected void readEntityFromNBT(NBTTagCompound nbt)
    {
        //EnumShuttleMode.
        //this.shuttleMode = EnumShuttleMode.values()[nbt.getInteger("ShuttleMode")];
        //this.setShuttleMode(shuttleMode);
        this.numTanks = nbt.getInteger("NumTanks");
        super.readEntityFromNBT(nbt);
    }

}
