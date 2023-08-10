package dev.limelier.foldoutmap.math

@JvmRecord
data class Vec2i(val x: Int, val y: Int) {
    fun toVec2d() = Vec2d(x.toDouble(), y.toDouble())
}
