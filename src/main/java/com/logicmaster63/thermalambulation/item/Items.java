package com.logicmaster63.thermalambulation.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class Items {
    public static ItemBase ingotCopper = new ItemBase("ingot_copper");
    public static ItemBase upgradeSolar = new ItemBase("upgrade_solar");
    public static ItemMachineTransformer machineTransformer = new ItemMachineTransformer();

    public static void register(IForgeRegistry<Item> registry) {
        registry.registerAll(
                ingotCopper, machineTransformer, upgradeSolar
        );
    }

    public static void registerModels() {
        ingotCopper.registerItemModel();
        machineTransformer.registerItemModel();
        upgradeSolar.registerItemModel();
    }
}
