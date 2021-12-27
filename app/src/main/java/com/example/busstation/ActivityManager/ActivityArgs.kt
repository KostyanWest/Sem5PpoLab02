package com.example.busstation.ActivityManager

import com.example.busstation.BusStation

interface IActivityArgs{
    val do_with: DoEnum
    var busStation: BusStation

    companion object {
        fun <T> convert(args: IActivityArgs): T{
            return args as T
        }
    }
}

enum class DoEnum{
    UPDATE, ADD
}

class AddArgs() : IActivityArgs {
    override val do_with: DoEnum = DoEnum.ADD
    override var busStation: BusStation = BusStation.getNullBusStations()
}

class UpdateArgs(var id: Int, override var busStation : BusStation): IActivityArgs {
    override val do_with: DoEnum = DoEnum.UPDATE

}