package com.logicmaster63.thermalambulation.entity;

import com.logicmaster63.thermalambulation.ThermalAmbulation;
import com.logicmaster63.thermalambulation.machine.IMachine;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import org.apache.logging.log4j.Level;

import javax.annotation.Nullable;
import java.io.*;

public class EntityWalker extends EntityLiving {
    private IMachine machine;

    public EntityWalker(World worldIn) {
        super(worldIn);
    }

    public void setMachine(IMachine machine) {
        this.machine = machine;
    }

    public IMachine getMachine() {
        return machine;
    }

    @Nullable
    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata) {
        //return super.onInitialSpawn(difficulty, livingdata);
        // send packet
        return new IEntityLivingData() {};
    }

    @Override
    protected void entityInit() {
        ThermalAmbulation.logger.log(Level.INFO, "init" + new BlockPos(posX, posY, posZ));
        super.entityInit();
    }

    @Override
    protected boolean processInteract(EntityPlayer player, EnumHand hand) {
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
        if(machine == null) {
            compound.setTag("machine", new NBTTagCompound());
            compound.setString("type", "");
        }
        else {
            try(ByteArrayOutputStream byteArray = new ByteArrayOutputStream()) {
                ObjectOutputStream outputStream = new ObjectOutputStream(byteArray);
                outputStream.writeObject(machine);
                outputStream.flush();
                compound.setByteArray("machine", byteArray.toByteArray());
            } catch (IOException e) {
                ThermalAmbulation.logger.log(Level.ERROR, e);
                return;
            }
            compound.setString("type", machine.getType());
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        String tag = compound.getString("type");
        if(!tag.equals("")) {
            try(ByteArrayInputStream byteArray = new ByteArrayInputStream(compound.getByteArray("machine"))) {
                ObjectInputStream outputStream = new ObjectInputStream(byteArray);
                machine = (IMachine) outputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                ThermalAmbulation.logger.log(Level.ERROR, e);
            }
        }
        ThermalAmbulation.logger.log(Level.INFO, "readFromNBT: " + machine);
    }
}
