package com.lifegate.app.ui.fragments.notification

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.NotificationApi
import com.lifegate.app.data.repositories.UserRepository
import com.lifegate.app.utils.*
import timber.log.Timber

class NotificationViewModel(
    private val repository: UserRepository
) : ViewModel() {

    val appContext by lazy {
        repository.appContext
    }

    var listener: NetworkListener? = null
    var errorMessage: String = ""

    var myNotificationList = mutableListOf<NotificationApi.NotificationData>()

    private val mutableNotificationList = MutableLiveData<MutableList<NotificationApi.NotificationData>>()

    val liveNotification : LiveData<MutableList<NotificationApi.NotificationData>>
        get() = mutableNotificationList

    fun getLoginStatus() = repository.getLoginStatus()

    fun fetchNotification() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userNotification()

                checkNotificationResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : NotificationApi.NotificationResponse = fromJson(error.message.toString())

                    checkNotificationResponse(response)
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

    private fun checkNotificationResponse(response : NotificationApi.NotificationResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {

            myNotificationList = mutableListOf()
            myNotificationList = data

            mutableNotificationList.value = data!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }
}