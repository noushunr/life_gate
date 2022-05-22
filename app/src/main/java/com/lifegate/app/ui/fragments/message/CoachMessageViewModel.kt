package com.lifegate.app.ui.fragments.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.CoachMessageApi
import com.lifegate.app.data.repositories.CoachRepository
import com.lifegate.app.utils.*
import timber.log.Timber

class CoachMessageViewModel(
    private val repository: CoachRepository
) : ViewModel() {

    val appContext by lazy {
        repository.appContext
    }

    var listener: NetworkListener? = null
    var errorMessage: String = ""

    var myMessageList = mutableListOf<CoachMessageApi.CoachMessageData>()

    private val mutableMessageList = MutableLiveData<MutableList<CoachMessageApi.CoachMessageData>>()

    val liveMessages : LiveData<MutableList<CoachMessageApi.CoachMessageData>>
        get() = mutableMessageList

    fun getLoginStatus() = repository.getLoginStatus()

    fun fetchMessages() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userGetAllCoachMessage()

                checkMessageResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : CoachMessageApi.CoachMessageResponse = fromJson(error.message.toString())

                    checkMessageResponse(response)
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

    private fun checkMessageResponse(response : CoachMessageApi.CoachMessageResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {

            myMessageList = mutableListOf()
            myMessageList = data

            mutableMessageList.value = data!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }

}