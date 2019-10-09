package com.logicmaster63.thermalambulation.proxy;

import clayborn.universalremote.hooks.world.HookedClientWorld;
import cofh.api.tileentity.IReconfigurableSides;
import cofh.api.tileentity.IRedstoneControl;
import cofh.core.block.TilePowered;
import cofh.core.gui.element.tab.TabBase;
import cofh.core.gui.element.tab.TabConfiguration;
import cofh.core.gui.element.tab.TabRedstoneControl;
import cofh.thermalexpansion.block.machine.*;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import cofh.thermalexpansion.gui.client.machine.GuiCrafter;
import cofh.thermalexpansion.gui.client.machine.GuiSmelter;
import com.logicmaster63.thermalambulation.gui.proxy.*;
import com.logicmaster63.thermalambulation.ThermalAmbulation;
import com.logicmaster63.thermalambulation.block.Blocks;
import com.logicmaster63.thermalambulation.dimension.Dimensions;
import com.logicmaster63.thermalambulation.entity.Entities;
import com.logicmaster63.thermalambulation.item.Items;
import com.logicmaster63.thermalambulation.machine.AmbulationUtils;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class CommonProxy {
    public void preInit(FMLPreInitializationEvent event) {
        Entities.register();
        Dimensions.init();
    }

    public void init(FMLInitializationEvent event) {
        ForgeChunkManager.setForcedChunkLoadingCallback(ThermalAmbulation.instance, (tickets, world) -> {

        });
    }

    public void postInit(FMLPostInitializationEvent event) {
    }

    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> event) {
        Items.register(event.getRegistry());
        Blocks.registerItemBlocks(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerBlocks(RegistryEvent.Register<Block> event) {
        Blocks.register(event.getRegistry());
    }

    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event) {
        Items.registerModels();
        Blocks.registerModels();
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        if (event.getWorld().provider.getDimensionType().getId() == 63 && !event.getWorld().isRemote) {
            ForgeChunkManager.Ticket ticket = ForgeChunkManager.requestTicket(ThermalAmbulation.instance, event.getWorld(), ForgeChunkManager.Type.NORMAL);
            ForgeChunkManager.forceChunk(ticket, event.getWorld().getChunkFromBlockCoords(new BlockPos(0, 0, 0)).getPos());
            ForgeChunkManager.forceChunk(ticket, event.getWorld().getChunkFromBlockCoords(new BlockPos(0, 0, 18)).getPos());
            ThermalAmbulation.logger.info("Persistant chunks: " + event.getWorld().getPersistentChunks());
        }
    }

    @SubscribeEvent
    public static void onGUI(GuiOpenEvent event) {
        // Remove unused tabs from machines, possibly adding custom ones in future
        // Todo: See if removing brief visibility of hidden tabs in GUIs is possible
        Gui gui = event.getGui();
        if (gui instanceof GuiPoweredBase) {
            TilePowered tile = ((TilePowered) AmbulationUtils.getPrivateValue(gui, GuiPoweredBase.class, "baseTile"));
            // Check if the remote gui spoofing is active
            if (AmbulationUtils.getPrivateValue(tile.getWorld(), HookedClientWorld.class, "m_tile") != null) {
                switch (BlockMachine.Type.values()[tile.getType()]) {
                    case FURNACE:
                        event.setGui(new GuiFurnaceProxy(Minecraft.getMinecraft().player.inventory, tile));
                        break;
                    case PULVERIZER:
                        event.setGui(new GuiPulverizerProxy(Minecraft.getMinecraft().player.inventory, tile));
                        break;
                    case SAWMILL:
                        event.setGui(new GuiSawmillProxy(Minecraft.getMinecraft().player.inventory, tile));
                        break;
                    case SMELTER:
                        event.setGui(new GuiSmelterProxy(Minecraft.getMinecraft().player.inventory, tile));
                        break;
                    case INSOLATOR:
                        event.setGui(new GuiInsolatormillProxy(Minecraft.getMinecraft().player.inventory, tile));
                        break;
                    case COMPACTOR:
                        event.setGui(new GuiCompactorProxy(Minecraft.getMinecraft().player.inventory, tile));
                        break;
                    case CRUCIBLE:
                        event.setGui(new GuiCrucibleProxy(Minecraft.getMinecraft().player.inventory, tile));
                        break;
                    case REFINERY:
                        event.setGui(new GuiRefineryProxy(Minecraft.getMinecraft().player.inventory, tile));
                        break;
                    case TRANSPOSER:
                        event.setGui(new GuiTransposerProxy(Minecraft.getMinecraft().player.inventory, tile));
                        break;
                    case CHARGER:
                        event.setGui(new GuiChargerProxy(Minecraft.getMinecraft().player.inventory, tile));
                        break;
                    case CENTRIFUGE:
                        event.setGui(new GuiCentrifugeProxy(Minecraft.getMinecraft().player.inventory, tile));
                        break;
                    case CRAFTER:
                        event.setGui(new GuiCrafterProxy(Minecraft.getMinecraft().player.inventory, tile));
                        break;
                    case BREWER:
                        event.setGui(new GuiBrewerProxy(Minecraft.getMinecraft().player.inventory, tile));
                        break;
                    case ENCHANTER:
                        event.setGui(new GuiEnchantorProxy(Minecraft.getMinecraft().player.inventory, tile));
                        break;
                    case PRECIPITATOR:
                        event.setGui(new GuiPrecipitatorProxy(Minecraft.getMinecraft().player.inventory, tile));
                        break;
                    case EXTRUDER:
                        event.setGui(new GuiExtruderProxy(Minecraft.getMinecraft().player.inventory, tile));
                        break;
                }
            }
        }
    }
}
