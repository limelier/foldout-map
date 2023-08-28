package dev.limelier.foldoutmap.state

import net.minecraft.item.map.MapState
import net.minecraft.nbt.NbtCompound
import net.minecraft.world.PersistentState

internal data class MapTile(
    val mapId: Int,
    val mapState: MapState,
) : PersistentState() {
    companion object {
        const val PIXEL_SIZE = 128.0

        fun fromNbt(nbt: NbtCompound): MapTile {
            val state = MapState.fromNbt(nbt.getCompound("mapState"))
            return MapTile(nbt.getInt("mapid"), state)
        }
    }

    override fun writeNbt(nbt: NbtCompound?): NbtCompound {
        val mapStateNbt = NbtCompound()
        mapState.writeNbt(mapStateNbt)

        nbt!!.putInt("mapId", mapId)
        nbt.put("mapState", mapStateNbt)
        return nbt
    }
}