package dev.limelier.foldoutmap.state

import net.minecraft.item.map.MapState

@JvmRecord
data class MapTile(
    val mapId: Int,
    val mapState: MapState,
) {
    companion object {
        const val PIXEL_SIZE = 128.0
    }
}