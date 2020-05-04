package de.katzenpapst.amunra.item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import buildcraft.api.tools.IToolWrench;
import cofh.api.block.IDismantleable;
import cpw.mods.fml.common.eventhandler.Event.Result;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import crazypants.enderio.api.tool.ITool;
import de.katzenpapst.amunra.AmunRa;
import de.katzenpapst.amunra.GuiIds;
import de.katzenpapst.amunra.helper.InteroperabilityHelper;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.util.Vec3;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import cpw.mods.fml.common.Optional;

@Optional.InterfaceList({
    @Optional.Interface(iface="crazypants.enderio.api.tool.ITool", modid="EnderIO", striprefs=true),
    @Optional.Interface(iface="buildcraft.api.tools.IToolWrench", modid="BuildCraft|Core", striprefs=true)
})
public class ItemNanotool extends ItemAbstractBatteryUser implements ITool, IToolWrench {

    protected IIcon[] icons = null;

    protected float efficiencyOnProperMaterial = 6.0F;

    protected String[] textures = new String[] {
            "nanotool",
            "nanotool-axe",
            "nanotool-hoe",
            "nanotool-pickaxe",
            "nanotool-shears",
            "nanotool-shovel",
            "nanotool-wrench"
    };

    public enum Mode {
        WORKBENCH,
        AXE,
        HOE,
        PICKAXE,
        SHEARS,
        SHOVEL,
        WRENCH
    }

    // total power with default battery = 15000
    // a diamond tool has 1562 small uses
    // with small = 20, it will be 750 uses
    public final float energyCostUseSmall= 20.0F;
    public final float energyCostUseBig  = 40.0F;
    public final float energyCostSwitch  = 60.0F;

    protected HashMap<Mode, Set<String>> toolClassesSet;

    public ItemNanotool(String name) {
        this.setUnlocalizedName(name);

        icons = new IIcon[textures.length];

        this.setTextureName(AmunRa.TEXTUREPREFIX + "nanotool-empty");

        // init this stuff
        toolClassesSet = new HashMap<>();

        Set<String> axe = new HashSet<>();
        axe.add("axe");
        toolClassesSet.put(Mode.AXE, axe);

        Set<String> hoe = new HashSet<>();
        hoe.add("hoe");
        toolClassesSet.put(Mode.HOE, hoe);

        Set<String> pick = new HashSet<>();
        pick.add("pickaxe");
        toolClassesSet.put(Mode.PICKAXE, pick);

        Set<String> shovel = new HashSet<>();
        shovel.add("shovel");
        toolClassesSet.put(Mode.SHOVEL, shovel);

        Set<String> empty = new HashSet<>();
        toolClassesSet.put(Mode.SHEARS, empty);
        toolClassesSet.put(Mode.WRENCH, empty);
        toolClassesSet.put(Mode.WORKBENCH, empty);
    }

    protected void setMode(ItemStack stack, Mode m) {
        if (stack.getTagCompound() == null)
        {
            stack.setTagCompound(new NBTTagCompound());
        }
        stack.getTagCompound().setInteger("toolMode", m.ordinal());
    }

    public Mode getMode(ItemStack stack) {
        int ord = getModeInt(stack);
        return Mode.values()[ord];
    }


    protected int getModeInt(ItemStack stack) {
        NBTTagCompound nbt = stack.getTagCompound();
        if(nbt == null) {
            return 0;
        }
        return nbt.getInteger("toolMode");
    }


    @Override
    public CreativeTabs getCreativeTab()
    {
        return AmunRa.instance.arTab;
    }

    public boolean hasEnoughEnergyAndMode(ItemStack stack, float energy, Mode mode)
    {
        return this.getMode(stack) == mode && hasEnoughEnergy(stack, energy);
    }

