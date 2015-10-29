package com.zconami.Core.repository;

import org.bukkit.Chunk;

import com.zconami.Core.ZconamiPlugin;
import com.zconami.Core.domain.LinkedEntity;
import com.zconami.Core.storage.DataKey;
import com.zconami.Core.util.EntityUtils;

public abstract class LinkedRepository<BE extends org.bukkit.entity.Entity, ME extends net.minecraft.server.v1_8_R3.Entity, E extends LinkedEntity<BE, ME>>
        extends Repository<E> {

    // ===================================
    // CONSTRUCTORS
    // ===================================

    protected LinkedRepository(ZconamiPlugin plugin) {
        super(plugin);
    }

    // ===================================
    // PUBLIC METHODS
    // ===================================

    public E find(BE bukkitEntity) {
        return super.find(bukkitEntity.getUniqueId().toString());
    }

    public E recreate(DataKey entityData) {
        final String key = entityData.getPath();
        final Chunk chunkFromData = LinkedEntity.getChunkFromData(entityData);
        final BE bukkitEntity = (BE) EntityUtils.findBy(key, chunkFromData);
        if (bukkitEntity == null) {
            getPlugin().getLogger().info("Failed to find bukkitEntity for [" + getEntityName() + ":" + key
                    + "], can't recreate entity as a result!");
            return null;
        }
        return recreate(bukkitEntity, entityData);
    }

    // ===================================
    // ABSTRACT METHODS
    // ===================================

    protected abstract Class<BE> getBukkitEntityType();

    protected abstract E recreate(BE bukkitEntity, DataKey entityData);

}
