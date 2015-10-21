package de.katzenpapst.amunra.world.mapgen.village;



public class ARVillagePieceWeight {
	public Class<? extends ARVillageComponent> villagePieceClass;
    public final int villagePieceWeight;
    public int villagePiecesSpawned;
    public int villagePiecesLimit;

    public ARVillagePieceWeight(Class<? extends ARVillageComponent> par1Class, int weight, int piecesLimit)
    {
        this.villagePieceClass = par1Class;
        this.villagePieceWeight = weight;
        this.villagePiecesLimit = (int) (piecesLimit / 1.5D); // WHY?
    }

    public boolean canSpawnMoreVillagePiecesOfType(int par1) // figure out what this is
    {
        return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
    }

    public boolean canSpawnMoreVillagePieces()
    {
        return this.villagePiecesLimit == 0 || this.villagePiecesSpawned < this.villagePiecesLimit;
    }
}
