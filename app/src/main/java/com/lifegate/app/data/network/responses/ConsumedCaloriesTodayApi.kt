package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class ConsumedCaloriesTodayApi {

    data class ConsumedCaloriesResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: ConsumedCaloriesData? = null

    )

    data class ConsumedCaloriesData(

        @SerializedName("consume_goal")
        var consume_goal: String? = null,

        @SerializedName("consumed_today")
        var consumed_today: String? = null,

        @SerializedName("proteins_today")
        var proteins_today: String? = null,

        @SerializedName("fat_today")
        var fat_today: String? = null,

        @SerializedName("carbs_today")
        var carbs_today: String? = null,

        @SerializedName("proteins_goal")
        var proteins_goal: String? = null,

        @SerializedName("fat_goal")
        var fat_goal: String? = null,

        @SerializedName("carbs_goal")
        var carbs_goal: String? = null

    )

}