package com.logicmaster63.thermalambulation.networking;

import com.logicmaster63.thermalambulation.entity.EntityWalker;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class EntityMachineMessage implements IMessage {
    private int index, id;

    public EntityMachineMessage() {

    }

    public EntityMachineMessage(int id, int index) {
        this.index = index;
        this.id = id;
    }

    @Override
    public void fromBytes(ByteBuf bytes) {
        index = bytes.getInt(0);
    }

    @Override
    public void toBytes(ByteBuf buffer) {
        buffer.setInt(0, index);
    }

    public static class MachineMessageHandler implements IMessageHandler<EntityMachineMessage, IMessage> {
        @Override
        public IMessage onMessage(EntityMachineMessage message, MessageContext ctx) {
            return null;
        }
    }
}
