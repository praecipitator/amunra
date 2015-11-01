package de.katzenpapst.amunra;

import micdoodle8.mods.galacticraft.api.GalacticraftRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody;
import micdoodle8.mods.galacticraft.api.galaxies.CelestialBody.ScalableDistance;
import micdoodle8.mods.galacticraft.api.galaxies.GalaxyRegistry;
import micdoodle8.mods.galacticraft.api.galaxies.Moon;
import micdoodle8.mods.galacticraft.api.galaxies.Planet;
import micdoodle8.mods.galacticraft.api.galaxies.SolarSystem;
import micdoodle8.mods.galacticraft.api.galaxies.Star;
import micdoodle8.mods.galacticraft.api.recipe.CircuitFabricatorRecipes;
import micdoodle8.mods.galacticraft.api.vector.Vector3;
import micdoodle8.mods.galacticraft.api.world.IAtmosphericGas;
import micdoodle8.mods.galacticraft.core.GalacticraftCore;
import micdoodle8.mods.galacticraft.core.dimension.TeleportTypeMoon;
import micdoodle8.mods.galacticraft.core.dimension.TeleportTypeOverworld;
import micdoodle8.mods.galacticraft.core.items.GCItems;
import micdoodle8.mods.galacticraft.core.util.ConfigManagerCore;
import micdoodle8.mods.galacticraft.core.util.CreativeTabGC;
import micdoodle8.mods.galacticraft.planets.asteroids.items.AsteroidsItems;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import de.katzenpapst.amunra.block.ARBlocks;
import de.katzenpapst.amunra.block.BlockBasicMulti;
import de.katzenpapst.amunra.entity.EntityCryoArrow;
import de.katzenpapst.amunra.entity.EntityLaserArrow;
import de.katzenpapst.amunra.event.CraftingHandler;
import de.katzenpapst.amunra.event.EventHandlerAR;
import de.katzenpapst.amunra.event.FurnaceHandler;
import de.katzenpapst.amunra.item.ARItems;
import de.katzenpapst.amunra.mob.RobotVillagerProfession;
import de.katzenpapst.amunra.mob.entity.EntityARVillager;
import de.katzenpapst.amunra.mob.entity.EntityPorcodon;
import de.katzenpapst.amunra.mob.entity.EntityRobotVillager;
import de.katzenpapst.amunra.proxy.ARSidedProxy;
import de.katzenpapst.amunra.world.anubis.AnubisWorldProvider;
import de.katzenpapst.amunra.world.horus.HorusWorldProvider;
import de.katzenpapst.amunra.world.maahes.MaahesWorldProvider;
import de.katzenpapst.amunra.world.neper.NeperWorldProvider;

@Mod(modid = AmunRa.MODID, version = AmunRa.VERSION, dependencies = "required-after:GalacticraftCore; required-after:GalacticraftMars",
	name = AmunRa.MODNAME)
public class AmunRa
{
	public static final String MODID = "GalacticraftAmunRa";
    public static final String MODNAME = "Pra's Galacticraft Mod";
    public static final String VERSION = "0.0.1";

    @Instance(AmunRa.MODID)
    public static AmunRa instance;

    public static final String ASSETPREFIX = "amunra";
    public static final String TEXTUREPREFIX = ASSETPREFIX + ":";

    public Star starRa = null;
    public Planet starAmun = null;
    public SolarSystem systemAmunRa = null;

    public Planet planetOsiris = null;
    public Planet planetHorus = null;
    public Planet planetBaal = null;
    public Planet planetAnubis = null;
    public Planet asteroidBeltMehen = null;
    public Planet planetSekhmet = null;

    public Moon moonBaalRings = null;
    public Moon moonKhonsu;
    public Moon moonNeper;
    public Moon moonIah;
    public Moon moonBastet;
    public Moon moonMaahes;
    public Moon moonThoth;
    public Moon moonSeth;

    public Moon moonKebe;

    private int dimNeper;
    private int dimMaahes;
    private int dimAnubis;
    private int dimHorus;

    public boolean confAdvancedVillageMachines = false;

    public static CreativeTabs arTab;

