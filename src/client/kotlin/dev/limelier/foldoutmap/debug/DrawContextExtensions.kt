// debug functions will not always be used
@file:Suppress("unused")

package dev.limelier.foldoutmap.debug

import dev.limelier.foldoutmap.math.Vec2d
import net.minecraft.client.gui.DrawContext

internal fun DrawContext.drawDebugDot(dot: Vec2d, color: Int = 0xFFFF0000.toInt()) {
    fill(
        (dot.x - 1).toInt(),
        (dot.y - 1).toInt(),
        (dot.x + 1).toInt(),
        (dot.y + 1).toInt(),
        99,
        color
    )
}

internal fun DrawContext.drawDebugRect(topLeft: Vec2d, bottomRight: Vec2d, color: Int = 0xFFFF0000.toInt()) {
    val (x1, y1) = topLeft.floorToVec2i()
    val (x2, y2) = bottomRight.floorToVec2i()

    drawHorizontalLine(x1, x2, y1, color)
    drawHorizontalLine(x1, x2, y2, color)
    drawVerticalLine(x1, y1, y2, color)
    drawVerticalLine(x2, y1, y2, color)
}