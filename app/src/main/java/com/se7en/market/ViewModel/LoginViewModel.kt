package com.se7en.market.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.se7en.market.Model.LoginInfoModel
import com.se7en.market.Network.NetworkResult
import com.se7en.market.Repository.LoginRepository
import com.se7en.market.Repository.PreferencesRepository
import com.se7en.market.Response.OTP.Code
import com.se7en.market.Response.OTP.Confirm
import com.se7en.market.Response.OTP.Token
import com.se7en.market.Utils.Constance.Companion.KEY_CODE
import com.se7en.market.Utils.Constance.Companion.KEY_PHONE
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginViewModel(application: Application) : AndroidViewModel(application) {

    private val _tokenStatus = MutableLiveData<NetworkResult<Token>>()
    val tokenStatus get() = _tokenStatus
    private val _codeStatus = MutableLiveData<NetworkResult<Code>>()
    val codeStatus get() = _codeStatus
    private val _confirmStatus = MutableLiveData<NetworkResult<Confirm>>()
    val confirmStatus get() = _confirmStatus

    private val preferencesRepository = PreferencesRepository.getInstance(application)
    private var repository = LoginRepository()

    fun sendPhone(phone: String) {
        viewModelScope.launch {
            _tokenStatus.value = NetworkResult.Loading
            repository.sendPhone(mapOf(KEY_PHONE to phone)).enqueue(object : Callback<Token> {
                override fun onResponse(call: Call<Token>, response: Response<Token>) {
                    if (response.isSuccessful && response.body()?.status.equals("success"))
                        _tokenStatus.value = NetworkResult.Success(response.body()!!)
                    else
                        _tokenStatus.value = response.body()
                            ?.let { NetworkResult.Error(it.system_message) }
                }

                override fun onFailure(call: Call<Token>, t: Throwable) {
                    _tokenStatus.value = NetworkResult.Error("Network error: ${t.message}")
                }
            })
        }
    }

    fun otpCode(token: String) {
        viewModelScope.launch {
            _codeStatus.value = NetworkResult.Loading
            repository.otpCode(token).enqueue(object : Callback<Code> {
                override fun onResponse(call: Call<Code>, response: Response<Code>) {
                    if (response.isSuccessful && response.body()!!.status == "success")
                        _codeStatus.value = NetworkResult.Success(response.body()!!)
                    else
                        _codeStatus.value = response.body()?.errors?.get(0)
                            ?.let { NetworkResult.Error(it.toString()) }

                }

                override fun onFailure(call: Call<Code>, t: Throwable) {
                    _codeStatus.value = NetworkResult.Error("Error ${t.message}")
                }
            })

        }

    }

    fun confirmCode(token: String, code: String) {
        viewModelScope.launch {
            _confirmStatus.value = NetworkResult.Loading
            repository.confirmCode(token, mapOf(KEY_CODE to code))
                .enqueue(object : Callback<Confirm> {
                    override fun onResponse(call: Call<Confirm>, response: Response<Confirm>) {
                        if (response.isSuccessful && response.body()?.status == "success")
                            _confirmStatus.value = NetworkResult.Success(response.body()!!)
                        else
                            _confirmStatus.value =
                                NetworkResult.Error(response.body()?.errors?.get(0)!!.toString())
                    }

                    override fun onFailure(call: Call<Confirm>, t: Throwable) {
                        _confirmStatus.value = NetworkResult.Error("Error ${t.message}")
                    }
                })
        }
    }

    fun saveLoginInfo(loginInfoModel: LoginInfoModel) =
        preferencesRepository.saveLoginInfo(loginInfoModel)

    fun getLoginInfo(): LiveData<LoginInfoModel> = preferencesRepository.getLoginInfo()

}