package com.chaos.eki_lib.utils.extensions

import net.minecraft.util.math.BlockPos

object UtilBlockPos {
    fun fromArray(array: IntArray): BlockPos = BlockPos(array[0], array[1], array[2])
}

fun BlockPos.format(): String = "($x,$y,$z)"

fun BlockPos.asArray(): IntArray = intArrayOf(x, y, z)