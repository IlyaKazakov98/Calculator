package com.readyfo.calculator

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.list_item_view.view.*

class Adapter(private val value: MutableList<String>): RecyclerView.Adapter<Adapter.ViewHolder>() {

    override fun getItemCount() = value.size

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_view, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView?.text = value[position]
    }
    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var textView : TextView? = null
        init {
            textView = itemView.text_list_view
        }

    }
}