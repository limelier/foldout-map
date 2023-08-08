package dev.limelier.foldoutmap.state

import dev.limelier.foldoutmap.math.Vec2i

object FoldoutMapState {
    private val serverData: MutableMap<String, MutableMap<FoldoutMapKey, FoldoutMap>> = mutableMapOf()

    /**
     * Put the map [tile] into the state, creating empty defaults as needed.
     */
    fun putMapTile(server: String, coords: Vec2i, tile: MapTile) {
        val key = FoldoutMapKey(tile.mapState.scale, tile.mapState.dimension)
        serverData.getOrPut(server) { mutableMapOf() }.getOrPut(key) { mutableMapOf() }[coords] = tile
    }

    operator fun get(server: String): MutableMap<FoldoutMapKey, FoldoutMap> = serverData.getValue(server)
}