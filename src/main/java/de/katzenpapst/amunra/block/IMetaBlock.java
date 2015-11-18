package de.katzenpapst.amunra.block;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;

public interface IMetaBlock {
	public BlockMetaPair addSubBlock(int meta, SubBlock sb);

	public int getMetaByName(String name);

	public SubBlock getSubBlock(int meta);

	public void register();
}
