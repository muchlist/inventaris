package com.muchlis.inventaris.recycler_adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.response.CheckListResponse
import com.muchlis.inventaris.utils.*
import kotlinx.android.synthetic.main.item_check.view.*
import java.util.*

class CheckAdapter(
    private val context: Context?,
    private val itemList: List<CheckListResponse.Check>,
    private val itemClick: (CheckListResponse.Check) -> Unit,
    private val itemLongClick: (CheckListResponse.Check) -> Unit,
) : RecyclerView.Adapter<CheckAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_check, parent, false)
        return ViewHolder(
            view,
            itemClick,
            itemLongClick,
        )
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(itemList[position])
    }


    class ViewHolder(
        view: View,
        val itemClick: (CheckListResponse.Check) -> Unit,
        val itemLongClick: (CheckListResponse.Check) -> Unit
    ) :
        RecyclerView.ViewHolder(view) {

        private var dateTimeNow = Calendar.getInstance()

        @SuppressLint("SetTextI18n")
        fun bindItem(items: CheckListResponse.Check) {

            itemView.apply {
                this.tv_check_name.text = items.createdBy
                this.tv_check_shift.text = "SHIFT ${items.shift}"
                this.tv_check_date.text = items.createdAt.toDate().toStringDateForView()

                if (items.isFinish) {
                    tv_check_complete.text = "Complete"
                    tv_check_complete.setBackgroundResource(R.drawable.text_rounded_cctv_up)
                } else {
                    tv_check_complete.text = "Progress"
                    tv_check_complete.setBackgroundResource(R.drawable.text_rounded_cctv_down)
                }
            }

            if (dateTimeNow.time.toStringddMMMyyyy() == items.createdAt.toDate()
                    .toStringddMMMyyyy()
            ) {
                itemView.tv_check_today.visible()
            } else {
                itemView.tv_check_today.invisible()
            }

            //onClick
            itemView.setOnClickListener {
                itemClick(items)
            }
            itemView.setOnLongClickListener {
                itemLongClick(items)
                true
            }
        }
    }
}