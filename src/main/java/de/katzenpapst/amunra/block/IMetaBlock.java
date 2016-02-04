package de.katzenpapst.amunra.block;

import micdoodle8.mods.galacticraft.api.prefab.core.BlockMetaPair;

public interface IMetaBlock {
	public BlockMetaPair addSubBlock(int meta, SubBlock sb);

	public int getMetaByName(String name);

	public SubBlock getSubBlock(int meta);

	public String getUnlocalizedSubBlockName(int meta);

	public void register();

	/**
	 * This should take a metadata, and return only the part of it which is used for subblock distinction,
	 * aka, strip off things like rotational information
	 *
	 * @param meta
	 * @return
	 */
	public int getDistinctionMeta(int meta);
}
