package com.muchlis.inventaris.views.activity.handheld

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.muchlis.inventaris.databinding.ActivityHandheldDetailBinding
import com.muchlis.inventaris.utils.INTENT_HH_TO_DETAIL
import com.muchlis.inventaris.view_model.handheld.HandheldDetailViewModel
import com.muchlis.inventaris.view_pager_adapter.HandheldPagerAdapter

class HandheldDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: HandheldDetailViewModel
    private lateinit var bd: ActivityHandheldDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityHandheldDetailBinding.inflate(layoutInflater)
        val view = bd.root
        setContentView(view)

        //MENERIMA DATA DARI intent dan mengirim ke viewModel
        val handheldID = intent.getStringExtra(
            INTENT_HH_TO_DETAIL
        )

        viewModel = ViewModelProvider(this).get(HandheldDetailViewModel::class.java)

        handheldID?.let {
            viewModel.setHandheldId(it)
        }

        initPager()

    }

    private fun initPager() {
        bd.viewPagerMain.adapter = HandheldPagerAdapter(supportFragmentManager)
        bd.viewPagerMain.offscreenPageLimit = 1
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