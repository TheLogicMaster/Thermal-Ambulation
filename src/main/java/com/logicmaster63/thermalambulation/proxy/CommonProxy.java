package com.logicmaster63.thermalambulation.proxy;

import clayborn.universalremote.hooks.client.MinecraftProxy;
import clayborn.universalremote.hooks.entity.HookedEntityPlayerMP;
import clayborn.universalremote.hooks.world.HookedClientWorld;
import com.logicmaster63.thermalambulation.ThermalAmbulation;
import com.logicmaster63.thermalambulation.block.Blocks;
import com.logicmaster63.thermalambulation.dimension.Dimensions;
import com.logicmaster63.thermalambulation.entity.Entities;
import com.logicmaster63.thermalambulation.item.Items;
import com.logicmaster63.thermalambulation.networking.EntityMachineMessage;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.relauncher.Side;

import java.util.List;

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

    }
}
