package com.muchlis.inventaris.utils

import android.content.Context
import android.content.SharedPreferences

class SharedPrefs(context: Context) {
    private val _prefsFileName = "prefs"

    //URL
    private val _baseUrl = "baseUrl"

    //TOKEN
    private val _authToken = "authTokenSave"

    //DATA USER DIMUAT KETIKA LOGIN
    private val _name = "nameAccount"
    private val _userBranch = "userBranch"
    private val _isAdmin = "isAdmin"
    private val _isTally = "isTally"
    private val _optionsJsonVersion = "dropdown_options_json_version"
    private val _optionsJson = "dropdown_options_json"

    private val prefs: SharedPreferences = context.getSharedPreferences(_prefsFileName, 0)

    var baseUrl: String
        get() = prefs.getString(_baseUrl, INTERNET_SERVER) ?: INTERNET_SERVER
        set(value) = prefs.edit().putString(_baseUrl, value).apply()

    var authTokenSave: String
        get() = prefs.getString(_authToken, "") ?: ""
        set(value) = prefs.edit().putString(_authToken, value).apply()

    var userBranchSave: String
        get() = prefs.getString(_userBranch, "") ?: ""
        set(value) = prefs.edit().putString(_userBranch, value).apply()

    var nameSave: String
        get() = prefs.getString(_name, "") ?: ""
        set(value) = prefs.edit().putString(_name, value).apply()

    var optionsJson: String
        get() = prefs.getString(_optionsJson, "") ?: ""
        set(value) = prefs.edit().putString(_optionsJson, value).apply()

    var optionsJsonVersion: Int
        get() = prefs.getInt(_optionsJsonVersion, 0) ?: 0
        set(value) = prefs.edit().putInt(_optionsJsonVersion, value).apply()

    var isAdmin: Boolean
        get() = prefs.getBoolean(_isAdmin, false)
        set(value) = prefs.edit().putBoolean(_isAdmin, value).apply()

    var isEndUser: Boolean
        get() = prefs.getBoolean(_isTally, false)
        set(value) = prefs.edit().putBoolean(_isTally, value).apply()
}