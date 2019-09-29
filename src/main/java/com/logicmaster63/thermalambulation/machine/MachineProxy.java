package com.logicmaster63.thermalambulation.machine;

import clayborn.universalremote.config.UniversalRemoteConfiguration;
import clayborn.universalremote.hooks.entity.HookedEntityPlayerMP;
import clayborn.universalremote.hooks.events.PlayerRemoteGuiDataManagerServer;
import clayborn.universalremote.hooks.events.PlayerWorldSyncServer;
import clayborn.universalremote.hooks.world.WorldServerProxy;
import clayborn.universalremote.items.ItemRegistry;
import clayborn.universalremote.items.ItemUniversalRemote;
import clayborn.universalremote.util.TextFormatter;
import clayborn.universalremote.util.Util;
import codechicken.lib.model.bakery.ModelBakery;
import cofh.api.item.IUpgradeItem;
import cofh.api.tileentity.IUpgradeable;
import cofh.core.block.BlockCoreTile;
import cofh.core.block.TileNameable;
import cofh.core.util.helpers.ChatHelper;
import cofh.thermalexpansion.ThermalExpansion;
import com.logicmaster63.thermalambulation.RemoteMachineRegistry;
import com.logicmaster63.thermalambulation.ThermalAmbulation;
import com.logicmaster63.thermalambulation.item.ItemUpgrade;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.UnpooledByteBufAllocator;
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
import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import static clayborn.universalremote.items.ItemUniversalRemote.ActivateBlock;

public class MachineProxy implements IMachine {
    private int index;
    private transient TileEntity tileEntity;
    private transient World proxiedWorld;

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
    public void destroy() {

    }

