package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class MyPlanApi {

    data class MyPlanResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: MutableList<MyPlanData>? = null

    )

    data class MyPlanData(

        @SerializedName("purchase_id")
        var purchase_id: String? = null,

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

        @SerializedName("plan_image")
        var plan_image: String? = null,

        @SerializedName("purchase_start_date")
        var purchase_start_date: String? = null,

        @SerializedName("purchase_exp_date")
        var purchase_exp_date: String? = null,

        @SerializedName("plan_basic_type")
        var plan_basic_type: String? = null,

        @SerializedName("plan_purchase_status")
        var plan_purchase_status: String? = null

    )

}