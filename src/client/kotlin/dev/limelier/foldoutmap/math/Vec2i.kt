package dev.limelier.foldoutmap.math

/**
 * A vector of two ints. By convention, X+ is right, Y+ is down.
 */
@JvmRecord
internal data class Vec2i(val x: Int, val y: Int) {
    companion object {
        val ZERO = Vec2i(0, 0)
        val DOWN_RIGHT = Vec2i(1, 1)
    }
    fun toVec2d() = Vec2d(x.toDouble(), y.toDouble())
    operator fun plus(other: Vec2i) = Vec2i(x + other.x, y + other.y)
    operator fun minus(other: Vec2i) = Vec2i(x - other.x, y - other.y)
    operator fun div(scalar: Int) = Vec2i(x / scalar, y / scalar)
    operator fun unaryMinus() = Vec2i(-x, -y)

    override fun toString() = "($x, $y)"
}
