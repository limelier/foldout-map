package dev.limelier.foldoutmap.state

import dev.limelier.foldoutmap.math.Vec2d
import dev.limelier.foldoutmap.math.Vec2i
import kotlin.math.max
import kotlin.math.min

typealias FoldoutMap = MutableMap<Vec2i, MapTile>

/**
 * Get the minimum tile size `(width, height)` required for all the tiles in the foldout map.
 */
val FoldoutMap.tileSize: Vec2i get() {
    var minX = 0
    var minY = 0
    var maxX = 0
    var maxY = 0

    for ((x, y) in keys) {
        minX = min(minX, x)
        maxX = max(maxX, x)
        minY = min(minY, y)
        maxY = max(maxY, y)
    }

    return Vec2i(maxX - minX + 1, maxY - minY + 1)
}

val FoldoutMap.pixelSize: Vec2d get() = tileSize.toVec2d() * MapTile.PIXEL_SIZE

/**
 * Get the pixel position of the top left corner of the tile at [tilePos] relative to its FoldoutMap.
 */
fun tilePosToRelativePixelPos(tilePos: Vec2i): Vec2d = tilePos.toVec2d() * MapTile.PIXEL_SIZE

fun relativePixelPosToTilePos(pixelPos: Vec2d): Vec2i = (pixelPos / MapTile.PIXEL_SIZE).floorToVec2i()