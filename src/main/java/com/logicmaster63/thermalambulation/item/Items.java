package com.logicmaster63.thermalambulation.item;

import com.logicmaster63.thermalambulation.item.upgrade.ItemUpgrade;
import com.logicmaster63.thermalambulation.item.upgrade.SolarUpgrade;
import com.zeitheron.solarflux.api.SolarFluxAPI;
import com.zeitheron.solarflux.api.SolarInfo;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.libraries.ModList;
import net.minecraftforge.registries.IForgeRegistry;

public class Items {
    public static ItemBase ingotCopper = new ItemBase("ingot_copper");
    public static ItemUpgrade upgradeSolar;
    public static ItemMachineTransformer machineTransformer = new ItemMachineTransformer();

    public static void register(IForgeRegistry<Item> registry) {
        if (Loader.isModLoaded("solarflux"))
            registerSolarFlux(registry);
        registry.registerAll(
                ingotCopper, machineTransformer//, upgradeSolar
        );
    }

    @Optional.Method(modid = "solarflux")
    private static void registerSolarFlux(IForgeRegistry<Item> registry) {
        for (SolarInfo info: SolarFluxAPI.SOLAR_PANELS)
            registry.register(new SolarUpgrade(info));

    }

    public static void registerModels() {
        ingotCopper.registerItemModel();
        machineTransformer.registerItemModel();
        //upgradeSolar.registerItemModel();
    }
}
