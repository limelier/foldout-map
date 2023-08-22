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
import net.minecraft.text.Text
import org.lwjgl.glfw.GLFW

private const val FULL_BRIGHT = 0xF000F0

internal class MapScreen(private val parent: Screen?)
    : Screen(Text.translatable("text.foldout-map.map_screen_title"))
{
    private lateinit var selectedFoldoutMap: FoldoutMap
    private lateinit var foldoutMapTopLeftPos: Vec2d
    private lateinit var foldoutMapOriginPos: Vec2d
    private lateinit var replaceButton: ButtonWidget
    private var selectionGoal: SelectionGoal? = null

    override fun init() {
        replaceButton = ButtonWidget.builder(Text.translatable("text.foldout-map.button.replace")) {
            selectionGoal = SelectionGoal.REPLACE
        }
            .position(10, 10)
            .build()
        addDrawableChild(replaceButton)

        selectedFoldoutMap = FoldoutMapState.getOrCreate("todo", 0, client!!.world!!.registryKey)
        foldoutMapTopLeftPos = (Vec2d(width, height) - selectedFoldoutMap.pixelSize) / 2.0

        foldoutMapOriginPos = foldoutMapTopLeftPos - if (selectedFoldoutMap.isEmpty()) {
            Vec2d.DOWN_RIGHT * MapTile.PIXEL_SIZE / 2.0
        } else {
            selectedFoldoutMap.boundingBox!!.topLeft.toVec2d() * MapTile.PIXEL_SIZE
        }
    }

    override fun render(context: DrawContext?, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(context!!)
        replaceButton.visible = selectionGoal == null

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
        if (selectionGoal == SelectionGoal.REPLACE && button == GLFW.GLFW_MOUSE_BUTTON_1) {
            return trySetTile(pixelToTile(Vec2d(mouseX, mouseY)))
        }

        return super.mouseClicked(mouseX, mouseY, button)
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

    private fun drawReplaceRectangle(context: DrawContext, tilePos: Vec2i) {
        val screenPos = tileToPixel(tilePos)
        context.fill(
            screenPos.x.toInt(),
            screenPos.y.toInt(),
            (screenPos.x + MapTile.PIXEL_SIZE).toInt(),
            (screenPos.y + MapTile.PIXEL_SIZE).toInt(),
            2,
            0x22FFFFFF
        )
    }

    private fun tileToPixel(tilePos: Vec2i): Vec2d = foldoutMapOriginPos + FoldoutMap.tileToPixel(tilePos)

    private fun pixelToTile(pixelPos: Vec2d): Vec2i = FoldoutMap.pixelToTile(pixelPos - foldoutMapOriginPos)
}