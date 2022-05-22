package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class HistoryApi {

    data class HistoryResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: HistoryData? = null

    )

    data class HistoryAllPlanResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: MutableList<HistoryAllPlanData>? = null

    )

    data class HistoryData(

        @SerializedName("plan_details")
        var plan_details: PlanDetail? = null,

        @SerializedName("history")
        var history: MutableList<PlanHistory>? = null

    )

    data class HistoryAllPlanData(

        @SerializedName("log_date")
        var log_date: String? = null,

        @SerializedName("consumed")
        var consumed: String? = null,

        @SerializedName("burned")
        var burned: String? = null,

        @SerializedName("plan_details")
        var plan_details: PlanDetail? = null,

        @SerializedName("extraconsumed")
        var extraconsumed: String? = null,

        @SerializedName("extraburned")
        var extraburned: String? = null,

    )

    data class PlanDetail(

        @SerializedName("purchase_id")
        var purchase_id: String? = null,

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
        var plan_image: String? = null,

        @SerializedName("plan_coach")
        var plan_coach: String? = null,

        @SerializedName("plan_basic_type")
        var plan_basic_type: String? = null

    )

    data class PlanHistory(

        @SerializedName("log_date")
        var log_date: String? = null,

        @SerializedName("burned")
        var burned: String? = null,

        @SerializedName("consumed")
        var consumed: String? = null

    )

}