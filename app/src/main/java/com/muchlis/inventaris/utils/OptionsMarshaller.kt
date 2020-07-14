package com.muchlis.inventaris.utils

import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.muchlis.inventaris.data.response.SelectOptionResponse

class OptionsMarshaller {

    fun getOption(): SelectOptionResponse? {
        val json = App.prefs.optionsJson
        if (json.isNotEmpty()) {
            return try {
                val options = Gson().fromJson(json, SelectOptionResponse::class.java)
                options
            } catch (e: JsonParseException) {
                null
            }
        }
        return null
    }
}