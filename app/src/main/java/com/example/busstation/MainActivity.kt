package com.example.busstation

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.GridView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.busstation.ActivityManager.*
import com.example.busstation.Adapter.DataAdapter

class MainActivity : AppCompatActivity() {
    companion object {
        var id = 5636
    }

    private lateinit var mAdapter: DataAdapter

    private val REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE = 127
    private val REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE = 128

    private var allBusStation: List<BusStation> = listOf()
    private var station: String? = null
    private var time: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAdapter = DataAdapter(
            applicationContext,
            R.layout.cellgrid
        )


        try {
            var busStationArgs = ActivityController.GetArgs<IActivityArgs>(id)
            if (busStationArgs.do_with == DoEnum.UPDATE) {
                mAdapter.getItem((busStationArgs as UpdateArgs).id).setup(busStationArgs.busStation)
            } else
                mAdapter.addItem(busStationArgs.busStation)
        }catch (e : Exception)
        {

        }

        val g = findViewById<GridView>(R.id.grid)
        g.adapter = mAdapter

        registerForContextMenu(g)

        findViewById<Button>(R.id.add).setOnClickListener {
            var intent = Intent(this, AddUpdateActivity::class.java)
            ActivityController.SetArgs(AddUpdateActivity.id, AddArgs())
            this.startActivity(intent)
        }

        var loadActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val uri = data?.data
                if(uri != null){
                    var inputString= ""

                    val permissionStatus =
                        ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)

                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                        inputString = contentResolver.openInputStream(uri)?.bufferedReader().use { it?.readText() }?: ""
                    } else {
                        ActivityCompat.requestPermissions(
                            this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                            REQUEST_CODE_PERMISSION_READ_EXTERNAL_STORAGE
                        )
                    }
                    try {
                        mAdapter.addItems(BusStation.getBusStations(inputString))
                        allBusStation = mAdapter.getAll().toList()
                    } catch (e: Exception){

                    }
                }
            }
        }

        var saveActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val uri = data?.data
                if(uri != null){
                    val permissionStatus =
                        ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

                    if (permissionStatus == PackageManager.PERMISSION_GRANTED) {
                        contentResolver.openOutputStream(uri)?.bufferedWriter().use{ it?.write(BusStation.getJSON(mAdapter.getAll()))}
                    } else {
                        ActivityCompat.requestPermissions(
                            this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            REQUEST_CODE_PERMISSION_WRITE_EXTERNAL_STORAGE
                        )
                    }
                }
            }
        }

        findViewById<Button>(R.id.save).setOnClickListener {
            var myFileIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            myFileIntent.type = "*/*"
            saveActivity.launch(myFileIntent)
        }

        findViewById<Button>(R.id.load).setOnClickListener {
            var myFileIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            myFileIntent.type = "*/*"
            loadActivity.launch(myFileIntent)
        }


        findViewById<EditText>(R.id.station_sort).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                station = s.toString()
                setfilter(station!!, time.toString())

            }
        })


        findViewById<EditText>(R.id.time_end_sort).addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                time = s.toString()
                setfilter(station.toString(), time!!)
            }
        })

        allBusStation = mAdapter.getAll().toList()
    }

    private fun setfilter(station: String, time: String) {
        mAdapter.deleteAll()
        var l = allBusStation.toList()

        if(station != "null" && station.isNotEmpty()) {
            l = l.filter { x -> x.station.contains(station) }
        }

        if(AddUpdateActivity.checkTime(time))
        {
            l = l.filter { x ->
                val lhsH = x.timeEnd.substring(0, 2).toInt()
                val rhsH = time.substring(0, 2).toInt()
                if (lhsH <= rhsH)
                    if(lhsH == rhsH) (x.timeEnd.substring(3, 5).toInt() < time.substring(3, 5).toInt()) else true
                else
                    false
            }
        }

        mAdapter.addItems(l)

    }


    override fun onCreateContextMenu(menu: ContextMenu?, v: View?, menuInfo: ContextMenu.ContextMenuInfo?) {
        super.onCreateContextMenu(menu, v, menuInfo)
        val inflater = menuInflater
        inflater.inflate(R.menu.context_menu, menu)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.getMenuInfo() as AdapterView.AdapterContextMenuInfo
        return when (item.getItemId()) {
            R.id.edit -> {
                var intent = Intent(this, AddUpdateActivity::class.java)
                ActivityController.SetArgs(AddUpdateActivity.id, UpdateArgs(info.position, mAdapter.getItem(info.position)))
                this.startActivity(intent)
                true
            }
            R.id.delete -> {
                mAdapter.deleteItem(info.position)
                true
            }
            else -> super.onContextItemSelected(item)
        }
    }
}