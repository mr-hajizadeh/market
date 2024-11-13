package com.se7en.market.Repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.se7en.market.Model.AddressModel
import com.se7en.market.Model.LoginInfoModel
import com.se7en.market.Utils.Constance.Companion.KEY_ACTIVE_ADDRESS
import com.se7en.market.Utils.Constance.Companion.KEY_PHONE
import com.se7en.market.Utils.Constance.Companion.KEY_TOKEN

class PreferencesRepository(context: Context) {

    private val preferencesManager = PreferencesManager.getInstance(context)
    private val loginInfo: MutableLiveData<LoginInfoModel> = MutableLiveData()

    companion object {
        @Volatile
        var instance: PreferencesRepository? = null
        fun getInstance(context: Context): PreferencesRepository {
            return instance ?: synchronized(this) {
                instance ?: PreferencesRepository(context).also { instance = it }
            }
        }
    }

    fun getLoginInfo(): LiveData<LoginInfoModel> {
        val phone = preferencesManager.getString(KEY_PHONE) ?: ""
        val token = preferencesManager.getString(KEY_TOKEN) ?: ""
        loginInfo.postValue(LoginInfoModel(phone, token))
        return loginInfo
    }

    fun getToken() = preferencesManager.getString(KEY_TOKEN) ?: ""

    fun saveLoginInfo(loginInfoModel: LoginInfoModel) {
        preferencesManager.saveString(KEY_PHONE, loginInfoModel.phone)
        preferencesManager.saveString(KEY_TOKEN, loginInfoModel.token)
    }

    fun saveActiveAddress(address: AddressModel) {
        preferencesManager.saveAddress(KEY_ACTIVE_ADDRESS, address)
    }

    fun loadActiveAddress() = preferencesManager.activeAddress

    fun logout() {
        preferencesManager.clear()
    }

}