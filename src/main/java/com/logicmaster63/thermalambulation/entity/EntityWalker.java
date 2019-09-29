package com.logicmaster63.thermalambulation.entity;

import clayborn.universalremote.util.Util;
import com.logicmaster63.thermalambulation.ThermalAmbulation;
import com.logicmaster63.thermalambulation.item.ItemUpgrade;
import com.logicmaster63.thermalambulation.machine.IMachine;
import com.logicmaster63.thermalambulation.machine.MachineUtils;
import com.logicmaster63.thermalambulation.machine.NullMachine;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class EntityWalker extends EntityAnimal {
    private IMachine machine;
    private boolean initialized;
    private Set<ItemUpgrade.UpgradeType> upgrades = new HashSet<>();

    public static final DataSerializer<IMachine> MACHINE_SERIALIZER = new DataSerializer<IMachine>() {
        public void write(PacketBuffer buf, IMachine value) {
            try {
                buf.writeByteArray(MachineUtils.toBytes(value));
            } catch (IOException e) {
                ThermalAmbulation.logger.error("Failed to write IMachine data to packetbuffer: " + e);
            }
        }
        public IMachine read(PacketBuffer buf) throws IOException {
            return MachineUtils.fromBytes(buf.readByteArray());
        }
        public DataParameter<IMachine> createKey(int id) {
            return new DataParameter<>(id, this);
        }
        public IMachine copyValue(IMachine value) {
            return value;
        }
    };
    private static final DataParameter<IMachine> MACHINE = EntityDataManager.createKey(EntityAgeable.class, MACHINE_SERIALIZER);
    static {
        DataSerializers.registerSerializer(MACHINE_SERIALIZER);
    }

    public EntityWalker(World worldIn) {
        super(worldIn);
        setSize(1, 1);
    }

    public void setMachine(IMachine machine) {
        this.machine = machine;
        dataManager.set(MACHINE, machine);
    }

    public IMachine getMachine() {
        if (world.isRemote)
            return dataManager.get(MACHINE);
        else
            return machine;
    }

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        return null;
    }

    @Nullable
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        return super.onInitialSpawn(difficulty, livingdata);

        // send packet
        //return new IEntityLivingData() {};
    }

    @Override
    public void onEntityUpdate() {
        super.onEntityUpdate();
        if (!initialized) {
            getMachine().init();
            initialized = true;
        }
            ;//ThermalAmbulation.logger.log(Level.INFO, machine.getType());
    }

    @Override
    protected void entityInit() {
        ThermalAmbulation.logger.log(Level.INFO, "init" + new BlockPos(posX, posY, posZ));
        super.entityInit();
        dataManager.register(MACHINE, new NullMachine());
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack held = Util.playerAndHandToItemStack(player, hand);

        if (held.getItem() instanceof ItemUpgrade) {
            switch (((ItemUpgrade) held.getItem()).getType()) {
                case SOLAR:
                    if (!upgrades.contains(ItemUpgrade.UpgradeType.SOLAR)) {
                        setSize(1, 1.3f);
                        held.shrink(1);
                        upgrades.add(ItemUpgrade.UpgradeType.SOLAR);
                        return true;
                    }
                    return false;
            }
        }
        if(machine == null)
            return false;
        return machine.processInteract(player, hand);
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if(machine != null)
            machine.destroy();
        ThermalAmbulation.logger.log(Level.INFO, "death");

    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        ThermalAmbulation.logger.log(Level.INFO, "writeToNBT: " + machine);
        if(machine != null) {
            try {
                compound.setByteArray("machine", MachineUtils.toBytes(machine));
            } catch (IOException e) {
                ThermalAmbulation.logger.log(Level.ERROR, e);
            }
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        try {
            setMachine(MachineUtils.fromBytes(compound.getByteArray("machine")));
            //machine.init();
        } catch (IOException e) {
            ThermalAmbulation.logger.log(Level.ERROR, e);
        }
        ThermalAmbulation.logger.log(Level.INFO, "readFromNBT: " + machine);
    }
}
