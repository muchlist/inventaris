package com.muchlis.inventaris.recycler_adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.response.HistoryResponse
import com.muchlis.inventaris.utils.*
import kotlinx.android.synthetic.main.item_history.view.*
import java.util.*

class HistoryAdapter(
    private val context: Context?,
    private val itemList: List<HistoryResponse>,
    private val itemClick: (HistoryResponse) -> Unit,
    private val itemLongClick: (HistoryResponse) -> Unit,
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_history, parent, false)
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
        val itemClick: (HistoryResponse) -> Unit,
        val itemLongClick: (HistoryResponse) -> Unit
    ) :
        RecyclerView.ViewHolder(view) {

        private var dateTimeNow = Calendar.getInstance()

        @SuppressLint("SetTextI18n")
        fun bindItem(items: HistoryResponse) {

            itemView.apply {
                tv_history_name.text = items.parentName

                //OLD: author - NEW: updatedby  because api has change
                val updatedBy: String =
                    if (items.author == items.updatedBy) items.author else "Open: ${items.author}\nClose: ${items.updatedBy}"

                tv_history_author.text = updatedBy
                tv_history_branch.text = items.branch
                tv_history_date.text = items.date.toDate().toStringDateForView()
                tv_history_status.text = items.status
                tv_history_note.text = items.note
                tv_history_resolve.text = items.resolveNote

                if (items.durationMinute == 0) {
                    tv_history_minute.invisible()
                } else {
                    tv_history_minute.visible()
                    tv_history_minute.text = TranslateMinuteToHour(items.durationMinute).getStringHour()
                }

                iv_circle_history.setImageResource(getImageResourceFromCategory(items.category))

                when (items.completeStatus) {
                    2 -> {
                        tv_history_complete.text = "Complete"
                        tv_history_complete.setBackgroundResource(R.drawable.text_rounded_cctv_up)
                    }
                    1 -> {
                        tv_history_complete.text = "Pending"
                        tv_history_complete.setBackgroundResource(R.drawable.text_rounded_cctv_mid)
                    }
                    else -> {
                        tv_history_complete.text = "Progress"
                        tv_history_complete.setBackgroundResource(R.drawable.text_rounded_cctv_down)
                    }
                }

                if (dateTimeNow.time.toStringddMMMyyyy() == items.date.toDate()
                        .toStringddMMMyyyy()
                ) {
                    tv_history_today.visible()
                } else {
                    tv_history_today.invisible()
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

        private fun getImageResourceFromCategory(category: String): Int {
            return when (category) {
                CATEGORY_PC -> R.drawable.ic_029_computer
                CATEGORY_CCTV -> R.drawable.ic_018_cctv
                CATEGORY_STOCK -> R.drawable.ic_049_stock
                CATEGORY_DAILY -> R.drawable.ic_023_poster
                CATEGORY_APPLICATION -> R.drawable.ic_032_cd
                CATEGORY_TABLET -> R.drawable.ic_043_ipad
                else -> R.drawable.ic_029_computer
            }
        }
    }
}