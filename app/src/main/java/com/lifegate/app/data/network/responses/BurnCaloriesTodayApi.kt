package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class BurnCaloriesTodayApi {

    data class BurnCaloriesResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: BurnCaloriesData? = null

    )

    data class BurnCaloriesData(

        @SerializedName("burning_goal")
        var burning_goal: String? = null,

        @SerializedName("actual_burned")
        var actual_burned: String? = null,

        @SerializedName("calories_id")
        var calories_id: String? = null,

        @SerializedName("calories_user_id")
        var calories_user_id: String? = null,

        @SerializedName("calories_calorie_consume_goal")
        var calories_calorie_consume_goal: String? = null,

        @SerializedName("calories_calorie_burn_goal")
        var calories_calorie_burn_goal: String? = null,

        @SerializedName("calories_calorie_consume")
        var calories_calorie_consume: String? = null,

        @SerializedName("calories_calorie_burn")
        var calories_calorie_burn: String? = null,

        @SerializedName("calories_proteins")
        var calories_proteins: String? = null,

        @SerializedName("calories_carbs")
        var calories_carbs: String? = null,

        @SerializedName("calories_fat")
        var calories_fat: String? = null,

        @SerializedName("calories_proteins_goal")
        var calories_proteins_goal: String? = null,

        @SerializedName("calories_carbs_goal")
        var calories_carbs_goal: String? = null,

        @SerializedName("calories_fat_goal")
        var calories_fat_goal: String? = null,

        @SerializedName("calories_date")
        var calories_date: String? = null

    )

}