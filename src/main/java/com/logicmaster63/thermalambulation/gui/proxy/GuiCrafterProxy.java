package com.logicmaster63.thermalambulation.gui.proxy;

import cofh.thermalexpansion.gui.client.machine.GuiCrafter;
import cofh.thermalexpansion.gui.client.machine.GuiFurnace;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.tileentity.TileEntity;

public class GuiCrafterProxy extends GuiCrafter {

    public GuiCrafterProxy(InventoryPlayer inventory, TileEntity tile) {
        super(inventory, tile);
    }

    // Todo: See if removing brief visibility of GUIs is possible

    @Override
    public void func_73866_w_() {
        super.func_73866_w_();
        redstoneTab.setVisible(false);
        configTab.setVisible(false);
    }

    @Override
    public void func_73876_c() {
        // Todo: Investigate the need for this super() call
        //super.func_73876_c();
        redstoneTab.setVisible(false);
        configTab.setVisible(false);
    }
}
