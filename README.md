#  通讯录的设计与实现说明文档 

## 一、软件名称

​	简易通讯录

> GitHub项目地址：[https://github.com/lnm011223/My_contact](https://github.com/lnm011223/My_contact)

## 二、软件内容简介

一个真的很简单的通讯录，素材配色什么的直接用了上次日记本的（不过本来这个实验就没什么要做的，书上都有）

实现了短按打电话，长按发短信的功能，右侧的按钮点击也可以实现打电话和发短信

## 三、界面设计

### 1.界面展示

<img src="https://gitee.com/lnm011223/lnm011223-picture/raw/master/uPic/Screenshot_1640223080.png" alt="" style="zoom:50%;" />

<img src="https://gitee.com/lnm011223/lnm011223-picture/raw/master/uPic/Screenshot_1640223087.png" alt="" style="zoom:50%;" />

### 2.权限获取

<img src="https://gitee.com/lnm011223/lnm011223-picture/raw/master/uPic/Screenshot_1640223091.png" style="zoom:50%;" />

<img src="https://gitee.com/lnm011223/lnm011223-picture/raw/master/uPic/Screenshot_1640223110.png" style="zoom:50%;" />

<img src="https://gitee.com/lnm011223/lnm011223-picture/raw/master/uPic/Screenshot_1640223178.png" style="zoom:50%;" />

## 四、关键代码

### 1.Activity逻辑

> 1.MainActivity.kt

```kotlin
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
```

> 2.SendActivity.kt

```kotlin
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
```

### 2.Recycleview相关

> 1.contact类

```kotlin
package com.lnm011223.my_contacts

class Contact(val avadar: Int,val name: String,val number: String)
```

> 2.ContactAdapter

```kotlin
package com.lnm011223.my_contacts

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class ContactAdapter(val contactlist: List<Contact>): RecyclerView.Adapter<ContactAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val contact_image : ImageView = view.findViewById(R.id.contact_imageView)
        val contact_name : TextView = view.findViewById(R.id.contact_name)
        val contact_phone : TextView = view.findViewById(R.id.contact_phone)
        val phone_image : ImageView = view.findViewById(R.id.phone_image)
        val message_image : ImageView = view.findViewById(R.id.message_image)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.contact_item,parent,false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnClickListener {
            val position = viewHolder.adapterPosition
            val contact = contactlist[position]

            if (ContextCompat.checkSelfPermission(parent.context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(parent.context as Activity, arrayOf(Manifest.permission.CALL_PHONE), 1)
            } else {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:${contact.number}")
                parent.context.startActivity(intent)
            }
        }
        viewHolder.itemView.setOnLongClickListener {
            val position = viewHolder.adapterPosition
            val contact = contactlist[position]
            val intent = Intent(parent.context,SendActivity::class.java)
            intent.putExtra("number",contact.number)
            intent.putExtra("name",contact.name)
            parent.context.startActivity(intent)
            true
        }
        viewHolder.message_image.setOnClickListener {
            val position = viewHolder.adapterPosition
            val contact = contactlist[position]
            val intent = Intent(parent.context,SendActivity::class.java)
            intent.putExtra("number",contact.number)
            intent.putExtra("name",contact.name)
            parent.context.startActivity(intent)
        }
        viewHolder.phone_image.setOnClickListener {
            val position = viewHolder.adapterPosition
            val contact = contactlist[position]

            if (ContextCompat.checkSelfPermission(parent.context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(parent.context as Activity, arrayOf(Manifest.permission.CALL_PHONE), 1)
            } else {
                val intent = Intent(Intent.ACTION_CALL)
                intent.data = Uri.parse("tel:${contact.number}")
                parent.context.startActivity(intent)
            }
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: ContactAdapter.ViewHolder, position: Int) {
        val contact = contactlist[position]
        holder.contact_image.setImageResource(contact.avadar)
        holder.contact_name.text = contact.name
        holder.contact_phone.text = contact.number
    }

    override fun getItemCount() = contactlist.size

}
```

> 3.contact_item.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:layout_margin="5dp">

    <ImageView
        android:id="@+id/contact_imageView"
        android:layout_width="50dp"
        android:layout_height="50dp"


        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/contact_name"
        android:layout_width="144dp"
        android:layout_height="26dp"
        android:layout_marginStart="10dp"
        android:textColor="#3EB06A"
        android:textSize="20sp"
        android:textStyle="bold"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.0"
        app:layout_constraintStart_toEndOf="@+id/contact_imageView"
        app:layout_constraintTop_toTopOf="parent" />

    <TextView
        android:id="@+id/contact_phone"

        android:layout_width="199dp"
        android:layout_height="24dp"
        android:layout_marginStart="10dp"
        android:gravity="bottom"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintHorizontal_bias="0.0"
        app:layout_constraintStart_toEndOf="@+id/contact_imageView"
        app:layout_constraintTop_toBottomOf="@+id/contact_name" />

    <ImageView
        android:id="@+id/phone_image"
        android:layout_width="30dp"
        android:layout_height="30dp"
        android:src="@drawable/phone"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toEndOf="@+id/contact_phone"
        app:layout_constraintTop_toTopOf="parent"
        android:layout_marginStart="20dp"
        />

    <ImageView

        android:id="@+id/message_image"
        android:layout_width="30dp"
        android:layout_height="30dp"
        android:src="@drawable/message"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toEndOf="@+id/phone_image"
        app:layout_constraintTop_toTopOf="parent"
        android:layout_marginStart="5dp"


        />

</androidx.constraintlayout.widget.ConstraintLayout>
```



### 3.布局文件

> 1.activity_main.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".MainActivity"
    android:background="#3EB06A">

    <com.google.android.material.appbar.AppBarLayout
        android:id="@+id/appbar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:elevation="0dp"
        >

        <androidx.appcompat.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:gravity="center"
            android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
            app:layout_constraintTop_toTopOf="parent"
            android:elevation="0dp"
            />

    </com.google.android.material.appbar.AppBarLayout>


    <com.google.android.material.card.MaterialCardView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginTop="?attr/actionBarSize"
        app:cardCornerRadius="20dp"
        android:layout_marginBottom="-20dp"
        app:layout_constraintEnd_toEndOf="parent"

        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/appbar">

        <androidx.recyclerview.widget.RecyclerView
            android:id="@+id/contactview"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:layout_marginHorizontal="15dp"
            android:layout_margin="15dp"
            />
    </com.google.android.material.card.MaterialCardView>


</androidx.constraintlayout.widget.ConstraintLayout>
```

> 2.activity_send.xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<androidx.constraintlayout.widget.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context=".SendActivity"
    android:background="#3EB06A">
    <com.google.android.material.appbar.AppBarLayout
        android:id="@+id/appbar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        app:elevation="0dp"
        >

        <androidx.appcompat.widget.Toolbar
            android:id="@+id/toolbar"
            android:layout_width="match_parent"
            android:layout_height="?attr/actionBarSize"
            android:gravity="center"
            android:theme="@style/ThemeOverlay.AppCompat.Dark.ActionBar"
            app:layout_constraintTop_toTopOf="parent"
            android:elevation="0dp"
            />

    </com.google.android.material.appbar.AppBarLayout>


    <com.google.android.material.card.MaterialCardView
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:layout_marginTop="?attr/actionBarSize"
        app:cardCornerRadius="20dp"
        android:layout_marginBottom="-20dp"
        app:layout_constraintEnd_toEndOf="parent"

        app:layout_constraintStart_toStartOf="parent"
        app:layout_constraintTop_toBottomOf="@+id/appbar">

        <androidx.constraintlayout.widget.ConstraintLayout
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:layout_marginStart="16dp"
            android:layout_marginTop="16dp"
            android:layout_marginEnd="16dp"
            android:layout_marginBottom="8dp"
            >
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:textColor="#3EB06A"
                android:text="电话："
                android:textStyle="bold"
                android:textSize="20sp"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toTopOf="parent"
                android:id="@+id/phonetext"

                />
            <TextView
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@id/phonetext"
                android:id="@+id/phonenumbertext"/>


            <com.google.android.material.textfield.TextInputLayout
                android:id="@+id/textField"
                android:layout_width="match_parent"
                android:layout_height="wrap_content"

                android:minHeight="200dp"
                android:layout_marginTop="32dp"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintHorizontal_bias="0.333"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@id/phonenumbertext">

                <com.google.android.material.textfield.TextInputEditText
                    android:id="@+id/sms_edit"
                    android:layout_width="match_parent"
                    android:layout_height="wrap_content"
                    android:minHeight="200dp"/>

            </com.google.android.material.textfield.TextInputLayout>

            <ImageView
                android:layout_width="279dp"
                android:layout_height="267dp"
                android:background="@drawable/undraw_new_message_re_fp03"
                app:layout_constraintBottom_toTopOf="@+id/sendbutton"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                app:layout_constraintTop_toBottomOf="@+id/textField"
                app:layout_constraintVertical_bias="0.952" />

            <com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
                android:layout_width="wrap_content"
                android:layout_height="wrap_content"
                android:layout_marginBottom="30dp"
                android:backgroundTint="#3EB06A"
                android:text="发送短信"
                android:textColor="#ffffff"
                app:icon="@drawable/ic_baseline_message_24"
                app:iconTint="#ffffff"
                app:layout_constraintBottom_toBottomOf="parent"
                app:layout_constraintEnd_toEndOf="parent"
                app:layout_constraintStart_toStartOf="parent"
                android:id="@+id/sendbutton"
                />

        </androidx.constraintlayout.widget.ConstraintLayout>
    </com.google.android.material.card.MaterialCardView>
</androidx.constraintlayout.widget.ConstraintLayout>
```

## 五、软件操作流程

这么简单就懒得画了。。。。

## 六、难点和解决方案

#### 1.长按调用问题

解决方案：和短按`setOnClickListener`不一样的地方在于，长按`setOnLongClickListener`需要返回一个boolen参数，即返回一个`true`，在Java里直接return true就好,但是在kotlin里，默认用的是lambda表达式，最后一行语句是返回值，所以直接`true`就好

```kotlin
viewHolder.itemView.setOnLongClickListener {
            val position = viewHolder.adapterPosition
            val contact = contactlist[position]
            val intent = Intent(parent.context,SendActivity::class.java)
            intent.putExtra("number",contact.number)
            intent.putExtra("name",contact.name)
            parent.context.startActivity(intent)
            true
        }
```

#### 2.随机头像

解决方案：直接用了上次日记的素材了（kotlin的代码写起来真简洁）

```kotlin
private fun getavadar()= when ((1..5).random()) {
            1 -> R.drawable.mood_1
            2 -> R.drawable.mood_2
            3 -> R.drawable.mood_3
            4 -> R.drawable.mood_4
            5 -> R.drawable.mood_5

            else -> {R.drawable.mood_1}
    }
```

#### 3.隐藏导航栏问题

解决方案：好像直接隐藏有导航栏的手机会导致界面无法点击，所以加一个`BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE`就好

```kotlin
val insetsController = WindowCompat.getInsetsController(
            window, window.decorView
        )
        insetsController?.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        insetsController?.hide(WindowInsetsCompat.Type.navigationBars())
```

## 七、不足之处

功能简陋啦，还有很多可以做，比如增删改联系人，通话记录，短信读取什么的

## 八、今后设想

可以丰富一些功能