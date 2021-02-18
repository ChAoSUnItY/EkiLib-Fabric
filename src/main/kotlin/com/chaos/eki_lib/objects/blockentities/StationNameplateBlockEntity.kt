package com.chaos.eki_lib.objects.blockentities

import com.chaos.eki_lib.station.data.Station
import com.chaos.eki_lib.utils.TagFacts
import com.chaos.eki_lib.utils.extensions.UtilBlockPos
import com.chaos.eki_lib.utils.extensions.asArray
import com.chaos.eki_lib.utils.handlers.RegistryHandler
import com.chaos.eki_lib.utils.handlers.StationManager
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable
import net.minecraft.block.BlockState
import net.minecraft.block.entity.BlockEntity
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.math.BlockPos

class StationNameplateBlockEntity :
    BlockEntity(RegistryHandler.STATION_NAMEPLATE_BLOCK_ENTITY),
    BlockEntityClientSerializable,
    IStationBoundable {
    var boundStationPosition: BlockPos? = null

    override fun toTag(tag: CompoundTag?): CompoundTag? {
        super.toTag(tag)

        if (boundStationPosition != null) tag?.putIntArray(TagFacts.Station.POS, boundStationPosition?.asArray())

        return tag
    }

    override fun fromTag(state: BlockState?, tag: CompoundTag?) {
        super.fromTag(state, tag)

        boundStationPosition = if (tag?.contains(TagFacts.Station.POS) == true) {
            UtilBlockPos.fromArray(tag.getIntArray(TagFacts.Station.POS))
        } else {
            null
        }
    }

    override fun isTargetExist(): Boolean =
        if (boundStationPosition != null) StationManager.has(boundStationPosition) else false

    override fun getBoundStation(): Station? =
        if (boundStationPosition != null) StationManager.getStation(boundStationPosition, world?.registryKey?.value) else null

    override fun toClientTag(tag: CompoundTag?): CompoundTag? =
        toTag(tag)

    override fun fromClientTag(tag: CompoundTag?) =
        fromTag(world?.getBlockState(pos), tag)
}