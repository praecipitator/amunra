package de.katzenpapst.amunra.mob.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cpw.mods.fml.common.network.NetworkRegistry.TargetPoint;
import de.katzenpapst.amunra.entity.EntityLaserArrow;
import de.katzenpapst.amunra.helper.NbtHelper;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.tile.ITileDungeonSpawner;
import de.katzenpapst.amunra.vec.Vector3int;
import micdoodle8.mods.galacticraft.api.entity.IEntityBreathable;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
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
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class EntityMummyBoss extends EntityMob implements IBossDisplayData, IRangedAttackMob, IEntityBreathable, IAmunRaBoss {

    protected int deathTicks = 0;
    protected long ticks = 0;
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
        this.tasks.addTask(2, new EntityAIArrowAttack(this, 1.0D, 25, 20.0F));
        this.tasks.addTask(2, new EntityAIWander(this, 1.0D));
        this.tasks.addTask(3, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(3, new EntityAILookIdle(this));
        this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false));
        this.targetTasks.addTask(2, new EntityAINearestAttackableTarget(this, EntityPlayer.class, 0, true));

        if(guaranteedLoot == null) {
            guaranteedLoot = new ArrayList<>();
            guaranteedLoot.add(ARItems.shuttleSchematic.getItemStack(1));
        }

        if(extraLoot == null) {
            extraLoot = new ArrayList<>();
            extraLoot.add(ARItems.mummyDust.getItemStack(3));
            extraLoot.add(new ItemStack(Items.string, 3, 0));
            extraLoot.add(new ItemStack(Items.gold_nugget, 1, 0));
            extraLoot.add(new ItemStack(Items.gold_ingot, 1, 0));
            extraLoot.add(new ItemStack(Items.dye, 3, 4));
        }
    }

    @Override
    public void attackEntityWithRangedAttack(EntityLivingBase targetIthink, float unknown) {

        if(!this.isDead) {
            performAttack(targetIthink);
        }

    }

    protected void performAttack(Entity target) {

        double startX = target.posX - this.posX;
        double startY = target.boundingBox.minY + (double)(target.height / 2.0F) - (this.posY + (double)(this.height / 2.0F));
        double startZ = target.posZ - this.posZ;

        EntityLargeFireball entitylargefireball = new EntityLargeFireball(this.worldObj, this, startX, startY, startZ);

        entitylargefireball.field_92057_e = 1;
        double d8 = 4.0D;
        Vec3 vec3 = this.getLook(1.0F);
        entitylargefireball.posX = this.posX + vec3.xCoord * d8;
        entitylargefireball.posY = this.posY + (double)(this.height / 2.0F) + 0.5D;
        entitylargefireball.posZ = this.posZ + vec3.zCoord * d8;
        this.worldObj.spawnEntityInWorld(entitylargefireball);
        //entitylargefireball.field_92057_e = this.explosionStrength;
        /*double size = 4.0D;
        Vec3 vec3 = this.getLook(1.0F);
        double x = this.posX + vec3.xCoord * size;
        double y = this.posY + (double)(this.height / 2.0F) + 0.5D;
        double z = this.posZ + vec3.zCoord * size;*/


        EntityLaserArrow attack =  new EntityLaserArrow(worldObj, (EntityLivingBase)this, new Vector3(this), (EntityLivingBase)target);//new EntityLaserArrow(this.worldObj, (EntityLivingBase)this, (EntityLivingBase)target, 0.0F);
        attack.setDamage(0.5F);
        //attack.setDoesFireDamage(false);
        this.worldObj.spawnEntityInWorld(attack);


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
        this.getEntityAttribute(SharedMonsterAttributes.movementSpeed).setBaseValue(0.05F);
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
                //              PacketDispatcher.sendPacketToAllAround(this.posX, this.posY, this.posZ, 40.0, this.worldObj.provider.dimensionId, PacketUtil.createPacket(GalacticraftCore.CHANNEL, EnumPacketClient.PLAY_SOUND_EXPLODE, new Object[] { 0 }));
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
                //              PacketDispatcher.sendPacketToAllAround(this.posX, this.posY, this.posZ, 40.0, this.worldObj.provider.dimensionId, PacketUtil.createPacket(GalacticraftCore.CHANNEL, EnumPacketClient.PLAY_SOUND_BOSS_DEATH, new Object[] { 0 }));
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
    public void onLivingUpdate()
    {
        if (this.ticks >= Long.MAX_VALUE)
        {
            this.ticks = 1;
        }

        this.ticks++;

        final EntityPlayer player = this.worldObj.getClosestPlayer(this.posX, this.posY, this.posZ, 20.0);

        if (player != null && !player.equals(this.targetEntity))
        {
            if (this.getDistanceSqToEntity(player) < 400.0D)
            {
                this.getNavigator().getPathToEntityLiving(player);
                this.targetEntity = player;
            }
        }
        else
        {
            this.targetEntity = null;
        }

        new Vector3(this);

        super.onLivingUpdate();
    }


    @Override
    protected Item getDropItem()
    {
        return null;
    }

    protected void dropLoot(boolean hitByPlayer, int lootLevel)
    {
        List<ItemStack> result = getDrops(guaranteedLoot, getRNG(), 0);
        result.addAll(getDrops(extraLoot, getRNG(), lootLevel));

        for(ItemStack stack: result) {
            this.entityDropItem(stack, 1);
        }
    }

    protected List<ItemStack> getDrops(List<ItemStack> source, Random rand, int lootLevel)
    {
        List<ItemStack> result = new ArrayList<ItemStack>();
        int size = source.size();
        if(size == 0) {
            return result;
        }
        //rand.next
        int numDrops = 1;// + rand.nextInt(lootLevel);
        if(lootLevel > 0) {
            numDrops += rand.nextInt(lootLevel);
        }
        for(int i=0;i<numDrops;i++) {

            int randIndex = 0;

            if(size > 1) {
                randIndex = rand.nextInt(size);
            }

            ItemStack stack = source.get(randIndex).copy();
            int stackSize = Math.max(Math.min(stack.stackSize, lootLevel), 1);
            if(stackSize > 1) {
                stackSize = rand.nextInt(stackSize)+1;
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

            //Vector3int pos = spawner.getBlockPosition();
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
