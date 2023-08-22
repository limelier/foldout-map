package dev.limelier.foldoutmap.state

import dev.limelier.foldoutmap.math.Vec2d
import dev.limelier.foldoutmap.math.Vec2i

internal class FoldoutMap {
    companion object {
        /**
         * Get the pixel position of the top left corner of the tile at [tilePos] relative to its FoldoutMap.
         */
        fun tileToPixel(tilePos: Vec2i): Vec2d = tilePos.toVec2d() * MapTile.PIXEL_SIZE
        /**
         * Get the position of the tile containing the given [pixelPos].
         */
        fun pixelToTile(pixelPos: Vec2d): Vec2i = (pixelPos / MapTile.PIXEL_SIZE).floorToVec2i()
    }

    private val data: MutableMap<Vec2i, MapTile> = mutableMapOf()
    var boundingBox: Rect2i? = null

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

}