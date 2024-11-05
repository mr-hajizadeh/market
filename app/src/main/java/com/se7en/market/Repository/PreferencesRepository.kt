package com.se7en.market.Repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.se7en.market.Model.LoginInfo
import com.se7en.market.Utils.Constance.Companion.KEY_PHONE
import com.se7en.market.Utils.Constance.Companion.KEY_TOKEN
import com.se7en.market.Utils.PreferencesManager

class PreferencesRepository(context: Context) {

    private val preferencesManager = PreferencesManager.getInstance(context)
    private val phoneNumber: MutableLiveData<LoginInfo> = MutableLiveData()

    fun getLoginInfo(): LiveData<LoginInfo> {
        val phone = preferencesManager.getString(KEY_PHONE) ?: ""
        val token = preferencesManager.getString(KEY_TOKEN) ?: ""
        phoneNumber.postValue(LoginInfo(phone, token))
        return phoneNumber
    }

    fun saveLoginInfo(loginInfo: LoginInfo) {
        preferencesManager.saveString(KEY_PHONE, loginInfo.phone)
        preferencesManager.saveString(KEY_TOKEN, loginInfo.toke)
    }
}