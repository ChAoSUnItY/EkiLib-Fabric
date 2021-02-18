package com.chaos.eki_lib.utils.handlers

import com.chaos.eki_lib.objects.blockentities.StationNameplateBlockEntity
import com.chaos.eki_lib.objects.blocks.StationNameplateBlock
import com.chaos.eki_lib.objects.items.StationTunerItem
import com.chaos.eki_lib.utils.util.createIdentifier
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntityType
import net.minecraft.item.BlockItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.util.Identifier

object RegistryHandler {
    fun defaultItemSettings(): Item.Settings = Item.Settings().group(ItemGroup.MISC)
    val blockMap = mutableMapOf<Identifier, Block>()
    val itemMap = mutableMapOf<Identifier, Item>()

    val blockEntityMap = mutableMapOf<Identifier, BlockEntityType<*>>()

    // ====================================BLOCK===================================== //

    val STATION_NAMEPLATE_BLOCK = blockMap.putOrGet(createIdentifier("station_nameplate"), StationNameplateBlock())

    // ====================================ITEM===================================== //

    val STATION_NAMEPLATE_ITEM =
        itemMap.putOrGet(
            createIdentifier("station_nameplate"),
            BlockItem(STATION_NAMEPLATE_BLOCK, defaultItemSettings())
        )

    val STATION_TUNER = itemMap.putOrGet(createIdentifier("station_tuner"), StationTunerItem())

    // =================================BLOCK ENTITY================================= //

    val STATION_NAMEPLATE_BLOCK_ENTITY = blockEntityMap.putOrGet(
        createIdentifier("station_nameplate"),
        BlockEntityType.Builder.create(::StationNameplateBlockEntity, STATION_NAMEPLATE_BLOCK).build(null)
    ) as BlockEntityType<StationNameplateBlockEntity>

    private fun <K, V> MutableMap<K, V>.putOrGet(key: K, valueIfAbsence: V): V =
        getOrPut(key) { valueIfAbsence }
}