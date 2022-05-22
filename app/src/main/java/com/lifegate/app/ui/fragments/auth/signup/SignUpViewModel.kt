package com.lifegate.app.ui.fragments.auth.signup

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.CitiesApi
import com.lifegate.app.data.network.responses.CountriesApi
import com.lifegate.app.data.network.responses.SignUpApi
import com.lifegate.app.data.repositories.UserRepository
import com.lifegate.app.utils.*
import timber.log.Timber

class SignUpViewModel(
    private val repository: UserRepository
) : ViewModel() {

    val appContext by lazy {
        repository.appContext
    }

    var listener: NetworkListener? = null
    var errorMessage: String = ""

    var name : String? = null
    var email : String? = null
    var phone : String? = null
    var password : String? = null
    var confirmPass : String? = null

    var countryName: String? = null
    var countryId : String? = null
    var cityName : String? = null
    var cityId : String? = null

    var isSignUp: Boolean = true

    private val mutableCountry = MutableLiveData<MutableList<CountriesApi.CountriesData>>()
    private val mutableCity = MutableLiveData<MutableList<CitiesApi.CitiesData>>()

    val liveCountry : LiveData<MutableList<CountriesApi.CountriesData>>
        get() = mutableCountry
    val liveCity : LiveData<MutableList<CitiesApi.CitiesData>>
        get() = mutableCity

    fun signUp() {

        /*FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Timber.d(token)
                repository.saveFireBaseDeviceToken(token)
            }
        })*/

        val mName = name
        val mEmail = email
        val mPhone = phone
        val mPassword = password
        val mConfirmPass = confirmPass
        val city = cityId

        if (mEmail.isNullOrEmpty() || mName.isNullOrEmpty() ||
            mPhone.isNullOrEmpty() || mPassword.isNullOrEmpty() ||
            mConfirmPass.isNullOrEmpty()) {
            errorMessage = appContext.getLocaleStringResource(R.string.all_field_mandate)
            listener?.onFailure()
            return
        }

        if (!mEmail.isValidEmail()) {
            errorMessage = appContext.getLocaleStringResource(R.string.enter_valid_email)
            listener?.onFailure()
            return
        }

        if (mPhone.length < 8) {
            errorMessage = appContext.getLocaleStringResource(R.string.enter_valid_phone)
            listener?.onFailure()
            return
        }

        if (mPassword.length < 6) {
            errorMessage = appContext.getLocaleStringResource(R.string.strong_password)
            listener?.onFailure()
            return
        }

        if (!mPassword.equals(mConfirmPass)) {
            errorMessage = appContext.getLocaleStringResource(R.string.password_mismatch)
            listener?.onFailure()
            return
        }

        if (city.equals("null") || city == null) {
            errorMessage = appContext.getLocaleStringResource(R.string.city_mandatory)
            listener?.onFailure()
            return
        }

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        isSignUp = true

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userSignUp(
                    mName, mEmail, mPhone, mPassword, mConfirmPass, city
                )

                checkSignUpResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : SignUpApi.SignUpResponse = fromJson(error.message.toString())

                    checkSignUpResponse(response)
                } catch (e: Exception) {
                    errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                    println(e.message)
                    listener?.onFailure()
                }
            } catch (e: Exception) {
                errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                println(e.message)
                listener?.onFailure()
            }
        }

    }

    private fun checkSignUpResponse(response : SignUpApi.SignUpResponse) {

        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (status != null && status) {

            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }

    fun fetchCountry() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        isSignUp = false

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userAllCountries()

                checkCountryResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : CountriesApi.CountriesResponse = fromJson(error.message.toString())

                    checkCountryResponse(response)
                } catch (e: Exception) {
                    errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                    println(e.message)
                    Timber.e(e)
                    listener?.onFailure()
                }
            } catch (e: Exception) {
                errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                println(e.message)
                Timber.e(e)
                listener?.onFailure()
            }
        }

    }

    private fun checkCountryResponse(response : CountriesApi.CountriesResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            mutableCountry.value = data!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }

    fun fetchCity(country:String) {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        isSignUp = false

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userCities(country)

                checkCityResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : CitiesApi.CitiesResponse = fromJson(error.message.toString())

                    checkCityResponse(response)
                } catch (e: Exception) {
                    errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                    println(e.message)
                    Timber.e(e)
                    listener?.onFailure()
                }
            } catch (e: Exception) {
                errorMessage = appContext.getLocaleStringResource(R.string.something_wrong)
                println(e.message)
                Timber.e(e)
                listener?.onFailure()
            }
        }

    }

    private fun checkCityResponse(response : CitiesApi.CitiesResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            mutableCity.value = data!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }

}