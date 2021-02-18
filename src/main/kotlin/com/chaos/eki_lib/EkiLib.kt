package com.chaos.eki_lib

import com.chaos.eki_lib.objects.items.StationTunerItem
import com.chaos.eki_lib.station.StationWorldData
import com.chaos.eki_lib.station.data.OpCode
import com.chaos.eki_lib.station.data.Station
import com.chaos.eki_lib.utils.TagFacts
import com.chaos.eki_lib.utils.handlers.StationManager
import io.netty.buffer.Unpooled
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry
import net.fabricmc.fabric.api.server.PlayerStream
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.Identifier
import net.minecraft.util.registry.Registry
import net.minecraft.world.World

object EkiLib : ModInitializer {
    const val MODID = "eki_lib"
    const val MODNAME = "Eki Lib"

    val STATION_TUNER = StationTunerItem()

    val S2C_SERVER_RETURN_STATION_LIST = Identifier(MODID, "s2c_server_ret_sta")

    override fun onInitialize() {
        registerItems()

        ServerSidePacketRegistry.INSTANCE.register(
            EkiLibClient.C2S_CLIENT_REQUEST_STATIONS
        ) { context, _ ->
            context.taskQueue.execute {
                val data = PacketByteBuf(Unpooled.buffer())
                val stations = StationManager.getStationList()

                data.writeBoolean(true)
                data.writeInt(stations.size)
                stations.forEach { it.toByteBuf(data) }
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(
                    context.player,
                    S2C_SERVER_RETURN_STATION_LIST,
                    data
                )
            }
        }

        ServerSidePacketRegistry.INSTANCE.register(
            EkiLibClient.C2S_CLIENT_UPDATE_STATIONS
        ) { context, data ->
            val opCode = data.readEnumConstant(OpCode::class.java)
            val station = Station.fromByteBuf(data)

            context.taskQueue.execute {
                when (opCode) {
                    OpCode.ADD -> StationManager.addStation(station)
                    OpCode.REPLACE -> StationManager.replaceStation(station)
                    OpCode.DELETE -> StationManager.removeStation(station.pos)
                    else -> println("Unknown Operation to Station Manager: $opCode")
                }

                StationManager.markDirty(context.player.server?.getWorld(World.OVERWORLD)!!)

                val returnData = PacketByteBuf(Unpooled.buffer())
                val stations = StationManager.getStationList()

                data.writeBoolean(false)
                data.writeInt(stations.size)
                stations.forEach { it.toByteBuf(data) }

                PlayerStream.all(context.player.server).forEach {
                    ServerSidePacketRegistry.INSTANCE.sendToPlayer(
                        it,
                        S2C_SERVER_RETURN_STATION_LIST,
                        returnData
                    )
                }
            }
        }

        ServerSidePacketRegistry.INSTANCE.register(
            EkiLibClient.C2S_CLIENT_BIND_TUNER
        ) { context, data ->
            val tag = CompoundTag()
            tag.putIntArray(
                TagFacts.Station.POS,
                data.readIntArray()
            )

            context.taskQueue.execute {
                context.player.mainHandStack.tag = tag
            }
        }

        ServerWorldEvents.LOAD.register(ServerWorldEvents.Load { _, world ->
            if (isServerOverWorld(world)) {
                val saver = StationWorldData.forLevel(world)

                StationManager.init(saver.stations)
            }
        })
    }

    private fun registerItems() {
        Registry.register(Registry.ITEM, Identifier(MODID, "station_tuner"), STATION_TUNER)
    }

    private fun isServerOverWorld(world: ServerWorld): Boolean =
        !world.isClient && world.registryKey == World.OVERWORLD
}