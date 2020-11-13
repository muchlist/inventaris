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
                _cctvData.postValue(it)
                _isLoading.value = false
            }
        }
    }

    fun getCctvDataFiltered(): CctvListResponse? {
        _cctvData.value?.let { cctv ->
            val filtered = cctv.cctvs.filter {
                it.caseSize > 0
            }
            return CctvListResponse(cctvs = filtered)
        }
        return null
    }

    fun getInfoBarData(): List<InfoBarData> {
        val dataAll = _cctvData.value
        val dataFiltered = getCctvDataFiltered()
        val dataInfoBar = mutableListOf<InfoBarData>()
        val dataAllMap = mutableMapOf<String, Int>()
        val dataProblemMap = mutableMapOf<String, Int>()

        dataAll?.let {
            for (cctv in it.cctvs) {
                if (dataAllMap.containsKey(cctv.location)) {
                    val tempValue = dataAllMap[cctv.location] ?: 0
                    dataAllMap[cctv.location] = tempValue + 1
                } else {
                    dataAllMap[cctv.location] = 1
                }
            }
        }
        dataFiltered?.let {
            for (cctv in it.cctvs) {
                if (dataProblemMap.containsKey(cctv.location)) {
                    val tempValue = dataProblemMap[cctv.location] ?: 0
                    dataProblemMap[cctv.location] = tempValue + 1
                } else {
                    dataProblemMap[cctv.location] = 1
                }
            }
        }

        for (key in dataAllMap.keys) {
            val name = key.split(" #")
            dataInfoBar.add(
                InfoBarData(
                    name = name[0],
                    total = dataAllMap[key] ?: 0,
                    problem = dataProblemMap[key] ?: 0
                )
            )
        }

        return dataInfoBar
    }

}