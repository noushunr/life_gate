package com.lifegate.app.ui.fragments.workout.flow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.PlanFlowChartApi
import com.lifegate.app.data.repositories.PlanRepository
import com.lifegate.app.utils.*
import timber.log.Timber

class FlowChartViewModel(
    private val repository: PlanRepository
) : ViewModel() {

    val appContext by lazy {
        repository.appContext
    }

    var listener: NetworkListener? = null
    var errorMessage: String = ""

    var planId = ""
    var purchaseId = ""

    var myChartList = mutableListOf<PlanFlowChartApi.PlanFlowChart>()

    private val mutableChartList = MutableLiveData<MutableList<PlanFlowChartApi.PlanFlowChart>>()

    val liveChart : LiveData<MutableList<PlanFlowChartApi.PlanFlowChart>>
        get() = mutableChartList

    fun getLoginStatus() = repository.getLoginStatus()

    fun fetchFlowChart() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userPlanFlowChart(planId, purchaseId)

                checkFlowChartResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : PlanFlowChartApi.PlanFlowChartResponse = fromJson(error.message.toString())

                    checkFlowChartResponse(response)
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

    private fun checkFlowChartResponse(response : PlanFlowChartApi.PlanFlowChartResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {

            val chart = data.chart
            if (!chart.isNullOrEmpty()) {
                myChartList = mutableListOf()
                myChartList = chart

                mutableChartList.value = chart!!
            }
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }

}