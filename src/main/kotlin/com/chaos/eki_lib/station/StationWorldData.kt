package com.chaos.eki_lib.station

import com.chaos.eki_lib.EkiLib
import com.chaos.eki_lib.station.data.Station
import com.chaos.eki_lib.utils.TagFacts
import net.fabricmc.fabric.api.util.NbtType
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.server.world.ServerWorld
import net.minecraft.world.PersistentState
import java.util.function.Supplier

class StationWorldData(name: String? = null) : PersistentState(name ?: EkiLib.MODID), Supplier<StationWorldData> {
    companion object {
        fun forLevel(world: ServerWorld): StationWorldData {
            val manager = world.persistentStateManager
            val supplier = StationWorldData()

            return manager.getOrCreate(supplier, EkiLib.MODID)
        }
    }

    var stations: MutableList<Station> = mutableListOf()

    override fun fromTag(tag: CompoundTag?) {
        val list = tag?.getList(TagFacts.StationWorldData.LIST_NAME, NbtType.COMPOUND)
        list?.let {
            stations.addAll(it.map { tag -> Station.fromTag(tag as CompoundTag) })
        }
    }

    override fun toTag(tag: CompoundTag?): CompoundTag? {
        val list = ListTag()
        list.addAll(stations.map(Station::toTag))
        tag?.put(TagFacts.StationWorldData.LIST_NAME, list)

        return tag
    }

    override fun get(): StationWorldData = this


}