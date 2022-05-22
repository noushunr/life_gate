package com.lifegate.app.ui.fragments.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.HistoryApi
import com.lifegate.app.data.repositories.PlanRepository
import com.lifegate.app.utils.*
import timber.log.Timber

class HistoryViewModel(
    private val repository: PlanRepository
) : ViewModel() {

    val appContext by lazy {
        repository.appContext
    }

    var listener: NetworkListener? = null
    var errorMessage: String = ""

    var planId: String = ""
    var purchaseId: String = ""
    var date: String = ""

    fun getLoginStatus() = repository.getLoginStatus()

    private val mutableData = MutableLiveData<HistoryApi.HistoryData>()

    val liveData : LiveData<HistoryApi.HistoryData>
        get() = mutableData

    private val mutableHistory = MutableLiveData<MutableList<HistoryApi.HistoryAllPlanData>>()

    val liveHistory : LiveData<MutableList<HistoryApi.HistoryAllPlanData>>
        get() = mutableHistory

    fun fetchAllHistory() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userAllPlanHistory()

                checkAllHistoryResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : HistoryApi.HistoryAllPlanResponse = fromJson(error.message.toString())

                    checkAllHistoryResponse(response)
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

    private fun checkAllHistoryResponse(response : HistoryApi.HistoryAllPlanResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            mutableHistory.value = data!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }

    fun fetchWorkoutHistory() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userWorkoutHistory(planId, purchaseId)

                checkWorkoutPlanResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : HistoryApi.HistoryResponse = fromJson(error.message.toString())

                    checkWorkoutPlanResponse(response)
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

    fun fetchNutritionHistory() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userNutritionHistory(planId, purchaseId)

                checkWorkoutPlanResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : HistoryApi.HistoryResponse = fromJson(error.message.toString())

                    checkWorkoutPlanResponse(response)
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

    private fun checkWorkoutPlanResponse(response : HistoryApi.HistoryResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            mutableData.value = data!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }

}