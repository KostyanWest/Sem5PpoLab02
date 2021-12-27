package com.example.busstation

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import com.example.busstation.ActivityManager.ActivityController
import com.example.busstation.ActivityManager.DoEnum
import com.example.busstation.ActivityManager.IActivityArgs

class AddUpdateActivity : AppCompatActivity() {
    companion object{
        var id = 5636


        fun checkTime(str: String): Boolean{
            return (str.isNotEmpty() && str.length == 5
                    && str[0].isDigit()
                    && str[1].isDigit()
                    && str[2] == ':'
                    && str[3].isDigit()
                    && str[4].isDigit())
        }
    }

    var notNullEditViews = mutableMapOf<String, Int>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_update)

        var btn = findViewById<Button>(R.id.refresh)

        btn.isEnabled = false
        btn.isClickable = false

        var num = findViewById<EditText>(R.id.num)
        num.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var str = s?.toString()?: ""
                changeActiveButton(btn, str, "number")
            }
        })
        notNullEditViews["number"] = 0

        var station = findViewById<EditText>(R.id.station)

        station.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var str = s?.toString()?: ""
                changeActiveButton(btn, str, "type")
            }
        })
        notNullEditViews["type"] = 0

        var type = findViewById<EditText>(R.id.type)
        type.addTextChangedListener (object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var str = s?.toString()?: ""
                changeActiveButton(btn, str, "station")
            }
        })
        notNullEditViews["station"] = 0

        var timeStart = findViewById<EditText>(R.id.time_start)

        timeStart.addTextChangedListener (object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var str = s?.toString()?: ""
                if(!checkTime(str))
                    str = ""
                changeActiveButton(btn, str, "start")
            }
        })
        notNullEditViews["start"] = 0

        var timeEnd = findViewById<EditText>(R.id.time_end)

        timeEnd.addTextChangedListener (object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                var str = s?.toString()?: ""
                if(!checkTime(str))
                    str = ""
                changeActiveButton(btn, str, "end")
            }
        })
        notNullEditViews["end"] = 0


        var busStationArgs = ActivityController.GetArgs<IActivityArgs>(id)
        if(busStationArgs.do_with == DoEnum.UPDATE)
        {
            num.text = busStationArgs.busStation.number.toString().toEditable()
            station.text = busStationArgs.busStation.station.toEditable()
            type.text = busStationArgs.busStation.type.toEditable()
            timeStart.text = busStationArgs.busStation.timeStart.toEditable()
            timeEnd.text = busStationArgs.busStation.timeEnd.toEditable()
        }


        btn.setOnClickListener {
            var busStation = BusStation(
                num.text.toString().toInt(),
                type.text.toString(),
                station.text.toString(),
                timeStart.text.toString(),
                timeEnd.text.toString()
            )
            busStationArgs.busStation.setup(busStation)
            var intent = Intent(this, MainActivity::class.java)
            ActivityController.SetArgs(MainActivity.id, busStationArgs)
            this.startActivity(intent)
        }


    }

    private fun changeActiveButton(btn: Button, str: String, editName: String){
        if(str.isEmpty()) {
            btn.isClickable = false
            btn.isEnabled = false
            notNullEditViews[editName] = 0
        }
        else{
            notNullEditViews[editName] = 1
            if(checkActiveButton()) {
                btn.isClickable = true
                btn.isEnabled = true
            }
        }
    }


    private fun checkActiveButton() : Boolean{
        for((_, value) in notNullEditViews)
        {
            if(value == 0) {
                return false
            }
        }
        return true
    }
}

fun String.toEditable(): Editable = Editable.Factory.getInstance().newEditable(this)