    public boolean hasEnoughEnergy(ItemStack stack, float energy) {
        float storedEnergy = this.getElectricityStored(stack);
        return storedEnergy >= energy;
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer entityPlayer)
    {
        if(entityPlayer.isSneaking()) {
            // the wrench sometimes works when sneak-rightclicking
            if(this.hasEnoughEnergyAndMode(itemStack, energyCostUseBig, Mode.WRENCH)) {

                MovingObjectPosition movingobjectposition = getPlayerLookingAt(world, entityPlayer);

                if(movingobjectposition != null && movingobjectposition.typeOfHit == MovingObjectType.BLOCK) {
                    return super.onItemRightClick(itemStack, world, entityPlayer);
                }
            }
            // try switching
            if(hasEnoughEnergy(itemStack, energyCostSwitch)) {
                Mode m = getMode(itemStack);
                m = getNextMode(m);
                this.consumePower(itemStack, entityPlayer, energyCostSwitch);
                setMode(itemStack, m);
            }
            return itemStack;
        }
        if(this.hasEnoughEnergyAndMode(itemStack, energyCostUseBig, Mode.WORKBENCH)) {
            this.consumePower(itemStack, entityPlayer, energyCostUseBig);
            entityPlayer.openGui(AmunRa.instance, GuiIds.GUI_CRAFTING, world, (int)entityPlayer.posX, (int)entityPlayer.posY, (int)entityPlayer.posZ);
            return itemStack;
        }
        //
        return super.onItemRightClick(itemStack, world, entityPlayer);
    }

    protected MovingObjectPosition getPlayerLookingAt(World world, EntityPlayer player)
    {
        // mostly stolen from ItemBoat
        float touchDistance = 1.0F;
        float f1 = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * touchDistance;
        float f2 = player.prevRotationYaw + (player.rotationYaw - player.prevRotationYaw) * touchDistance;
        double startX = player.prevPosX + (player.posX - player.prevPosX) * (double)touchDistance;
        double startY = player.prevPosY + (player.posY - player.prevPosY) * (double)touchDistance + 1.62D - (double)player.yOffset;
        double startZ = player.prevPosZ + (player.posZ - player.prevPosZ) * (double)touchDistance;
        Vec3 vectorStart = Vec3.createVectorHelper(startX, startY, startZ);
        float f3 = MathHelper.cos(-f2 * 0.017453292F - (float)Math.PI);
        float f4 = MathHelper.sin(-f2 * 0.017453292F - (float)Math.PI);
        float f5 = -MathHelper.cos(-f1 * 0.017453292F);
        float endY = MathHelper.sin(-f1 * 0.017453292F);
        float endX = f4 * f5;
        float endZ = f3 * f5;
        double d3 = 5.0D;
        Vec3 vectorEnd = vectorStart.addVector((double)endX * d3, (double)endY * d3, (double)endZ * d3);
        return world.rayTraceBlocks(vectorStart, vectorEnd, true);
    }

    public Mode getNextMode(Mode fromMode)
    {
        switch(fromMode) {
        case WORKBENCH:
            return Mode.PICKAXE;
        case PICKAXE:
            return Mode.SHOVEL;
        case SHOVEL:
            return Mode.AXE;
        case AXE:
            return Mode.HOE;
        case HOE:
            return Mode.SHEARS;
        case SHEARS:
            return Mode.WRENCH;
        case WRENCH:
            return Mode.WORKBENCH;
        default:
            return Mode.PICKAXE;
        }

    }

    @Override
    public int getItemEnchantability()
    {
        return 0;
    }

