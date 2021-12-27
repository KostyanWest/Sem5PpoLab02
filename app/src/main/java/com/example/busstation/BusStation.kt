package com.example.busstation

import org.json.JSONArray
import org.json.JSONObject



class BusStation(var number : Int, var type : String,
               var station : String, var timeStart : String,
               var timeEnd : String) {

    constructor(jsonObject: JSONObject) :
            this(jsonObject.getInt("n"), jsonObject.getString("type"),
                jsonObject.getString("s"), jsonObject.getString("ts"),
                jsonObject.getString("te")) {
    }

    companion object {
        fun getNullBusStations(): BusStation {
            return BusStation(-1, "", "", "", "")
        }


        fun getBusStations(str: String): List<BusStation> {
            var busStations = mutableListOf<BusStation>()
            var json_arr = JSONArray(str)
            for (i in 0 until json_arr.length()) {
                busStations.add(BusStation(json_arr.getJSONObject(i)))
            }

            return busStations
        }

        fun getJSON(busStations: List<BusStation>): String {
            val json_arr = JSONArray()
            for (i in busStations) {
                var busStation = JSONObject()
                busStation.put("n", i.number).put("type", i.type)
                    .put("s", i.station).put("ts", i.timeStart)
                    .put("te", i.timeEnd)
                json_arr.put(busStation)
            }
            var str = json_arr.toString()
            return json_arr.toString()
        }
    }

    fun setup(busStations: BusStation) {
        number = busStations.number
        station = busStations.station
        type = busStations.type
        timeStart = busStations.timeStart
        timeEnd = busStations.timeEnd
    }

}