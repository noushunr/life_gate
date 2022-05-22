package com.lifegate.app.ui.fragments.restaurant

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.RestaurantApi
import com.lifegate.app.data.network.responses.RestaurantApi.Restaurant
import com.lifegate.app.data.repositories.HomeRepository
import com.lifegate.app.utils.*
import timber.log.Timber

class RestaurantViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    val appContext by lazy {
        repository.appContext
    }

    var listener: NetworkListener? = null
    var errorMessage: String = ""
    var isFirst = true

    var restaurantList = mutableListOf<Restaurant>()

    private val mutableRestaurant = MutableLiveData<MutableList<Restaurant>>()

    val liveRestaurantList : LiveData<MutableList<Restaurant>>
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
                val response = repository.userRestaurant()

                checkRestaurantResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : RestaurantApi.RestaurantResponse = fromJson(error.message.toString())

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

    private fun checkRestaurantResponse(response : RestaurantApi.RestaurantResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status && !data.isNullOrEmpty()) {
            restaurantList = data!!
            mutableRestaurant.value = data!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        println(response.toString())
    }
}