package com.logicmaster63.thermalambulation.item;

import cofh.core.util.helpers.ServerHelper;
import com.logicmaster63.thermalambulation.RemoteMachineRegistry;
import com.logicmaster63.thermalambulation.ThermalAmbulation;
import com.logicmaster63.thermalambulation.machine.MachineProxy;
import com.logicmaster63.thermalambulation.entity.EntityWalker;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
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
        if (worldIn.isAirBlock(pos) || !ServerHelper.isServerWorld(worldIn) || !player.isSneaking() || worldIn.getTileEntity(pos) == null)
            return EnumActionResult.PASS;
        NBTTagCompound nbt = worldIn.getTileEntity(pos).serializeNBT();
        MachineProxy machine = RemoteMachineRegistry.get().proxyMachine(worldIn.getBlockState(pos), nbt);
        if(machine == null)
            return EnumActionResult.PASS;

        /*
            This needs to be redone to create machine on client, too
         */

        EntityWalker walker = (EntityWalker) EntityList.createEntityByID(EntityList.getID(EntityWalker.class), worldIn);
        if (walker == null) {
            ThermalAmbulation.logger.warn("Failed to create walker entity(Add more debug output here)");
            // Destroy machine world proxy on fail
            return EnumActionResult.PASS;
        }

        walker.setMachine(machine);
        walker.setPosition(pos.getX(), pos.getY(), pos.getZ());
        worldIn.destroyBlock(pos, false);
        ThermalAmbulation.logger.log(Level.INFO, "Created walker entity(Add more debug info)");
        ThermalAmbulation.logger.info(walker);

        //walker.onInitialSpawn(worldIn.getDifficultyForLocation(pos), null);
        worldIn.spawnEntity(walker);
        return EnumActionResult.SUCCESS;
    }
}
