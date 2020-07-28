package com.muchlis.inventaris.recycler_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.response.StockListResponse
import com.muchlis.inventaris.utils.invisible
import com.muchlis.inventaris.utils.visible
import kotlinx.android.synthetic.main.item_stock.view.*

class StockAdapter (
    private val context: Context?,
    private val itemList: List<StockListResponse.Stock>,
    private val itemClick: (StockListResponse.Stock) -> Unit
) : RecyclerView.Adapter<StockAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_stock, parent, false)
        return ViewHolder(
            view,
            itemClick
        )
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(itemList[position])
    }


    class ViewHolder(view: View, val itemClick: (StockListResponse.Stock) -> Unit) :
        RecyclerView.ViewHolder(view) {
        fun bindItem(items: StockListResponse.Stock) {

            itemView.apply {

                tv_stock_location.text = items.branch
                tv_stock_category.text = items.category
                tv_stock_name.text = items.stockName
                tv_stock_sisa_value.text = items.qty.toString() + " " + items.unit
                tv_stock_note.text = items.location

                if (items.qty - items.threshold <= 0.0){
                    iv_stock_indicator.visible()
                } else {
                    iv_stock_indicator.invisible()
                }

                //onClick
                itemView.setOnClickListener { itemClick(items) }
            }
        }
    }
}