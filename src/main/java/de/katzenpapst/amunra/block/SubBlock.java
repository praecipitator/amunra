package de.katzenpapst.amunra.block;

import net.minecraft.block.Block;
import net.minecraft.block.Block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;

public class SubBlock {
	public float hardness = 1.5F;
	public float resistance = 10.0F;
	public String name = null;
	public String texture = null;
	public String tool = "pickaxe";
	/**
	 *     Wood:    0
     *     Stone:   1
     *     Iron:    2
     *     Diamond: 3
     *     Gold:    0
	 */
	public int miningLevel = 0;
	public boolean isOpaque = false;
	public int lightOpacity = 0;
	public int lightValue = 0;
	public Material material = Material.rock;
	public SoundType soundType = Block.soundTypeStone;
	
	protected IIcon textureIcon;
	
	public SubBlock(String name, String texture) {
		this.name = name;
		this.texture = texture;
	}
}
