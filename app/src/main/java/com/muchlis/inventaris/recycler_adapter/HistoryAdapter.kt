package com.muchlis.inventaris.recycler_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.response.HistoryResponse
import kotlinx.android.synthetic.main.item_history.view.*

class HistoryAdapter(
    private val context: Context?,
    private val itemPrint: List<HistoryResponse>,
    private val itemClick: (HistoryResponse) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_history, parent, false)
        return ViewHolder(
            view,
            itemClick
        )
    }

    override fun getItemCount(): Int = itemPrint.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(itemPrint[position])
    }


    class ViewHolder(view: View, val itemClick: (HistoryResponse) -> Unit) :
        RecyclerView.ViewHolder(view) {
        fun bindItem(items: HistoryResponse) {

            itemView.apply {
                tv_history_author.text = items.author
                tv_history_branch.text = items.branch
                tv_history_date.text = items.date
                tv_history_name.text = items.parentName
                tv_history_note.text = items.note
                val categoryStatus = items.category + " - " + items.status
                tv_history_status.text = categoryStatus

                //onClick
                itemView.setOnClickListener { itemClick(items) }
            }
        }
    }
}