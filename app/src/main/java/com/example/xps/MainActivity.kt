package com.example.xps

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.xps.databinding.ActivityMainBinding
import android.widget.ArrayAdapter
import android.widget.ListView
import  android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver
import android.bluetooth.BluetoothClass
import android.content.Context
import android.content.Intent


import android.widget.Toast
import android.content.IntentFilter
import android.R.string.no
import android.bluetooth.BluetoothSocket
import androidx.activity.result.contract.ActivityResultContracts
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.reflect.typeOf


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var discoveredDevices:MutableList<BluetoothDevice> = mutableListOf()
    lateinit var listView: ListView
    lateinit var listViewF: ListView
    private var MYUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    lateinit var mBluetoothAdapter: BluetoothAdapter
    var list: ArrayList<String> = ArrayList()
    var list_device: ArrayList<BluetoothDevice> = ArrayList()
    lateinit var arrayAdapter: ArrayAdapter<String>
    var mmSocket: BluetoothSocket? = null
    private var mMac: String = "D8:F3:BC:53:1F:B0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listView = findViewById(R.id.listView)

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        val pairedDevices: Set<BluetoothDevice> = mBluetoothAdapter.bondedDevices

        listView.setOnItemClickListener { parent, view, position, id ->

            connectBluetooth(list_device[position])

        }

        if(verifyConnected(pairedDevices)){
            search_device()
        }else{

            addPaired(pairedDevices)
        }


    }

    fun openPortao(view: View){

        val pairedDevices: Set<BluetoothDevice> = mBluetoothAdapter.bondedDevices

        if(pairedDevices.isEmpty()){

            if(!mBluetoothAdapter.isDiscovering){

                if (mBluetoothAdapter.isDiscovering){
                    mBluetoothAdapter.cancelDiscovery()
                    toast("A descoberta foi cancelada")
                }else{

                    search_device()

                }
            }

        }else{

            if(verifyIsArd()){

                connectBluetooth(list_device[0])
                sendChar()

            }else{

                sendChar()
            }

        }

    }

    fun addPaired(pairedDevices: Set<BluetoothDevice>){

        pairedDevices.forEach { device ->

            val deviceStr: String =  """
                        ${device!!.name}
                        ${device!!.address}
                        """.trimIndent()

            add_item("CONECTADO -> " + deviceStr)
            add_device(device)

        }

    }

    fun verifyConnected(pairedDevices: Set<BluetoothDevice>): Boolean{

        return pairedDevices.isEmpty()

    }

    fun verifyIsArd(): Boolean {

        var dispConectado: String? = list.find { it.contains("CONECTADO -> ") }

        return dispConectado !is String

    }

    fun toast(vl: String) {

        val toast = Toast.makeText(applicationContext, vl, Toast.LENGTH_SHORT)
        toast.show()

    }
    fun add_item(vl: String){

        if(!list.contains(vl)) {
            list.add(vl)
            arrayAdapter.notifyDataSetChanged()
            listView.adapter = arrayAdapter
        }

    }

    fun search_device(){

        mBluetoothAdapter.startDiscovery();

        toast("A descoberta iniciou")

        val receiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                val action = intent.action
                if (BluetoothDevice.ACTION_FOUND == action) {
                    val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

                    if(device!!.address == mMac){

                        add_item(
                            """
                        ${device!!.name}
                        ${device!!.address}
                        """.trimIndent()
                        )

                        add_device(device)

                        connectBluetooth(list_device[0])
                    }
                }
            }
        }

        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(receiver, filter)

        fun onDestroy() {
            unregisterReceiver(receiver);
            super.onDestroy();
        }

    }

    fun add_device(device: BluetoothDevice){
        if(!list_device.contains(device)){
            list_device.add(device)
        }

    }

    fun connectBluetooth(device: BluetoothDevice){

        try {

            if(verifyIsArd()){

                mmSocket =  device.createRfcommSocketToServiceRecord(MYUUID)
                mmSocket!!.connect()

                val deviceStr: String =  """
                        ${device.name}
                        ${device.address}
                        """.trimIndent()

                list.remove(deviceStr)

                add_item("CONECTADO -> " + deviceStr)

            }else{
                toast("Ja conectado")
            }

        }catch (e: IOException){

            toast("Erro ao conectar no dispositivo: " + e)
            /*
            var dispConectado: String? = list.find { it.contains(device.address) }
            list.remove("CONECTADO -> " + dispConectado)

             */
        }


    }

    fun sendChar() {

        try{

            if(mmSocket != null){
                mmSocket!!.outputStream.write("a".toByteArray())
            }


        }catch(e: IOException){

            toast("Erro ao enviar dado")

        }

    }

    fun connBl(view: View){

        listView = findViewById(R.id.listView)

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)

        list.add("teste2222")
        arrayAdapter.notifyDataSetChanged()
        listView.adapter = arrayAdapter

    }

    /**
     * A native method that is implemented by the 'xps' native library,
     * which is packaged with this application.
     */
    external fun stringFromJNI(): String

    companion object {
        // Used to load the 'xps' library on application startup.
        init {
            System.loadLibrary("xps")
        }
    }
}