package com.chaos.eki_lib.objects.items

import com.chaos.eki_lib.station.data.Station
import com.chaos.eki_lib.utils.TagFacts
import com.chaos.eki_lib.utils.extensions.UtilBlockPos
import com.chaos.eki_lib.utils.extensions.format
import com.chaos.eki_lib.utils.handlers.RegistryHandler
import com.chaos.eki_lib.utils.handlers.StationManager
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.item.TooltipContext
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.nbt.CompoundTag
import net.minecraft.text.Text
import net.minecraft.text.TranslatableText
import net.minecraft.util.Formatting
import net.minecraft.world.World

class StationTunerItem : Item(RegistryHandler.defaultItemSettings().maxCount(1)) {
    override fun appendTooltip(
        stack: ItemStack?,
        world: World?,
        tooltip: MutableList<Text>?,
        context: TooltipContext?
    ) {
        val tag = stack?.tag
        val boundStation = getBoundStation(stack, tag, world)
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

        tooltip?.add(tooltipText.formatted(Formatting.GRAY))
        if (boundStation != null) {
            tooltip?.add(
                TranslatableText(
                    "eki_lib.shift.tooltip"
                )
            )

            if (Screen.hasShiftDown()) {
                tooltip?.removeLast()
                val (name, pos, level, dimension) = boundStation
                tooltip?.add(
                    TranslatableText(
                        "eki_lib.station.name"
                    ).append(
                        ": $name"
                    )
                )
                tooltip?.add(
                    TranslatableText(
                        "eki_lib.station.pos"
                    ).append(
                        ": ${pos.format()}"
                    )
                )
                tooltip?.add(
                    TranslatableText(
                        "eki_lib.station.level"
                    ).append(
                        ": "
                    ).append(
                        TranslatableText(
                            level.getTranslationKey()
                        )
                    )
                )
                tooltip?.add(
                    TranslatableText(
                        "eki_lib.station.dimension"
                    ).append(
                        ": ${dimension.path.capitalize()}"
                    )
                )
            }
        }
    }

    fun getBoundStation(stack: ItemStack?, tag: CompoundTag?, world: World?): Station? {
        return if (tag == null || !tag.contains(TagFacts.Station.POS))
            null
        else {
            val targetPos = UtilBlockPos.fromArray(tag.getIntArray(TagFacts.Station.POS))
            val boundStation = StationManager.getStation(targetPos, world?.registryKey?.value)

            boundStation
        }
    }
}