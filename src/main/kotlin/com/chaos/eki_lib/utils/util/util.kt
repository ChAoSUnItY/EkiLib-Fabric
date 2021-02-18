package com.chaos.eki_lib.utils.util

import com.chaos.eki_lib.EkiLib
import net.minecraft.util.Identifier

internal fun createIdentifier(value: String): Identifier = Identifier(EkiLib.MODID, value)