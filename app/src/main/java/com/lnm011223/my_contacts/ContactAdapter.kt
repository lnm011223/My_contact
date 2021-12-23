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