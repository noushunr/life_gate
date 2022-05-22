package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class HomeFeaturedPlansApi {

    data class HomeFeaturedPlansResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: MutableList<HomeFeaturedPlans>? = null

    )

    data class HomeFeaturedPlans(

        @SerializedName("plan_id")
        var plan_id: String? = null,

        @SerializedName("plan_name")
        var plan_name: String? = null,

        @SerializedName("plan_desc")
        var plan_desc: String? = null,

        @SerializedName("plan_cost")
        var plan_cost: String? = null,

        @SerializedName("plan_extracost")
        var plan_extracost: String? = null,

        @SerializedName("plan_tax")
        var plan_tax: String? = null,

        @SerializedName("plan_basic_type")
        var plan_basic_type: String? = null,

        @SerializedName("coach_fname")
        var coachFname: String? = null,

        @SerializedName("coach_lname")
        var coachLname: String? = null,


        @SerializedName("plan_pics")
        var plan_pics: MutableList<String>? = null

    )

}