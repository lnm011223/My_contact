package com.lnm011223.my_contacts

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import kotlinx.android.synthetic.main.activity_send.*

class SendActivity : AppCompatActivity() {
    var phonenumber = ""
    var phonename = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)
        val insetsController = WindowCompat.getInsetsController(
            window, window.decorView
        )
        insetsController?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController?.hide(WindowInsetsCompat.Type.navigationBars())
        toolbar.title = "Send Message"
        phonenumber = intent.getStringExtra("number").toString()
        phonename = intent.getStringExtra("name").toString()
        phonenumbertext.text = phonenumber
        phonetext.text = phonename
        sendbutton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 1)
            } else {
                val sms_text = sms_edit.text.toString()
                if (sms_text.isNotEmpty() && phonenumber.isNotEmpty()){
                    val smsManager = SmsManager.getDefault()
                    smsManager.sendTextMessage(phonenumber,null,sms_text,null,null)
                    Toast.makeText(this,"发送成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this,"发送内容不能为空！", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //call()
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show()
                }

            }

        }
    }
}