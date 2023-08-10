package dev.limelier.foldoutmap.state

import dev.limelier.foldoutmap.math.Vec2i
import net.minecraft.registry.RegistryKey
import net.minecraft.world.World

object FoldoutMapState {
    private val serverData: MutableMap<String, MutableMap<FoldoutMapKey, FoldoutMap>> = mutableMapOf()

    /**
     * Put the map [tile] into the state, creating empty defaults as needed.
     */
    fun putMapTile(server: String, coords: Vec2i, tile: MapTile) {
        val key = FoldoutMapKey(tile.mapState.scale, tile.mapState.dimension)
        serverData.getOrPut(server) { mutableMapOf() }.getOrPut(key) { mutableMapOf() }[coords] = tile
    }

    /**
     * Get an existing foldout map or create a new one and return it.
     */
    fun getOrCreate(server: String, scale: Byte, dimension: RegistryKey<World>): FoldoutMap {
        val key = FoldoutMapKey(scale, dimension)
        return serverData.getOrPut(server) { mutableMapOf() }
            .getOrPut(key) { mutableMapOf() }
    }

}