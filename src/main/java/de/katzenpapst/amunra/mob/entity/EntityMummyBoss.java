package de.katzenpapst.amunra.mob.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import de.katzenpapst.amunra.entity.EntityCryoArrow;
import de.katzenpapst.amunra.entity.EntityOsirisBossFireball;
import de.katzenpapst.amunra.helper.NbtHelper;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.mob.DamageSourceAR;
import de.katzenpapst.amunra.tile.ITileDungeonSpawner;
import de.katzenpapst.amunra.vec.Vector3int;
import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.entities.EntityAIArrowAttack;
import micdoodle8.mods.galacticraft.core.network.PacketSimple;
import micdoodle8.mods.galacticraft.core.network.PacketSimple.EnumSimplePacket;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.GCCoreUtil;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAILookIdle;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityMummyBoss extends EntityMob implements IBossDisplayData, IRangedAttackMob, IEntityBreathable, IAmunRaBoss {

    protected int deathTicks = 0;
    //protected long ticks = 0;
    protected Entity targetEntity;

    protected ITileDungeonSpawner spawner;
    protected AxisAlignedBB roomArea;
    protected Vector3int spawnerPos;

    protected int attackedWithLootLevel = 0;

    protected static List<ItemStack> guaranteedLoot = null;
    protected static List<ItemStack> extraLoot = null;

    public EntityMummyBoss(World world) {
        super(world);

        this.setSize(2.0F, 5.0F);
        this.isImmuneToFire = true;
        this.tasks.addTask(1, new EntityAISwimming(this));
        // entity, entityMoveSpeed, time something, maxRangedAttackTime, dist something?
        this.tasks.addTask(2, new EntityAIArrowAttack(this, 1.0D, 25, 20.0F));
        this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 30.0F));
        this.tasks.addTask(3, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));

        if(guaranteedLoot == null) {
            guaranteedLoot = new ArrayList<>();
            guaranteedLoot.add(ARItems.shuttleSchematic.getItemStack(1));
            //guaranteedLoot.add(new ItemStack(ARItems.batteryQuantum, 0, 0));
        }

        if(extraLoot == null) {
            extraLoot = new ArrayList<>();
            extraLoot.add(ARItems.mummyDust.getItemStack(3));
            extraLoot.add(new ItemStack(Items.string, 3, 0));
            extraLoot.add(new ItemStack(Items.gold_nugget, 1, 0));
            extraLoot.add(new ItemStack(Items.gold_ingot, 1, 0));
            extraLoot.add(new ItemStack(Items.dye, 3, 4));
        }

        //this.getNavigator().getPathSearchRange()
    }

    /**
     * Called when the entity is attacked.
     */
    @Override
    public boolean attackEntityFrom(DamageSource ds, float amount)
    {
        // modify the damage
        if(ds != DamageSource.outOfWorld && ds != DamageSourceAR.dsFallOffShip) {
            if(ds instanceof EntityDamageSourceIndirect && (((EntityDamageSourceIndirect)ds).getEntity() instanceof EntityCryoArrow)) {
                amount *= 1.5F;
            } else {
                amount /= 2.0F;
            }
        }
        return super.attackEntityFrom(ds, amount);
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase targetIthink, float unknown) {

        if(!this.isDead) {
            performAttack(targetIthink);
        }

    }

    protected void performAttack(Entity target) {

        double startX = target.posX - this.posX;
        double startY = target.posY - this.posY - target.height - 1.5D;//target.boundingBox.minY + (double)(target.height / 2.0F) - (this.posY + (double)(this.height / 2.0F));
        double startZ = target.posZ - this.posZ;

        EntityOsirisBossFireball entitylargefireball = new EntityOsirisBossFireball(this.worldObj, this, startX, startY, startZ);

        //entitylargefireball.field_92057_e = 1;
        entitylargefireball.damage = 10.0F;
        double d8 = 0.0D;
        Vec3 vec3 = this.getLook(1.0F);
        entitylargefireball.posX = this.posX + vec3.xCoord * d8;
        entitylargefireball.posY = this.posY + (double)(this.height / 2.0F) + 1.5D;
        entitylargefireball.posZ = this.posZ + vec3.zCoord * d8;
        this.worldObj.spawnEntityInWorld(entitylargefireball);
    }

    @Override
    public void knockBack(Entity par1Entity, float par2, double par3, double par5)
    {
    }

    @Override
    protected void applyEntityAttributes()
    {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.maxHealth).setBaseValue(200.0F * ConfigManagerCore.dungeonBossHealthMod);
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.25F);
        //
        this.getEntityAttribute(SharedMonsterAttributes.followRange).setBaseValue(32.0F);
    }

    @Override
    public boolean canBreath() {
        return true;
    }

    @Override
    public boolean isAIEnabled()
    {
        return true;
    }

    @Override
    public boolean canBePushed()
    {
        return false;
    }

    @Override
    protected String getLivingSound()
    {
        return null;
    }

    @Override
    protected String getHurtSound()
    {
        this.playSound(GalacticraftCore.TEXTURE_PREFIX + "entity.bossliving", this.getSoundVolume(), this.getSoundPitch() + 6.0F);
        return null;
    }

    @Override
    protected String getDeathSound()
    {
        return null;
    }

    @Override
    public void onDeath(DamageSource ds)
    {
        super.onDeath(ds);
        Entity entity = ds.getEntity();

        //boolean hitBy
        if (entity instanceof EntityPlayer)
        {
            attackedWithLootLevel = EnchantmentHelper.getLootingModifier((EntityLivingBase)entity);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void onDeathUpdate()
    {
        ++this.deathTicks;

        if (this.deathTicks >= 180 && this.deathTicks <= 200)
        {
            final float f = (this.rand.nextFloat() - 0.5F) * 1.5F;
            final float f1 = (this.rand.nextFloat() - 0.5F) * 2.0F;
            final float f2 = (this.rand.nextFloat() - 0.5F) * 1.5F;
            this.worldObj.spawnParticle("hugeexplosion", this.posX + f, this.posY + 2.0D + f1, this.posZ + f2, 0.0D, 0.0D, 0.0D);
        }

        int i;
        int j;

        if (!this.worldObj.isRemote)
        {
            if (this.deathTicks >= 180 && this.deathTicks % 5 == 0)
            {
                GalacticraftCore.packetPipeline.sendToAllAround(new PacketSimple(EnumSimplePacket.C_PLAY_SOUND_EXPLODE, new Object[] { }), new TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 40.0D));
            }

            if (this.deathTicks > 150 && this.deathTicks % 5 == 0)
            {
                i = 30;

                while (i > 0)
                {
                    j = EntityXPOrb.getXPSplit(i);
                    i -= j;
                    this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
                }
            }

            if (this.deathTicks == 1)
            {
                GalacticraftCore.packetPipeline.sendToAllAround(new PacketSimple(EnumSimplePacket.C_PLAY_SOUND_BOSS_DEATH, new Object[] {}), new TargetPoint(this.worldObj.provider.dimensionId, this.posX, this.posY, this.posZ, 40.0D));
            }
        }

        this.moveEntity(0.0D, 0.10000000149011612D, 0.0D);
        this.renderYawOffset = this.rotationYaw += 20.0F;

        if (this.deathTicks == 200 && !this.worldObj.isRemote)
        {
            i = 20;

            while (i > 0)
            {
                j = EntityXPOrb.getXPSplit(i);
                i -= j;
                this.worldObj.spawnEntityInWorld(new EntityXPOrb(this.worldObj, this.posX, this.posY, this.posZ, j));
            }

            // generate loot here
            dropLoot(true, attackedWithLootLevel);

            super.setDead();

            if (this.getSpawner() != null)
            {
                this.getSpawner().onBossDefeated();
            }
        }
    }

    @Override
    protected Item getDropItem()
    {
        return null;
    }

    protected void dropLoot(boolean hitByPlayer, int lootLevel)
    {
        List<ItemStack> result = getDrops(guaranteedLoot, getRNG(), 0);

        int lootModifier = (lootLevel+1)/2;

        result.addAll(getDrops(extraLoot, getRNG(), lootLevel, lootModifier, 5+lootModifier));

        for(ItemStack stack: result) {
            this.entityDropItem(stack, 1);
        }
    }



    protected List<ItemStack> getDrops(List<ItemStack> source, Random rand, int lootLevel)
    {
        return getDrops(source, rand, lootLevel, 1, 1);
    }

    protected List<ItemStack> getDrops(List<ItemStack> source, Random rand, int lootLevel, int minStacks, int maxStacks)
    {
        List<ItemStack> result = new ArrayList<>();
        int size = source.size();
        if(size == 0) {
            return result;
        }


        for(int i=0;i<maxStacks;i++) {
            int randIndex = 0;
            if(size > 1) {
                randIndex = rand.nextInt(size);
            }

            ItemStack stack = source.get(randIndex).copy();

            int stackMin = 0;
            if(minStacks > result.size()) {
                stackMin = 1;
            }
            stackMin = Math.min(stackMin+lootLevel, stack.stackSize);

            int stackSize = MathHelper.getRandomIntegerInRange(rand, stackMin, stack.stackSize);

            if(stackSize <= 0) {
                continue;
            }

            stack.stackSize = stackSize;
            result.add(stack);
        }
        return result;
    }

    @Override
    protected void dropRareDrop(int par1)
    {

    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbt)
    {
        super.writeEntityToNBT(nbt);

        if(spawnerPos != null) {
            nbt.setTag("spawnerPosition", spawnerPos.toNBT());
        }

        if(roomArea != null) {
            nbt.setTag("roomArea", NbtHelper.getAsNBT(roomArea));
        }

        nbt.setInteger("atkLootLevel", attackedWithLootLevel);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbt)
    {
        super.readEntityFromNBT(nbt);

        attackedWithLootLevel = nbt.getInteger("atkLootLevel");

        if(nbt.hasKey("spawnerPosition")) {
            spawnerPos = new Vector3int(nbt.getCompoundTag("spawnerPosition"));
        }

        if(nbt.hasKey("roomArea")) {
            this.roomArea = NbtHelper.readAABB(nbt.getCompoundTag("roomArea"));
        }
    }

    @Override
    public void setSpawner(ITileDungeonSpawner spawner) {
        this.spawner = spawner;
        spawnerPos = spawner.getBlockPosition();
    }

    @Override
    public ITileDungeonSpawner getSpawner() {
        if(spawner == null && spawnerPos != null) {
            TileEntity te = this.worldObj.getTileEntity(spawnerPos.x, spawnerPos.y, spawnerPos.z);
            if(te instanceof ITileDungeonSpawner) {
                this.spawner = (ITileDungeonSpawner) te;
            }
        }
        return spawner;
    }

    @Override
    public void setRoomArea(AxisAlignedBB aabb) {
        this.roomArea = aabb.copy();
    }

    @Override
    public AxisAlignedBB getRoomArea() {
        return this.roomArea;
    }

    @Override
    public void despawnBoss() {
        AxisAlignedBB aabb = this.roomArea.expand(11, 11, 11);

        @SuppressWarnings("unchecked")
        List<EntityPlayer> entitiesWithin2 = this.worldObj.getEntitiesWithinAABB(EntityPlayer.class, aabb);

        for (EntityPlayer p : entitiesWithin2)
        {
            p.addChatMessage(new ChatComponentText(GCCoreUtil.translate("gui.skeletonBoss.message")));
        }
        this.setDead();

    }
}
