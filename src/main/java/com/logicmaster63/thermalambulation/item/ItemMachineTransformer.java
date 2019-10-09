package com.logicmaster63.thermalambulation.item;

import clayborn.universalremote.config.UniversalRemoteConfiguration;
import clayborn.universalremote.hooks.entity.HookedEntityPlayerMP;
import clayborn.universalremote.hooks.events.PlayerRemoteGuiDataManagerServer;
import clayborn.universalremote.hooks.events.PlayerWorldSyncServer;
import clayborn.universalremote.hooks.world.WorldServerProxy;
import clayborn.universalremote.items.ItemRegistry;
import clayborn.universalremote.items.ItemUniversalRemote;
import clayborn.universalremote.util.TextFormatter;
import clayborn.universalremote.util.Util;
import cofh.core.block.BlockCoreTile;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalexpansion.block.machine.TileMachineBase;
import com.logicmaster63.thermalambulation.RemoteMachineRegistry;
import com.logicmaster63.thermalambulation.ThermalAmbulation;
import com.logicmaster63.thermalambulation.machine.MachineProxy;
import com.logicmaster63.thermalambulation.entity.EntityWalker;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.property.IExtendedBlockState;
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
        ThermalAmbulation.logger.info("DIR: " + ((TileMachineBase) worldIn.getTileEntity(pos)).getFacing());
        float dir = EnumFacing.getFront(((TileMachineBase) worldIn.getTileEntity(pos)).getFacing()).getHorizontalAngle();
        MachineProxy machine = RemoteMachineRegistry.get().proxyMachine(worldIn.getBlockState(pos), nbt);
        if (machine == null)
            return EnumActionResult.PASS;

        EntityWalker walker = (EntityWalker) EntityList.createEntityByID(EntityList.getID(EntityWalker.class), worldIn);
        if (walker == null) {
            ThermalAmbulation.logger.warn("Failed to create walker entity(Add more debug output here)");
            // Destroy machine world proxy on fail
            return EnumActionResult.PASS;
        }

        walker.setMachine(machine);
        walker.setPositionAndRotation(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, dir, 0);
        walker.setTamed(true);
        walker.setOwnerId(player.getUniqueID());
        NBTTagCompound emptied = worldIn.getTileEntity(pos).serializeNBT();
        emptied.setTag("Inventory", new NBTTagList());
        emptied.setTag("Items", new NBTTagList());
        worldIn.getTileEntity(pos).deserializeNBT(emptied);
        worldIn.destroyBlock(pos, false);
        ThermalAmbulation.logger.log(Level.INFO, "Created walker entity(Add more debug info)");

        worldIn.spawnEntity(walker);
        return EnumActionResult.SUCCESS;
    }
}
