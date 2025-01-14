package com.vi.androidserialport

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.vi.vioserial.NormalSerial
import com.vi.vioserial.listener.OnNormalDataListener

/**
 * 快速使用
 * Quick use
 * @author Vi
 * @date 2019-07-17 16:50
 * @e-mail 380180278@qq.com
 */
class MainActivity : AppCompatActivity(), OnNormalDataListener {

    var isOpenSerial = false //串口是否打开 Is the serial port open?
    lateinit var mEtCK: android.widget.EditText
    lateinit var mEtBTL: android.widget.EditText
    lateinit var mBtnConnect: android.widget.Button
    lateinit var mEtInput: android.widget.EditText
    lateinit var mBtnSend: android.widget.Button
    lateinit var mTvSendData: android.widget.TextView
    lateinit var mTvReviData: android.widget.TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initClick()
    }

    override fun onDestroy() {
        super.onDestroy()
        //【移除数据监听 remove listener】
        NormalSerial.instance().removeDataListener(this)
        NormalSerial.instance().close()
    }

    private fun initView() {
        mEtCK = findViewById(R.id.mEtCK)
        mEtBTL = findViewById(R.id.mEtBTL)
        mBtnConnect = findViewById(R.id.mBtnConnect)
        mEtInput = findViewById(R.id.mEtInput)
        mBtnSend = findViewById(R.id.mBtnSend)
        mTvSendData = findViewById(R.id.mTvSendData)
        mTvReviData = findViewById(R.id.mTvReviData)
    }

    private fun initClick() {
        //连接/断开串口 Connect/disconnect the serial port
        mBtnConnect.setOnClickListener {
            val ck = mEtCK.text.toString()
            val btl = mEtBTL.text.toString()
            if (ck.isBlank() || btl.isBlank()) {
                Toast.makeText(this@MainActivity, resources.getString(R.string.text_full_data), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (!isOpenSerial) {
                // 【第1步：打开串口 Open serial port 】
                val openStatus = NormalSerial.instance().open(ck, btl.toInt())
                if (openStatus == 0) {
                    isOpenSerial = true
                    mBtnConnect.text = resources.getString(R.string.text_disconnect)
                    Toast.makeText(
                        this@MainActivity,
                        resources.getString(R.string.text_open_success),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    isOpenSerial = false
                    mBtnConnect.text = resources.getString(R.string.text_connect)
                    Toast.makeText(
                        this@MainActivity,
                        String.format(resources.getString(R.string.text_open_fail), openStatus),
                        Toast.LENGTH_SHORT
                    ).show()
                }

                //【添加数据接收回调 Add data receive callback】
                NormalSerial.instance().addDataListener(this)
            } else {
                isOpenSerial = false
                mBtnConnect.text = resources.getString(R.string.text_connect)
                NormalSerial.instance().close()
            }
        }

        //【往串口发送数据 Send data to the serial port】
        mBtnSend.setOnClickListener {
            val input = mEtInput.text.toString()
            if (input.isBlank()) {
                Toast.makeText(this@MainActivity, resources.getString(R.string.text_full_send_Data), Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            if (!isOpenSerial) {
                Toast.makeText(
                    this@MainActivity,
                    resources.getString(R.string.text_serial_open_fail),
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            NormalSerial.instance().sendHex(input)
            mTvSendData.append("\n$input")
        }

        mTvSendData.setOnClickListener { mTvSendData.text = "" }
        mTvReviData.setOnClickListener { mTvReviData.text = "" }
    }

    /**
     * 串口返回数据 Serial port return data
     */
    override fun normalDataBack(data: String?) {
        mTvReviData.append("\n$data")
    }

}
