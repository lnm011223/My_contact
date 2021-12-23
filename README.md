## 三、界面设计

![Screenshot_1640237293](https://gitee.com/lnm011223/lnm011223-picture/raw/master/uPic/Screenshot_1640237293.png)

![Screenshot_1640237550](https://gitee.com/lnm011223/lnm011223-picture/raw/master/uPic/Screenshot_1640237550.png)

![Screenshot_1640237474](https://gitee.com/lnm011223/lnm011223-picture/raw/master/uPic/Screenshot_1640237474.png)

![Screenshot_1640237329](https://gitee.com/lnm011223/lnm011223-picture/raw/master/uPic/Screenshot_1640237329.png)

![Screenshot_1640237322](https://gitee.com/lnm011223/lnm011223-picture/raw/master/uPic/Screenshot_1640237322.png)





## 四、关键代码

#### 1.Activity逻辑

> 1.MainActivity.kt

```kotlin
package com.lnm011223.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val contactsList = ArrayList<Contact>()
    private lateinit var adapter: ContactAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = ContactAdapter(this,R.layout.contect_item,contactsList)
        contactview.adapter = adapter
        if (ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_CONTACTS),2)
        } else {
            readContacts()
        }
        contactview.setOnItemClickListener { parent, view, position, id ->
            val contact = contactsList[position]
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), 1)
            } else {
                call(contact.number)
            }
        }
        contactview.setOnItemLongClickListener { parent, view, position, id ->

            val contact = contactsList[position]
            val intent = Intent(this,SendActivity::class.java)
            intent.putExtra("number",contact.number)
            startActivity(intent)
            true
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
            2 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    readContacts()
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun call(number: String) {
        try {
            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$number")
            startActivity(intent)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }
    @SuppressLint("Range")
    private fun readContacts() {
        // 查询联系人数据
        contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null)?.apply {
            while (moveToNext()) {
                // 获取联系人姓名
                val displayName = getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                // 获取联系人手机号
                val number = getString(getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contactsList.add(Contact(displayName,number))
            }
            adapter.notifyDataSetChanged()
            close()
        }
    }
}
```



> 2.SendActivity.kt

```kotlin
package com.lnm011223.myapplication

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_send.*

class SendActivity : AppCompatActivity() {
    var phonenumber = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)
        phonenumber = intent.getStringExtra("number").toString()
        phonenumbertext.text = phonenumber
        sendbutton.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.SEND_SMS), 1)
            } else {
                val sms_text = sms_edit.text.toString()
                if (sms_text.isNotEmpty() && phonenumber.isNotEmpty()){
                    val smsManager = SmsManager.getDefault()
                    smsManager.sendTextMessage(phonenumber,null,sms_text,null,null)
                    Toast.makeText(this,"发送成功",Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this,"发送内容不能为空！",Toast.LENGTH_SHORT).show()
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
```

#### 2.listview相关

> 1.contact类

```kotlin
package com.lnm011223.myapplication

class Contact (val name: String,val number: String)
```

> 2.contactadapter

```kotlin
package com.lnm011223.myapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView


class ContactAdapter(activity: Activity,val resourceId: Int,data: List<Contact>) : ArrayAdapter<Contact>(activity,resourceId,data) {
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = LayoutInflater.from(context).inflate(resourceId,parent,false)
        val contactname: TextView = view.findViewById(R.id.item_name)
        val contactnumber: TextView = view.findViewById(R.id.item_number)
        val contact = getItem(position)
        if (contact != null) {
            contactname.text = contact.name
            contactnumber.text = contact.number
        }
        return view
    }
}
```

> 3.contact_item.xml

```kotlin
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">
    <TextView
        android:layout_marginTop="5dp"
        android:id="@+id/item_name"
        android:textColor="#2196f3"
        android:textSize="13sp"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>
    <TextView
        android:id="@+id/item_number"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:maxLines="1"/>

</LinearLayout>
```

#### 3.布局文件

> 1.activity_main.xml

```kotlin
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:background="#E91E63">
    <com.google.android.material.card.MaterialCardView
        android:id="@+id/card"
        app:cardCornerRadius="10dp"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_margin="15dp"

        >

        <ListView
            android:id="@+id/contactview"
            android:layout_width="wrap_content"
            android:layout_height="621dp"
            android:layout_margin="15dp" />
    </com.google.android.material.card.MaterialCardView>






</androidx.constraintlayout.widget.ConstraintLayout>
```



> 2.activity_send.xml

```kotlin
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".SendActivity"
    android:background="#E91E63">
    <com.google.android.material.card.MaterialCardView
        android:id="@+id/card1"
        app:cardCornerRadius="10dp"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_margin="15dp"

        >
        <androidx.constraintlayout.widget.ConstraintLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:layout_marginStart="16dp"
            android:layout_marginTop="16dp"
            android:layout_marginEnd="16dp"
            android:layout_marginBottom="8dp"
            >
            <TextView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                android:textColor="#2196F3"
                android:text="电话："
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toTopOf="parent"
                android:id="@+id/phonetext"

                />
            <TextView
                android:layout_width="match_parent"
                android:layout_height="wrap_content"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@id/phonetext"
                android:id="@+id/phonenumbertext"/>


            <com.google.android.material.textfield.TextInputLayout
                android:id="@+id/textField"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"


                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@id/phonenumbertext">

                <com.google.android.material.textfield.TextInputEditText
                    android:id="@+id/sms_edit"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content" />

            </com.google.android.material.textfield.TextInputLayout>

            <com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginBottom="16dp"
                android:backgroundTint="#2196F3"
                android:text="发送短信"
                android:textColor="#ffffff"
                app:icon="@drawable/ic_baseline_message_24"
                app:iconTint="#ffffff"
                app:layout_constraintBottom_toBottomOf="parent"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                android:id="@+id/sendbutton"/>

        </androidx.constraintlayout.widget.ConstraintLayout>


    </com.google.android.material.card.MaterialCardView>

</androidx.constraintlayout.widget.ConstraintLayout>
```

## 六、难点和解决方案

#### 1.长按调用问题

解决方案：和短按`setOnClickListener`不一样的地方在于，长按`setOnLongClickListener`需要返回一个boolen参数，即返回一个`true`，在Java里直接return true就好,但是在kotlin里，默认用的是lambda表达式，最后一行语句是返回值，所以直接`true`就好

```kotlin
contactview.setOnItemLongClickListener { parent, view, position, id ->

    val contact = contactsList[position]
    val intent = Intent(this,SendActivity::class.java)
    intent.putExtra("number",contact.number)
    startActivity(intent)
    true
}
```