package com.muchlis.inventaris.recycler_adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.muchlis.inventaris.R
import com.muchlis.inventaris.data.MenuData

class DashboardMenuAdapter (
    private var c: Context?,
    private var listMenuData: List<MenuData>,
    private var itemClick: (MenuData) -> Unit
) : BaseAdapter() {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        if (view == null) {
            view = LayoutInflater.from(c).inflate(R.layout.item_grid_menu_dashboard, parent, false)
        }

        val menu = this.getItem(position) as MenuData

        val menuImage = view?.findViewById(R.id.iv_menu_dashboard) as ImageView
        val menuTitle = view.findViewById(R.id.tv_title_dashboard) as TextView

        menuTitle.text = menu.title
        menuImage.setImageResource(menu.image)
        view.setOnClickListener {
            itemClick(menu)
        }

        return view
    }

    override fun getItem(position: Int): Any {
        return listMenuData[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return listMenuData.size
    }

}