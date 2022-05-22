package com.lifegate.app.ui.fragments.workout.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.MyWorkoutPlanApi
import com.lifegate.app.data.network.responses.WorkoutPlanBlocksApi
import com.lifegate.app.data.repositories.PlanRepository
import com.lifegate.app.utils.*
import timber.log.Timber

class WorkoutDetailViewModel(
    private val repository: PlanRepository
) : ViewModel() {

    val appContext by lazy {
        repository.appContext
    }

    var listener: NetworkListener? = null
    var errorMessage: String = ""
    var isFirst = true

    var planId: String = ""
    var purchaseId: String = ""
    var date: String? = ""

    var totalTime: String = ""
    var caloriesBurned: String = "0"

    var myPlanList = mutableListOf<MyWorkoutPlanApi.MyWorkoutPlanMainTitle>()

    private val mutableMyPlan = MutableLiveData<MutableList<MyWorkoutPlanApi.MyWorkoutPlanMainTitle>>()

    val liveMyPlan : LiveData<MutableList<MyWorkoutPlanApi.MyWorkoutPlanMainTitle>>
        get() = mutableMyPlan

    fun fetchMyWorkoutPlan() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userMyWorkoutPlanHistoryDetail(planId, purchaseId, date)

                checkWorkoutPlanResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : MyWorkoutPlanApi.MyWorkoutPlanResponse = fromJson(error.message.toString())

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

    private fun checkWorkoutPlanResponse(response : MyWorkoutPlanApi.MyWorkoutPlanResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        val list = data?.generation?.current_set?.mywplanmaintitle

        errorMessage = message.toString()

        if (data != null  && status != null && status && !list.isNullOrEmpty()) {
            myPlanList = list!!
            mutableMyPlan.value = list!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())

        fetchPlanBlocks()
    }

    fun fetchPlanBlocks() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userMyWorkoutPlanBlock(planId, purchaseId)

                checkPlanBlocksResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : WorkoutPlanBlocksApi.WorkoutPlanBlocksResponse = fromJson(error.message.toString())

                    checkPlanBlocksResponse(response)
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

    private fun checkPlanBlocksResponse(response : WorkoutPlanBlocksApi.WorkoutPlanBlocksResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {

            val time = data.total_time
            val cal = data.calories

            if (time != null) {
                totalTime = time
            }
            if (cal != null) {
                caloriesBurned = cal
            }
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }
}