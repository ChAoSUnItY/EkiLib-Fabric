package com.chaos.eki_lib.utils

sealed class TagFacts {
    object Station : TagFacts() {
        const val NAME = "name"
        const val POS = "position"
        const val LEVEL = "level"
        const val DIMENSION = "dimension"
    }
    object StationWorldData : TagFacts() {
        const val LIST_NAME = "Station List"
    }
}
