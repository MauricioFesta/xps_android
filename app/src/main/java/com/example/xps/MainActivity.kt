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


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var discoveredDevices:MutableList<BluetoothDevice> = mutableListOf()
    lateinit var listView: ListView
    lateinit var listViewF: ListView
    private var MYUUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    lateinit var mBluetoothAdapter: BluetoothAdapter
    var list: ArrayList<String> = ArrayList()
    lateinit var arrayAdapter: ArrayAdapter<String>
    var mmSocket: BluetoothSocket? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listView = findViewById(R.id.listView)

        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, list)
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        search_device()
        add_item("Constructor")

        // Example of a call to a native method
        //binding.sampleText.text = stringFromJNI()
    }

    fun listBl(view: View){

            //Toast.makeText(applicationContext,"this is toast message",Toast.LENGTH_SHORT).show()



        /*
        var requestBluetooth = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val toast = Toast.makeText(applicationContext, "Liberado", Toast.LENGTH_SHORT)
                toast.show()
            }else{
                val toast = Toast.makeText(applicationContext, "Não Liberado", Toast.LENGTH_SHORT)
                toast.show()
            }
        }

        if (mBluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            requestBluetooth.launch(enableBtIntent)
        }

        */

        val pairedDevices: Set<BluetoothDevice> = mBluetoothAdapter.bondedDevices

        listView.setOnItemClickListener { parent, view, position, id ->

            add_item("teste2222")


        }

        if(pairedDevices.isEmpty()){

            if(!mBluetoothAdapter.isDiscovering){

                if (mBluetoothAdapter.isDiscovering){
                    mBluetoothAdapter.cancelDiscovery()
                    toast("A descoberta foi cancelada")
                }else{

                    search_device()

                }
            }

        }

        //if(pairedDevices.isNotEmpty()){

           // pairedDevices.forEach { device ->
               // val deviceName = device.name
                //list.add(deviceName)
                //val deviceHardwareAddress = device.address // MAC address

            //}

       // }



    }

    fun toast(vl: String) {

        val toast = Toast.makeText(applicationContext, vl, Toast.LENGTH_SHORT)
        toast.show()

    }
    fun add_item(vl: String){

        list.add(vl)
        arrayAdapter.notifyDataSetChanged()
        listView.adapter = arrayAdapter

    }

    fun search_device(){

        mBluetoothAdapter.startDiscovery();

        toast("A descoberta iniciou")

        val receiver: BroadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {

                arrayAdapter.notifyDataSetChanged()
                listView.adapter = arrayAdapter

                val action = intent.action
                if (BluetoothDevice.ACTION_FOUND == action) {
                    val device = intent
                        .getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)

                    add_item(
                        """
                        ${device!!.name}
                        ${device!!.address}
                        """.trimIndent()
                    )

                    if(device!!.name == ""){
                        //conect here bluethooth vamonanar

                        connectBl(device)
                    }

                    arrayAdapter.notifyDataSetChanged()
                    listView.adapter = arrayAdapter


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

    fun connectBl(device: BluetoothDevice){

        try {

            mmSocket =  device.createRfcommSocketToServiceRecord(MYUUID)
            mmSocket!!.connect()


        }catch (e: IOException){
            toast("Erro ao conectar no dispositivo")
        }


    }

    fun sendChar() {

        try{

            if(mmSocket != null){
                mmSocket!!.outputStream.write("a".toByteArray())
            }


        }catch(e: IOException){

            toast("Erro enviar dado")

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