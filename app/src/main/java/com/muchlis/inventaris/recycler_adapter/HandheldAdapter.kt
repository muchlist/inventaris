package com.muchlis.inventaris.recycler_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.response.ComputerListResponse
import com.muchlis.inventaris.data.response.HandheldListResponse
import com.muchlis.inventaris.utils.invisible
import com.muchlis.inventaris.utils.visible
import kotlinx.android.synthetic.main.item_computer.view.*
import kotlinx.android.synthetic.main.item_handheld.view.*

class HandheldAdapter(
    private val context: Context?,
    private val itemList: List<HandheldListResponse.Handheld>,
    private val itemClick: (HandheldListResponse.Handheld) -> Unit
) : RecyclerView.Adapter<HandheldAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_handheld, parent, false)
        return ViewHolder(
            view,
            itemClick
        )
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(itemList[position])
    }


    class ViewHolder(view: View, val itemClick: (HandheldListResponse.Handheld) -> Unit) :
        RecyclerView.ViewHolder(view) {
        fun bindItem(items: HandheldListResponse.Handheld) {

            itemView.apply {
                tv_handheldlist_location.text = items.location
                tv_handheldlist_ip.text = items.ipAddress
                tv_handheldlist_name.text = items.handheldName

                //onClick
                itemView.setOnClickListener { itemClick(items) }
            }
        }
    }
}