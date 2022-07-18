package com.example.rehberim

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class RehberAdapter(var userList: ArrayList<DataClass>) :
    RecyclerView.Adapter<RehberAdapter.RehberTasarim>() {

    fun setData(userList: ArrayList<DataClass>) {
        this.userList = userList
        notifyDataSetChanged()
    }

    class RehberTasarim(view: View) : RecyclerView.ViewHolder(view) {
        val nameText = view.findViewById<TextView>(R.id.textName)
        val surnameText = view.findViewById<TextView>(R.id.textSurname)
        val numberText = view.findViewById<TextView>(R.id.textNumber)

        fun bindItems(item: DataClass) {
            nameText.text = item.name
            surnameText.text = item.surName
            numberText.text = item.number

        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RehberTasarim {
        val binding = LayoutInflater.from(parent.context)
        val view = binding.inflate(R.layout.item, parent, false)

        return RehberTasarim(view)
    }

    override fun onBindViewHolder(holder: RehberTasarim, position: Int) {
        holder.bindItems(userList.get(position))
    }

    override fun getItemCount(): Int {
        return userList.size
    }

}