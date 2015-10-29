package com.zconami.Core.domain;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import com.zconami.Core.storage.DataKey;
import com.zconami.Core.util.EntityUtils;
import com.zconami.Core.util.NMSUtils;

public abstract class LinkedEntity<BE extends org.bukkit.entity.Entity, ME extends net.minecraft.server.v1_8_R3.Entity>
        extends Entity {

    // ===================================
    // ATTRIBUTES
    // ===================================

    private BE bukkitEntity;

    private ME minecraftEntity;

    public static final String CHUNK = "chunk";
    public static final String CHUNK_WORLD = CHUNK + ".world";
    public static final String CHUNK_X = CHUNK + ".x";
    public static final String CHUNK_Z = CHUNK + ".z";
    private Chunk chunk;

    // ===================================
    // CONSTRUCTORS
    // ===================================

    public LinkedEntity(BE bukkitEntity, DataKey dataKey) {
        super(bukkitEntity.getUniqueId().toString(), dataKey);
        this.bukkitEntity = bukkitEntity;
        this.minecraftEntity = (ME) NMSUtils.getMinecraftEntity(bukkitEntity);
        setChunk(bukkitEntity.getLocation().getChunk());
    }

    public LinkedEntity(LinkedEntityCreateParameters<BE, ME> params) {
        super(params);
        apply(params);
    }

    // ===================================
    // PUBLIC METHODS
    // ===================================

    public BE getBukkitEntity() {
        if (!bukkitEntity.isValid()) {
            final BE foundEntity = (BE) EntityUtils.findBy(getKey(), chunk);
            if (foundEntity != null) {
                this.bukkitEntity = foundEntity;
            }
        }
        return bukkitEntity;
    }

    public ME getNMSEntity() {
        return minecraftEntity;
    }

    public Chunk getChunk() {
        return chunk;
    }

    public void setChunk(Chunk chunk) {
        if (!chunk.equals(this.chunk)) {
            this.chunk = chunk;
            this.setDirty(true);
        }
    }

    public static Chunk getChunkFromData(DataKey dataKey) {
        final UUID worldUUID = UUID.fromString(dataKey.getString(CHUNK_WORLD));
        final int chunkX = dataKey.getInt(CHUNK_X);
        final int chunkZ = dataKey.getInt(CHUNK_Z);
        final World world = Bukkit.getWorld(worldUUID);
        if (world == null) {
            return null;
        }
        return world.getChunkAt(chunkX, chunkZ);
    }

    // ===================================
    // PRIVATE METHODS
    // ===================================

    @SuppressWarnings("unchecked")
    private void apply(LinkedEntityCreateParameters<BE, ME> params) {
        this.bukkitEntity = params.getBukkitEntity();
        this.chunk = bukkitEntity.getLocation().getChunk();
        final ME pMinecraftEntity = params.getMinecraftEntity();
        this.minecraftEntity = pMinecraftEntity == null ? (ME) NMSUtils.getMinecraftEntity(bukkitEntity)
                : pMinecraftEntity;
    }

    // ===================================
    // IMPLEMENTATION OF Entity
    // ===================================

    @Override
    public void readData(DataKey dataKey) {
        this.chunk = getChunkFromData(dataKey);
    }

    @Override
    public void writeData(DataKey dataKey) {
        dataKey.setString(CHUNK_WORLD, chunk.getWorld().getUID().toString());
        dataKey.setInt(CHUNK_X, chunk.getX());
        dataKey.setInt(CHUNK_Z, chunk.getZ());
    }

    // ===================================
    // ABSTRACT METHODS
    // ===================================

    public abstract Class<BE> getBukkitEntityType();

}
