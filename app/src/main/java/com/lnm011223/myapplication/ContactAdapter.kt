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