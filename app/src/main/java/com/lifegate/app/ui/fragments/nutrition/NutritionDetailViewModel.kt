package com.lifegate.app.ui.fragments.nutrition

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.MyNutritionPlanApi
import com.lifegate.app.data.repositories.PlanRepository
import com.lifegate.app.utils.*
import timber.log.Timber

class NutritionDetailViewModel(
    private val repository: PlanRepository
) : ViewModel() {

    val appContext by lazy {
        repository.appContext
    }

    var listener: NetworkListener? = null
    var errorMessage: String = ""
    var isFirst = true

    var topDay: String? = null
    var topCal: String? = null
    var topVit: String? = null
    var topMin: String? = null

    var planId: String = ""
    var purchaseId: String = ""
    var date: String? = ""

    var myPlanList = mutableListOf<MyNutritionPlanApi.MyNutritionPlanSetFood>()

    private val mutableMyPlan = MutableLiveData<MutableList<MyNutritionPlanApi.MyNutritionPlanSetFood>>()

    val liveMyPlan : LiveData<MutableList<MyNutritionPlanApi.MyNutritionPlanSetFood>>
        get() = mutableMyPlan

    fun fetchMyDietPlan() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userMyNutritionPlanHistoryDetail(planId, purchaseId, date)

                checkDietPlanResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : MyNutritionPlanApi.MyNutritionPlanResponse = fromJson(error.message.toString())

                    checkDietPlanResponse(response)
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

    private fun checkDietPlanResponse(response : MyNutritionPlanApi.MyNutritionPlanResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        val list = data?.generation?.current_set?.setfood

        topDay = data?.generation?.dgeneration_days
        topMin = data?.generation?.current_set?.dietset_total_minarals
        topVit = data?.generation?.current_set?.dietset_total_vitamins

        if (data != null  && status != null && status && !list.isNullOrEmpty()) {
            myPlanList = list!!
            try {
                var cals = 0
                for (content in myPlanList) {
                    if (content.setfood_calories!=null && content.setfood_calories?.isNotEmpty()!!){
                        cals += content.setfood_calories?.toInt()!!
                    }

                }
                topCal = "$cals"
            } catch (e: Exception) {
                Timber.e(e)
            }
            mutableMyPlan.value = list!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }
}