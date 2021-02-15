package com.chaos.eki_lib

import com.chaos.eki_lib.station.StationWorldData
import com.chaos.eki_lib.utils.handlers.StationManager
import io.netty.buffer.Unpooled
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.world.World

object EkiLib : ModInitializer {
    const val MODID = "eki_lib"
    const val MODNAME = "Eki Lib"

    val S2C_SERVER_RETURN_STATION_LIST = Identifier(MODID, "s2c_server_ret_sta")

    override fun onInitialize() {
        ServerSidePacketRegistry.INSTANCE.register(
            EkiLibClient.C2S_CLIENT_REQUEST_STATIONS
        ) { context, _ ->
            context.taskQueue.execute {
                val data = PacketByteBuf(Unpooled.buffer())
                data.writeInt(StationManager.stations.size)
                StationManager.stations.forEach { it.toByteBuf(data) }
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(context.player, S2C_SERVER_RETURN_STATION_LIST, data)
            }
        }

        ServerWorldEvents.LOAD.register(ServerWorldEvents.Load { _, world ->
            if (isServerOverWorld(world)) {
                val saver = StationWorldData.forLevel(world)

                StationManager.init(saver.stations)
            }
        })
    }

    private fun isServerOverWorld(world: ServerWorld): Boolean =
        !world.isClient && world.registryKey == World.OVERWORLD
}