    protected BlockBasicMulti basicMultiBlock;
	private int nextID = 0;

	@SidedProxy(clientSide = "de.katzenpapst.amunra.proxy.ClientProxy", serverSide = "de.katzenpapst.amunra.proxy.ServerProxy")
    public static ARSidedProxy proxy;

	@EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());

		config.load();

		// Configuration goes here.
		dimNeper = config.get("dimension_ids", "Neper", 20).getInt();
		dimMaahes = config.get("dimension_ids", "Maahes", 21).getInt();
		dimAnubis = config.get("dimension_ids", "Anubis", 22).getInt();
		dimHorus = config.get("dimension_ids", "Horus", 23).getInt();

		confAdvancedVillageMachines = config.get("villages", "UseAdvancedMachines", false,
				"If true, robot villages will have advanced solar collectors, storage clusters and heavy wires").getBoolean();

		config.save();

		ARBlocks.initBlocks();
    	ARItems.initItems();
    	// this works for entityLivingEvent...
    	MinecraftForge.EVENT_BUS.register(new EventHandlerAR());
    	// ...but not for onCrafting. Because FUCK YOU, that's why!
    	FMLCommonHandler.instance().bus().register(new CraftingHandler());
    	GameRegistry.registerFuelHandler(new FurnaceHandler());


        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	AmunRa.arTab = new CreativeTabGC(CreativeTabs.getNextID(), "AmunRaTab", ARItems.baseItem, 0);




        initCelestialBodies();
        initCreatures();
        initOtherEntities();
        initRecipes();

        proxy.init(event);


      //  GCBlocks
    }


    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        proxy.postInit(event);
    }

    // stolen from GC....
    public int nextInternalID()
    {
    	nextID ++;
    	return nextID - 1;
    }

    public void registerCreature(Class<? extends Entity> entityClass, String entityName, int eggBgColor, int eggFgColor) {
    	int newID = EntityRegistry.instance().findGlobalUniqueEntityId();
        EntityRegistry.registerGlobalEntityID(entityClass, entityName, newID, eggBgColor, eggFgColor);
        EntityRegistry.registerModEntity(entityClass, entityName, nextInternalID(), AmunRa.instance, 80, 3, true);
    }


    public void registerNonMobEntity(Class<? extends Entity> var0, String var1, int trackingDistance, int updateFreq, boolean sendVel)
    {
    	//int newID = EntityRegistry.instance().findGlobalUniqueEntityId();
    	/*
    	if (FMLCommonHandler.instance().getEffectiveSide() == Side.CLIENT)
		{
    		LanguageRegistry.instance().addStringLocalization("entity.GalacticraftCore." + var1 + ".name", GCCoreUtil.translate("entity." + var1 + ".name"));
		}*/
        EntityRegistry.registerModEntity(var0, var1, nextInternalID(), AmunRa.instance, trackingDistance, updateFreq, sendVel);
    }

    protected void initCreatures() {
    	registerCreature(EntityPorcodon.class, "porcodon", 0xff9d9d, 0x4fc451);
    	registerCreature(EntityARVillager.class, "alienVillagerAR", 44975, 7969893);
    	registerCreature(EntityRobotVillager.class, "robotVillager", 0x626260, 0x141514);



    	// register trading stuff
    	registerTrading();


    }

    protected void initOtherEntities() {
    	registerNonMobEntity(EntityLaserArrow.class, "laserArrow", 150, 5, true);
    	registerNonMobEntity(EntityCryoArrow.class, "cryoArrow", 150, 5, true);
    }

    protected void initRecipes() {

    	//ItemStack enderWaferStack = ARItems.baseItem.getItemStack("waferEnder", 1);
    	ItemStack enderWaferStack = ARItems.waferEnder.getItemStack(1);
    	ItemStack lithiumMeshStack = ARItems.lithiumMesh.getItemStack(1);
    	ItemStack lithiumGemStack = ARItems.lithiumGem.getItemStack(1);
    	ItemStack compressedAluStack = new ItemStack(GCItems.basicItem, 1, 8);
    	ItemStack compressedSteelStack = new ItemStack(GCItems.basicItem, 1, 9);
    	ItemStack button = new ItemStack(Item.getItemFromBlock(Blocks.stone_button), 1);
    	ItemStack laserDiodeStack = ARItems.laserDiode.getItemStack(1);
        ItemStack cryoDiodeStack = ARItems.cryoDiode.getItemStack(1);
        ItemStack beamCore = new ItemStack(AsteroidsItems.basicItem, 1, 8);

    	// *** circuit fabricator recipes ***
    	int siliconCount = OreDictionary.getOres(ConfigManagerCore.otherModsSilicon).size();
        for (int j = 0; j <= siliconCount; j++)
        {
        	ItemStack silicon;
        	if (j == 0) {
        		silicon = new ItemStack(GCItems.basicItem, 1, 2);
    		} else {
    			silicon = OreDictionary.getOres("itemSilicon").get(j - 1);
			}

        	CircuitFabricatorRecipes.addRecipe(enderWaferStack,
        			new ItemStack[] {
	        			new ItemStack(Items.diamond),
	        			silicon, silicon,
	        			new ItemStack(Items.redstone),
	        			new ItemStack(Items.ender_pearl)
			});

        	CircuitFabricatorRecipes.addRecipe(lithiumMeshStack,
        			new ItemStack[] {
        				lithiumGemStack,
	        			silicon, silicon,
	        			new ItemStack(Items.redstone),
	        			null,//new ItemStack(Items.ender_pearl)
			});


        }

        GameRegistry.addSmelting(
        		ARBlocks.getItemStack(ARBlocks.blockBasaltCobble, 1),
        		ARBlocks.getItemStack(ARBlocks.blockBasalt, 1), 1.0F);

        GameRegistry.addSmelting(
        		ARBlocks.getItemStack(ARBlocks.blockRedCobble, 1),
        		ARBlocks.getItemStack(ARBlocks.blockRedRock, 1), 1.0F);

        GameRegistry.addSmelting(
        		ARBlocks.getItemStack(ARBlocks.blockYellowCobble, 1),
        		ARBlocks.getItemStack(ARBlocks.blockYellowRock, 1), 1.0F);

        GameRegistry.addShapelessRecipe(ARBlocks.getItemStack(ARBlocks.blockSmoothBasalt, 1), ARBlocks.getItemStack(ARBlocks.blockBasalt, 1));

        // *** raygun reload ***
        ItemStack battery = new ItemStack(GCItems.battery, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack liBattery = new ItemStack(ARItems.batteryLithium, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack quBattery = new ItemStack(ARItems.batteryQuantum, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack enBattery = new ItemStack(ARItems.batteryEnder,   1, OreDictionary.WILDCARD_VALUE);

        ItemStack raygun = new ItemStack(ARItems.raygun, 1, OreDictionary.WILDCARD_VALUE);
        ItemStack cryogun = new ItemStack(ARItems.cryogun, 1, OreDictionary.WILDCARD_VALUE);
        initRaygunReloadingRecipes(new ItemStack[]{
        		raygun,
        		cryogun
        }, new ItemStack[]{
        		battery,
        		liBattery,
        		quBattery,
        		enBattery
        });

        // *** regular crafting ***

        // batteries
        GameRegistry.addRecipe(liBattery, new Object[]{
            	" X ",
            	"XBX",
            	"XAX",
            	'X', compressedAluStack, // 8 = metadata for compressed alu
            	'A', enderWaferStack,
            	'B', lithiumMeshStack
            });

        // TODO find a better recipe, it's too cheap otherwise
        GameRegistry.addRecipe(enBattery, new Object[]{
            	" X ",
            	"XBX",
            	"XAX",
            	'X', compressedAluStack, // 8 = metadata for compressed alu
            	'A', enderWaferStack,
            	'B', Blocks.redstone_block
            });

        // laser diode
        GameRegistry.addRecipe(laserDiodeStack, new Object[]{
        	"XXX",
        	"ABC",
        	"XXX",
        	'X', compressedAluStack, // 8 = metadata for compressed alu
        	'A', Blocks.glass_pane,
        	'B', ARItems.rubyGem.getItemStack(1),
        	'C', beamCore
        });

        // cryo diode
        GameRegistry.addRecipe(cryoDiodeStack, new Object[]{
        	"XXX",
        	"ABC",
        	"XXX",
        	'X', compressedAluStack, // 8 = metadata for compressed alu
        	'A', Blocks.glass_pane,
        	'B', ARItems.coldCrystal.getItemStack(1),
        	'C', beamCore
        });

        // laser gun
        GameRegistry.addRecipe(raygun, new Object[]{
        	"XYZ",
        	" AZ",
        	"  B",
        	'X', laserDiodeStack,
        	'Y', enderWaferStack,
        	'Z', compressedSteelStack,
        	'A', button,
        	'B', battery
        });

        // cryo gun
        GameRegistry.addRecipe(cryogun, new Object[]{
            	"XYZ",
            	" AZ",
            	"  B",
            	'X', cryoDiodeStack,
            	'Y', enderWaferStack,
            	'Z', compressedSteelStack,
            	'A', button,
            	'B', battery
            });



        GameRegistry.addRecipe(ARBlocks.getItemStack(ARBlocks.blockBasaltBrick, 4), new Object[]{
        	"XX",
        	"XX",
        	'X', ARBlocks.getItemStack(ARBlocks.blockBasalt, 1)
        });

        GameRegistry.addRecipe(ARBlocks.getItemStack(ARBlocks.blockObsidianBrick, 4), new Object[]{
        	"XX",
        	"XX",
        	'X', Blocks.obsidian
        });

        GameRegistry.addRecipe(ARBlocks.getItemStack(ARBlocks.blockAluCrate, 32), new Object[]{
        	" X ",
        	"X X",
        	" X ",
        	'X', new ItemStack(GCItems.basicItem, 1, 8) // 8 = metadata for compressed alu
        });



    }

    /**
     * Helper function to add all crafting recipes for all rayguns and batteries...
     *
     * @param guns
     * @param batteries
     */
    protected void initRaygunReloadingRecipes(ItemStack[] guns, ItemStack[] batteries) {
    	for(ItemStack gun: guns) {
    		for(ItemStack battery: batteries) {
    			GameRegistry.addShapelessRecipe(gun, new Object[]{gun, battery});
    		}
    	}
    }

    protected void registerTrading() {
    	RobotVillagerProfession.addProfession(new RobotVillagerProfession(
    			new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/blocks/electricFurnace.png"),
    			"furnace")
        			.addRecipe(Items.beef, 4, Items.cooked_beef)
        			.addRecipe(new ItemStack(Items.iron_axe, 1), new ItemStack(Items.emerald, 6), new ItemStack(Items.iron_ingot, 3))
        			.addRecipe(new ItemStack(Items.iron_door, 1), new ItemStack(Items.emerald, 12), new ItemStack(Items.iron_ingot, 6))
        			.addRecipe(new ItemStack(Items.iron_hoe, 1), new ItemStack(Items.emerald, 4), new ItemStack(Items.iron_ingot, 2))
    				.addRecipe(new ItemStack(Items.iron_pickaxe, 1), new ItemStack(Items.emerald, 6), new ItemStack(Items.iron_ingot, 3))
    				.addRecipe(new ItemStack(Items.iron_shovel,  1), new ItemStack(Items.emerald, 2), new ItemStack(Items.iron_ingot, 1))
    		);

        	ItemStack emptyCan = new ItemStack(GCItems.oilCanister, 1, GCItems.oilCanister.getMaxDamage());


        	// offers oxygen refill, and maybe other stuff, TBD
        	RobotVillagerProfession.addProfession(new RobotVillagerProfession(
        			new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/blocks/machine_compressor_1.png"),
        			"compressor")
        		.addRecipe(emptyCan, new ItemStack(Items.emerald, 24), new ItemStack(AsteroidsItems.canisterLOX, 1, 1))
        		.addRecipe(emptyCan, new ItemStack(Items.emerald, 4), new ItemStack(AsteroidsItems.canisterLN2, 1, 1))
        		.addRecipe(new ItemStack(Items.emerald, 2), emptyCan)
        		.addRecipe(new ItemStack(GCItems.oxTankLight, 1, GCItems.oxTankLight.getMaxDamage()), new ItemStack(Items.emerald, 4), new ItemStack(GCItems.oxTankLight, 1))
        		.addRecipe(new ItemStack(GCItems.oxTankMedium, 1, GCItems.oxTankMedium.getMaxDamage()), new ItemStack(Items.emerald, 8), new ItemStack(GCItems.oxTankMedium, 1))
        		.addRecipe(new ItemStack(GCItems.oxTankHeavy, 1, GCItems.oxTankHeavy.getMaxDamage()), new ItemStack(Items.emerald, 16), new ItemStack(GCItems.oxTankHeavy, 1))
        	);

        	/*
        	 * can't make the battery work, because it resets on being crafted
        	// register battery refill
        	RobotVillagerProfession.addProfession(new RobotVillagerProfession(
        			new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/blocks/coalGenerator.png"),
        			"generator")
        		.addRecipe(new ItemStack(GCItems.battery, 1, GCItems.battery.getMaxDamage()), new ItemStack(Items.emerald, 8) , new ItemStack(GCItems.battery, 1, 50))
        	);*/
        	RobotVillagerProfession.addProfession(new RobotVillagerProfession(
        			new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/blocks/refinery_front.png"),
        			"refinery")
        		.addRecipe(new ItemStack(GCItems.oilCanister, 1, 1), new ItemStack(Items.emerald, 16), new ItemStack(GCItems.fuelCanister, 1, 1))
        		.addRecipe(emptyCan, new ItemStack(Items.emerald, 26), new ItemStack(GCItems.fuelCanister, 1, 1))
        	);
        	RobotVillagerProfession.addProfession(new RobotVillagerProfession(
        			new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/blocks/electric_compressor.png"),
        			"ingotcompressor")
        		.addRecipe(new ItemStack(Items.iron_ingot, 2), new ItemStack(Items.emerald, 4), new ItemStack(GCItems.basicItem, 1, 11))// 11 = iron

        		.addRecipe(new ItemStack(GCItems.basicItem, 2, 5), new ItemStack(Items.emerald, 4), new ItemStack(GCItems.basicItem, 1, 8))// 8 = alu
        		.addRecipe(new ItemStack(GCItems.basicItem, 2, 4), new ItemStack(Items.emerald, 4), new ItemStack(GCItems.basicItem, 1, 7))// 7 = tin
        		.addRecipe(new ItemStack(GCItems.basicItem, 2, 3), new ItemStack(Items.emerald, 4), new ItemStack(GCItems.basicItem, 1, 6))// 6 = copper
        	);

        	RobotVillagerProfession.addProfession(new RobotVillagerProfession(
        			new ResourceLocation(GalacticraftCore.ASSET_PREFIX, "textures/blocks/circuit_fabricator.png"),
        			"circuitfabricator")
        		.addRecipe(new ItemStack(Items.dye, 1, 4), new ItemStack(Items.emerald, 4), new ItemStack(GCItems.basicItem, 9, 12))// solar thingys
        		.addRecipe(new ItemStack(Blocks.redstone_torch), new ItemStack(Items.emerald, 6), new ItemStack(GCItems.basicItem, 3, 13))// basic wafer
        		.addRecipe(new ItemStack(Items.repeater), new ItemStack(Items.emerald, 8), new ItemStack(GCItems.basicItem, 2, 14))// advanced wafer
        		.addRecipe(new ItemStack(Items.ender_pearl), new ItemStack(Items.emerald, 10), ARItems.baseItem.getItemStack("waferEnder", 1))// ender wafer


        	);

        	/*RobotVillagerProfession.addProfession(new RobotVillagerProfession(
        			new ResourceLocation(AmunRa.ASSETPREFIX, "textures/blocks/crafter.png"),
        			"crafter")
        		.addRecipe(new ItemStack(Items.dye, 1, 4), new ItemStack(Items.emerald, 4), new ItemStack(GCItems.basicItem, 9, 12))



        	);*/
    }

    protected void initCelestialBodies() {

    	systemAmunRa = new SolarSystem("systemAmunRa", "milkyWay");
    	starRa = new Star("starRa");
    	systemAmunRa.setMainStar(starRa).setMapPosition(new Vector3(1.5F, -1.15F, 0.0F));
    	GalaxyRegistry.registerSolarSystem(systemAmunRa);

    	starRa.setBodyIcon(new ResourceLocation(this.ASSETPREFIX, "textures/gui/celestialbodies/sun-red2.png"));
    	starRa.setParentSolarSystem(systemAmunRa);



    	starAmun = createPlanet("starAmun", "sun-blue.png", Math.PI * 0.1, 0.7, 0.9);
    	starAmun.setParentSolarSystem(systemAmunRa);
    	GalaxyRegistry.registerPlanet(starAmun);


    	// two inner planets
    	planetOsiris = createPlanet("osiris", "planet-mercury.png", Math.PI * 0.8, 0.34, 0.4);
    	planetOsiris.setParentSolarSystem(systemAmunRa);
    	GalaxyRegistry.registerPlanet(planetOsiris);

    	planetHorus = createPlanet("horus", "planet-horus.png", Math.PI * 1.3, 0.55, 0.458);
    	planetHorus.setParentSolarSystem(systemAmunRa);
    	planetHorus.setDimensionInfo(dimHorus, HorusWorldProvider.class);
    	GalacticraftRegistry.registerTeleportType(HorusWorldProvider.class, new TeleportTypeMoon());
    	planetHorus.setTierRequired(3);
    	GalaxyRegistry.registerPlanet(planetHorus);



    	// gas giant
    	planetBaal = createPlanet("baal", "planet-gas03.png", Math.PI * 1.9, 1.2, 1.4);
    	planetBaal.setParentSolarSystem(systemAmunRa);
    	GalaxyRegistry.registerPlanet(planetBaal);

    	// .. and its moons
    	// ring, aka innermost moon
    	// the regular moon has a distance of 13
    	moonBaalRings = createMoon("baalRings", "micromoon.png", 1.58, 9, 100);
    	moonBaalRings.setParentPlanet(planetBaal);
    	GalaxyRegistry.registerMoon(moonBaalRings);

    	// moon god, but something to do with the creation of life? so maybe stuff here as well
    	moonKhonsu = createMoon("khonsu", "moon.png", 1.9*Math.PI, 12.45, 110);
    	moonKhonsu.setParentPlanet(planetBaal);
    	GalaxyRegistry.registerMoon(moonKhonsu);

    	// this will have an oxygen atmosphere. neper was some kind of a grain god, so
    	moonNeper = createMoon("neper", "planet-life-o2.png", 1.58, 14.9, 140);
    	moonNeper.atmosphere.add(IAtmosphericGas.NITROGEN);
    	moonNeper.atmosphere.add(IAtmosphericGas.OXYGEN);
    	moonNeper.atmosphere.add(IAtmosphericGas.CO2);
    	moonNeper.atmosphere.add(IAtmosphericGas.HELIUM);
    	moonNeper.setDimensionInfo(dimNeper, NeperWorldProvider.class);
    	moonNeper.setParentPlanet(planetBaal);
    	moonNeper.setTierRequired(3);
    	GalacticraftRegistry.registerTeleportType(NeperWorldProvider.class, new TeleportTypeOverworld());
    	// GalacticraftRegistry.registerTeleportType(WorldProviderMoon.class, new TeleportTypeMoon());
    	// GalacticraftRegistry.registerTeleportType(WorldProviderSurface.class, new TeleportTypeOverworld());
    	GalaxyRegistry.registerMoon(moonNeper);

    	// just some dead rock. iah was a moon god
    	moonIah = createMoon("iah", "moon.png", 3.1, 18.5, 162);
    	moonIah.setParentPlanet(planetBaal);
    	GalaxyRegistry.registerMoon(moonIah);



    	// an asteroid belt. todo figure the other stuff out later
    	asteroidBeltMehen = createPlanet("asteroidBeltMehen", "micromoon.png", Math.PI * 0.19, 1.4, 1.6);
    	asteroidBeltMehen.setParentSolarSystem(systemAmunRa);
    	GalaxyRegistry.registerPlanet(asteroidBeltMehen);

    	// another gas giant?
    	planetSekhmet = createPlanet("sekhmet", "planet-gas02.png", Math.PI * 0.6, 1.6, 1.8);
    	planetSekhmet.setParentSolarSystem(systemAmunRa);
    	GalaxyRegistry.registerPlanet(planetSekhmet);


    	// ... and it's moons
    	// cat goddess, of course it's a moon of sekhmet
    	moonBastet = createMoon("bast", "moon.png", 3.1, 9.8, 122);
    	moonBastet.setParentPlanet(planetSekhmet);
    	GalaxyRegistry.registerMoon(moonBastet);

    	// lion goddess, dito
    	moonMaahes = createMoon("maahes", "planet-life-ch4.png", 4.514, 11.4, 136);
    	moonMaahes.setParentPlanet(planetSekhmet);
    	moonMaahes.atmosphere.add(IAtmosphericGas.CO2);
    	moonMaahes.atmosphere.add(IAtmosphericGas.METHANE);
    	moonMaahes.atmosphere.add(IAtmosphericGas.HYDROGEN);
    	moonMaahes.atmosphere.add(IAtmosphericGas.ARGON);
    	moonMaahes.setDimensionInfo(dimMaahes, MaahesWorldProvider.class);
    	moonMaahes.setTierRequired(3);
    	GalacticraftRegistry.registerTeleportType(MaahesWorldProvider.class, new TeleportTypeOverworld());

    	GalaxyRegistry.registerMoon(moonMaahes);

    	moonThoth = createMoon("thoth", "moon.png", 1.9, 15.5, 145);
    	moonThoth.setParentPlanet(planetSekhmet);
    	GalaxyRegistry.registerMoon(moonThoth);

    	moonSeth = createMoon("seth", "moon.png", 6, 17.98, 198);
    	moonSeth.setParentPlanet(planetSekhmet);
    	GalaxyRegistry.registerMoon(moonSeth);
    	/*
moon.bast=Bastet
moon.maahes=Maahes
moon.thoth=Thoth
moon.seth=Seth*/

    	// a small rocky planet
    	planetAnubis = createPlanet("anubis", "moon.png", Math.PI * 0.36, 1.9, 2.2);
    	planetAnubis.setParentSolarSystem(systemAmunRa);
    	planetAnubis.setDimensionInfo(dimAnubis, AnubisWorldProvider.class);
    	GalacticraftRegistry.registerTeleportType(AnubisWorldProvider.class, new TeleportTypeMoon());
    	planetAnubis.setTierRequired(3);
    	GalaxyRegistry.registerPlanet(planetAnubis);

    	//..with a moon nonetheless
    	moonKebe = createMoon("kebe", "moon.png", 5.1, 19, 253);
    	moonKebe.setParentPlanet(planetAnubis);
    	GalaxyRegistry.registerMoon(moonKebe);


    }

    protected Planet createPlanet(String name, String texture, double phaseShift, double distance, double orbitTime) {
    	Planet pl = new Planet(name);
    	setCelestialBodyStuff(pl, texture, phaseShift, distance, orbitTime);
    	return pl;
    }

    protected Moon createMoon(String name, String texture, double phaseShift, double distance, double orbitTime) {
    	Moon pl = new Moon(name);
    	setCelestialBodyStuff(pl, texture, phaseShift, distance, orbitTime);
    	return pl;
    }

    protected void setCelestialBodyStuff(CelestialBody body, String texture, double phaseShift, double distance, double orbitTime) {
    	body.setBodyIcon(new ResourceLocation(this.ASSETPREFIX, "textures/gui/celestialbodies/"+texture))
	    	.setPhaseShift((float) phaseShift)
	    	.setRelativeDistanceFromCenter(new ScalableDistance((float)distance, (float)distance))
	    	.setRelativeOrbitTime((float)orbitTime);
    }


}
