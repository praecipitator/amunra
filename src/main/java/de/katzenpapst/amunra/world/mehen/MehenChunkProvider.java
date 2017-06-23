package de.katzenpapst.amunra.world.mehen;

import java.util.ArrayList;
import java.util.List;

import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.mob.entity.EntitySentry;
import de.katzenpapst.amunra.world.asteroidWorld.AmunRaAsteroidsChunkProvider;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase.SpawnListEntry;

public class MehenChunkProvider extends AmunRaAsteroidsChunkProvider {

    public MehenChunkProvider(World par1World, long par2, boolean par4) {
        super(par1World, par2, par4);
    }

    @Override
    public List getPossibleCreatures(EnumCreatureType par1EnumCreatureType, int i, int j, int k)
    {
        if (par1EnumCreatureType == EnumCreatureType.monster)
        {
            final List monsters = new ArrayList();

            monsters.add(new SpawnListEntry(EntitySentry.class, 3000, 1, 3));
            return monsters;
        }
        else
        {
            return null;
        }
    }

    @Override
    protected void initBlockTypes() {
        super.initBlockTypes();

        addBlockToHandler(coreHandler, ARBlocks.oreDiamondAsteroid, 1, .1);
        addBlockToHandler(coreHandler, ARBlocks.oreRubyAsteroid,    1, .1);
        addBlockToHandler(coreHandler, ARBlocks.oreEmeraldAsteroid, 1, .1);
        addBlockToHandler(coreHandler, ARBlocks.oreCopperAsteroid,  15, .2);
        addBlockToHandler(coreHandler, ARBlocks.oreLeadAsteroid,    8, .2);
        addBlockToHandler(coreHandler, ARBlocks.oreUraniumAsteroid, 4, .05);
        addBlockToHandler(coreHandler, ARBlocks.blockDarkmatter,    1, .05);
    }

    @Override
    protected int adjustBrightnessValue(int count)
    {
        count += 8;

        if (count > 12) count = 12;

        return count;
    }

}
