package dev.limelier.foldoutmap.state

import net.minecraft.item.map.MapState

@JvmRecord
data class MapTile(
    val mapId: Int,
    val mapState: MapState,
)