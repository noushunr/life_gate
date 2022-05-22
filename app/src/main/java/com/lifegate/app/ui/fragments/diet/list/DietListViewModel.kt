package com.lifegate.app.ui.fragments.diet.list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.MyPlanApi
import com.lifegate.app.data.repositories.PlanRepository
import com.lifegate.app.utils.*
import timber.log.Timber

class DietListViewModel(
    private val repository: PlanRepository
) : ViewModel() {

    val appContext by lazy {
        repository.appContext
    }

    var listener: NetworkListener? = null
    var errorMessage: String = ""

    var myPlanList = mutableListOf<MyPlanApi.MyPlanData>()

    private val mutableMyPlan = MutableLiveData<MutableList<MyPlanApi.MyPlanData>>()

    val liveMyPlan : LiveData<MutableList<MyPlanApi.MyPlanData>>
        get() = mutableMyPlan

    fun getLoginStatus() = repository.getLoginStatus()

    fun fetchMyPlan() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userMyPlan()

                checkPlanResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : MyPlanApi.MyPlanResponse = fromJson(error.message.toString())

                    checkPlanResponse(response)
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

    private fun checkPlanResponse(response : MyPlanApi.MyPlanResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {

            myPlanList = mutableListOf()

            for (item in data) {
                if (item.plan_basic_type.equals(KEY_PLAN_TYPE_NUTRITION)) {
                    myPlanList.add(item)
                }
            }
            //myPlanList = data

            mutableMyPlan.value = data!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }
}