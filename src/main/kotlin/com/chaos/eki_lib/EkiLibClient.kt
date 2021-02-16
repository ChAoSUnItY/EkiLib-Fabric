package com.chaos.eki_lib

import com.chaos.eki_lib.gui.screen.StationOverviewScreen
import com.chaos.eki_lib.station.data.Station
import com.chaos.eki_lib.utils.handlers.StationManager
import io.netty.buffer.Unpooled
import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry
import net.minecraft.client.MinecraftClient
import net.minecraft.client.options.KeyBinding
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import org.lwjgl.glfw.GLFW
import kotlin.math.min

object EkiLibClient : ClientModInitializer {
    private val openStationTabKeyBind = KeyBindingHelper.registerKeyBinding(
        KeyBinding(
            "eki_lib.open_station_tab",
            GLFW.GLFW_KEY_K,
            "Eki Lib"
        )
    )

    val C2S_CLIENT_REQUEST_STATIONS = Identifier(EkiLib.MODID, "c2s_client_req_sta")
    val C2S_CLIENT_UPDATE_STATIONS = Identifier(EkiLib.MODID, "c2s_client_upd_sta")

    override fun onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickEvents.EndTick {
            if (openStationTabKeyBind.isPressed) {
                ClientSidePacketRegistry.INSTANCE.sendToServer(
                    C2S_CLIENT_REQUEST_STATIONS,
                    PacketByteBuf(Unpooled.buffer())
                )
            }
        })

        ClientSidePacketRegistry.INSTANCE.register(EkiLib.S2C_SERVER_RETURN_STATION_LIST) { context, data ->
            val size = data.readInt()
            val stations = mutableListOf<Station>()

            for (i in 0 until size)
                stations += Station.fromByteBuf(data)

            StationManager.reload(stations)

            context.taskQueue.execute {
                val client = MinecraftClient.getInstance()
                val player = context.player
                client.openScreen(StationOverviewScreen(null, player.world.registryKey.value, player))
            }
        }
    }
}