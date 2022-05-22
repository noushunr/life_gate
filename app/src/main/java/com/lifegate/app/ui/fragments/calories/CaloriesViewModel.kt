package com.lifegate.app.ui.fragments.calories

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lifegate.app.R
import com.lifegate.app.data.network.responses.*
import com.lifegate.app.data.repositories.HomeRepository
import com.lifegate.app.utils.*
import timber.log.Timber

class CaloriesViewModel(
    private val repository: HomeRepository
) : ViewModel() {

    val appContext by lazy {
        repository.appContext
    }

    var listener: NetworkListener? = null
    var errorMessage: String = ""
    var isFirst = true

    var consumeGoal = "0"
    var burnGoal = "0"
    var proteinGoal = "0"
    var carbGoal = "0"
    var fatGoal = "0"

    fun getLoginStatus() = repository.getLoginStatus()

    fun getAdsCoachList() = repository.getAdsCoachList()

    fun getAdsHome() = repository.getAdsHome()

    fun getAdsCalories() = repository.getAdsCalories()

    var mealFoodList: MutableList<FoodMealListApi.FoodMealListData> = mutableListOf()

    private val mutableBurn = MutableLiveData<BurnCaloriesTodayApi.BurnCaloriesData>()
    private val mutableConsumed = MutableLiveData<ConsumedCaloriesTodayApi.ConsumedCaloriesData>()
    private val mutableFood = MutableLiveData<MutableList<FoodMealListApi.FoodMealListData>>()

    val liveBurn : LiveData<BurnCaloriesTodayApi.BurnCaloriesData>
        get() = mutableBurn
    val liveConsumed : LiveData<ConsumedCaloriesTodayApi.ConsumedCaloriesData>
        get() = mutableConsumed
    val liveFood : LiveData<MutableList<FoodMealListApi.FoodMealListData>>
        get() = mutableFood

    fun fetchConsumed() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userMyConsumedCaloriesToday()

                checkConsumedResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : ConsumedCaloriesTodayApi.ConsumedCaloriesResponse = fromJson(error.message.toString())

                    checkConsumedResponse(response)
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

    private fun checkConsumedResponse(response : ConsumedCaloriesTodayApi.ConsumedCaloriesResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            mutableConsumed.value = data!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        //println(response.toString())

        fetchBurned()
    }

    private fun fetchBurned() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userMyBurnCaloriesToday()

                checkBurnedResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : BurnCaloriesTodayApi.BurnCaloriesResponse = fromJson(error.message.toString())

                    checkBurnedResponse(response)
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

    private fun checkBurnedResponse(response : BurnCaloriesTodayApi.BurnCaloriesResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            mutableBurn.value = data!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        //println(response.toString())

        fetchFoodList()
    }

    fun addBurnedConsumed(
        burned: String?,
        consumed: String?,
        proteins: String?,
        carbs: String?,
        fat: String?
    ) {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userAddCaloriesBurnedConsumed(burned, consumed, proteins, carbs, fat,
                consumeGoal, burnGoal, proteinGoal, carbGoal, fatGoal)

                checkBurnedConsumedResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : BurnCaloriesTodayApi.BurnCaloriesResponse = fromJson(error.message.toString())

                    checkBurnedConsumedResponse(response)
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

    private fun checkBurnedConsumedResponse(response : BurnCaloriesTodayApi.BurnCaloriesResponse) {

        //val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        listener?.onFailure()

        fetchConsumed()

        //println(response.toString())
    }

    fun fetchFoodList() {

        if (!hasNetwork()) {
            errorMessage = appContext.getLocaleStringResource(R.string.check_network)
            listener?.onFailure()
            return
        }

        listener?.onStarted()

        CoRoutines.main {
            try {
                val response = repository.userFoodMealList()

                checkFoodResponse(response)

            } catch (error: ErrorBodyException) {
                try {
                    println(error.message.toString())
                    val response : FoodMealListApi.FoodMealListResponse = fromJson(error.message.toString())

                    checkFoodResponse(response)
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

    private fun checkFoodResponse(response : FoodMealListApi.FoodMealListResponse) {

        val data = response.data
        val message = response.message
        val status = response.status

        errorMessage = message.toString()

        if (data != null  && status != null && status) {
            mealFoodList = data!!
            mutableFood.value = data!!
            listener?.onSuccess()
        } else {
            errorMessage = response.message.toString()
            listener?.onFailure()
        }

        //println(response.toString())
    }

    fun updateMealList(item: FoodMealListApi.FoodMealListData, qty: String? = null) {
        mutableFood.value = mutableListOf()
        Log.d("Sizeee",mealFoodList.size.toString())
        for (i in 0 until mealFoodList.size) {
            if (mealFoodList[i].groceries_id == item.groceries_id) {

                try {
                    if (qty != null) {
                        mealFoodList[i].qty = qty.toInt()
                    } else {
                        mealFoodList[i].isSelected = !mealFoodList[i].isSelected
                    }
                } catch (e: Exception) {

                    e.printStackTrace()
                }
                Timber.e(mealFoodList[i].qty.toString())
                Timber.e("${mealFoodList[i].isSelected}")
            }
        }
        mutableFood.value = mealFoodList
    }

    fun checkFoodListUpdate() {
        var userCal = 0.0
        var userPro = 0.0
        var userCarb = 0.0
        var userfat = 0.0

        for (item in mealFoodList) {
            try {
                if (item.isSelected) {
                    userCal += (item.groceries_calory!!.toDouble() * item.qty)
                    userPro += (item.groceries_protien!!.toDouble() * item.qty)
                    userCarb += (item.groceries_carb!!.toDouble() * item.qty)
                    userfat += (item.groceries_fat!!.toDouble() * item.qty)
                }
            } catch (e: Exception) {

            }
        }
        addBurnedConsumed(null, "$userCal", "$userPro", "$userCarb", "$userfat")
    }
}