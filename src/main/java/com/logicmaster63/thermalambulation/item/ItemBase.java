package com.logicmaster63.thermalambulation.item;

import com.logicmaster63.thermalambulation.ThermalAmbulation;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;

public class ItemBase extends Item {

    protected String name;

    public ItemBase(String name) {
        this.name = name;
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(ThermalAmbulation.creativeTab);
    }

    public void registerItemModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(ThermalAmbulation.MOD_ID + ":" + name, "inventory"));
    }

    @Override
    public ItemBase setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(tab);
        return this;
    }
}