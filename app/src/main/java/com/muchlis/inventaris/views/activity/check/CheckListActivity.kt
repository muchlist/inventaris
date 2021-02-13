package com.muchlis.inventaris.views.activity.check

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.muchlis.inventaris.data.response.CheckListResponse
import com.muchlis.inventaris.databinding.ActivityCheckListBinding
import com.muchlis.inventaris.recycler_adapter.CheckAdapter
import com.muchlis.inventaris.utils.App
import com.muchlis.inventaris.utils.invisible
import com.muchlis.inventaris.utils.visible
import com.muchlis.inventaris.view_model.check.ChecksViewModel
import es.dmoral.toasty.Toasty

class CheckListActivity : AppCompatActivity() {

    private lateinit var bd: ActivityCheckListBinding
    private lateinit var viewModel: ChecksViewModel


    //recyclerview
    private lateinit var checkAdapter: CheckAdapter
    private var checkData = mutableListOf<CheckListResponse.Check>()

    //first time animation
    private var isFirstTimeLoad = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bd = ActivityCheckListBinding.inflate(layoutInflater)
        val view = bd.root
        setContentView(view)

        viewModel = ViewModelProvider(this).get(ChecksViewModel::class.java)

        observeViewModel()

        setRecyclerView()

        viewModel.findChecksFromServer()

        bd.refreshDetailCheck.setOnRefreshListener {
            viewModel.findChecksFromServer()
        }
    }

    private fun observeViewModel() {

        viewModel.run {
            getCheckData().observe(this@CheckListActivity, {
                loadRecyclerView(it)
            })
            isLoading.observe(this@CheckListActivity, { showLoading(it) })
            messageError.observe(this@CheckListActivity, { showErrorToast(it) })
        }
    }

    private fun setRecyclerView() {
        bd.rvDetailCheck.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val onClickItem: (CheckListResponse.Check) -> Unit = {
//            if (it.completeStatus != 2 && it.branch == App.prefs.userBranchSave) {
//                val intent = Intent(this, EditCheckActivity::class.java)
//                intent.putExtra(INTENT_TO_HISTORY_EDIT, it)
//                startActivity(intent)
//            }
            showErrorToast(it.id)
        }

        val onLongClickItem: (CheckListResponse.Check) -> Unit = {
//            if (it.category == CATEGORY_DAILY) {
//                //Khusus Daily perilakunya delete daily
//                if (it.author == App.prefs.nameSave) {
//                    deleteDailyCheck(it.id)
//                }
//            } else {
//                intentToDetailActivity(parentID = it.parentId, category = it.category)
//            }
            showErrorToast(it.createdBy)
        }

        checkAdapter = CheckAdapter(this, checkData, onClickItem, onLongClickItem)
        bd.rvDetailCheck.adapter = checkAdapter
        bd.rvDetailCheck.setHasFixedSize(true)
    }

    private fun loadRecyclerView(data: CheckListResponse) {
        checkData.clear()
        checkData.addAll(data.check)
        if (isFirstTimeLoad) {
            //Jika aktifity dimuat pertama kali akan melakukan animasi
            runLayoutAnimation()
            isFirstTimeLoad = false
        }
        checkAdapter.notifyDataSetChanged()

        //JIKA ITEMLIST KOSONG MUNCULKAN GAMBAR
        if (data.check.count() == 0) {
            bd.ivEmptyList.visible()
        } else {
            bd.ivEmptyList.invisible()
        }
    }


    private fun runLayoutAnimation() {
        bd.rvDetailCheck.scheduleLayoutAnimation()
        bd.rvDetailCheck.invalidate()
    }


    private fun showLoading(isLoading: Boolean) {
        bd.refreshDetailCheck.isRefreshing = isLoading
    }

    private fun showErrorToast(text: String) {
        if (text.isNotEmpty()) {
            Toasty.error(this, text, Toasty.LENGTH_LONG).show()
        }
    }

    override fun onResume() {
        super.onResume()
        if (App.activityCheckListMustBeRefresh) {
            viewModel.findChecksFromServer()
            App.activityCheckListMustBeRefresh = false
        }
    }
}