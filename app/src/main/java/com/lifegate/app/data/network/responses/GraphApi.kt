package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class GraphApi {

    data class GraphResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: MutableList<GraphData>? = null

    )

    data class GraphData(

        @SerializedName("burned")
        var burned: String? = null,

        @SerializedName("log_date")
        var log_date: String? = null,

        @SerializedName("consumed")
        var consumed: String? = null

    )

}