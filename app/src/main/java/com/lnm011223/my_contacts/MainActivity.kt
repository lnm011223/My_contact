package com.lnm011223.my_contacts

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val contactlist = ArrayList<Contact>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        val layoutManager = LinearLayoutManager(this)
        contactview.layoutManager = layoutManager
        val adapter = ContactAdapter(contactlist)
        contactview.adapter = adapter
        Log.d("111","111")
        val insetsController = WindowCompat.getInsetsController(
            window, window.decorView
        )
        insetsController?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController?.hide(WindowInsetsCompat.Type.navigationBars())
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS), 2)
        } else {
            readContacts()
        }
    }

    @SuppressLint("Range")
    private fun readContacts() {
        // 查询联系人数据
        contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )?.apply {
            while (moveToNext()) {
                // 获取联系人姓名
                val displayName =
                    getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                // 获取联系人手机号
                val number =
                    getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))


                contactlist.add(Contact(getavadar(), displayName, number))
            }

            close()
        }
    }

    private fun getavadar()= when ((1..5).random()) {
            1 -> R.drawable.mood_1
            2 -> R.drawable.mood_2
            3 -> R.drawable.mood_3
            4 -> R.drawable.mood_4
            5 -> R.drawable.mood_5

            else -> {R.drawable.mood_1}
    }


}