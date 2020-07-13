package com.muchlis.inventaris.recycler_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.response.ComputerListResponse
import com.muchlis.inventaris.data.response.HistoryResponse
import com.muchlis.inventaris.utils.toDate
import com.muchlis.inventaris.utils.toStringJustDate
import kotlinx.android.synthetic.main.item_computer.view.*

class ComputerAdapter (
    private val context: Context?,
    private val itemList: List<ComputerListResponse.Computer>,
    private val itemClick: (ComputerListResponse.Computer) -> Unit
) : RecyclerView.Adapter<ComputerAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_computer, parent, false)
        return ViewHolder(
            view,
            itemClick
        )
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(itemList[position])
    }


    class ViewHolder(view: View, val itemClick: (ComputerListResponse.Computer) -> Unit) :
        RecyclerView.ViewHolder(view) {
        fun bindItem(items: ComputerListResponse.Computer) {

            itemView.apply {
                tv_computerlist_branch.text = items.branch
                tv_computerlist_division.text = items.division
                tv_computerlist_ip.text = items.ipAddress
                tv_computerlist_name.text = items.clientName

                //onClick
                itemView.setOnClickListener { itemClick(items) }
            }
        }
    }
}