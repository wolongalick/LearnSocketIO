package com.alick.learnsocketio

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.alick.learnsocketio.gson.JsonUtils
import com.example.MessageBean
import io.socket.client.Ack
import io.socket.client.IO
import io.socket.client.Manager
import io.socket.client.Socket
import io.socket.engineio.client.transports.Polling
import io.socket.engineio.client.transports.WebSocket
import org.json.JSONObject


class MainActivity : AppCompatActivity() {

    private lateinit var mSocket: Socket

    private val btnConnect: Button by lazy {
        findViewById(R.id.btnConnect)
    }
    private val btnDisconnect: Button by lazy {
        findViewById(R.id.btnDisconnect)
    }
    private val etMessage: EditText by lazy {
        findViewById(R.id.etMessage)
    }

    private val btnSend: Button by lazy {
        findViewById(R.id.btnSend)
    }

    private val tvReceiveMessage: TextView by lazy {
        findViewById(R.id.tvReceiveMessage)
    }

    private val rgServerType: RadioGroup by lazy {
        findViewById(R.id.rgServerType)
    }

    private val tvDisconnect: TextView by lazy {
        findViewById(R.id.tvDisconnect)
    }

    private val fileName: String by lazy {
        TimeUtils.getCurrentTime() + ".txt"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnConnect.setOnClickListener {
            var url = ""
            when (rgServerType.checkedRadioButtonId) {
                R.id.rbLocalServer -> {
                    url = "http://192.168.0.103:9092"
                }
                R.id.rbRemoteServer -> {
//                    url = "http://syz-websocket.ahdev51.guahao-test.com"
//                    url = "http://192.168.87.51:10247"
                    url = "http://netty-websocket.ahdev59.guahao-test.com"
                }
                else -> {
                    Toast.makeText(this, "请先选择服务器类型", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            val options: IO.Options = IO.Options() // IO factory options
//                .setForceNew(false)
//                .setMultiplex(true) // low-level engine options
//                .setTransports(arrayOf(Polling.NAME, WebSocket.NAME))
//                .setTransports(arrayOf(WebSocket.NAME))
//                .setUpgrade(true)
//                .setRememberUpgrade(false)
//                .setPath("/socket.io/")
//                .setQuery(null)
//                .setExtraHeaders(null) // Manager options
//                .setReconnection(true)
//                .setReconnectionAttempts(Int.MAX_VALUE)
//                .setReconnectionDelay(1000)
//                .setReconnectionDelayMax(5000)
//                .setRandomizationFactor(0.5)
//                .setTimeout(10000) // Socket options
//                .setAuth(null)
//                .setQuery("UID=cxw")
//                .build()
            options.transports = arrayOf(Polling.NAME, WebSocket.NAME)
            options.forceNew = true
            options.upgrade = false
            options.query = "UID=1002"
            mSocket = IO.socket(url, options)
            mSocket.on(Socket.EVENT_CONNECT) {
                saveLog("已连接")
                runOnUiThread {
                    btnSend.isEnabled = true
                }
            }

            mSocket.on(Socket.EVENT_DISCONNECT) {
                saveLog("断开连接:${it[0]}")
                runOnUiThread {
                    btnSend.isEnabled = false
                    tvDisconnect.text = tvDisconnect.text.toString() + "\n" + it[0]
                }
            }

            mSocket.on(Socket.EVENT_CONNECT_ERROR) {
                runOnUiThread {
                    btnSend.isEnabled = false
                }
                saveLog("连接错误:${it[0]}")
            }

            mSocket.on("sendMessaged") {
                saveLog("收到消息:${it[0]}")
                it.find {
                    it is Ack
                }?.apply {
                    (this as Ack).call(JSONObject(JsonUtils.parseBean2json(MessageBean("我是客户端,我收到了服务端的消息", "崔兴旺"))))
                }
                runOnUiThread {
                    tvReceiveMessage.text = tvReceiveMessage.text.toString() + "\n" + it[0]
                    btnSend.isEnabled = true
                }
            }//["Broadcast","当前时间",1657547284572]

            mSocket.on("myBroadcast") {
                saveLog("收到广播消息:${it[0]},${it[0]}")
                runOnUiThread {
//                    tvReceiveMessage.text = tvReceiveMessage.text.toString() + "\n" + it[0]
                    btnSend.isEnabled = true
                }
            }
            mSocket.on(Socket.EVENT_PING) {
                saveLog("收到PING消息")
                runOnUiThread {
                    tvReceiveMessage.text = tvReceiveMessage.text.toString() + "\n" + "收到PING消息"
                }
            }
            mSocket.on(Socket.EVENT_PONG) {
                saveLog("收到PONG消息")
                runOnUiThread {
                    tvReceiveMessage.text = tvReceiveMessage.text.toString() + "\n" + "收到PONG消息"
                }
            }

            mSocket.on("sendMessageOnAck") {
                saveLog("收到OnAck消息:${it[0]}")
            }

            mSocket.on("ackevent2") {
                saveLog("收到ackevent2消息:${it[0]},${it[1]}")
            }

            mSocket.on(Manager.EVENT_TRANSPORT) {
                saveLog("收到transport消息")
            }

            mSocket.connect()
        }

        btnDisconnect.setOnClickListener {
            mSocket.disconnect()
        }

        btnSend.setOnClickListener {
            val msg = etMessage.text.toString()
            if (msg.isEmpty()) {
                Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            mSocket.emit("ServerReceive", JSONObject("{\"name\":\"client\",\"message\":\"${msg}\"}"), object : Ack {
                override fun call(vararg args: Any?) {
                    saveLog("服务器已应答:${args[0]}")
//                    mSocket.emit("sendMessageOnAck", "好的,我知道了", object : Ack {
//                        override fun call(vararg args: Any?) {
//                            saveLog("收到sendMessageOnAck回调消息:${args[0]},${args[1]}")
//                        }
//                    })
                }
            })
        }
    }

    //42["Broadcast","当前时间",1657548823391]
    private fun saveLog(log: String) {
        println(log)
        FileUtils.writeLog(this, fileName, log)
    }
}