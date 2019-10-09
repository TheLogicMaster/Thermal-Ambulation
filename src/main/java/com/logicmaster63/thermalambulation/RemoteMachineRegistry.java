package com.logicmaster63.thermalambulation;

import cofh.api.block.IDismantleable;
import cofh.api.tileentity.IInventoryRetainer;
import cofh.core.block.BlockCoreTile;
import cofh.core.util.CoreUtils;
import cofh.core.util.helpers.BlockHelper;
import cofh.core.util.helpers.ServerHelper;
import cofh.thermalexpansion.block.machine.BlockMachine;
import cofh.thermalexpansion.block.machine.TileMachineBase;
import com.logicmaster63.thermalambulation.machine.MachineProxy;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import net.minecraft.world.storage.WorldSavedData;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Level;

import java.util.*;

public class RemoteMachineRegistry extends WorldSavedData {

    private static final String DATA_NAME = ThermalAmbulation.MOD_ID + "_RemoteMachineData";
    private static final int MAX_MACHINES = 9001;

    private HashMap<Integer, BlockPos> machines = new HashMap<>();
    private World proxyWorld;

    public RemoteMachineRegistry() {
        this(DATA_NAME);
    }

    public RemoteMachineRegistry(String s) {
        super(s);
        proxyWorld = DimensionManager.getWorld(63);
        markDirty();
    }

    public static RemoteMachineRegistry get() {
        MapStorage storage = DimensionManager.getWorld(63).getMapStorage();
        RemoteMachineRegistry instance = (RemoteMachineRegistry) storage.getOrLoadData(RemoteMachineRegistry.class, DATA_NAME);
        if (instance == null) {
            ThermalAmbulation.logger.info("Machine world data didn't exist, so creating it.");
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
            machines.put(nbt.getInteger("K" + i), BlockPos.fromLong(nbt.getLong("V" + i)));
        }
        ThermalAmbulation.logger.info("Loaded remote machine registry");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound.setInteger("entries", machines.size());
        int i = 0;
        for (Map.Entry<Integer, BlockPos> entry : machines.entrySet()) {
            compound.setLong("V" + i, entry.getValue().toLong());
            compound.setInteger("K" + i, entry.getKey());
            i++;
        }
        ThermalAmbulation.logger.info("Saved remote machine registry");
        return compound;
    }

    public void clear() {
        for (BlockPos pos: machines.values())
            proxyWorld.setBlockToAir(pos);
        machines.clear();
        markDirty();
    }

    public ArrayList<ItemStack> retrieveMachine(int index) {
        BlockPos pos = machines.get(index);
        if (proxyWorld.isAirBlock(pos))
            return null;
        //NBTTagCompound nbt = proxyWorld.getTileEntity(pos).serializeNBT();
        BlockCoreTile block = (BlockMachine) proxyWorld.getBlockState(pos).getBlock();
        ArrayList<ItemStack> drops = block.dropDelegate(block.getItemStackTag(proxyWorld, pos), proxyWorld, pos, 1);
        TileEntity tile = proxyWorld.getTileEntity(pos);
        if (tile instanceof IInventoryRetainer && ((IInventoryRetainer) tile).retainInventory()) {
            // do nothing
        } else if (tile instanceof IInventory) {
            IInventory inv = (IInventory) tile;
            for (int i = 0; i < inv.getSizeInventory(); i++)
                drops.add(inv.getStackInSlot(i));
        }
        ThermalAmbulation.logger.info("DROPS: " + drops);
        //ItemStack item = proxyWorld.getBlockState(pos).getBlock().getItem(proxyWorld, pos, proxyWorld.getBlockState(pos));
        //ThermalAmbulation.logger.info(nbt);
        //item.setTagCompound(nbt);
        //ThermalAmbulation.logger.info(item.serializeNBT());
        //proxyWorld.setBlockToAir(pos);
        proxyWorld.setBlockToAir(pos);
        machines.remove(index);
        markDirty();
        return drops;
    }

    public TileEntity getRemoteTileEntity(int index) {
        if (machines.get(index) == null)
            return null;
        return proxyWorld.getTileEntity(machines.get(index));
    }

    public MachineProxy proxyMachine(IBlockState blockState, NBTTagCompound nbt) {
        for (int i = 0; i < MAX_MACHINES; i++) {
            if (!machines.containsKey(i)) {
                BlockPos pos = new BlockPos(2 * i, 0, 0);
                if (!proxyWorld.setBlockState(pos, blockState, 11))
                    ThermalAmbulation.logger.info("Failed to set state?");//return null;
                NBTTagCompound copy = nbt.copy();
                copy.setByte("Facing", (byte)4);
                copy.setByteArray("SideCache", new byte[]{1, 1, 2, 2, 0, 2});
                copy.setInteger("x", pos.getX());
                copy.setInteger("y", pos.getY());
                copy.setInteger("z", pos.getZ());
                //copy.setInteger("ProcRem", 0);
                //copy.setInteger("ProcMax", 0);
                //copy.setByte("Active", (byte)0);
                ThermalAmbulation.logger.info("NBT: " + copy);
                //proxyWorld.getTileEntity(pos).deserializeNBT(copy);
                //proxyWorld.getTileEntity(pos).readFromNBT(copy);
                IBlockState state = proxyWorld.getBlockState(pos);
                ThermalAmbulation.logger.info("State: " + state.getBlock());
                //state.getBlock().onBlockPlacedBy(proxyWorld, pos, state, null, null);
                //((TileMachineBase) proxyWorld.getTileEntity(pos)).markDirty();
                //((TileMachineBase) proxyWorld.getTileEntity(pos)).markChunkDirty();
                //((TileMachineBase) proxyWorld.getTileEntity(pos)).callBlockUpdate();
                //((TileMachineBase) proxyWorld.getTileEntity(pos)).invalidate();
                //((TileMachineBase) proxyWorld.getTileEntity(pos)).updateAugmentStatus();
                //((TileMachineBase) proxyWorld.getTileEntity(pos)).validate();
                //((TileMachineBase) proxyWorld.getTileEntity(pos)).updateContainingBlockInfo();
                //((TileMachineBase) proxyWorld.getTileEntity(pos)).sendTilePacket(Side.SERVER);
                machines.put(i, pos);
                markDirty();
                MachineProxy machine = new MachineProxy();
                machine.setIndex(i);
                machine.init();
                ThermalAmbulation.logger.info("Created Machine Proxy: " + i);
                return machine;
            }
        }
        return null;
    }
}
