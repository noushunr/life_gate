package com.lifegate.app.data.repositories

import android.content.Context
import com.lifegate.app.data.db.AppDatabase
import com.lifegate.app.data.network.MyApi
import com.lifegate.app.data.network.SafeApiRequest
import com.lifegate.app.data.network.responses.*
import com.lifegate.app.data.preferences.PreferenceProvider
import com.lifegate.app.utils.APP_HEADER_KEY


/*
 *Created by Adithya T Raj on 24-06-2021
*/

class HomeRepository(
    val appContext: Context,
    private val api: MyApi,
    private val db: AppDatabase,
    private val prefs: PreferenceProvider
) : SafeApiRequest() {

    suspend fun userHomeFeaturedCoaches() : HomeFeaturedCoachesApi.HomeFeaturedCoachesResponse {
        return apiRequest {
            api.userHomeFeaturedCoaches(APP_HEADER_KEY)
        }
    }

    suspend fun userHomeTutorialSliders() : HomeTutorialSlidersApi.HomeTutorialSlidersResponse {
        return apiRequest {
            api.userHomeTutorialSliders(APP_HEADER_KEY)
        }
    }

    suspend fun userHomeBanners() : HomeBannersApi.HomeBannersResponse {
        return apiRequest {
            api.userHomeBanners(APP_HEADER_KEY)
        }
    }

    suspend fun userHomeFeaturedPlans() : HomeFeaturedPlansApi.HomeFeaturedPlansResponse {
        return apiRequest {
            api.userHomeFeaturedPlans(APP_HEADER_KEY)
        }
    }

    suspend fun userRestaurant() : RestaurantApi.RestaurantResponse {

        return apiRequest {
            api.userRestaurant(APP_HEADER_KEY)
        }
    }

    suspend fun userMyBurnCaloriesToday() : BurnCaloriesTodayApi.BurnCaloriesResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userMyBurnCaloriesToday(APP_HEADER_KEY, userToken)
        }
    }

    suspend fun userMyConsumedCaloriesToday() : ConsumedCaloriesTodayApi.ConsumedCaloriesResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userMyConsumedCaloriesToday(APP_HEADER_KEY, userToken)
        }
    }

    fun getLoginStatus() = prefs.getLoginStatus()

    suspend fun userMyPlan() : MyPlanApi.MyPlanResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userMyPlan(
                APP_HEADER_KEY, userToken
            )
        }
    }

    fun getUserName() = prefs.getUserName()

    fun getUserPic() = prefs.getUserPic()

    fun getUserCityName() = prefs.getUserCityName()

    suspend fun userAddCaloriesBurnedConsumed(
        calories_burn: String?,
        calories_consume: String?,
        proteins: String?,
        carbs: String?,
        fat: String?,
        calories_consume_goal: String,
        calories_burn_goal: String,
        proteins_goal: String,
        carbs_goal: String,
        fat_goal: String
    ) : BurnCaloriesTodayApi.BurnCaloriesResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userAddCaloriesBurnedConsumed(
                APP_HEADER_KEY, userToken, calories_consume,
                calories_burn, proteins, carbs, fat,
                calories_consume_goal, calories_burn_goal,
                proteins_goal, carbs_goal, fat_goal
            )
        }
    }

    suspend fun userMyTodayPlan() : MyPlanApi.MyPlanResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userMyTodayPlan(
                APP_HEADER_KEY, userToken
            )
        }
    }

    suspend fun userFoodMealList() : FoodMealListApi.FoodMealListResponse {

        val userToken = prefs.getUserToken().toString()

        return apiRequest {
            api.userFoodMealList(APP_HEADER_KEY, userToken)
        }
    }

    suspend fun userAds() : HomeAdsApi.HomeAdsResponse {
        return apiRequest {
            api.userAds(APP_HEADER_KEY)
        }
    }

    fun updatePrefAds(list : MutableList<HomeAdsApi.HomeAds>) {
        for (item in list) {
            when (item.ads_page) {
                "Coach Listing" -> prefs.saveAdsCoachList(item.ads_pic)
                "Home" -> prefs.saveAdsHome(item.ads_pic)
                "Calories" -> prefs.saveAdsCalories(item.ads_pic)
            }
        }
    }

    fun getAdsCoachList() = prefs.getAdsCoachList()

    fun getAdsHome() = prefs.getAdsHome()

    fun getAdsCalories() = prefs.getAdsCalories()

}