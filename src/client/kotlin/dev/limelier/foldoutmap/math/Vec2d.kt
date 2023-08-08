package dev.limelier.foldoutmap.math

@JvmRecord
data class Vec2d(val x: Double, val y: Double) {
    constructor(x: Int, y: Int) : this(x.toDouble(), y.toDouble())
    operator fun plus(other: Vec2d) = Vec2d(this.x + other.x, this.y + other.y)
    operator fun minus(other: Vec2d) = Vec2d(this.x - other.x, this.y - other.y)
    operator fun times(scalar: Double) = Vec2d(this.x * scalar, this.y * scalar)
}
