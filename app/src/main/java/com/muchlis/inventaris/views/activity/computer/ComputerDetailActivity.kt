package com.muchlis.inventaris.views.activity.computer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.muchlis.inventaris.databinding.ActivityComputerDetailBinding
import com.muchlis.inventaris.utils.INTENT_PC_TO_DETAIL
import com.muchlis.inventaris.view_pager_adapter.ComputerPagerAdapter
import com.muchlis.inventaris.view_model.computer.ComputerDetailViewModel

class ComputerDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: ComputerDetailViewModel
    private lateinit var bd: ActivityComputerDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityComputerDetailBinding.inflate(layoutInflater)
        val view = bd.root
        setContentView(view)

        //MENERIMA DATA DARI intent dan mengirim ke viewModel
        val computerID = intent.getStringExtra(
            INTENT_PC_TO_DETAIL
        )

        viewModel = ViewModelProvider(this).get(ComputerDetailViewModel::class.java)

        computerID?.let {
            viewModel.setComputerId(it)
        }

        initPager()

    }

    private fun initPager() {
        bd.viewPagerMain.adapter = ComputerPagerAdapter(supportFragmentManager)
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