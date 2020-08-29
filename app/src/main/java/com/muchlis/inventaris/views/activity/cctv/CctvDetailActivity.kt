package com.muchlis.inventaris.views.activity.cctv

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.muchlis.inventaris.databinding.ActivityCctvDetailBinding
import com.muchlis.inventaris.utils.INTENT_CCTV_TO_DETAIL
import com.muchlis.inventaris.view_model.cctv.CctvDetailViewModel
import com.muchlis.inventaris.view_pager_adapter.CctvPagerAdapter

class CctvDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: CctvDetailViewModel
    private lateinit var bd: ActivityCctvDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityCctvDetailBinding.inflate(layoutInflater)
        val view = bd.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(CctvDetailViewModel::class.java)

        //MENERIMA DATA DARI intent dan mengirim ke viewModel
        val cctvID = intent.getStringExtra(
            INTENT_CCTV_TO_DETAIL
        )
        cctvID?.let {
            viewModel.setCctvID(it)
        }

        initPager()

    }

    private fun initPager() {
        bd.viewPagerMain.adapter = CctvPagerAdapter(supportFragmentManager)
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
    }
}