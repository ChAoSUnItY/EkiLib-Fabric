package com.chaos.eki_lib.utils.voxelshapes

import net.minecraft.util.function.BooleanBiFunction
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes

class HorizontalVoxelShapes(vararg shapesStream: Sequence<VoxelShape>) : IShapeMapper {
    val shapes: Array<VoxelShape> = shapesStream.map {
        it.reduce { shape1, shape2 ->
            VoxelShapes.combineAndSimplify(shape1, shape2, BooleanBiFunction.OR)
        }
    }.toTypedArray()

    fun getByDirection(direction: Direction): VoxelShape =
        when (direction) {
            Direction.NORTH -> getNorth()
            Direction.SOUTH -> getSouth()
            Direction.WEST -> getWest()
            Direction.EAST -> getEast()
            else -> throw IllegalArgumentException("No voxel shape match found for direction $direction")
        }

    override fun getNorth(): VoxelShape =
        shapes[0]

    override fun getSouth(): VoxelShape =
        shapes[1]

    override fun getEast(): VoxelShape =
        shapes[2]

    override fun getWest(): VoxelShape =
        shapes[3]
}