    @Override
    public String getType() {
        return "Proxy";
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack held = Util.playerAndHandToItemStack(player, hand);

        if (held.getItem() instanceof IUpgradeItem && tileEntity instanceof IUpgradeable) {
            if (!((IUpgradeable) tileEntity).canUpgrade(held))
                return false;
            if (((IUpgradeable) tileEntity).installUpgrade(held)) {
                if (!player.capabilities.isCreativeMode) {
                    held.shrink(1);
                }
                player.world.playSound(null, player.getPosition(), SoundEvents.BLOCK_ANVIL_USE, SoundCategory.PLAYERS, 0.6F, 1.0F);
                ChatHelper.sendIndexedChatMessageToPlayer(player, new TextComponentTranslation("chat.thermalfoundation.upgrade.install.success"));
            } else {
                ChatHelper.sendIndexedChatMessageToPlayer(player, new TextComponentTranslation("chat.thermalfoundation.upgrade.install.failure"));
            }
            return true;
        }

        WorldServer worldIn = (WorldServer) proxiedWorld;

        if (!worldIn.isRemote) {

            //ItemStack stack = Util.playerAndHandToItemStack(player, handIn);
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("energy", 96671);
            ItemUniversalRemote.ItemUniversalRemoteNBTParser myNBT = new ItemUniversalRemote.ItemUniversalRemoteNBTParser(tag);
            myNBT.configureNBT(player, tileEntity.getPos(), proxiedWorld, EnumHand.MAIN_HAND, EnumFacing.NORTH, 0.5f, 0.5f, 0.5f);
            tag = myNBT.getTag();
            tag.setFloat("remote.player.position.X", tileEntity.getPos().getX());
            tag.setFloat("remote.player.position.Y", tileEntity.getPos().getY() + 1);
            tag.setFloat("remote.player.position.Z", tileEntity.getPos().getZ());
            tag.setInteger("remote.dimension.id", 63);
            tag.setString("remote.dimension.name", "Thermalambulation");
            myNBT = new ItemUniversalRemote.ItemUniversalRemoteNBTParser(tag);
            // do we have bound block data?
            if (!myNBT.validateNBT()) {
                // let the player know he needs data!

                //player.sendMessage(TextFormatter.translateAndStyle("universalremote.strings.notbounderror", TextFormatting.DARK_RED));

                // default behavior
                //return super.onItemRightClick(worldIn, player, handIn);
            }

            // transform as needed (this covers people who upgrade)
            /*if (stack.getMetadata() != 1)
            {
                stack = new ItemStack(ItemRegistry.Items().UniveralRemote, 1, 1);
                stack.setTagCompound(myNBT.getTag());
                Util.setPlayerItemStackInHand(stack, player, handIn);
            }

            int energyCost = computeEnergyCost(player, myNBT.getDimensionId(), myNBT.getBlockPosition());
        */
            WorldServer world = DimensionManager.getWorld(myNBT.getDimensionId());

            // this should be null only if the target dimension is not loaded
            if (world != null) {

                // The block needs to be in a loaded chunk
                if (world.isBlockLoaded(myNBT.getBlockPosition(), false)) {

                    IBlockState state = world.getBlockState(myNBT.getBlockPosition());

                    if (!UniversalRemoteConfiguration.isBlockBlacklisted(state.getBlock())) {

                        String test = state.getBlock().getClass().getName();

                        if (test.equals(myNBT.getBlockClass())) {

                            // Make sure we have enough energy and if so extract it
                            if (true /*internalExtractEnergy(stack, energyCost)*/) {

                                // ensure player is in world sync
                                PlayerWorldSyncServer.INSTANCE.resyncIfNeeded(player);

                                // container backup
                                Container oldContainer = player.openContainer;

                                // world backup
                                WorldServer oldWorld = (WorldServer) player.world;

                                // setup extra field need to setup client for remote modded gui activation!
                                if (!test.startsWith("net.minecraft")) {
                                    // prepare for remote activation!
                                    PlayerRemoteGuiDataManagerServer.INSTANCE.PrepareForRemoteActivation(world, (EntityPlayerMP) player, myNBT.getBlockPosition(), new Vec3d(myNBT.getPlayerX(), myNBT.getPlayerY(), myNBT.getPlayerZ()));

                                    // Send the pre-activation trigger and config packet!
                                    PlayerRemoteGuiDataManagerServer.INSTANCE.SendPreparePacket(world, player, myNBT.getTag());

                                    // make sure player.GetEntityWorld returns the TE's world
                                    if (oldWorld != world) {

                                        if (false/*!Util.doesStringStartWithAnyInArray(m_worldProxyDuringActivationExceptionsList, state.getClass().getName())*/) {
                                            player.world = new WorldServerProxy(oldWorld, world, test);
                                        } else {
                                            player.world = world;
                                        }

                                    }

                                }

                                EntityPlayer fakePlayer = ActivateBlock(player, state, myNBT, world);

                                // did we get re-routed to another block?
                                // then we need to try again!
                                while (PlayerRemoteGuiDataManagerServer.INSTANCE.IsRetryNeeded(player)) {
                                    ThermalAmbulation.logger.info("Retrying OpenGui..");

                                    // note: count of tries kept in RemoteGuiPlayerData
                                    PlayerRemoteGuiDataManagerServer.INSTANCE.Retry(player);
                                }

                                // player opened a container, time to make a wrapper if needed
                                if (player.openContainer != oldContainer) {

                                    if (player instanceof HookedEntityPlayerMP) {
                                        ((HookedEntityPlayerMP) player).SetRemoteFilter(
                                                test, world,
                                                myNBT.getBlockPosition(),
                                                myNBT.getPlayerX(), myNBT.getPlayerY(), myNBT.getPlayerZ(),
                                                myNBT.getPlayerPitch(), myNBT.getPlayerYaw());
                                    } else {
                                        // uh ho...
                                        ThermalAmbulation.logger.error("Unable to set player's remote filter because player was not instance of RemoteEnabledEntityPlayerMP!");
                                    }

//									// don't need this for vanilla
//									if (!test.startsWith("net.minecraft") && oldWorld != world)
//									{

                                    PlayerWorldSyncServer.INSTANCE.setPlayerData(player, player.openContainer);

//									}

                                } else {

                                    // it didn't open anything, clear the player data
                                    PlayerRemoteGuiDataManagerServer.INSTANCE.CancelRemoteActivation(player);

                                    // put the world back since the player didn't open a container!
                                    player.world = oldWorld;

                                }

                            } else {

                                // uh ho not enough power!
                                player.sendMessage(TextFormatter.translateAndStyle("universalremote.strings.notenoughpower", TextFormatting.DARK_RED));

                            }

                        } else {

                            // bad binding...
                            player.sendMessage(TextFormatter.translateAndStyle("universalremote.strings.blockchanged", TextFormatting.DARK_RED));

                        }

                    } else {

                        // blacklisted!
                        player.sendMessage(TextFormatter.translateAndStyle("universalremote.strings.blockblacklist", TextFormatting.DARK_RED));

                    }

                } else {
                    // chunk isn't loaded!

                    // let the player know the chunk isn't loaded
                    player.sendMessage(TextFormatter.translateAndStyle("universalremote.strings.boundnotloaded", TextFormatting.DARK_RED));

                }


            } else {

                // let the player know the chunk (or dimension in this case) isn't loaded
                player.sendMessage(TextFormatter.translateAndStyle("universalremote.strings.boundnotloaded", TextFormatting.DARK_RED));

            }

        }


        //if (!proxiedWorld.isRemote)
        //    ((TileNameable) tileEntity).openGui(player);
        // player.openGui(ThermalExpansion.instance, 0, proxiedWorld, tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ());
        //tileEntity.getBlockType().onBlockActivated(proxiedWorld, tileEntity.getPos(), tileEntity.getBlockType().getDefaultState(), player, hand, EnumFacing.NORTH, tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ());
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
        GlStateManager.translate(x - 0.4, y, z - 0.4);
        GlStateManager.scale(0.8, 0.8, 0.8);

        RenderHelper.disableStandardItemLighting();
        mc.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
        BufferBuilder worldRenderer = tessellator.getBuffer();
        worldRenderer.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);
        //worldRenderer.setTranslation(-.5, -.5, -.5);
        //GlStateManager.rotate(yaw - 90, 0, 1, 0);
        mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModel(world, ibakedmodel, state, new BlockPos(0, 0, 0), tessellator.getBuffer(), true);

        worldRenderer.setTranslation(0, 0, 0);
        tessellator.draw();
        //GlStateManager.scale(2, 2, 2);
        RenderHelper.enableStandardItemLighting();
        GlStateManager.disableBlend();
        GlStateManager.enableCull();
        GlStateManager.popMatrix();

    }
}
