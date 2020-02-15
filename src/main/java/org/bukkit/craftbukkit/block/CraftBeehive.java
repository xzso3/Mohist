package org.bukkit.craftbukkit.block;

import com.google.common.base.Preconditions;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.BeehiveTileEntity;
import net.minecraft.util.math.BlockPos;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Beehive;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.entity.CraftBee;
import org.bukkit.entity.Bee;

import java.util.ArrayList;
import java.util.List;

public class CraftBeehive extends CraftBlockEntityState<BeehiveTileEntity> implements Beehive {

    public CraftBeehive(final Block block) {
        super(block, BeehiveTileEntity.class);
    }

    public CraftBeehive(final Material material, final BeehiveTileEntity te) {
        super(material, te);
    }

    @Override
    public Location getFlower() {
        BlockPos flower = getSnapshot().field_226959_b_;
        return (flower == null) ? null : new Location(getWorld(), flower.getX(), flower.getY(), flower.getZ());
    }

    @Override
    public void setFlower(Location location) {
        Preconditions.checkArgument(location == null || this.getWorld().equals(location.getWorld()), "Flower must be in same world");
        getSnapshot().field_226959_b_ = (location == null) ? null : new BlockPos(location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    @Override
    public boolean isFull() {
        return getSnapshot().func_226970_h_();
    }

    @Override
    public boolean isSedated() {
        return isPlaced() && getSnapshot().func_226972_k_(); // PAIL rename isSedated
    }

    @Override
    public int getEntityCount() {
        return getSnapshot().func_226971_j_(); // PAIL rename beeCount
    }

    @Override
    public int getMaxEntities() {
        return getSnapshot().maxBees;
    }

    @Override
    public void setMaxEntities(int max) {
        Preconditions.checkArgument(max > 0, "Max bees must be more than 0");

        getSnapshot().maxBees = max;
    }

    @Override
    public List<Bee> releaseEntities() {
        List<Bee> bees = new ArrayList<>();

        if (isPlaced()) {
            BeehiveTileEntity beehive = ((BeehiveTileEntity) this.getTileEntityFromWorld());
            for (Entity bee : beehive.releaseBees(this.getHandle(), BeehiveTileEntity.State.BEE_RELEASED, true)) {
                bees.add((Bee) bee.getBukkitEntity());
            }
        }

        return bees;
    }

    @Override
    public void addEntity(Bee entity) {
        Preconditions.checkArgument(entity != null, "Entity must not be null");

        getSnapshot().func_226961_a_(((CraftBee) entity).getHandle(), false); // PAIL rename addBee
    }
}
