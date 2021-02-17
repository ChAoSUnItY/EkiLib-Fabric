package com.chaos.eki_lib.objects.items

import com.chaos.eki_lib.station.data.Station
import com.chaos.eki_lib.utils.TagFacts
import com.chaos.eki_lib.utils.extensions.UtilBlockPos
import com.chaos.eki_lib.utils.extensions.asArray
import com.chaos.eki_lib.utils.handlers.StationManager
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.world.World

class StationTunerItem : Item(Settings().maxCount(1).group(ItemGroup.MISC)) {
    override fun appendTooltip(
        stack: ItemStack?,
        world: World?,
        tooltip: MutableList<Text>?,
        context: TooltipContext?
    ) {
        val tag = stack?.tag
        val boundStation = getBoundStation(tag, world)
        val tooltipText = if (boundStation != null) {
            TranslatableText(
                "${getTranslationKey(stack)}.tooltip",
                boundStation.format()
            )
        } else {
            TranslatableText(
                "${getTranslationKey(stack)}.tooltip",
                null,
                null,
                null,
                null
            )
        }

        tooltip?.plusAssign(tooltipText.formatted(Formatting.GRAY))
    }

    private fun getBoundStation(tag: CompoundTag?, world: World?): Station? {
        return if (tag == null || !tag.contains(TagFacts.Station.POS))
            null
        else {
            val targetPos = UtilBlockPos.fromArray(tag.getIntArray(TagFacts.Station.POS))

            StationManager.getStationList()
                .filter { it.pos == targetPos && it.dimension == world?.registryKey?.value }[0]
        }
    }
}