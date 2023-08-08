package dev.limelier.foldoutmap.math

@JvmInline
value class Vec2i(private val data: Long) {
    public val x get() = data.shr(32).toInt()
    public val y get() = data.toInt()
    public constructor(x: Int, y: Int) : this(x.toLong().shl(32).or(y.toLong()))

    public fun toVec2d() = Vec2d(x.toDouble(), y.toDouble())
    public operator fun component1(): Int = x
    public operator fun component2(): Int = y
}
