package com.muchlis.inventaris.recycler_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.response.StockDetailResponse
import com.muchlis.inventaris.utils.toDate
import com.muchlis.inventaris.utils.toStringJustDate
import kotlinx.android.synthetic.main.item_stock_consume.view.*

class StockIncrementDecrementAdapter(
    private val context: Context?,
    private val itemList: List<StockDetailResponse.IncrementDecrement>,
    private val itemClick: (StockDetailResponse.IncrementDecrement) -> Unit
) : RecyclerView.Adapter<StockIncrementDecrementAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_stock_consume, parent, false)
        return ViewHolder(
            view,
            itemClick
        )
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(itemList[position])
    }


    class ViewHolder(view: View, val itemClick: (StockDetailResponse.IncrementDecrement) -> Unit) :
        RecyclerView.ViewHolder(view) {
        fun bindItem(items: StockDetailResponse.IncrementDecrement) {

            itemView.apply {
                tv_stock_con_user.text = items.author
                tv_stock_con_date.text = items.time.toDate().toStringJustDate()
                tv_stock_con_nomer.text =
                    if (items.baNumber.isNotEmpty()) items.baNumber else items.dummyId.toString()
                tv_stock_con_note.text = items.note
                tv_stock_con_value.text = items.qty.toString()

                //onClick
                itemView.setOnClickListener {}
                itemView.setOnLongClickListener {
                    itemClick(items)
                    true
                }
            }
        }
    }
}