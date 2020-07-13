package com.muchlis.inventaris.views.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.muchlis.inventaris.R
import com.muchlis.inventaris.databinding.ActivityComputerDetailBinding
import com.muchlis.inventaris.view_pager_adapter.ComputerPagerAdapter

class ComputerDetailActivity : AppCompatActivity() {

    private lateinit var bd: ActivityComputerDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //bd = DataBindingUtil.setContentView(this, R.layout.activity_computer_detail)
        bd = ActivityComputerDetailBinding.inflate(layoutInflater)
        val view = bd.root
        setContentView(view)

        initPager()

    }

    private fun initPager() {
        bd.viewPagerMain.adapter = ComputerPagerAdapter(supportFragmentManager)
        bd.viewPagerMain.offscreenPageLimit = 2
        bd.viewPagerMain.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {
            }

            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
            }
        })

        bd.mainTab.setupWithViewPager(bd.viewPagerMain)
//        bd.mainTab.getTabAt(1)?.setIcon(R.drawable.ic_arrow)
//        bd.mainTab.getTabAt(2)?.setIcon(R.drawable.ic_arrow)
    }
}