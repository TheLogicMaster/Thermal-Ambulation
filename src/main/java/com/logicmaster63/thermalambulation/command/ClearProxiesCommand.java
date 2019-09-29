package com.logicmaster63.thermalambulation.command;

import clayborn.universalremote.util.TextFormatter;
import com.google.common.collect.Lists;
import com.logicmaster63.thermalambulation.CustomTeleporter;
import com.logicmaster63.thermalambulation.RemoteMachineRegistry;
import com.logicmaster63.thermalambulation.ThermalAmbulation;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.DimensionManager;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

public class ClearProxiesCommand extends CommandBase {

    public ClearProxiesCommand(){
        aliases = Lists.newArrayList(ThermalAmbulation.MOD_ID, "TP", "tp");
    }

    private final List<String> aliases;

    @Override
    @Nonnull
    public String getName() {
        return "machineproxy";
    }

    @Override
    @Nonnull
    public String getUsage(@Nonnull ICommandSender sender) {
        return "machineproxy <clear>";
    }

    @Override
    @Nonnull
    public List<String> getAliases() {
        return aliases;
    }

    @Override
    public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] args) throws CommandException {
        if (args.length < 1) {
            return;
        }
        String s = args[0];
        if (s.equals("clear")) {
            RemoteMachineRegistry.get().clear();
            sender.sendMessage(TextFormatter.translateAndStyle("thermalambulation.strings.clearproxies", TextFormatting.GREEN));
        }
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    @Nonnull
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return Collections.emptyList();
    }
}