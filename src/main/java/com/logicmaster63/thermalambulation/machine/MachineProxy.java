package com.logicmaster63.thermalambulation.machine;

import clayborn.universalremote.config.UniversalRemoteConfiguration;
import clayborn.universalremote.hooks.entity.HookedEntityPlayerMP;
import clayborn.universalremote.hooks.events.PlayerRemoteGuiDataManagerServer;
import clayborn.universalremote.hooks.events.PlayerWorldSyncServer;
import clayborn.universalremote.hooks.world.WorldServerProxy;
import clayborn.universalremote.items.ItemUniversalRemote;
import clayborn.universalremote.util.TextFormatter;
import clayborn.universalremote.util.Util;
import codechicken.lib.model.bakery.ModelBakery;
import cofh.api.item.IUpgradeItem;
import cofh.api.tileentity.IUpgradeable;
import cofh.core.block.BlockCoreTile;
import cofh.core.util.helpers.ChatHelper;
import cofh.redstoneflux.api.IEnergyProvider;
import cofh.redstoneflux.api.IEnergyReceiver;
import com.logicmaster63.thermalambulation.RemoteMachineRegistry;
import com.logicmaster63.thermalambulation.ThermalAmbulation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class MachineProxy implements IMachine {
    private int index;
    private TileEntity tileEntity;
    private World proxiedWorld;

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public void init() {
        tileEntity = RemoteMachineRegistry.get().getRemoteTileEntity(index);
        proxiedWorld = DimensionManager.getWorld(63);
        ThermalAmbulation.logger.info("Initialized Machine: " + index);
    }

    @Override
    public ArrayList<ItemStack> destroy() {
        return RemoteMachineRegistry.get().retrieveMachine(index);
    }

    @Override
    public String getType() {
        return "Proxy";
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack held = Util.playerAndHandToItemStack(player, hand);
        if (!player.world.isRemote) {
            if (held.getItem() instanceof IUpgradeItem && tileEntity instanceof IUpgradeable && ((IUpgradeable) tileEntity).canUpgrade(held)) {
                if (((IUpgradeable) tileEntity).installUpgrade(held)) {
                    if (!player.capabilities.isCreativeMode) {
                        held.shrink(1);
                    }
                    player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_ANVIL_USE, SoundCategory.PLAYERS, 0.6F, 1.0F);
                    // Todo: Customized chat messages
                    ChatHelper.sendIndexedChatMessageToPlayer(player, new TextComponentTranslation("chat.thermalfoundation.upgrade.install.success"));
                } else {
                    ChatHelper.sendIndexedChatMessageToPlayer(player, new TextComponentTranslation("chat.thermalfoundation.upgrade.install.failure"));
                }
                return true;
            }

            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("energy", 96671);
            ItemUniversalRemote.ItemUniversalRemoteNBTParser myNBT = new ItemUniversalRemote.ItemUniversalRemoteNBTParser(tag);
            myNBT.configureNBT(player, tileEntity.getPos(), DimensionManager.getWorld(63), EnumHand.MAIN_HAND, EnumFacing.NORTH, 0.5f, 0.5f, 0.5f);
            tag = myNBT.getTag();
            tag.setDouble("remote.player.position.X", tileEntity.getPos().getX());
            tag.setDouble("remote.player.position.Y", tileEntity.getPos().getY() + 1);
            tag.setDouble("remote.player.position.Z", tileEntity.getPos().getZ());
            tag.setInteger("remote.dimension.id", 63);
            tag.setString("remote.dimension.name", "Thermalambulation");
            myNBT = new ItemUniversalRemote.ItemUniversalRemoteNBTParser(tag);
            if (!myNBT.validateNBT())
                ThermalAmbulation.logger.warn("Something went wrong with nbt for remote machine: {}", myNBT.getTag());
            WorldServer world = DimensionManager.getWorld(myNBT.getDimensionId());
            if (world != null) {
                if (world.isBlockLoaded(myNBT.getBlockPosition(), false)) {
                    IBlockState state = world.getBlockState(myNBT.getBlockPosition());
                    if (!UniversalRemoteConfiguration.isBlockBlacklisted(state.getBlock())) {
                        String test = state.getBlock().getClass().getName();
                        if (test.equals(myNBT.getBlockClass())) {
                            if (true /*this.internalExtractEnergy(stack, energyCost)*/) {
                                PlayerWorldSyncServer.INSTANCE.resyncIfNeeded(player);
                                Container oldContainer = player.openContainer;
                                WorldServer oldWorld = (WorldServer)player.world;
                                if (!test.startsWith("net.minecraft")) {
                                    PlayerRemoteGuiDataManagerServer.INSTANCE.PrepareForRemoteActivation(world, (EntityPlayerMP)player, myNBT.getBlockPosition(), new Vec3d(myNBT.getPlayerX(), myNBT.getPlayerY(), myNBT.getPlayerZ()));
                                    PlayerRemoteGuiDataManagerServer.INSTANCE.SendPreparePacket(world, player, myNBT.getTag());
                                    if (oldWorld != world) {
                                        if (true/*!Util.doesStringStartWithAnyInArray(m_worldProxyDuringActivationExceptionsList, state.getClass().getName())*/) {
                                            player.world = new WorldServerProxy(oldWorld, world, test);
                                        } else {
                                            player.world = world;
                                        }
                                    }
                                }

                                ItemUniversalRemote.ActivateBlock(player, state, myNBT, world);

                                while(PlayerRemoteGuiDataManagerServer.INSTANCE.IsRetryNeeded(player)) {
                                    Util.logger.info("Retrying OpenGui..", new Object[0]);
                                    PlayerRemoteGuiDataManagerServer.INSTANCE.Retry(player);
                                }

                                if (player.openContainer != oldContainer) {
                                    if (player instanceof HookedEntityPlayerMP) {
                                        ((HookedEntityPlayerMP)player).SetRemoteFilter(test, world, myNBT.getBlockPosition(), myNBT.getPlayerX(), myNBT.getPlayerY(), myNBT.getPlayerZ(), myNBT.getPlayerPitch(), myNBT.getPlayerYaw());
                                    } else {
                                        Util.logger.error("Unable to set player's remote filter because player was not instance of RemoteEnabledEntityPlayerMP!", new Object[0]);
                                    }

                                    PlayerWorldSyncServer.INSTANCE.setPlayerData(player, player.openContainer);
                                } else {
                                    PlayerRemoteGuiDataManagerServer.INSTANCE.CancelRemoteActivation(player);
                                    player.world = oldWorld;
                                }
                            } else {
                                player.sendMessage(TextFormatter.translateAndStyle("universalremote.strings.notenoughpower", TextFormatting.DARK_RED));
                            }
                        } else {
                            player.sendMessage(TextFormatter.translateAndStyle("universalremote.strings.blockchanged", TextFormatting.DARK_RED));
                        }
                    } else {
                        player.sendMessage(TextFormatter.translateAndStyle("universalremote.strings.blockblacklist", TextFormatting.DARK_RED));
                    }
                } else {
                    player.sendMessage(TextFormatter.translateAndStyle("universalremote.strings.boundnotloaded", TextFormatting.DARK_RED));
                }
            } else {
                player.sendMessage(TextFormatter.translateAndStyle("universalremote.strings.boundnotloaded", TextFormatting.DARK_RED));
            }
        }

        return true;
    }

    @Override
    public void render(double x, double y, double z, float yaw, float partialTicks) {
        Minecraft mc = Minecraft.getMinecraft();
        World world = mc.world;
        IBlockState state;
        IBakedModel ibakedmodel;
        if (tileEntity != null && proxiedWorld.getBlockState(tileEntity.getPos()).getBlock() instanceof BlockCoreTile) {
            state = proxiedWorld.getBlockState(tileEntity.getPos());
            ibakedmodel = ModelBakery.generateModel((IExtendedBlockState) ((BlockCoreTile) state.getBlock()).getExtendedState(state, proxiedWorld, tileEntity.getPos()));
        } else {
            state = Block.getBlockFromName("minecraft:dirt").getDefaultState();
            ibakedmodel = mc.getBlockRendererDispatcher().getModelForState(state);
        }
        GlStateManager.pushMatrix();
        Tessellator tessellator = Tessellator.getInstance();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.translate(x - 0.4, y + 0.1, z - 0.4);
        GlStateManager.scale(0.8, 0.8, 0.8);
        GlStateManager.translate(.5, .5, .5);
        GlStateManager.rotate(-yaw + 90, 0, 1, 0);
        GlStateManager.translate(-.5, -.5, -.5);

        RenderHelper.disableStandardItemLighting();
        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        BufferBuilder worldRenderer = tessellator.getBuffer();
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

        mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModel(world, ibakedmodel, state, new BlockPos(0, 0, 0), tessellator.getBuffer(), true);
        worldRenderer.setTranslation(0, 0, 0);
        tessellator.draw();
        //GlStateManager.scale(2, 2, 2);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();

    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        index = nbt.getInteger("Index");
    }

    @Override
    public NBTTagCompound toNBT() {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setInteger("Index", index);
        nbt.setString("Type", MachineType.PROXY.name());
        return nbt;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (tileEntity instanceof IEnergyReceiver)
            return ((IEnergyReceiver) tileEntity).receiveEnergy(EnumFacing.UP, maxReceive, simulate);
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (tileEntity instanceof IEnergyProvider)
            return ((IEnergyProvider) tileEntity).extractEnergy(EnumFacing.UP, maxExtract, simulate);
        return 0;
    }

    @Override
    public int getEnergyStored() {
        if (tileEntity instanceof IEnergyReceiver)
            return ((IEnergyReceiver) tileEntity).getEnergyStored(EnumFacing.UP);
        return 0;
    }

    @Override
    public int getMaxEnergyStored() {
        if (tileEntity instanceof IEnergyReceiver)
            return ((IEnergyReceiver) tileEntity).getMaxEnergyStored(EnumFacing.UP);
        return 0;
    }
}
