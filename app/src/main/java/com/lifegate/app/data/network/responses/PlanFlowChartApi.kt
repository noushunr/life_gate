package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class PlanFlowChartApi {

    data class PlanFlowChartResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: PlanFlowChartData? = null

    )

    data class PlanFlowChartData(

        @SerializedName("chart")
        var chart: MutableList<PlanFlowChart>? = null

    )

    data class PlanFlowChart(

        @SerializedName("title")
        var title: String? = null,

        @SerializedName("status")
        var status: String? = null,

    )

}