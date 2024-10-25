package com.se7en.market.Repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.se7en.market.Utils.PreferencesManager

class PreferencesRepository(context: Context) {

    private val preferencesManager = PreferencesManager.getInstance(context)
    private val phoneNumber: MutableLiveData<String> = MutableLiveData()

    fun getPhoneNumber(): LiveData<String> {
        phoneNumber.postValue(preferencesManager.getString("phone"))
        return phoneNumber
    }
}