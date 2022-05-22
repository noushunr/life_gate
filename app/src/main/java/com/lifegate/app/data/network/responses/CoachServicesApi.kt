package com.lifegate.app.data.network.responses

import com.google.gson.annotations.SerializedName

/**
 * Created by Adithya T Raj
 **/

class CoachServicesApi {

    data class CoachServicesResponse(

        @SerializedName("status")
        var status: Boolean? = null,

        @SerializedName("message")
        var message: String? = null,

        @SerializedName("data")
        var data: MutableList<ServicesData>? = null

    )

    data class ServicesData(

        @SerializedName("service_id")
        var serviceId: String? = null,

        @SerializedName("service")
        var service: String? = null

    )

}