package com.chaos.eki_lib.objects.blockentities

import com.chaos.eki_lib.station.data.Station

interface IStationBoundable {
    fun isTargetExist(): Boolean

    fun getBoundStation(): Station?
}