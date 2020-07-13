package com.muchlis.inventaris.utils

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.muchlis.inventaris.data.response.SelectOptionResponse

class OptionsMarshaller {

    fun getOption(): SelectOptionResponse? {
        var options = SelectOptionResponse(
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            listOf(),
            0
        )
        val json = App.prefs.optionsJson
        if (json.isNotEmpty()) {
            try {
                options = Gson().fromJson(json, SelectOptionResponse::class.java)
            } catch (e: JsonParseException) {
                return null
            }
        }
        return options
    }
}