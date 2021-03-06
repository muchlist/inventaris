package com.muchlis.inventaris.view_pager_adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.muchlis.inventaris.views.fragment.CctvDetailFragment
import com.muchlis.inventaris.views.fragment.CctvHistoryFragment

class CctvPagerAdapter(fm: FragmentManager) :
    FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val pages = listOf(
        CctvDetailFragment(),
        CctvHistoryFragment()
    )

    override fun getItem(position: Int): Fragment {
        return pages[position]
    }

    override fun getCount(): Int {
        return pages.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Detail"
            else -> "Riwayat"
        }
    }


}