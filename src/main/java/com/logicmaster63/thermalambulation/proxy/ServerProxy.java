package com.logicmaster63.thermalambulation.proxy;

import com.logicmaster63.thermalambulation.ThermalAmbulation;
import com.logicmaster63.thermalambulation.networking.EntityMachineMessage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.SERVER)
public class ServerProxy extends ClientProxy {
    @Override
    public void preInit(FMLPreInitializationEvent e) {
        super.preInit(e);
        ThermalAmbulation.networkWrapper.registerMessage(EntityMachineMessage.MachineMessageHandler.class, EntityMachineMessage.class, 0, Side.SERVER);
    }
}
