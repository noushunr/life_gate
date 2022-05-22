package com.lifegate.app.ui.fragments.grocery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.NutritionGroceryResApi
import com.lifegate.app.data.repositories.PlanRepository
import com.lifegate.app.utils.*
import timber.log.Timber

class NutritionGroceryViewModel(
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

    var groceryList = mutableListOf<NutritionGroceryResApi.NutritionGrocery>()

    private val mutableGrocery = MutableLiveData<MutableList<NutritionGroceryResApi.NutritionGrocery>>()

    val liveGroceryList : LiveData<MutableList<NutritionGroceryResApi.NutritionGrocery>>
        get() = mutableGrocery

    fun fetchRestaurant() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                println("$planId , $purchaseId")
                val response = repository.userNutritionGrocery(
                    planId, purchaseId
                )

                checkRestaurantResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : NutritionGroceryResApi.NutritionGroceryResResponse = fromJson(error.message.toString())

                    checkRestaurantResponse(response)
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

    private fun checkRestaurantResponse(response : NutritionGroceryResApi.NutritionGroceryResResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        val list = data?.generation?.current_set?.grocery

        errorMessage = message.toString()

        if (data != null  && status != null && status && !list.isNullOrEmpty()) {
            groceryList = list!!
            mutableGrocery.value = list!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }
}