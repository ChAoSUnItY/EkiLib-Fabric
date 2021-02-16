package com.chaos.eki_lib.station.data

import com.chaos.eki_lib.utils.Cyclable
import net.minecraft.text.TranslatableText

enum class StationLevel(val key: String) : Cyclable<StationLevel> {
    SPECIAL("special_class"), FIRST("first_class"), SECOND("second_class"), THRID("third_class"),
    SIMPLE("simple"), STAFFLESS("staffless"), SIGNAL("signal"), NON("non");

    override fun next(): StationLevel = values()[(ordinal + 1) % values().size]

    override fun previous(): StationLevel = values()[if (ordinal > 0) ordinal - 1 else values().size - 1]

    fun getTranslationKey(): String = "eki_lib.station.level.$key"

    fun toTranslated(): TranslatableText = TranslatableText(getTranslationKey())
}