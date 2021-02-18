package com.chaos.eki_lib.objects.blocks.base

import net.minecraft.block.Block
import net.minecraft.block.BlockState
import net.minecraft.block.HorizontalFacingBlock
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.DirectionProperty
import net.minecraft.util.BlockMirror
import net.minecraft.util.BlockRotation

open class HorizontalBaseBlock(settings: Settings) :
    Block(settings) {
    companion object {
        val FACING: DirectionProperty = HorizontalFacingBlock.FACING
    }

    override fun getPlacementState(ctx: ItemPlacementContext?): BlockState? =
        defaultState.with(FACING, ctx?.playerFacing?.opposite)

    override fun rotate(state: BlockState?, rotation: BlockRotation?): BlockState? =
        state?.with(FACING, rotation?.rotate(state.get(FACING)))

    override fun mirror(state: BlockState?, mirror: BlockMirror?): BlockState? =
        state?.rotate(mirror?.getRotation(state.get(FACING)))

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>?) {
        builder?.add(FACING)
    }
}