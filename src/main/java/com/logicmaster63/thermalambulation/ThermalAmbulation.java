package com.logicmaster63.thermalambulation;

import com.logicmaster63.thermalambulation.block.Blocks;
import com.logicmaster63.thermalambulation.command.ClearProxiesCommand;
import com.logicmaster63.thermalambulation.command.TeleportCommand;
import com.logicmaster63.thermalambulation.dimension.Dimensions;
import com.logicmaster63.thermalambulation.item.Items;
import com.logicmaster63.thermalambulation.proxy.CommonProxy;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import org.apache.logging.log4j.Logger;

@Mod(modid = ThermalAmbulation.MOD_ID, name = ThermalAmbulation.MOD_NAME, version = ThermalAmbulation.VERSION, dependencies = "required-after:thermalexpansion")
public class ThermalAmbulation {
    public static final String MOD_ID = "thermalambulation";
    public static final String MOD_NAME = "Thermal Ambulation";
    public static final String VERSION = "1.0";

    public static final AmbulationCreativeTab creativeTab = new AmbulationCreativeTab();
    public static Logger logger;
    public static SimpleNetworkWrapper networkWrapper = new SimpleNetworkWrapper(MOD_ID);

    @SidedProxy(serverSide = "com.logicmaster63.thermalambulation.proxy.ServerProxy", clientSide = "com.logicmaster63.thermalambulation.proxy.ClientProxy")
    public static CommonProxy proxy;

    @Mod.Instance(MOD_ID)
    public static ThermalAmbulation instance;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
        proxy.preInit(event);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init(event);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        proxy.postInit(event);
    }

    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new TeleportCommand());
        event.registerServerCommand(new ClearProxiesCommand());
    }
}
