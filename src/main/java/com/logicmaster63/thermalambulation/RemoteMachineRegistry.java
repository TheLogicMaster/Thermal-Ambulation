package com.logicmaster63.thermalambulation;

import cofh.api.block.IDismantleable;
import cofh.core.block.BlockCoreTile;
import cofh.core.util.helpers.BlockHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.block.machine.TileMachineBase;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import org.apache.logging.log4j.Level;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RemoteMachineRegistry extends WorldSavedData {

    private static final String DATA_NAME = ThermalAmbulation.MOD_ID + "_RemoteMachineData";
    private static final int MAX_MACHINES = 9001;

    private HashMap<Integer, BlockPos> machines = new HashMap<>();

    public RemoteMachineRegistry() {
        super(DATA_NAME);
    }

    public RemoteMachineRegistry(String s) {
        super(s);
    }

    public static RemoteMachineRegistry get() {
        MapStorage storage = Minecraft.getMinecraft().world.getMapStorage();
        RemoteMachineRegistry instance = (RemoteMachineRegistry) storage.getOrLoadData(RemoteMachineRegistry.class, DATA_NAME);

        if (instance == null) {
            instance = new RemoteMachineRegistry();
            storage.setData(DATA_NAME, instance);
        }
        return instance;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        machines = new HashMap<>();
        int entries = nbt.getInteger("entries");
        for (int i = 0; i < entries; i++) {
            machines.put(nbt.getInteger(Integer.toString(i)), BlockPos.fromLong(nbt.getLong(Integer.toString(i))));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("entries", machines.size());
        int i = 0;
        for (Map.Entry<Integer, BlockPos> entry : machines.entrySet()) {
            compound.setLong(Integer.toString(i), entry.getValue().toLong());
            compound.setInteger(Integer.toString(i), entry.getKey());
            i++;
        }
        return compound;
    }

    public TileEntity getRemoteTileEntity(int index) {
        return DimensionManager.getWorld(63).getTileEntity(machines.get(index));
    }

    public int proxyMachine(World world, BlockPos source) {
        if (world.getTileEntity(source) == null || world.isAirBlock(source))
            return -1;
        for (int i = 0; i < MAX_MACHINES; i++) {
            if (!machines.containsKey(i)) {
                BlockPos pos = new BlockPos(2 * i, 0, 0);
                DimensionManager.getWorld(63).setBlockState(pos, world.getBlockState(source));
                NBTTagCompound nbt = world.getTileEntity(source).writeToNBT(new NBTTagCompound());
                NBTTagCompound emptied = nbt.copy();
                emptied.setTag("Inventory", new NBTTagList());
                emptied.setTag("Items", new NBTTagList());
                //ThermalAmbulation.logger.log(Level.INFO, nbt);
                world.getTileEntity(source).deserializeNBT(emptied);
                //if(world.getTileEntity(sourcePos) instanceof TileMachineBase)
                //((TileMachineBase) world.getTileEntity(sourcePos)).readInventoryFromNBT(new NBTTagCompound());
                world.destroyBlock(source, false);
                nbt.setInteger("x", pos.getX());
                nbt.setInteger("y", pos.getY());
                nbt.setInteger("z", pos.getZ());
                DimensionManager.getWorld(63).getTileEntity(pos).deserializeNBT(nbt);
                machines.put(i, pos);
                markDirty();
                return i;
            }
        }
        return -1;
    }
}
