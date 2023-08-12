package dev.limelier.foldoutmap.math

import kotlin.math.floor

@JvmRecord
internal data class Vec2d(val x: Double, val y: Double) {
    companion object {
        val ZERO = Vec2d(0, 0)
        val DOWN_RIGHT = Vec2d(1, 1)
    }

    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())
    operator fun plus(other: Vec2d) = Vec2d(this.x + other.x, this.y + other.y)
    operator fun minus(other: Vec2d) = Vec2d(this.x - other.x, this.y - other.y)
    operator fun times(scalar: Double) = Vec2d(this.x * scalar, this.y * scalar)
    operator fun div(scalar: Double) = Vec2d(this.x / scalar, this.y / scalar)
    fun floorToVec2i() = Vec2i(floor(this.x).toInt(), floor(this.y).toInt())

    override fun toString() = "($x, $y)"
}
