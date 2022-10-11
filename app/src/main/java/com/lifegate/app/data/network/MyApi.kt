package com.lifegate.app.data.network

import com.lifegate.app.data.network.responses.*
import com.lifegate.app.utils.PRO_BASE_API_URL
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit


/*
 *Created by Adithya T Raj on 24-06-2021
*/

interface MyApi {

    @FormUrlEncoded
    @POST("Auth/login")
    suspend fun userLogin(
        @Header("LG-APP-KEY") appKey: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : Response<LoginApi.LoginResponse>

    @FormUrlEncoded
    @POST("Lifegate/forgot_password")
    suspend fun forgotPassword(
        @Header("LG-APP-KEY") appKey: String,
        @Field("email") email: String
    ) : Response<LoginApi.LoginResponse>

    @FormUrlEncoded
    @POST("Auth/register_user")
    suspend fun userSignUp(
        @Header("LG-APP-KEY") appKey: String,
        @Field("fullname") name: String,
        @Field("email") email: String,
        @Field("mobile") mobile: String,
        @Field("password") password: String,
        @Field("confirm_password") confirm: String,
        @Field("country")country: String,
        @Field("city_id") city: String
    ) : Response<SignUpApi.SignUpResponse>

    @FormUrlEncoded
    @POST("Users/notification_android")
    suspend fun addDeviceToken(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("token") deviceToken: String
    ) : Response<ProfileApi.ProfileResponse>

    @POST("Users/userprofile")
    suspend fun userProfile(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String
    ) : Response<ProfileApi.ProfileResponse>


    @POST("Users/user_profile_update")
    suspend fun userProfileUpdate(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Body files: MultipartBody
    ) : Response<ProfileApi.ProfileResponse>

    @POST("Lifegate/countries")
    suspend fun userAllCountries(
        @Header("LG-APP-KEY") appKey: String
    ) : Response<CountriesApi.CountriesResponse>

    @POST("Lifegate/reg_countries")
    suspend fun coachRegisteredCountries(
        @Header("LG-APP-KEY") appKey: String
    ) : Response<CountriesApi.CountriesResponse>

    @FormUrlEncoded
    @POST("Lifegate/cities")
    suspend fun userCities(
        @Header("LG-APP-KEY") appKey: String,
        @Field("country") country: String
    ) : Response<CitiesApi.CitiesResponse>

    @POST("Lifegate/featuredcoaches")
    suspend fun userHomeFeaturedCoaches(
        @Header("LG-APP-KEY") appKey: String
    ) : Response<HomeFeaturedCoachesApi.HomeFeaturedCoachesResponse>

    @POST("Lifegate/tutorialsandsliders")
    suspend fun userHomeTutorialSliders(
        @Header("LG-APP-KEY") appKey: String
    ) : Response<HomeTutorialSlidersApi.HomeTutorialSlidersResponse>

    @POST("Lifegate/banners")
    suspend fun userHomeBanners(
        @Header("LG-APP-KEY") appKey: String
    ) : Response<HomeBannersApi.HomeBannersResponse>

    @POST("Lifegate/ads")
    suspend fun userAds(
        @Header("LG-APP-KEY") appKey: String
    ) : Response<HomeAdsApi.HomeAdsResponse>

    @POST("Lifegate/featuredplans")
    suspend fun userHomeFeaturedPlans(
        @Header("LG-APP-KEY") appKey: String
    ) : Response<HomeFeaturedPlansApi.HomeFeaturedPlansResponse>

    @FormUrlEncoded
    @POST("Lifegate/coaches_list")
    suspend fun userCoachesList(
        @Header("LG-APP-KEY") appKey: String,
        @Field("search") search: String?,
        @Field("country") country: String?,
        @Field("city") city: String?,
        @Field("type") type: String?,
        @Field("action") action: String?,
        @Field("service") service: String?,
        @Field("page") page: String?,
        @Field("per_page") per_page: String?
    ) : Response<CoachesListApi.CoachesListResponse>

    @POST("Lifegate/coach_types")
    suspend fun userCoachesType(
        @Header("LG-APP-KEY") appKey: String
    ) : Response<CoachesTypeApi.CoachesTypeResponse>

    @FormUrlEncoded
    @POST("Lifegate/coach_details")
    suspend fun userCoachDetail(
        @Header("LG-APP-KEY") appKey: String,
        @Field("coach") coach: String
    ) : Response<CoachDetailsApi.CoachDetailsResponse>

    @FormUrlEncoded
    @POST("Lifegate/coachratinglist")
    suspend fun userCoachRatingList(
        @Header("LG-APP-KEY") appKey: String,
        @Field("coachid") coachid: String
    ) : Response<CoachRatingApi.CoachRatingListResponse>

    @FormUrlEncoded
    @POST("Lifegate/coachratingadd")
    suspend fun userCoachRatingAdd(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("coachid") coachid: String,
        @Field("comment") comment: String,
        @Field("rating") rating: String
    ) : Response<CoachRatingApi.CoachRatingAddResponse>

    @FormUrlEncoded
    @POST("Users/messagethecoach")
    suspend fun userCoachMessageAdd(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("coachid") coachid: String,
        @Field("message") message: String
    ) : Response<CoachRatingApi.CoachRatingAddResponse>

    @POST("Lifegate/restaurants")
    suspend fun userRestaurant(
        @Header("LG-APP-KEY") appKey: String
    ) : Response<RestaurantApi.RestaurantResponse>

    @FormUrlEncoded
    @POST("Lifegate/plan_details")
    suspend fun userPlanDetail(
        @Header("LG-APP-KEY") appKey: String,
        @Field("plan") plan: String
    ) : Response<PlanDetailsApi.PlanDetailsResponse>

    @FormUrlEncoded
    @POST("Lifegate/planratinglist")
    suspend fun userPlanRatingList(
        @Header("LG-APP-KEY") appKey: String,
        @Field("planid") planid: String
    ) : Response<PlanRatingApi.PlanRatingListResponse>

    @FormUrlEncoded
    @POST("Lifegate/planratingadd")
    suspend fun userPlanRatingAdd(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("planid") planid: String,
        @Field("comment") comment: String,
        @Field("rating") rating: String
    ) : Response<PlanRatingApi.PlanRatingAddResponse>

    @FormUrlEncoded
    @POST("Users/purchase")
    suspend fun userPlanPurchase(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("plan") plan: String,
        @Field("coupon") coupon: String?
    ) : Response<PurchaseApi.PurchaseResponse>

    @POST("Users/purchase_post_payment")
    suspend fun userPurchasePostPayment(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Body data: PostPurchaseApi.PostPurchaseData
    ) : Response<PostPurchaseApi.PostPurchaseResponse>

    @POST("Users/myplans")
    suspend fun userMyPlan(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String
    ) : Response<MyPlanApi.MyPlanResponse>

    @POST("Users/fitnessplantoday")
    suspend fun userMyTodayPlan(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String
    ) : Response<MyPlanApi.MyPlanResponse>

    @FormUrlEncoded
    @POST("Users/mynutritionplan")
    suspend fun userMyNutritionPlan(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("plan") plan: String,
        @Field("purchase") purchase: String
    ) : Response<MyNutritionPlanApi.MyNutritionPlanResponse>

    @FormUrlEncoded
    @POST("Users/mynutritionplan_history_details")
    suspend fun userMyNutritionPlanHistoryDetail(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("plan") plan: String,
        @Field("purchase") purchase: String,
        @Field("history_date") history_date: String?
    ) : Response<MyNutritionPlanApi.MyNutritionPlanResponse>

    @FormUrlEncoded
    @POST("Users/log_nutritionplan")
    suspend fun userUpdateStatusNutritionPlan(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("plan") plan: String,
        @Field("purchase") purchase: String,
        @Field("setfood") setfood: String,
        @Field("status") status: String
    ) : Response<MyNutritionPlanApi.MyNutritionPlanResponse>

    @FormUrlEncoded
    @POST("Users/log_nutritionplan")
    suspend fun userLogNutritionPlan(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("plan") plan: String,
        @Field("purchase") purchase: String,
        @Field("setfood") setfood: String,
        @Field("calories") calories: String?,
        @Field("carbs") carbs: String?,
        @Field("proteins") proteins: String?,
        @Field("fat") fat: String?
    ) : Response<MyNutritionPlanApi.MyNutritionPlanResponse>

    @FormUrlEncoded
    @POST("Users/myworkoutplan")
    suspend fun userMyWorkoutPlan(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("plan") plan: String,
        @Field("purchase") purchase: String
    ) : Response<MyWorkoutPlanApi.MyWorkoutPlanResponse>

    @FormUrlEncoded
    @POST("Users/myworkoutplan_blocks")
    suspend fun userMyWorkoutPlanBlock(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("plan") plan: String,
        @Field("purchase") purchase: String
    ) : Response<WorkoutPlanBlocksApi.WorkoutPlanBlocksResponse>

    @FormUrlEncoded
    @POST("Users/myworkoutplan_history_details")
    suspend fun userMyWorkoutPlanHistoryDetail(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("plan") plan: String,
        @Field("purchase") purchase: String,
        @Field("history_date") history_date: String?
    ) : Response<MyWorkoutPlanApi.MyWorkoutPlanResponse>

    @FormUrlEncoded
    @POST("Users/log_myworkoutplan")
    suspend fun userUpdateStatusWorkoutPlan(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("plan") plan: String,
        @Field("purchase") purchase: String,
        @Field("subtitle") subtitle: String,
        @Field("status") status: String
    ) : Response<MyWorkoutPlanApi.MyWorkoutPlanResponse>

    @Multipart
    @POST("Users/log_myworkoutplan")
    suspend fun userVideoUploadWorkoutPlan(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Part("plan") plan: RequestBody?,
        @Part("purchase") purchase: RequestBody?,
        @Part("subtitle") subtitle: RequestBody?,
        /*@Part("status") status: RequestBody?,*/
        @Part video: MultipartBody.Part?
    ): Response<MyWorkoutPlanApi.MyWorkoutPlanResponse>

    @Multipart
    @POST("Users/log_myworkoutplan")
    suspend fun userVideoUploadWorkoutPlanV2(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Part("plan") plan: String,
        @Part("purchase") purchase: String,
        @Part("subtitle") subtitle: String,
        @Part("video") video: RequestBody?
    ): Response<MyWorkoutPlanApi.MyWorkoutPlanResponse>

    @FormUrlEncoded
    @POST("Users/log_myworkoutplan")
    suspend fun userWorkoutAddWeight(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("plan") plan: String,
        @Field("purchase") purchase: String,
        @Field("subtitle") subtitle: String,
        @Field("weight") status: String
    ) : Response<MyWorkoutPlanApi.MyWorkoutPlanResponse>

    @FormUrlEncoded
    @POST("Users/log_myworkoutplan")
    suspend fun userWorkoutAddNotes(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("plan") plan: String,
        @Field("purchase") purchase: String,
        @Field("subtitle") subtitle: String,
        @Field("notes") status: String
    ) : Response<MyWorkoutPlanApi.MyWorkoutPlanResponse>

    @POST("Users/burn_calories_today")
    suspend fun userMyBurnCaloriesToday(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String
    ) : Response<BurnCaloriesTodayApi.BurnCaloriesResponse>

    @POST("Users/mycalories_consumed")
    suspend fun userMyConsumedCaloriesToday(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String
    ) : Response<ConsumedCaloriesTodayApi.ConsumedCaloriesResponse>

    @FormUrlEncoded
    @POST("Users/calories_without_plan_add")
    suspend fun userAddCaloriesBurnedConsumed(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("calories_consume") calories_consume: String?,
        @Field("calories_burn") calories_burn: String?,
        @Field("proteins") proteins: String?,
        @Field("carbs") carbs: String?,
        @Field("fat") fat: String?,
        @Field("calories_consume_goal") calories_consume_goal: String,
        @Field("calories_burn_goal") calories_burn_goal: String,
        @Field("proteins_goal") proteins_goal: String,
        @Field("carbs_goal") carbs_goal: String,
        @Field("fat_goal") fat_goal: String
    ) : Response<BurnCaloriesTodayApi.BurnCaloriesResponse>

    @FormUrlEncoded
    @POST("Users/myworkoutplan_history")
    suspend fun userWorkoutHistory(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("plan") plan: String,
        @Field("purchase") purchase: String
    ) : Response<HistoryApi.HistoryResponse>

    @FormUrlEncoded
    @POST("Users/mynutritionplan_history")
    suspend fun userNutritionHistory(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("plan") plan: String,
        @Field("purchase") purchase: String
    ) : Response<HistoryApi.HistoryResponse>

    @POST("Users/myallplan_history")
    suspend fun userAllPlanHistory(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String
    ) : Response<HistoryApi.HistoryAllPlanResponse>

    @FormUrlEncoded
    @POST("Users/mynutritionplan_restaurant")
    suspend fun userNutritionRestaurant(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("plan") plan: String,
        @Field("purchase") purchase: String
    ) : Response<NutritionGroceryResApi.NutritionGroceryResResponse>

    @FormUrlEncoded
    @POST("Users/mynutritionplan_grocery")
    suspend fun userNutritionGrocery(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("plan") plan: String,
        @Field("purchase") purchase: String
    ) : Response<NutritionGroceryResApi.NutritionGroceryResResponse>

    @POST("Users/notifications")
    suspend fun userNotification(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String
    ) : Response<NotificationApi.NotificationResponse>

    @POST("Lifegate/Foodmealslist")
    suspend fun userFoodMealList(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String
    ) : Response<FoodMealListApi.FoodMealListResponse>

    @FormUrlEncoded
    @POST("Users/myworkoutplanflowchart")
    suspend fun userPlanFlowChart(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String,
        @Field("plan") plan: String,
        @Field("purchase") purchase: String
    ) : Response<PlanFlowChartApi.PlanFlowChartResponse>

    @POST("Users/getcoachmsgnotifs")
    suspend fun userGetAllCoachMessage(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String
    ) : Response<CoachMessageApi.CoachMessageResponse>

    @POST("Users/graph")
    suspend fun userGetGraph(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String
    ) : Response<GraphApi.GraphResponse>

    @POST("Lifegate/Equipmentlist")
    suspend fun userEquipmentList(
        @Header("LG-APP-KEY") appKey: String,
        @Header("Authorization") token: String
    ) : Response<EquipmentListApi.EquipmentListResponse>

    @POST("Lifegate/Registercoach")
    suspend fun registerCoach(
        @Header("LG-APP-KEY") appKey: String,
        @Body files: MultipartBody

    ) : Response<SignUpApi.SignUpResponse>

    @POST("Lifegate/policy")
    suspend fun policyData(
        @Header("LG-APP-KEY") appKey: String
    ) : Response<PolicyApi.PolicyResponse>

    @POST("Lifegate/coach_services")
    suspend fun coachServices(
        @Header("LG-APP-KEY") appKey: String
    ) : Response<CoachServicesApi.CoachServicesResponse>


    companion object{
        operator fun invoke(
            networkConnectionInterceptor: NetworkConnectionInterceptor
        ) : MyApi {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val okkHttpclient = OkHttpClient.Builder()
                .addInterceptor(logging)
                .callTimeout(300, TimeUnit.SECONDS)
                .connectTimeout(300, TimeUnit.SECONDS)
                .readTimeout(300, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .client(okkHttpclient)
                .baseUrl(PRO_BASE_API_URL)
//                .baseUrl(TEST_BASE_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }

}