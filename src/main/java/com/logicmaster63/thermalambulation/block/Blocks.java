package com.logicmaster63.thermalambulation.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.registries.IForgeRegistry;

public class Blocks {

    public static BlockOre oreCopper = new BlockOre("ore_copper");

    public static void register(IForgeRegistry<Block> registry) {
        registry.registerAll(
                oreCopper
        );
    }

    public static void registerItemBlocks(IForgeRegistry<Item> registry) {
        registry.registerAll(
                oreCopper.createItemBlock()
        );
    }

    public static void registerModels() {
        oreCopper.registerItemModel();
    }
}