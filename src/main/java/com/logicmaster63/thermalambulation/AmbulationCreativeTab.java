package com.logicmaster63.thermalambulation;

import com.logicmaster63.thermalambulation.item.Items;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class AmbulationCreativeTab extends CreativeTabs {

    public AmbulationCreativeTab() {
        super(ThermalAmbulation.MOD_ID);
        setBackgroundImageName("thermalambulation.png");
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(Items.ingotCopper);
    }

}