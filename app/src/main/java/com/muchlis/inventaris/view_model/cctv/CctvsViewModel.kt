package com.muchlis.inventaris.view_model.cctv

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.muchlis.inventaris.data.dto.FindCctvDto
import com.muchlis.inventaris.data.dto.InfoBarData
import com.muchlis.inventaris.data.response.CctvListResponse
import com.muchlis.inventaris.repository.CctvRepo

class CctvsViewModel : ViewModel() {

    //Data untuk RecyclerView
    private val _cctvData: MutableLiveData<CctvListResponse> = MutableLiveData()
    fun getCctvData(): MutableLiveData<CctvListResponse> {
        return _cctvData
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean>
        get() = _isLoading

    private val _messageError = MutableLiveData<String>()
    val messageError: LiveData<String>
        get() = _messageError

    private val _messageSuccess = MutableLiveData<String>()
    val messageSuccess: LiveData<String>
        get() = _messageSuccess

    init {
        _isLoading.value = false
        _messageError.value = ""
    }


    fun findCctvFromServer(data: FindCctvDto) {
        _isLoading.value = true
        _messageError.value = ""

        CctvRepo.findCctvs(data) { response, error ->
            if (error.isNotEmpty()) {
                _isLoading.value = false
                _messageError.value = error
                return@findCctvs
            }
            response.let {
                _isLoading.value = false
                _cctvData.postValue(sortCCTV(it))
                _isLoading.value = false
            }
        }
    }

    // Data from database already sorted but we must pin cctv down without check to top of the list
    private fun sortCCTV(data: CctvListResponse?): CctvListResponse? {
        data?.let {
            if (data.cctvs.size > 0) {
                val cctvList = data.cctvs
                val pinnedCCTV = mutableListOf<CctvListResponse.Cctv>()
                val indexToRemove = mutableListOf<Int>()

                cctvList.forEachIndexed { index, cctv ->
                    val cctvDown = cctv.lastPing == "DOWN"
                    val cctvHaveNotBeenCheck = cctv.caseSize == 0
                    if (cctvDown && cctvHaveNotBeenCheck) {
                        pinnedCCTV.add(cctv)
                        indexToRemove.add(index)
                    }
                }

                for (i in indexToRemove.reversed()) {
                    cctvList.removeAt(i)
                }

                cctvList.addAll(0, pinnedCCTV)
                return CctvListResponse(cctvs = cctvList)
            }
        }

        return null

    }

    fun getCctvDataFiltered(): CctvListResponse? {
        _cctvData.value?.let { cctv ->
            val filtered = cctv.cctvs.filter {
                it.caseSize > 0
            }.toMutableList()
            return CctvListResponse(cctvs = filtered)
        }
        return null
    }

    fun getInfoBarData(): List<InfoBarData> {
        //MAYBE SLOW , IF IS, CHANGE SEQUENCE TO COROUTINE
        val dataInfoBarMap = mutableMapOf<String, InfoBarData>()
        val dataAll = _cctvData.value
        dataAll?.let {
            for (cctv in it.cctvs) {

                if (dataInfoBarMap.containsKey(cctv.location)) {
                    val tempData: InfoBarData? = dataInfoBarMap[cctv.location]
                    tempData?.let {
                        if (cctv.caseSize > 0) {
                            var tempProblem = tempData.problem
                            tempProblem += 1
                            tempData.problem = tempProblem
                        }

                        if (cctv.lastPing == "DOWN") {
                            var tempDown = tempData.down
                            tempDown += 1
                            tempData.down = tempDown
                        }

                        var tempTotal = tempData.total
                        tempTotal += 1
                        tempData.total = tempTotal
                    }
                    dataInfoBarMap[cctv.location] = tempData as InfoBarData
                } else {
                    dataInfoBarMap[cctv.location] = InfoBarData(
                        name = cctv.location.split(" #")[0],
                        total = 1,
                        problem = if (cctv.caseSize > 0) 1 else 0,
                        down = if (cctv.lastPing == "DOWN") 1 else 0,
                    )
                }
            }
        }

        val returnInfoBar = mutableListOf<InfoBarData>()
        for (key in dataInfoBarMap.keys) {
            dataInfoBarMap[key]?.let {
                returnInfoBar.add(it)
            }
        }

        return returnInfoBar
    }

}