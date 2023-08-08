package dev.limelier.foldoutmap.gui

import dev.limelier.foldoutmap.math.Vec2i
import dev.limelier.foldoutmap.math.Vec2d
import dev.limelier.foldoutmap.state.*
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.FilledMapItem
import net.minecraft.item.Items
import net.minecraft.text.Text

const val TILE_SIZE = 128.0
val FULL_BRIGHT = "F000F0".toInt(16)

class MapScreen(private val parent: Screen?)
    : Screen(Text.translatable("text.foldout-map.map_screen_title"))
{
    override fun init() {
        client!!.player!!.inventory.main
            .filter { it?.item == Items.FILLED_MAP }
            .forEachIndexed { i, itemStack ->
                val tile = MapTile(
                    FilledMapItem.getMapId(itemStack)!!,
                    FilledMapItem.getMapState(itemStack, client!!.world)!!
                )
                FoldoutMapState.putMapTile("todo", Vec2i(i, 0), tile)
            }
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context!!)
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, 20, 0xFFFFFF)

        val foldoutMap = FoldoutMapState["todo"]
            .getValue(FoldoutMapKey(0 /* todo */, client!!.world!!.registryKey))

        for ((coords, tile) in foldoutMap) {
            drawMapTile(
                context.matrices,
                tile,
                screenPosition(foldoutMap, coords),
            )
        }
        super.render(context, mouseX, mouseY, delta)
    }

    override fun close() {
        client?.setScreen(parent)
    }

    private fun screenPosition(foldoutMap: FoldoutMap, coords: Vec2i): Vec2d {
        val center = Vec2d(width / 2.0, height / 2.0)
        val totalDimensions = foldoutMap.tileSize.toVec2d() * TILE_SIZE
        val topLeft = center - totalDimensions * 0.5
        return topLeft + coords.toVec2d() * TILE_SIZE
    }

    private fun drawMapTile(
        matrices: MatrixStack,
        tile: MapTile,
        screenPos: Vec2d,
    ) {
        val vcp = VertexConsumerProvider.immediate(Tessellator.getInstance().buffer)

        matrices.push()
        matrices.translate(screenPos.x, screenPos.y, 1.0)
        matrices.scale(1.0f, 1.0f, 1.0f)

        client!!.gameRenderer.mapRenderer.draw(
            matrices,
            vcp,
            tile.mapId,
            tile.mapState,
            false,
            FULL_BRIGHT
        )
        vcp.draw()
        matrices.pop()
    }
}