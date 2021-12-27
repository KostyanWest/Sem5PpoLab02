package com.example.busstation.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.example.busstation.BusStation
import com.example.busstation.R

class DataAdapter
    (var mContext: Context, textViewResourceId: Int) :
    ArrayAdapter<BusStation>(mContext, textViewResourceId, busStations) {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var grid: View

        if (convertView == null) {
            grid = View(mContext)
            val inflater =
                mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            grid = inflater.inflate(
                R.layout.cellgrid,
                null,
                false
            )
        } else {
            grid = convertView
        }

        setupTextView(grid, R.id.num, busStations[position].number.toString())
        setupTextView(grid, R.id.type, busStations[position].type)
        setupTextView(grid, R.id.station, busStations[position].station)
        setupTextView(grid, R.id.time_start, busStations[position].timeStart)
        setupTextView(grid, R.id.time_end, busStations[position].timeEnd)

        return grid
    }

    private fun setupTextView(grid: View, viewId: Int, str: kotlin.String) {
        val view  = grid.findViewById<TextView>(viewId)
        view.text = str
    }

    // возвращает содержимое выделенного элемента списка
    override fun getItem(position: Int): BusStation {
        return busStations[position]
    }

    fun addItem(busStation: BusStation) {
        busStations.add(busStation)
        notifyDataSetChanged()
    }

    fun deleteItem(position: Int) {
        busStations.removeAt(position)
        notifyDataSetChanged()
    }

    fun getAll() : List<BusStation> {
        return busStations
    }

    fun deleteAll() {
        busStations.clear()
        notifyDataSetChanged()
    }

    fun addItems(list: List<BusStation>) {
        busStations.addAll(list)
        notifyDataSetChanged()
    }

    companion object {
        private var busStations = mutableListOf<BusStation>()
    }
}