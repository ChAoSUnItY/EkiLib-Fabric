package com.chaos.eki_lib.station.data

import com.chaos.eki_lib.utils.TagFacts
import com.chaos.eki_lib.utils.extensions.UtilBlockPos
import com.chaos.eki_lib.utils.extensions.asArray
import com.chaos.eki_lib.utils.extensions.format
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.PacketByteBuf
import net.minecraft.util.Identifier
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

data class Station(var name: String, val pos: BlockPos, var level: StationLevel, val dimensionID: Identifier) {
    companion object {
        fun fromTag(tag: CompoundTag): Station =
            Station(
                tag.getString(TagFacts.Station.NAME),
                UtilBlockPos.fromArray(tag.getIntArray(TagFacts.Station.POS)),
                StationLevel.valueOf(tag.getString(TagFacts.Station.LEVEL)),
                Identifier.tryParse(tag.getString(TagFacts.Station.DIMENSION)) ?: World.OVERWORLD.value
            )

        fun fromByteBuf(byteBuf: PacketByteBuf): Station =
            Station(
                byteBuf.readString(),
                byteBuf.readBlockPos(),
                byteBuf.readEnumConstant(StationLevel::class.java),
                byteBuf.readIdentifier()
            )
    }

    fun toTag(): CompoundTag {
        val tag = CompoundTag()
        tag.putString(TagFacts.Station.NAME, name)
        tag.putIntArray(TagFacts.Station.POS, pos.asArray())
        tag.putString(TagFacts.Station.LEVEL, level.name)
        tag.putString(TagFacts.Station.DIMENSION, dimensionID.toString())

        return tag
    }

    fun toByteBuf(byteBuf: PacketByteBuf): PacketByteBuf {
        byteBuf.writeString(name, 100)
        byteBuf.writeBlockPos(pos)
        byteBuf.writeEnumConstant(level)
        byteBuf.writeIdentifier(dimensionID)

        return byteBuf
    }

    fun format(): String = "$name - ${pos.format()}"
}
