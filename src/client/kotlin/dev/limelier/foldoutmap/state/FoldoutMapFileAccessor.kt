package dev.limelier.foldoutmap.state

import net.fabricmc.loader.api.FabricLoader
import net.minecraft.nbt.NbtCompound
import net.minecraft.nbt.NbtIo
import net.minecraft.registry.RegistryKey
import net.minecraft.server.integrated.IntegratedServer
import net.minecraft.util.WorldSavePath
import net.minecraft.world.World
import java.nio.file.Path
import kotlin.io.path.createParentDirectories
import kotlin.io.path.div
import kotlin.io.path.notExists

internal class FoldoutMapFileAccessor
private constructor(
    private val filePath: Path,
) {
    companion object {
        fun singleplayer(integratedServer: IntegratedServer, dimension: RegistryKey<World>, scale: Byte): FoldoutMapFileAccessor =
            underRoot(
                integratedServer.getSavePath(WorldSavePath.ROOT) / "foldout-map",
                dimension, scale
            )

        fun multiplayer(seedHash: Long, dimension: RegistryKey<World>, scale: Byte): FoldoutMapFileAccessor =
            underRoot(
                FabricLoader.getInstance().gameDir / "foldout-map" / seedHash.toString(),
                dimension, scale
            )

        private fun underRoot(root: Path, dimension: RegistryKey<World>, scale: Byte): FoldoutMapFileAccessor {
            return FoldoutMapFileAccessor(root /
                    dimension.value.toUnderscoreSeparatedString() /
                    "scale_$scale.dat"
            )
        }
    }

    fun get(): FoldoutMap {
        if (filePath.notExists()) {
            return FoldoutMap()
        }

        val nbt = NbtIo.read(filePath.toFile())!!
        return FoldoutMap.fromNbt(nbt)
    }

    fun put(foldoutMap: FoldoutMap) {
        filePath.createParentDirectories()

        val nbt = foldoutMap.writeNbt(NbtCompound())
        NbtIo.write(nbt, filePath.toFile())
    }
}