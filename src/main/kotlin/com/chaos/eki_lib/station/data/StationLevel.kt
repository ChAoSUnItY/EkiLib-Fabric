package com.chaos.eki_lib.station.data

import net.minecraft.text.TranslatableText

enum class StationLevel(val key: String) {
    SPECIAL("special_class"), FIRST("first_class"), SECOND("second_class"), THRID("third_class"),
    SIMPLE("simple"), STAFFLESS("staffless"), SIGNAL("signal"), NON("non");

    fun getTranslationKey(): String = "eki_lib.station.level.$key"

    fun toTranslated(): TranslatableText = TranslatableText(getTranslationKey())
}