    @Override
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining)
    {
        return getIconIndex(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        super.registerIcons(iconRegister);
        for(int i=0;i<textures.length;i++) {
            icons[i] = iconRegister.registerIcon(AmunRa.TEXTUREPREFIX + textures[i]);
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconIndex(ItemStack stack)
    {
        float energy = this.getElectricityStored(stack);
        if(energy <= 0) {
            return this.itemIcon;
        }

        return icons[getModeInt(stack)];
        //return this.getIconFromDamage(stack.getItemDamage());
    }

    @Override
    public Set<String> getToolClasses(ItemStack stack)
    {
        float energy = this.getElectricityStored(stack);
        if(energy > 0) {
            Mode m = this.getMode(stack);
            return toolClassesSet.get(m);
        }
        return super.getToolClasses(stack);
    }

    /**
     * Queries the harvest level of this item stack for the specifred tool class,
     * Returns -1 if this tool is not of the specified type
     *
     * @param stack This item stack instance
     * @param toolClass Tool Class
     * @return Harvest level, or -1 if not the specified tool type.
     */
    @Override
    public int getHarvestLevel(ItemStack stack, String toolClass)
    {
        float energy = this.getElectricityStored(stack);
        if(energy < energyCostUseSmall) {
            return -1;
        }
        Mode m = this.getMode(stack);
        if(!toolClassesSet.get(m).contains(toolClass)) {
            return -1;
        }
        return 5;
    }

    /**
     * Metadata-sensitive version of getStrVsBlock
     * @param itemstack The Item Stack
     * @param block The block the item is trying to break
     * @param metadata The items current metadata
     * @return The damage strength
     */
    @Override
    public float getDigSpeed(ItemStack itemstack, Block block, int metadata)
    {
        if(!this.hasEnoughEnergy(itemstack, energyCostUseSmall)) {
            return 1.0F;
        }
        if (ForgeHooks.isToolEffective(itemstack, block, metadata) || this.isEffectiveAgainst(this.getMode(itemstack), block))
        {
            return efficiencyOnProperMaterial;
        }

        return super.getDigSpeed(itemstack, block, metadata);
        //return func_150893_a(itemstack, block);
    }

    protected boolean isEffectiveAgainst(Mode m, Block b) {

        switch(m) {
        case AXE:
            return b.getMaterial() == Material.wood || b.getMaterial() == Material.plants || b.getMaterial() == Material.vine;
        case PICKAXE:
            return b.getMaterial() == Material.iron || b.getMaterial() == Material.anvil || b.getMaterial() == Material.rock;
        case SHEARS:
            return b.getMaterial() == Material.leaves || b.getMaterial() == Material.cloth || b.getMaterial() == Material.carpet ||
            b == Blocks.web || b == Blocks.redstone_wire || b == Blocks.tripwire;
        case SHOVEL:
            return b.getMaterial() == Material.clay || b.getMaterial() == Material.ground || b.getMaterial() == Material.clay;

        case WRENCH:
        case WORKBENCH:
        case HOE:
        default:
            return false;
        }
    }

    protected String getTypeString(Mode m) {
        switch(m) {
        case AXE:
            return "item.nanotool.mode.axe";
        case HOE:
            return "item.nanotool.mode.hoe";
        case PICKAXE:
            return "item.nanotool.mode.pickaxe";
        case SHEARS:
            return "item.nanotool.mode.shears";
        case SHOVEL:
            return "item.nanotool.mode.shovel";
        case WORKBENCH:
            return "item.nanotool.mode.workbench";
        case WRENCH:
            return "item.nanotool.mode.wrench";
        default:
            return "";
        }
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void addInformation(ItemStack itemStack, EntityPlayer entityPlayer, List list, boolean par4)
    {
        super.addInformation(itemStack, entityPlayer, list, par4);

        Mode m = this.getMode(itemStack);

        list.add(StatCollector.translateToLocal("item.nanotool.mode-prefix")+": "+StatCollector.translateToLocal(getTypeString(m)));
    }

    // damaging start
    /**
     * Current implementations of this method in child classes do not use the entry argument beside ev. They just raise
     * the damage on the stack.
     */
    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase entity1, EntityLivingBase user)
    {
        if(this.hasEnoughEnergy(stack, energyCostUseBig)) {
            this.consumePower(stack, user, energyCostUseBig);
            return true;
        }
        return false;
    }

    // damaging end

    // shearing
    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase entity)
    {
        if(!this.hasEnoughEnergy(stack, energyCostUseSmall)) {
            return false;
        }
        this.consumePower(stack, entity, energyCostUseSmall);
        if(this.getMode(stack) == Mode.SHEARS) {

            if (block.getMaterial() != Material.leaves && block != Blocks.web && block != Blocks.tallgrass && block != Blocks.vine && block != Blocks.tripwire && !(block instanceof IShearable))
            {
                return super.onBlockDestroyed(stack, world, block, x, y, z, entity);
            }
            else
            {
                return true;
            }
        }
        return super.onBlockDestroyed(stack, world, block, x, y, z, entity);
    }

    /**
     * I think this is a "is effective against"
     * @param block
     * @return
     */
    @Override
    public boolean func_150897_b(Block block)
    {
        // I have no choice here...
        return super.func_150897_b(block);
    }

    /**
     * ItemStack sensitive version of {@link #canHarvestBlock(Block)}
     * @param par1Block The block trying to harvest
     * @param itemStack The itemstack used to harvest the block
     * @return true if can harvest the block
     */
    @Override
    public boolean canHarvestBlock(Block par1Block, ItemStack itemStack)
    {
        return this.isEffectiveAgainst(this.getMode(itemStack), par1Block);
    }

    protected void consumePower(ItemStack itemStack, EntityLivingBase user, float power) {
        EntityPlayer player = null;
        if(user instanceof EntityPlayer) {
            player = (EntityPlayer)user;

        }
        if(player == null || !player.capabilities.isCreativeMode) {
            this.setElectricity(itemStack, this.getElectricityStored(itemStack) - power);
        }
    }

    /**
     * Seems to be a variant of getDigSpeed?
     * @param stack
     * @param block
     * @return
     */
    @Override
    public float func_150893_a(ItemStack stack, Block block)
    {
        if(this.hasEnoughEnergyAndMode(stack, energyCostUseSmall, Mode.SHEARS)) {
            return Items.shears.func_150893_a(stack, block);
        }

        return super.func_150893_a(stack, block);
    }

    /**
     * Returns true if the item can be used on the given entity, e.g. shears on sheep.
     */
    @Override
    public boolean itemInteractionForEntity(ItemStack itemstack, EntityPlayer player, EntityLivingBase entity)
    {
        if(this.hasEnoughEnergyAndMode(itemstack, energyCostUseBig, Mode.SHEARS)) {
            this.consumePower(itemstack, player, energyCostUseBig);
            return Items.shears.itemInteractionForEntity(itemstack, player, entity);
        }
        return super.itemInteractionForEntity(itemstack, player, entity);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack itemstack, int x, int y, int z, EntityPlayer player)
    {
        if(this.hasEnoughEnergyAndMode(itemstack, energyCostUseSmall, Mode.SHEARS)) {
            return Items.shears.onBlockStartBreak(itemstack, x, y, z, player);
        }
        return super.onBlockStartBreak(itemstack, x, y, z, player);
    }

    // hoeing
    /**
     * Callback for item usage. If the item does something special on right clicking, he will have one of those. Return
     * True if something happen and false if it don't. This is for ITEMS, not BLOCKS
     */
    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if(this.hasEnoughEnergyAndMode(stack, energyCostUseSmall, Mode.HOE)) {
            //if(this.getMode(stack) == Mode.HOE) {
            if (!player.canPlayerEdit(x, y, z, side, stack))
            {
                return false;
            }
            else
            {
                UseHoeEvent event = new UseHoeEvent(player, stack, world, x, y, z);
                if (MinecraftForge.EVENT_BUS.post(event))
                {
                    return false;
                }

                if (event.getResult() == Result.ALLOW)
                {
                    this.consumePower(stack, player, energyCostUseSmall);
                    //stack.damageItem(1, player);
                    return true;
                }

                Block block = world.getBlock(x, y, z);

                if (side != 0 && world.getBlock(x, y + 1, z).isAir(world, x, y + 1, z) && (block == Blocks.grass || block == Blocks.dirt))
                {
                    Block block1 = Blocks.farmland;
                    world.playSoundEffect((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), block1.stepSound.getStepResourcePath(), (block1.stepSound.getVolume() + 1.0F) / 2.0F, block1.stepSound.getPitch() * 0.8F);

                    if (world.isRemote)
                    {
                        return true;
                    }
                    else
                    {
                        world.setBlock(x, y, z, block1);
                        //stack.damageItem(1, player);
                        this.consumePower(stack, player, energyCostUseSmall);
                        return true;
                    }
                }
                else
                {
                    return false;
                }
            }
        }
        return super.onItemUse(stack, player, world, x, y, z, side, hitX, hitY, hitZ);
    }

    // wrenching
    @Override
    public boolean canWrench(EntityPlayer entityPlayer, int x, int y, int z)
    {
        ItemStack stack = entityPlayer.inventory.getCurrentItem();

        return this.hasEnoughEnergyAndMode(stack, energyCostUseSmall, Mode.WRENCH);
    }

    @Override
    public void wrenchUsed(EntityPlayer entityPlayer, int x, int y, int z)
    {
        ItemStack stack = entityPlayer.inventory.getCurrentItem();

        if(this.hasEnoughEnergyAndMode(stack, energyCostUseSmall, Mode.WRENCH)) {
            this.consumePower(stack, entityPlayer, energyCostUseSmall);
        }
    }

    //EnderIO
    @Override
    public boolean canUse(ItemStack stack, EntityPlayer player, int x, int y, int z) {
        return this.hasEnoughEnergyAndMode(stack, energyCostUseSmall, Mode.WRENCH);
    }

    @Override
    public void used(ItemStack stack, EntityPlayer player, int x, int y, int z) {
        this.consumePower(stack, player, energyCostUseSmall);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean isFull3D()
    {
        return true;
    }

    private boolean attemptDismantle(EntityPlayer entityPlayer, Block block, World world, int x, int y, int z)
    {
        if(InteroperabilityHelper.hasIDismantleable) {
            if(block instanceof IDismantleable && ((IDismantleable) block).canDismantle(entityPlayer, world, x, y, z)) {

                ((IDismantleable) block).dismantleBlock(entityPlayer, world, x, y, z, false);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer entityPlayer, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if(hasEnoughEnergyAndMode(stack, energyCostUseSmall, Mode.WRENCH)) {

            if (world.isRemote) return false;

            Block blockID = world.getBlock(x, y, z);

            // try dismantle
            if (entityPlayer.isSneaking() && attemptDismantle(entityPlayer, blockID, world, x, y, z)) {

                return true;

            } else if (blockID == Blocks.furnace || blockID == Blocks.lit_furnace || blockID == Blocks.dropper || blockID == Blocks.hopper || blockID == Blocks.dispenser || blockID == Blocks.piston || blockID == Blocks.sticky_piston)
            {
                int metadata = world.getBlockMetadata(x, y, z);

                int[] rotationMatrix = { 1, 2, 3, 4, 5, 0 };

                if (blockID == Blocks.furnace || blockID == Blocks.lit_furnace)
                {
                    rotationMatrix = ForgeDirection.ROTATION_MATRIX[0];
                }

                world.setBlockMetadataWithNotify(x, y, z, ForgeDirection.getOrientation(rotationMatrix[metadata]).ordinal(), 3);
                this.wrenchUsed(entityPlayer, x, y, z);

                return true;
            }

            return false;
        }
        return super.onItemUseFirst(stack, entityPlayer, world, x, y, z, side, hitX, hitY, hitZ);
    }

    @Override
    public boolean shouldHideFacades(ItemStack stack, EntityPlayer player) {
        return this.getMode(stack) == Mode.WRENCH;
        //return true;
    }

    /**
     *
     * Should this item, when held, allow sneak-clicks to pass through to the underlying block?
     *
     * @param world The world
     * @param x The X Position
     * @param y The X Position
     * @param z The X Position
     * @param player The Player that is wielding the item
     * @return
     */
    @Override
    public boolean doesSneakBypassUse(World world, int x, int y, int z, EntityPlayer player)
    {
        ItemStack stack = player.inventory.getCurrentItem();

        if(this.hasEnoughEnergyAndMode(stack, energyCostUseSmall, Mode.WRENCH)) {

            MovingObjectPosition mpos = getPlayerLookingAt(world, player);

            if(mpos != null && mpos.typeOfHit == MovingObjectType.BLOCK) {
                return true;
            }
        }

        return false;
    }

    // try this

}
