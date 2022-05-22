package com.lifegate.app.data.repositories

import android.content.Context
import com.lifegate.app.data.db.AppDatabase
import com.lifegate.app.data.network.MyApi
import com.lifegate.app.data.network.SafeApiRequest
import com.lifegate.app.data.network.responses.*
import com.lifegate.app.data.preferences.PreferenceProvider
import com.lifegate.app.utils.APP_HEADER_KEY
import okhttp3.MultipartBody
import okhttp3.RequestBody


/*
 *Created by Adithya T Raj on 24-06-2021
*/

class PlanRepository(
    val appContext: Context,
    private val api: MyApi,
    private val db: AppDatabase,
    private val prefs: PreferenceProvider
) : SafeApiRequest() {

    suspend fun userPlanDetail(
        plan: String
    ) : PlanDetailsApi.PlanDetailsResponse {

        return apiRequest {
            api.userPlanDetail(
                APP_HEADER_KEY, plan
            )
        }
    }

    suspend fun userPlanRatingList(
        plan: String
    ) : PlanRatingApi.PlanRatingListResponse {

        return apiRequest {
            api.userPlanRatingList(
                APP_HEADER_KEY, plan
            )
        }
    }

    suspend fun userPlanRatingAdd(
        plan: String,
        comment: String,
        rating: String
    ) : PlanRatingApi.PlanRatingAddResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userPlanRatingAdd(
                APP_HEADER_KEY, userToken, plan, comment, rating
            )
        }
    }

    fun getLoginStatus() = prefs.getLoginStatus()

    suspend fun userPlanPurchase(
        plan: String,
        coupon: String? = null
    ) : PurchaseApi.PurchaseResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userPlanPurchase(
                APP_HEADER_KEY, userToken, plan, coupon
            )
        }
    }

    suspend fun userPurchasePostPayment(
        data: PostPurchaseApi.PostPurchaseData
    ) : PostPurchaseApi.PostPurchaseResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userPurchasePostPayment(
                APP_HEADER_KEY, userToken, data
            )
        }
    }

    suspend fun userMyPlan() : MyPlanApi.MyPlanResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userMyPlan(
                APP_HEADER_KEY, userToken
            )
        }
    }

    suspend fun userMyNutritionPlan(
        planId: String,
        purchaseId: String
    ) : MyNutritionPlanApi.MyNutritionPlanResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userMyNutritionPlan(
                APP_HEADER_KEY, userToken, planId, purchaseId
            )
        }
    }

    suspend fun userMyNutritionPlanHistoryDetail(
        planId: String,
        purchaseId: String,
        date: String?
    ) : MyNutritionPlanApi.MyNutritionPlanResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userMyNutritionPlanHistoryDetail(
                APP_HEADER_KEY, userToken, planId, purchaseId, date
            )
        }
    }

    suspend fun userMyWorkoutPlan(
        planId: String,
        purchaseId: String
    ) : MyWorkoutPlanApi.MyWorkoutPlanResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userMyWorkoutPlan(
                APP_HEADER_KEY, userToken, planId, purchaseId
            )
        }
    }

    suspend fun userMyWorkoutPlanBlock(
        planId: String,
        purchaseId: String
    ) : WorkoutPlanBlocksApi.WorkoutPlanBlocksResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userMyWorkoutPlanBlock(
                APP_HEADER_KEY, userToken, planId, purchaseId
            )
        }
    }

    suspend fun userMyWorkoutPlanHistoryDetail(
        planId: String,
        purchaseId: String,
        date: String?
    ) : MyWorkoutPlanApi.MyWorkoutPlanResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userMyWorkoutPlanHistoryDetail(
                APP_HEADER_KEY, userToken, planId, purchaseId, date
            )
        }
    }

    suspend fun userUpdateStatusWorkoutPlan(
        planId: String,
        purchaseId: String,
        subtitle: String,
        status: String
    ) : MyWorkoutPlanApi.MyWorkoutPlanResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userUpdateStatusWorkoutPlan(
                APP_HEADER_KEY, userToken, planId, purchaseId, subtitle, status
            )
        }
    }

    suspend fun userVideoUploadWorkoutPlan(
        planId: RequestBody?,
        purchaseId: RequestBody?,
        subtitle: RequestBody?,
        video: MultipartBody.Part?
    ): MyWorkoutPlanApi.MyWorkoutPlanResponse {

        val userToken = prefs.getUserToken().toString()

        //val status = RequestBody.create(MediaType.parse("multipart/form-data"), "1")

        return apiRequest {
            api.userVideoUploadWorkoutPlan(
                APP_HEADER_KEY, userToken, planId, purchaseId, subtitle/*, status*/, video
            )
        }
    }

    suspend fun userVideoUploadWorkoutPlanV2(
        planId: String,
        purchaseId: String,
        subtitle: String,
        video: RequestBody?
    ): MyWorkoutPlanApi.MyWorkoutPlanResponse {

        val userToken = prefs.getUserToken().toString()

        println("$planId $purchaseId $subtitle")

        return apiRequest {
            api.userVideoUploadWorkoutPlanV2(
                APP_HEADER_KEY, userToken, planId, purchaseId, subtitle, video
            )
        }
    }

    suspend fun userUpdateStatusNutritionPlan(
        planId: String,
        purchaseId: String,
        foodId: String,
        status: String
    ) : MyNutritionPlanApi.MyNutritionPlanResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userUpdateStatusNutritionPlan(
                APP_HEADER_KEY, userToken, planId, purchaseId, foodId, status
            )
        }
    }

    suspend fun userLogNutritionPlan(
        planId: String,
        purchaseId: String,
        foodId: String,
        calories: String?,
        carbs: String?,
        proteins: String?,
        fat: String?
    ) : MyNutritionPlanApi.MyNutritionPlanResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userLogNutritionPlan(
                APP_HEADER_KEY, userToken, planId, purchaseId, foodId, calories, carbs, proteins, fat
            )
        }
    }

    suspend fun userWorkoutHistory(
        planId: String,
        purchaseId: String
    ) : HistoryApi.HistoryResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userWorkoutHistory(
                APP_HEADER_KEY, userToken, planId, purchaseId
            )
        }
    }

    suspend fun userNutritionHistory(
        planId: String,
        purchaseId: String
    ) : HistoryApi.HistoryResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userNutritionHistory(
                APP_HEADER_KEY, userToken, planId, purchaseId
            )
        }
    }

    suspend fun userAllPlanHistory() : HistoryApi.HistoryAllPlanResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userAllPlanHistory(
                APP_HEADER_KEY, userToken
            )
        }
    }

    suspend fun userNutritionRestaurant(
        planId: String,
        purchaseId: String
    ) : NutritionGroceryResApi.NutritionGroceryResResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userNutritionRestaurant(
                APP_HEADER_KEY, userToken, planId, purchaseId
            )
        }
    }

    suspend fun userNutritionGrocery(
        planId: String,
        purchaseId: String
    ) : NutritionGroceryResApi.NutritionGroceryResResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userNutritionGrocery(
                APP_HEADER_KEY, userToken, planId, purchaseId
            )
        }
    }

    suspend fun userWorkoutAddWeight(
        planId: String,
        purchaseId: String,
        subtitle: String,
        weight: String
    ) : MyWorkoutPlanApi.MyWorkoutPlanResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userWorkoutAddWeight(
                APP_HEADER_KEY, userToken, planId, purchaseId, subtitle, weight
            )
        }
    }

    suspend fun userWorkoutAddNotes(
        planId: String,
        purchaseId: String,
        subtitle: String,
        notes: String
    ) : MyWorkoutPlanApi.MyWorkoutPlanResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userWorkoutAddNotes(
                APP_HEADER_KEY, userToken, planId, purchaseId, subtitle, notes
            )
        }
    }

    suspend fun userPlanFlowChart(
        planId: String,
        purchaseId: String
    ) : PlanFlowChartApi.PlanFlowChartResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userPlanFlowChart(
                APP_HEADER_KEY, userToken, planId, purchaseId
            )
        }
    }

    fun getAdsCoachList() = prefs.getAdsCoachList()

    fun getAdsHome() = prefs.getAdsHome()

    fun getAdsCalories() = prefs.getAdsCalories()

    suspend fun userEquipmentList() : EquipmentListApi.EquipmentListResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userEquipmentList(
                APP_HEADER_KEY, userToken
            )
        }
    }

    suspend fun userAds() : HomeAdsApi.HomeAdsResponse {
        return apiRequest {
            api.userAds(APP_HEADER_KEY)
        }
    }
    suspend fun userCoachMessageAdd(
        coach: String,
        message: String
    ) : CoachRatingApi.CoachRatingAddResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userCoachMessageAdd(
                APP_HEADER_KEY, userToken, coach, message
            )
        }
    }

}