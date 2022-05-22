package com.lifegate.app.ui.fragments.restaurant.nutrition

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.NutritionGroceryResApi
import com.lifegate.app.data.repositories.PlanRepository
import com.lifegate.app.utils.*
import timber.log.Timber

class NutritionRestaurantViewModel(
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

    var restaurantList = mutableListOf<NutritionGroceryResApi.NutritionRestaurant>()

    private val mutableRestaurant = MutableLiveData<MutableList<NutritionGroceryResApi.NutritionRestaurant>>()

    val liveRestaurantList : LiveData<MutableList<NutritionGroceryResApi.NutritionRestaurant>>
        get() = mutableRestaurant

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
                val response = repository.userNutritionRestaurant(
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

        val list = data?.generation?.current_set?.restaurant

        errorMessage = message.toString()

        if (data != null  && status != null && status && !list.isNullOrEmpty()) {
            restaurantList = list!!
            mutableRestaurant.value = list!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }
}