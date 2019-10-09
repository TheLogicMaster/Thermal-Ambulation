package com.logicmaster63.thermalambulation.proxy;

import clayborn.universalremote.hooks.world.HookedClientWorld;
import cofh.core.block.TilePowered;
import cofh.core.gui.element.ElementButton;
import cofh.thermalexpansion.block.machine.ItemBlockMachine;
import cofh.thermalexpansion.gui.client.GuiPoweredBase;
import com.logicmaster63.thermalambulation.ThermalAmbulation;
import com.logicmaster63.thermalambulation.block.Blocks;
import com.logicmaster63.thermalambulation.entity.Entities;
import com.logicmaster63.thermalambulation.entity.EntityWalker;
import com.logicmaster63.thermalambulation.entity.RenderWalker;
import com.logicmaster63.thermalambulation.item.Items;
import com.logicmaster63.thermalambulation.networking.EntityMachineMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemEgg;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.util.datafix.fixes.SpawnEggNames;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Level;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        Entities.registerRenderers();
        ThermalAmbulation.networkWrapper.registerMessage(EntityMachineMessage.MachineMessageHandler.class, EntityMachineMessage.class, 0, Side.CLIENT);
    }
}