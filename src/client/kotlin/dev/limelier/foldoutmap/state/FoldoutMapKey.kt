package dev.limelier.foldoutmap.state

import net.minecraft.registry.RegistryKey
import net.minecraft.world.World

@JvmRecord
internal data class FoldoutMapKey(
    val scale: Byte,
    val dimension: RegistryKey<World>
)
