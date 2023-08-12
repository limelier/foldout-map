package dev.limelier.foldoutmap.state

import dev.limelier.foldoutmap.math.Vec2i
import kotlin.math.max
import kotlin.math.min

/**
 * A [Vec2i] rectangle from [topLeft] inclusive to [bottomRight] inclusive.
 */
internal data class Rect2i (
    val topLeft: Vec2i,
    val bottomRight: Vec2i,
) {
    init {
        assert(topLeft.x > bottomRight.x && topLeft.y > bottomRight.y) {
            "$topLeft cannot be below or to the right of $bottomRight"
        }
    }

    companion object {
        fun ofTile(tilePos: Vec2i) = Rect2i(tilePos, tilePos)
    }

    val size: Vec2i get() = bottomRight - topLeft + Vec2i.DOWN_RIGHT

    fun isOnBorder(point: Vec2i): Boolean =
        point.x == topLeft.x ||
        point.y == topLeft.y ||
        point.x == bottomRight.x - 1 ||
        point.y == bottomRight.y - 1

    fun expandedToContain(point: Vec2i) = Rect2i(
        Vec2i(min(topLeft.x, point.x), min(topLeft.y, point.y)),
        Vec2i(max(bottomRight.x, point.x), max(bottomRight.y, point.y))
    )
}