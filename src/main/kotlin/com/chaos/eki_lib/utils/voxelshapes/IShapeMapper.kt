package com.chaos.eki_lib.utils.voxelshapes

import net.minecraft.util.shape.VoxelShape

interface IShapeMapper {
    fun getNorth(): VoxelShape

    fun getSouth(): VoxelShape

    fun getEast(): VoxelShape

    fun getWest(): VoxelShape

    fun getUp(): VoxelShape =
        throw Exception("GetUp() is not implemented in default")

    fun getDown(): VoxelShape =
        throw Exception("GetDown() is not implemented in default")
}