package com.muchlis.inventaris.recycler_adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.response.CctvListResponse
import kotlinx.android.synthetic.main.item_cctv.view.*

class CctvAdapter(
    private val context: Context?,
    private val itemList: List<CctvListResponse.Cctv>,
    private val itemClick: (CctvListResponse.Cctv) -> Unit
) : RecyclerView.Adapter<CctvAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_cctv, parent, false)
        return ViewHolder(
            view,
            itemClick
        )
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(itemList[position])
    }


    class ViewHolder(view: View, val itemClick: (CctvListResponse.Cctv) -> Unit) :
        RecyclerView.ViewHolder(view) {
        @SuppressLint("SetTextI18n")
        fun bindItem(items: CctvListResponse.Cctv) {

            itemView.apply {

                tv_cctvlist_name.text = items.cctvName
                tv_cctvlist_ip.text = items.ipAddress
                tv_cctvlist_location.text = items.location
                tv_cctvlist_condition.text = "${items.pingSum} %"

                if (items.lastPing == "UP") {
                    tv_cctvlist_condition.setBackgroundResource(R.drawable.text_rounded_cctv_up)
                } else {
                    if (items.pingSum == 0) {
                        tv_cctvlist_condition.setBackgroundResource(R.drawable.text_rounded_cctv_down)
                    } else {
                        tv_cctvlist_condition.setBackgroundResource(R.drawable.text_rounded_cctv_mid)
                    }
                }

                //Last status text
                var lastStatus = ""
                if (items.case.count() != 0){
                    for (case in items.case){
                        lastStatus = lastStatus + "* " + case.caseNote + "\n"
                    }
                }

                tv_cctvlist_last_status.text = lastStatus

                //onClick
                itemView.setOnClickListener { itemClick(items) }
            }
        }
    }
}