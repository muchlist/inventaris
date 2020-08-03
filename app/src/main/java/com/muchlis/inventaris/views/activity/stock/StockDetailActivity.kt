package com.muchlis.inventaris.views.activity.stock

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager
import com.muchlis.inventaris.databinding.ActivityStockDetailBinding
import com.muchlis.inventaris.utils.INTENT_STOCK_TO_DETAIL
import com.muchlis.inventaris.view_model.stock.StockDetailViewModel
import com.muchlis.inventaris.view_pager_adapter.StockPagerAdapter

class StockDetailActivity : AppCompatActivity() {

    private lateinit var viewModel: StockDetailViewModel
    private lateinit var bd: ActivityStockDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityStockDetailBinding.inflate(layoutInflater)
        val view = bd.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(StockDetailViewModel::class.java)

        //MENERIMA DATA DARI intent dan mengirim ke viewModel
        val stockID = intent.getStringExtra(
            INTENT_STOCK_TO_DETAIL
        )
        stockID?.let {
            viewModel.setStockId(it)
        }

        initPager()

    }

    private fun initPager() {
        bd.viewPagerMain.adapter = StockPagerAdapter(supportFragmentManager)
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