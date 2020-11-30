package com.muchlis.inventaris.recycler_adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.response.HistoryAppsListResponse
import com.muchlis.inventaris.utils.*
import kotlinx.android.synthetic.main.item_apps_history.view.*

class AppHistoryAdapter(
    private val context: Context?,
    private val itemList: List<HistoryAppsListResponse.History>,
    private val onClick: (HistoryAppsListResponse.History) -> Unit,
    private val onLongClick: (HistoryAppsListResponse.History) -> Unit
) : RecyclerView.Adapter<AppHistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.item_apps_history, parent, false)
        return ViewHolder(
            view,
            onClick,
            onLongClick
        )
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(itemList[position])
    }


    class ViewHolder(
        view: View,
        val onClick: (HistoryAppsListResponse.History) -> Unit,
        val onLongClick: (HistoryAppsListResponse.History) -> Unit,
    ) :
        RecyclerView.ViewHolder(view) {

        @SuppressLint("SetTextI18n")
        fun bindItem(items: HistoryAppsListResponse.History) {

            itemView.apply {
                tv_app_history_name.text = items.parentName
                tv_app_history_author.text = items.author
                tv_app_history_branch.text = items.branch
                tv_app_history_date.text = items.startDate.toDate().toStringDateForView()
                tv_app_history_status.text = items.status
                tv_app_history_desc.text = items.desc
                tv_app_history_resolve.text = items.resolveNote

                if (items.durationMinute == 0) {
                    tv_app_history_minute.invisible()
                } else {
                    tv_app_history_minute.visible()
                    tv_app_history_minute.text = TranslateMinuteToHour(items.durationMinute).getStringHour()
                }


                if (items.isComplete) {
                    tv_app_history_complete.text = "Complete"
                    tv_app_history_complete.setBackgroundResource(R.drawable.text_rounded_cctv_up)
                } else {
                    tv_app_history_complete.text = "Progress"
                    tv_app_history_complete.setBackgroundResource(R.drawable.text_rounded_cctv_down)
                }


                //onClick
                itemView.setOnClickListener {
                    onClick(items)
                }
                itemView.setOnLongClickListener {
                    onLongClick(items)
                    true
                }
            }
        }
    }
}