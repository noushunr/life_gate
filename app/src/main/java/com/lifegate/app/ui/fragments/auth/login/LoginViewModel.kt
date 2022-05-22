package com.lifegate.app.ui.fragments.auth.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.LoginApi
import com.lifegate.app.data.network.responses.ProfileApi
import com.lifegate.app.data.repositories.UserRepository
import com.lifegate.app.utils.*

class LoginViewModel(
    private val repository: UserRepository
) : ViewModel() {

    val appContext by lazy {
        repository.appContext
    }

    var email: String? = null
    var password: String? = null

    var mutableForgotPassword = MutableLiveData<LoginApi.LoginResponse>()
    var listener: NetworkListener? = null
    var errorMessage: String = ""

    var userEmail: String? = null
    var deviceToken: String?=null

    fun login() {

        /*FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Timber.d(token)
                repository.saveFireBaseDeviceToken(token)
            }
        })*/

        val mEmail = email
        val mPassword = password

        if (mEmail.isNullOrEmpty() || mPassword.isNullOrEmpty()) {
            errorMessage = appContext.getLocaleStringResource(R.string.all_field_mandate)
            listener?.onFailure()
            return
        }

        if (!mEmail.isValidEmail()) {
            errorMessage = appContext.getLocaleStringResource(R.string.enter_valid_email)
            listener?.onFailure()
            return
        }

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userLogin(
                    mEmail, mPassword
                )

                checkLoginResponse(response, mEmail, mPassword)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : LoginApi.LoginResponse = fromJson(error.message.toString())

                    checkLoginResponse(response, mEmail, mPassword)
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

    private fun checkLoginResponse(response : LoginApi.LoginResponse, mEmail: String, mPassword: String) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            errorMessage = response.data?.full_name.toString()
            repository.saveUserData(data)

            fetchProfile()
            addDeviceToken()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }

    fun addDeviceToken() {

        /*FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Timber.d(token)
                repository.saveFireBaseDeviceToken(token)
            }
        })*/


        if (!hasNetwork()) {
//            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
//            listener?.onFailure()
            return
        }

        CoRoutines.main {
            try {
                val response = repository.addDeviceToken(
                    deviceToken!!
                )



            } catch (error: ErrorBodyException) {
                try {

                } catch (e: Exception) {

                }
            } catch (e: Exception) {

            }
        }

    }

    fun forgotPassword() {

        /*FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                Timber.d(token)
                repository.saveFireBaseDeviceToken(token)
            }
        })*/

        val mEmail = userEmail

        if (mEmail.isNullOrEmpty() && !mEmail.isValidEmail()) {
            errorMessage = appContext.getLocaleStringResource(R.string.enter_valid_email)
            listener?.onFailure()
            return
        }

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.forgotPassword(
                    mEmail!!
                )


                checkForgotPasswordResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : LoginApi.LoginResponse = fromJson(error.message.toString())
                    checkForgotPasswordResponse(response)
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

    private fun checkForgotPasswordResponse(response : LoginApi.LoginResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (status != null && status) {
            mutableForgotPassword?.value = response

        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }

    fun fetchProfile() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userProfile()

                checkProfileResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : ProfileApi.ProfileResponse = fromJson(error.message.toString())

                    checkProfileResponse(response)
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

    private fun checkProfileResponse(response : ProfileApi.ProfileResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            errorMessage = response.message.toString()
            repository.saveProfileData(data)

            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }

}