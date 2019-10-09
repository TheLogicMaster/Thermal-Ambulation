package com.logicmaster63.thermalambulation.entity;

import clayborn.universalremote.util.TextFormatter;
import cofh.redstoneflux.impl.EnergyStorage;
import com.logicmaster63.thermalambulation.ThermalAmbulation;
import com.logicmaster63.thermalambulation.item.upgrade.ConsumptionUpgrade;
import com.logicmaster63.thermalambulation.item.upgrade.ItemUpgrade;
import com.logicmaster63.thermalambulation.item.upgrade.ProductionUpgrade;
import com.logicmaster63.thermalambulation.machine.IMachine;
import com.logicmaster63.thermalambulation.machine.AmbulationUtils;
import com.logicmaster63.thermalambulation.machine.NullMachine;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializer;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EntityWalker extends EntityMachine {
    private IMachine machine;
    private boolean initialized;
    private List<ItemStack> upgradeItems;
    // Todo: Switch implementations to use ItemUpgrade list to avoid constant casting from ItemStack list
    private List<ItemUpgrade> upgrades;
    private List<ProductionUpgrade> productionUpgrades;
    private List<ConsumptionUpgrade> consumptionUpgrades;
    private int entityCapacity;
    private EnergyStorage energyStorage;
    private int maxTransfer;

    private static final DataSerializer<IMachine> MACHINE_SERIALIZER = new DataSerializer<IMachine>() {
        public void write(PacketBuffer buf, IMachine value) {
            buf.writeCompoundTag(value.toNBT());
        }

        public IMachine read(PacketBuffer buf) throws IOException {
            ThermalAmbulation.logger.info("Machine packet");
            try {
                return AmbulationUtils.machineFromNBT(Objects.requireNonNull(buf.readCompoundTag()));
            } catch (NullPointerException e) {
                ThermalAmbulation.logger.error("Failed to create machine from packet", e);
                return new NullMachine();
            }
        }

        public DataParameter<IMachine> createKey(int id) {
            return new DataParameter<>(id, this);
        }

        public IMachine copyValue(IMachine value) {
            return value;
        }
    };
    private static final DataParameter<IMachine> MACHINE = EntityDataManager.createKey(EntityWalker.class, MACHINE_SERIALIZER);

    private static final DataSerializer<List<ItemStack>> UPGRADES_SERIALIZER = new DataSerializer<List<ItemStack>>() {
        public void write(PacketBuffer buf, List<ItemStack> value) {
            buf.writeCompoundTag(writeUpgradesToNBT(new NBTTagCompound(), value));
        }

        public List<ItemStack> read(PacketBuffer buf) throws IOException {
            ThermalAmbulation.logger.info("Upgrade packet");
            return readUpgradesFromNBT(buf.readCompoundTag());
        }

        public DataParameter<List<ItemStack>> createKey(int id) {
            return new DataParameter<>(id, this);
        }

        public List<ItemStack> copyValue(List<ItemStack> value) {
            return value;
        }
    };
    private static final DataParameter<List<ItemStack>> UPGRADES = EntityDataManager.createKey(EntityWalker.class, UPGRADES_SERIALIZER);

    static {
        DataSerializers.registerSerializer(MACHINE_SERIALIZER);
        DataSerializers.registerSerializer(UPGRADES_SERIALIZER);
    }

    public EntityWalker(World worldIn) {
        super(worldIn);
        setSize(1, 1);
        upgradeItems = new ArrayList<>();
        upgrades = new ArrayList<>();
        energyStorage = new EnergyStorage(0);
        consumptionUpgrades = new ArrayList<>();
        productionUpgrades = new ArrayList<>();
        maxTransfer = 500;
        entityCapacity = 1000;
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

    public List<ItemStack> getUpgradeItems() {
        if (world.isRemote)
            return dataManager.get(UPGRADES);
        else
            return upgradeItems;
    }

    public boolean hasUpgradeOfType(ItemUpgrade.UpgradeType type) {
        for (ItemStack upgrade : upgradeItems)
            if (type == ((ItemUpgrade) upgrade.getItem()).getUpgradeType())
                return true;
        return false;
    }

    @Override
    protected void initEntityAI() {
        super.initEntityAI();
        aiSit = new EntityAISit(this);
        //this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, this.aiSit);
        this.tasks.addTask(6, new EntityAIFollowOwner(this, 0.6D, 10.0F, 2.0F));
        //this.tasks.addTask(7, new EntityAIMate(this, 1.0D));
        //this.tasks.addTask(8, new EntityAIWanderAvoidWater(this, 1.0D));
        //this.tasks.addTask(9, new EntityAIBeg(this, 8.0F));
        //this.tasks.addTask(10, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        //this.tasks.addTask(10, new EntityAILookIdle(this));
    }

    private void refresh() {
        int capacity = entityCapacity;
        productionUpgrades.clear();
        consumptionUpgrades.clear();
        for (ItemStack upgrade : upgradeItems) {
            if (upgrade.getItem() instanceof ProductionUpgrade) {
                capacity += ((ProductionUpgrade) upgrade.getItem()).getCapacity();
                productionUpgrades.add((ProductionUpgrade) upgrade.getItem());
            }
            if (upgrade.getItem() instanceof ConsumptionUpgrade)
                consumptionUpgrades.add((ConsumptionUpgrade) upgrade.getItem());
        }
        energyStorage.setCapacity(capacity);
        energyStorage.setMaxTransfer(maxTransfer);
        // Todo: add ability to configure energy priority
        consumptionUpgrades.sort((u0, u1) -> u1.getUpgradeType().getPriority() - u0.getUpgradeType().getPriority());
        dataManager.set(UPGRADES, upgradeItems);
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

        if (!initialized && getMachine() != null) {
            getMachine().init();
            initialized = true;
        }

        if (world.isRemote)
            return;

        int produced = 0;
        for (ProductionUpgrade upgrade : productionUpgrades) {
            produced += upgrade.getProduction(this);
            // Prevent unnecessary calculations for solar panels and such
            if (produced >= maxTransfer)
                break;
        }
        produced = Math.min(produced, maxTransfer);
        int received = receiveEnergy(produced);

        // Possibly make this more efficient
        if (produced < maxTransfer)
            energyStorage.extractEnergy(receiveEnergy(energyStorage.extractEnergy(maxTransfer - produced, true), true), false);
    }

    public int receiveEnergy(int energy) {
        return receiveEnergy(energy, false);
    }

    public int receiveEnergy(int energy, boolean stored) {
        int max = Math.min(energy, maxTransfer);
        int received = 0;
        received += machine.receiveEnergy(energy, false);
        for (ConsumptionUpgrade upgrade : consumptionUpgrades) {
            if (received >= max)
                break;
            received += upgrade.getEnergyStorage().receiveEnergy(max - received, false);
        }
        if (!stored)
            received += energyStorage.receiveEnergy(max - received, false);
        return received;
    }

    @Override
    protected void entityInit() {
        //ThermalAmbulation.logger.log(Level.INFO, "init" + new BlockPos(posX, posY, posZ));
        super.entityInit();
        dataManager.register(MACHINE, new NullMachine());
        dataManager.register(UPGRADES, new ArrayList<>());
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (super.processInteract(player, hand))
            return true;

        ItemStack held = player.getHeldItem(hand);

        if (hand == EnumHand.OFF_HAND && held.getItem().getUnlocalizedName().equals("tile.air"))
            return false;

        if (!player.world.isRemote && player.isSneaking()) {
            if (held.getItem() instanceof ItemUpgrade) {
                switch (((ItemUpgrade) held.getItem()).getUpgradeType()) {
                    case SOLAR:
                        if (!hasUpgradeOfType(ItemUpgrade.UpgradeType.SOLAR)) {
                            setSize(1, 1.3f);
                            upgradeItems.add(held.splitStack(1));
                            //held.shrink(1);
                            player.sendMessage(TextFormatter.translateAndStyle("thermalambulation.strings.machineupgradedsolar", TextFormatting.BLUE));
                            refresh();
                            return true;
                        }
                }
            }
        }

        if (player.isSneaking()) {
            setSitting(!isSitting());
            return true;
        }

        if (machine == null)
            return false;
        return machine.processInteract(player, hand);
    }

    @Override
    public void onDeath(DamageSource cause) {
        super.onDeath(cause);
        if (machine != null)
            for (ItemStack stack : machine.destroy())
                world.spawnEntity(new EntityItem(world, posX, posY, posZ, stack));
        for (ItemStack stack : upgradeItems)
            world.spawnEntity(new EntityItem(world, posX, posY, posZ, stack));
    }

    private static NBTTagCompound writeUpgradesToNBT(NBTTagCompound nbt, List<ItemStack> upgrades) {
        NBTTagList upgradeTags = new NBTTagList();
        for (ItemStack upgrade : upgrades)
            upgradeTags.appendTag(upgrade.serializeNBT());
        nbt.setTag("Upgrades", upgradeTags);
        return nbt;
    }

    private static List<ItemStack> readUpgradesFromNBT(NBTTagCompound nbt) {
        List<ItemStack> upgrades = new ArrayList<>();
        for (NBTBase base : nbt.getTagList("Upgrades", 10))
            upgrades.add(new ItemStack((NBTTagCompound) base));
        return upgrades;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        if (machine != null)
            compound.setTag("Machine", machine.toNBT());
        writeUpgradesToNBT(compound, upgradeItems);
        energyStorage.writeToNBT(compound);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setMachine(AmbulationUtils.machineFromNBT(compound.getCompoundTag("Machine")));
        upgradeItems = readUpgradesFromNBT(compound);
        energyStorage.readFromNBT(compound);
        refresh();
    }
}
