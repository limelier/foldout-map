package dev.limelier.foldoutmap.gui

import dev.limelier.foldoutmap.math.Vec2i
import dev.limelier.foldoutmap.math.Vec2d
import dev.limelier.foldoutmap.model.SelectionGoal
import dev.limelier.foldoutmap.state.*
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.client.render.Tessellator
import net.minecraft.client.render.VertexConsumerProvider
import net.minecraft.client.util.math.MatrixStack
import net.minecraft.item.FilledMapItem
import net.minecraft.item.Items
import net.minecraft.item.map.MapIcon
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW

private const val FULL_BRIGHT = 0xF000F0
private const val ZOOM_STEP = 0.2

internal class MapScreen(private val parent: Screen?)
    : Screen(Text.translatable("text.foldout-map.map_screen_title"))
{
    private lateinit var selectedFoldoutMap: FoldoutMap
    private lateinit var foldoutMapTopLeftPos: Vec2d
    private lateinit var foldoutMapOriginPos: Vec2d
    private val buttons: MutableList<ButtonWidget> = mutableListOf()
    private var selectionGoal: SelectionGoal? = null
    private var zoomFactor = 1.0
    private var panOffset = Vec2d(0.0, 0.0)

    override fun init() {
        buttons.add(
            ButtonWidget.builder(Text.translatable("text.foldout-map.button.replace")) {
                selectionGoal = SelectionGoal.REPLACE
            }
                .position(10, 10)
                .build()
        )
        buttons.add(
            ButtonWidget.builder(Text.translatable("text.foldout-map.button.delete")) {
                selectionGoal = SelectionGoal.DELETE
            }
                .position(170, 10)
                .build()
        )
        buttons.forEach { addDrawableChild(it) }

        selectedFoldoutMap = FoldoutMapState.getOrCreate("todo", 0, client!!.world!!.registryKey)
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        foldoutMapTopLeftPos = ((Vec2d(width, height) - selectedFoldoutMap.pixelSize.zoom()) / 2.0).pan()

        foldoutMapOriginPos = foldoutMapTopLeftPos - if (selectedFoldoutMap.isEmpty()) {
            Vec2d.DOWN_RIGHT * MapTile.PIXEL_SIZE / 2.0
        } else {
            selectedFoldoutMap.boundingBox!!.topLeft.toVec2d() * MapTile.PIXEL_SIZE
        }.zoom()

        renderBackground(context!!)
        buttons.forEach { it.visible = selectionGoal == null }

        for ((tilePos, tile) in selectedFoldoutMap) {
            drawMapTile(context.matrices, tile, tilePos)
        }

        if (selectionGoal != null) {
            val mouseTile = pixelToTile(Vec2d(mouseX, mouseY))
            drawReplaceRectangle(context, mouseTile)
        }

        super.render(context, mouseX, mouseY, delta)
    }

    override fun close() {
        if (selectionGoal != null) {
            selectionGoal = null
            return
        }

        client?.setScreen(parent)
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (selectionGoal != null && button == GLFW.GLFW_MOUSE_BUTTON_1) {
            val clickedTile = pixelToTile(Vec2d(mouseX, mouseY))
            return when (selectionGoal) {
                SelectionGoal.REPLACE -> trySetTile(clickedTile)
                SelectionGoal.DELETE -> {
                    selectedFoldoutMap.remove(clickedTile)
                    selectionGoal = null
                    true
                }
                else -> { throw NotImplementedError("unknown selection goal: $selectionGoal") }
            }
        }

        return super.mouseClicked(mouseX, mouseY, button)
    }

    override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, deltaX: Double, deltaY: Double): Boolean {
        if (button != GLFW.GLFW_MOUSE_BUTTON_1) { return false }
        panOffset += Vec2d(deltaX, deltaY)
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)
    }

    override fun mouseScrolled(mouseX: Double, mouseY: Double, amount: Double): Boolean {
        if (amount > 0 || zoomFactor - ZOOM_STEP > ZOOM_STEP) {
            zoomFactor += amount * ZOOM_STEP
        }
        return super.mouseScrolled(mouseX, mouseY, amount)
    }

    /**
     * Try setting the tile at [tilePos] to a map in the player's hand, returning `true` if successful.
     *
     * Try the main hand, then the offhand.
     * Return `false` if there are no map items in the player's hands.
     */
    private fun trySetTile(tilePos: Vec2i): Boolean {
        val inv = client!!.player!!.inventory
        val mapItem = inv.mainHandStack?.takeIf { it.item == Items.FILLED_MAP }
            ?: inv.offHand[0].takeIf { it.item == Items.FILLED_MAP }
            ?: return false

        selectedFoldoutMap[tilePos] = MapTile(
            FilledMapItem.getMapId(mapItem)!!,
            FilledMapItem.getMapState(mapItem, client!!.world)!!
        )

        selectionGoal = null
        return true
    }

    private fun drawMapTile(matrices: MatrixStack, tile: MapTile, tilePos: Vec2i) {
        val vcp = VertexConsumerProvider.immediate(Tessellator.getInstance().buffer)
        val screenPos = tileToPixel(tilePos)

        matrices.push()
        matrices.translate(screenPos.x, screenPos.y, 1.0)
        matrices.scale(zoomFactor.toFloat(), zoomFactor.toFloat(), -1.0f)

        tile.mapState.icons.removeAll { it.type == MapIcon.Type.PLAYER_OFF_MAP }

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

    private fun drawReplaceRectangle(context: DrawContext, tilePos: Vec2i) {
        val screenPos = tileToPixel(tilePos)
        context.fill(
            screenPos.x.toInt(),
            screenPos.y.toInt(),
            (screenPos.x + MapTile.PIXEL_SIZE.zoom()).toInt(),
            (screenPos.y + MapTile.PIXEL_SIZE.zoom()).toInt(),
            2,
            0x22FFFFFF
        )
    }

    private fun tileToPixel(tilePos: Vec2i): Vec2d = foldoutMapOriginPos + FoldoutMap.tileToPixel(tilePos).zoom()

    private fun pixelToTile(pixelPos: Vec2d): Vec2i = FoldoutMap.pixelToTile((pixelPos - foldoutMapOriginPos).unzoom())

    private fun Double.zoom() = this * zoomFactor
    private fun Vec2d.zoom() = this * zoomFactor
    private fun Vec2d.unzoom() = this / zoomFactor
    private fun Vec2d.pan() = this + panOffset
}