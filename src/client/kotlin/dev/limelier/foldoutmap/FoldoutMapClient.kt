package dev.limelier.foldoutmap

import dev.limelier.foldoutmap.gui.MapScreen
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.minecraft.client.option.KeyBinding
import net.minecraft.client.util.InputUtil
import org.lwjgl.glfw.GLFW

object FoldoutMapClient : ClientModInitializer {
	private lateinit var displayMapGUIBinding: KeyBinding

	override fun onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		displayMapGUIBinding = KeyBindingHelper.registerKeyBinding(KeyBinding(
			"key.foldout-map.open_map",
			InputUtil.Type.KEYSYM,
			GLFW.GLFW_KEY_M,
			"category.foldout-map.map"
		))

		ClientTickEvents.END_CLIENT_TICK.register { client ->
			while (displayMapGUIBinding.wasPressed()) {
				if (client.world == null && client.player == null) return@register

				client.setScreen(MapScreen(client.currentScreen))
			}
		}
	}
}