import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.google.gson.Gson
import com.se7en.market.Model.AddressModel
import com.se7en.market.Utils.Constance.Companion.KEY_ACTIVE_ADDRESS

class PreferencesManager private constructor(context: Context) {

    private val sharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_prefs",
        MasterKey.Builder(context).setKeyScheme(MasterKey.KeyScheme.AES256_GCM).build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    private val gson = Gson()

    private val _activeAddress = MutableLiveData<AddressModel?>()
    val activeAddress: LiveData<AddressModel?> = _activeAddress

    companion object {
        @Volatile
        private var instance: PreferencesManager? = null

        fun getInstance(context: Context): PreferencesManager {
            return instance ?: synchronized(this) {
                instance ?: PreferencesManager(context).also { instance = it }
            }
        }
    }

    init {
        loadActiveAddress()
    }

    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String) = sharedPreferences.getString(key, null)

    fun saveAddress(key: String, address: AddressModel) {
        val addressJson = gson.toJson(address)
        sharedPreferences.edit().putString(key, addressJson).apply()
        _activeAddress.value = address
    }

    private fun loadActiveAddress() {
        val addressJson = sharedPreferences.getString(KEY_ACTIVE_ADDRESS, null)
        _activeAddress.value = addressJson?.let { gson.fromJson(it, AddressModel::class.java) }
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
        _activeAddress.value = null
    }
}
