package dev.limelier.foldoutmap.state

import net.minecraft.registry.RegistryKey
import net.minecraft.world.World

internal object FoldoutMapState {
    private val serverData: MutableMap<String, MutableMap<FoldoutMapKey, FoldoutMap>> = mutableMapOf()
    /**
     * Get an existing foldout map or create a new one and return it.
     */
    fun getOrCreate(server: String, scale: Byte, dimension: RegistryKey<World>): FoldoutMap {
        val key = FoldoutMapKey(scale, dimension)
        return serverData.getOrPut(server) { mutableMapOf() }
            .getOrPut(key) { FoldoutMap() }
    }

}