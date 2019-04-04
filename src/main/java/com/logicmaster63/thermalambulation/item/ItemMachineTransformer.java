package com.logicmaster63.thermalambulation.item;

import cofh.core.util.helpers.ServerHelper;
import com.logicmaster63.thermalambulation.RemoteMachineRegistry;
import com.logicmaster63.thermalambulation.ThermalAmbulation;
import com.logicmaster63.thermalambulation.machine.MachineProxy;
import com.logicmaster63.thermalambulation.entity.EntityWalker;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

public class ItemMachineTransformer extends ItemBase {

    public ItemMachineTransformer() {
        super("item_machine_transformer");
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isAirBlock(pos) || !ServerHelper.isServerWorld(worldIn) || !player.isSneaking())
            return EnumActionResult.PASS;
        int index = RemoteMachineRegistry.get().proxyMachine(worldIn, pos);
        if(index == -1)
            return EnumActionResult.PASS;

        /*
            This needs to be redone to create machine on client, too
         */

        EntityWalker walker = new EntityWalker(worldIn);
        MachineProxy machine = new MachineProxy();
        machine.setIndex(index);
        machine.init();
        walker.setMachine(machine);
        walker.posX = pos.getX();
        walker.posY = pos.getY();
        walker.posZ = pos.getZ();
        //ThermalAmbulation.logger.log(Level.INFO, pos);
        walker.onInitialSpawn(worldIn.getDifficultyForLocation(pos), null);
        worldIn.spawnEntity(walker);
        return EnumActionResult.SUCCESS;
    }
}
