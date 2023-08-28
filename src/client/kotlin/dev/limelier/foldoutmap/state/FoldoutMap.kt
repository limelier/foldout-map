package dev.limelier.foldoutmap.state

import dev.limelier.foldoutmap.math.Vec2d
import dev.limelier.foldoutmap.math.Vec2i
import net.minecraft.nbt.NbtCompound
import net.minecraft.world.PersistentState

internal class FoldoutMap : PersistentState() {
    companion object {
        /**
         * Get the pixel position of the top left corner of the tile at [tilePos] relative to its FoldoutMap.
         */
        fun tileToPixel(tilePos: Vec2i): Vec2d = tilePos.toVec2d() * MapTile.PIXEL_SIZE

        /**
         * Get the position of the tile containing the given [pixelPos].
         */
        fun pixelToTile(pixelPos: Vec2d): Vec2i = (pixelPos / MapTile.PIXEL_SIZE).floorToVec2i()

        fun fromNbt(nbt: NbtCompound): FoldoutMap {
            val foldoutMap = FoldoutMap()

            val dataNbt = nbt.getCompound("data")
            dataNbt.keys.forEach { k ->
                val keyComponents = k.split(",").map { it.toInt() }
                val key = Vec2i(keyComponents[0], keyComponents[1])
                val tile = MapTile.fromNbt(dataNbt.getCompound(k))
                foldoutMap.data[key] = tile
            }

            val boundingBox = Rect2i(
                Vec2i(nbt.getInt("topleftX"), nbt.getInt("topLeftY")),
                Vec2i(nbt.getInt("bottomRightX"), nbt.getInt("bottomRightY"))
            )
            foldoutMap.boundingBox = boundingBox

            return foldoutMap
        }
    }

    private val data: MutableMap<Vec2i, MapTile> = mutableMapOf()
    var boundingBox: Rect2i? = null
        private set

    /**
     * The size in texture pixels of the rectangle bounding all map tiles.
     */
    val pixelSize: Vec2d get() = boundingBox?.let { it.size.toVec2d() * MapTile.PIXEL_SIZE } ?: Vec2d.ZERO

    operator fun get(tilePos: Vec2i): MapTile? = data[tilePos]

    operator fun set(tilePos: Vec2i, tile: MapTile) {
        boundingBox = boundingBox?.expandedToContain(tilePos) ?: Rect2i.ofTile(tilePos)
        data[tilePos] = tile
    }

    /**
     * Remove a tile from that position if it exists.
     */
    fun remove(tilePos: Vec2i) {
        val removed = data.remove(tilePos)
        if (removed != null && boundingBox?.isOnBorder(tilePos) == true) {
            recalculateBounds()
        }
    }

    /**
     * Translate all the tiles so that their mean position is [Vec2i.ZERO].
     */
    fun recenter() {
        if (data.isEmpty()) return
        val meanPos = data.keys.reduce { acc, it -> acc + it } / data.keys.size
        if (meanPos == Vec2i.ZERO) return

        val delta = -meanPos
        val offsetData = data.map { (k, v) -> k + delta to v}
        data.clear()
        data += offsetData

        recalculateBounds()
    }

    fun isEmpty() = data.isEmpty()

    operator fun iterator(): Iterator<MutableMap.MutableEntry<Vec2i, MapTile>> = data.iterator()

    private fun recalculateBounds() {
        boundingBox = null
        for (tilePos in data.keys) {
            boundingBox = boundingBox?.expandedToContain(tilePos) ?: Rect2i.ofTile(tilePos)
        }
    }

    override fun writeNbt(nbt: NbtCompound?): NbtCompound {
        val dataNbt = NbtCompound()
        data.forEach { (k, v) ->
            val keyString = "${k.x},${k.y}"
            val valueNbt = NbtCompound()
            v.writeNbt(valueNbt)
            dataNbt.put(keyString, valueNbt)
        }
        nbt!!.put("data", dataNbt)

        boundingBox?.let {
            val bbNbt = NbtCompound()
            bbNbt.putInt("topLeftX", it.topLeft.x)
            bbNbt.putInt("topLeftY", it.topLeft.y)
            bbNbt.putInt("bottomRightX", it.bottomRight.x)
            bbNbt.putInt("bottomRightY", it.bottomRight.y)
            nbt.put("boundingBox", bbNbt)
        }

        return nbt
    }

}