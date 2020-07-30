package com.muchlis.inventaris.recycler_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.response.HistoryResponse
import com.muchlis.inventaris.utils.toDate
import com.muchlis.inventaris.utils.toStringJustDate
import kotlinx.android.synthetic.main.item_history.view.*

class HistoryAdapter(
    private val context: Context?,
    private val itemList: List<HistoryResponse>,
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

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(itemList[position])
    }


    class ViewHolder(view: View, val itemClick: (HistoryResponse) -> Unit) :
        RecyclerView.ViewHolder(view) {
        fun bindItem(items: HistoryResponse) {

            itemView.apply {
                tv_history_author.text = items.author
                tv_history_branch.text = items.branch
                tv_history_date.text = items.date.toDate().toStringJustDate()
                tv_history_name.text = items.parentName
                tv_history_note.text = items.note
                val categoryStatus = items.status
                tv_history_status.text = categoryStatus

                iv_circle_history.setImageResource(getImageResourceFromCategory(items.category))

                //onClick
                itemView.setOnClickListener {}
                itemView.setOnLongClickListener {
                    itemClick(items)
                    true
                }
            }
        }

        private fun getImageResourceFromCategory(category: String): Int{
            return when (category){
                "PC" -> R.drawable.ic_029_computer
                "Printer" -> R.drawable.ic_041_printer
                "Server" -> R.drawable.ic_047_server
                "CCTV" -> R.drawable.ic_018_cctv
                "STOCK" -> R.drawable.ic_049_stock
                else -> R.drawable.ic_029_computer
            }
        }
    }
}