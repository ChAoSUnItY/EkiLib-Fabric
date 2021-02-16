package com.chaos.eki_lib.gui.screen

import net.minecraft.client.gui.screen.Screen
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.text.Text
import net.minecraft.util.Identifier

open class BaseScreen(title: Text, val previous: Screen?, val dimension: Identifier, val player: PlayerEntity) : Screen(title)