package com.chaos.eki_lib.utils.handlers

import com.chaos.eki_lib.station.StationWorldData
import com.chaos.eki_lib.station.data.Station
import net.minecraft.server.world.ServerWorld
import net.minecraft.util.math.BlockPos

internal object StationManager {
    val stations: MutableList<Station> = mutableListOf()
        get() = field.toMutableList()

    fun init(stations: MutableList<Station>) =
        this.stations.addAll(stations)

    fun reload(stations: MutableList<Station>) {
        clear()
        init(stations)
    }

    private fun clear() =
        stations.clear()

    fun has(blockPos: BlockPos): Boolean =
        stations.any { it.pos == blockPos }

    fun addStation(station: Station): Boolean {
        if (has(station.pos))
            return false

        stations.add(station)
        return true
    }

    fun replaceStation(station: Station): Boolean {
        for (i in stations.indices) if (stations[i].pos == station.pos) {
            stations[i] = station
            return true
        }
        return false
    }

    fun removeStation(pos: BlockPos): Boolean {
        for (i in stations.indices) if (stations[i].pos == pos) {
            stations.removeAt(i)
            return true
        }
        return false
    }

    fun markDirty(world: ServerWorld) {
        val saver = StationWorldData.forLevel(world)
        saver.stations = stations
        saver.markDirty()
    }
}