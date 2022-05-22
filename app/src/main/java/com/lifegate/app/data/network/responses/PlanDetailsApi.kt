package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class PlanDetailsApi {

    data class PlanDetailsResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: PlanDetails? = null

    )

    data class PlanDetails(

        @SerializedName("plan_id")
        var plan_id: String? = null,

        @SerializedName("plan_name")
        var plan_name: String? = null,

        @SerializedName("plantype_name")
        var plantype_name: String? = null,

        @SerializedName("plan_desc")
        var plan_desc: String? = null,

        @SerializedName("plan_duration")
        var plan_duration: String? = null,

        @SerializedName("plan_rule")
        var plan_rule: String? = null,

        @SerializedName("plan_cost")
        var plan_cost: String? = null,

        @SerializedName("plan_extracost")
        var plan_extracost: String? = null,

        @SerializedName("plan_tax")
        var plan_tax: String? = null,

        @SerializedName("plan_image")
        var plan_image: MutableList<PlanImage>? = null,

        @SerializedName("plan_coach")
        var plan_coach: String? = null,

        @SerializedName("plan_basic_type")
        var plan_basic_type: String? = null,

        @SerializedName("plan_avg_rating")
        var plan_avg_rating: String? = null

    )

    data class PlanImage(

        @SerializedName("attachment")
        var attachment: String? = null,

        @SerializedName("type")
        var type: String? = null

    )

}